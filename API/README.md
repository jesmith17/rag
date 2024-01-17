# API #

Java & Spring-Boot based API to handle the execution of the RAG architecture and MongoDB Atlas Vector Search queries


## How to Build ##

### Prerequisites ###

* Java17
* Gradle
* AWS Bedrock (Titan LLM)


### Configure ###

Update the ```application.properties``` file located in ``` src/main/resources``` with the appropriate MongoDB Atlas connection string

Update you AWS credentials in your ``` .aws/credentials ``` file with the correct credentials. You can copy them from the AWS screen when you access AWS via Okta.

### Build ###

Run 
``` gradle bootJar ``` to create the executable jar file to run the application
Alternatively, you can run ``` gradle bootRun ``` to just execute it against the source code folder. 



### Details ### 
Application uses the AWS Titan LLM to handle the questions. We are experimenting with what happens when you adjust the configurations of the chunk size and the inputs to the vectorSearch and how it impacts the overall response from the LLM.
