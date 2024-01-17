from lxml import html
import requests
import pymongo
import boto3
from langchain_community.embeddings.bedrock import BedrockEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
import PyPDF2

CHUNKS = [{"chunks": 100, "overlap": 10}, 
          {"chunks": 500, "overlap": 50}, 
          {"chunks": 1000, "overlap": 100}]


url = 'mongodb+srv://<username>:<password>@cluster0.bjpsr.mongodb.net/?retryWrites=true&w=majority'
mongoClient = pymongo.MongoClient(url)
db = mongoClient['hackathon']

bedrock_client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")


def process_chunks(text, model_id="amazon.titan-embed-text-v1"):

    for c in CHUNKS:
        cname = str(c['chunks']) + "_chunks"
        print(cname)
        collection = db[cname]

        bedrock_embeddings = BedrockEmbeddings(model_id=model_id, client=bedrock_client)

        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=c['chunks'],
            chunk_overlap=c['overlap'],
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
    
    file1 = open('docs.txt', 'r')
    lines = file1.readlines()
 
    count = 0

    for line in lines:
        count += 1
        print("Source{}: {}".format(count, line.strip()))
        url = str(line.strip())
        page = requests.get(url)
        tree = html.fromstring(page.content)

        content1 = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')
        content2 = tree.xpath('//code[@class="leafygreen-ui-3lqzn5"]/text()')
        content = content1 + content2
        separator = ' '
        text = separator.join(content)
        text = text.replace('\n', ' ').replace('\r', '')

        process_chunks(text, model_id="amazon.titan-embed-text-v1")

    return text



def ask_question(question, model_id, collection):

    collection = db[collection]
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
    
    for c in CHUNKS:
        cname = str(c['chunks']) + "_chunks"
        db[cname].delete_many({})
    process_pages()

# uncomment to ingest data
# main()

ask_question("What are aggregations?", model_id="amazon.titan-embed-text-v1", collection="500_chunks")