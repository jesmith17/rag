from lxml import html
import requests
import pymongo
import boto3
from langchain_community.embeddings.bedrock import BedrockEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
import PyPDF2

CHUNKS = 1000
OVERLAP = 100


url = 'mongodb+srv://<username>:<password>@cluster0.bjpsr.mongodb.net/?retryWrites=true&w=majority'
mongoClient = pymongo.MongoClient(url)
db = mongoClient['hackathon']
cname = str(CHUNKS) + "_chunks"
print(cname)
collection = db[cname]

bedrock_client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")


def process_chunks(chunk_size, chunk_overlap, text, model_id="amazon.titan-embed-text-v1"):

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


def process_pages():
    text = ""

    collection.delete_many({})

    file1 = open('docs.txt', 'r')
    lines = file1.readlines()
 
    count = 0

    for line in lines:
        count += 1
        print("Line{}: {}".format(count, line.strip()))
        url = str(line.strip())
        page = requests.get(url)
        tree = html.fromstring(page.content)

        content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')
        separator = ' '
        text = separator.join(content)

        process_chunks(CHUNKS, OVERLAP, text, model_id="amazon.titan-embed-text-v1")

    return text



def ask_question(question, model_id):

    bedrock_embeddings = BedrockEmbeddings(model_id=model_id, client=bedrock_client)

    embedding = bedrock_embeddings.embed_query(question)

    context = collection.aggregate([{
        "$vectorSearch": {
            "queryVector": embedding,
            "path": "embeddings",
            "numCandidates": 10,
            "index": "vector_index",
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
    
    process_pages()

main()
    
ask_question("Can you delete Atlas Search indexes?", model_id="amazon.titan-embed-text-v1")