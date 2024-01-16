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
collection = db['100_chunks']

bedrock_client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")


def extract_context():
    text = ""

    page = requests.get('https://www.mongodb.com/docs/atlas/atlas-search/create-index/')
    tree = html.fromstring(page.content)

    content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')

    separator = ' '
    text1 = separator.join(content)

    page = requests.get('https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-overview/')
    tree = html.fromstring(page.content)

    content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')

    separator = ' '
    text2 = separator.join(content)

    page = requests.get('https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-type/')
    tree = html.fromstring(page.content)

    content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')

    separator = ' '
    text3 = separator.join(content)

    page = requests.get('https://www.mongodb.com/docs/atlas/atlas-vector-search/delete-index/')
    tree = html.fromstring(page.content)

    content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')

    separator = ' '
    text4 = separator.join(content)

    page = requests.get('https://www.mongodb.com/docs/atlas/atlas-vector-search/view-index/')
    tree = html.fromstring(page.content)

    content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')

    separator = ' '
    text5 = separator.join(content)

    page = requests.get('https://www.mongodb.com/docs/atlas/atlas-vector-search/edit-index/')
    tree = html.fromstring(page.content)

    content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')

    separator = ' '
    text6 = separator.join(content)


    page = requests.get('https://www.mongodb.com/docs/atlas/atlas-vector-search/changelog/')
    tree = html.fromstring(page.content)

    content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')

    separator = ' '
    text7 = separator.join(content)

    return text1 + ' ' + text2 + ' ' + text3 + ' ' + text4 + ' ' + text5 + ' ' + text6 + ' ' + text7

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
    
    extracted_text = extract_context()

    process_chunks(1000, 100, extracted_text, model_id="amazon.titan-embed-text-v1")

main()
    
ask_question("Can you delete Atlas Search indexes?", model_id="amazon.titan-embed-text-v1")