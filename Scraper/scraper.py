from lxml import html
import requests
import pymongo
import boto3
from langchain_community.embeddings.bedrock import BedrockEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
import PyPDF2


url = 'mongodb+srv://<username>:<password>@cluster0.bjpsr.mongodb.net/?retryWrites=true&w=majority'
mongoClient = pymongo.MongoClient(url)
db = mongoClient['hackathon']
collection = db['hackathon_rag']

bedrock_client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")

pdf_path = 'genai-principles.pdf'

def extract_text_from_pdf(pdf_path):
    text = ""

    # Open the PDF file
    with open(pdf_path, 'rb') as file:
        reader = PyPDF2.PdfReader(file)

        # Iterate over each page and extract text
        for page_num in range(len(reader.pages)):
            page = reader.pages[page_num]
            text += page.extract_text()

    text = text.replace('\n', ' ').replace('\r', '')
    return text


def process_chunks(chunk_size, chunk_overlap, text, model_id="amazon.titan-embed-text-v1"):

    collection.delete_many({})

    bedrock_embeddings = BedrockEmbeddings(model_id=model_id, client=bedrock_client)

    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=chunk_size,
        chunk_overlap=chunk_overlap,
    )
    chunks = text_splitter.split_text(text)

    for chunk in chunks:
        embedding = bedrock_embeddings.embed_query(chunk)
        payload = {
            "text": chunk,
            "embeddings": embedding
        }
        collection.insert_one(payload)


def ask_question(question, model_id):

    bedrock_embeddings = BedrockEmbeddings(model_id=model_id, client=bedrock_client)

    embedding = bedrock_embeddings.embed_query(question)

    context = collection.aggregate([{
        "$vectorSearch": {
            "queryVector": embedding,
            "path": "embeddings",
            "numCandidates": 10,
            "index": "rag",
            "limit": 1
            }
        },
        {
            "$project": {
                "embeddings": 0
            }
        }
    ])

    print(list(context))
    for l in list(context):
        print(l['text'])

def main():
    
    extracted_text = extract_text_from_pdf(pdf_path)

    process_chunks(500, 50, extracted_text, model_id="amazon.titan-embed-text-v1")

main()
    
ask_question("What are large language models used for?", model_id="amazon.titan-embed-text-v1")