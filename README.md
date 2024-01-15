# Chunky Mongo #

## Purpose ##

Building a RAG architecture based GenAI project is not difficult. What is hard is perfecting a GenAI product. That's because the work is in fine tuning the context that you pass to a prompt to get the correct answer. And  in understanding that answer can drift over time and must constantly be measured and monitored. 

Chunky Mongo work to demonstrate that the chunk sizes used to do embeddings of private data can greatly impact the accuracy of the eventual output. 


## Content ##

# Chunky Mongo #
Angular CLI app used to provide a pretty front-end for the data analysis. To act as a chatbot type interface so you don't have to do all of this via postman. 


# API #
Java and Spring-Boot based app that provides the REST endpoint for the chatbot, and does the actual RAG architecture

# Scraper #

Simple python script to scrape data from a mongodb documentation page, generate vector embeddings, and store it in Atlas. 






