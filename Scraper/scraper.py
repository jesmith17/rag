from lxml import html
import requests
import pymongo
import boto3
from langchain_community.embeddings.bedrock import BedrockEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter



#url = 'mongodb://localhost:27017'
url = 'mongodb+srv://<username>:<password>@cluster0.bjpsr.mongodb.net/?retryWrites=true&w=majority'

mongoClient = pymongo.MongoClient(url)
db = mongoClient['hackathon']
collection = db['1000_chunk']


bedrock_client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")
bedrock_embeddings = BedrockEmbeddings(model_id="amazon.titan-embed-text-v1",
                                       client=bedrock_client)

page = requests.get('https://www.mongodb.com/docs/atlas/atlas-search/create-index/')
tree = html.fromstring(page.content)

content = tree.xpath('//p[@class="leafygreen-ui-kkpb6g"]/text()')

separator = ' '
text = separator.join(content)

text_splitter = RecursiveCharacterTextSplitter(
    # Set a really small chunk size, just to show.
    chunk_size=1000,
    chunk_overlap=100,
)
chunks = text_splitter.split_text(text)

for chunk in chunks:
    embedding = bedrock_embeddings.embed_query(chunk)
    payload = {
        "text": chunk,
        "embeddings": embedding
    }
    collection.insert_one(payload)






