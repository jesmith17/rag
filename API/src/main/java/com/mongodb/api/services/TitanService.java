package com.mongodb.api.services;

import com.mongodb.api.models.ChatRequest;
import com.mongodb.api.models.ChatResponse;
import com.mongodb.api.models.Chunk;
import com.mongodb.api.models.Feedback;
import com.mongodb.api.repository.FeedbackRepository;
import org.bson.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.stage;

@Service
public class TitanService {

    private BedrockRuntimeClient runtime;

    @Autowired
    private MongoTemplate template;

    @Autowired
    private FeedbackRepository feedbackRepo;

    public TitanService(){
        runtime = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

    }



    public double[] generateEmbeddings(String prompt){

        JSONObject jsonBody = new JSONObject()
                .put("inputText", prompt);
        SdkBytes body = SdkBytes.fromUtf8String(
                jsonBody.toString()
        );
        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("amazon.titan-embed-text-v1")
                .contentType("application/json")
                .body(body)
                .build();

        InvokeModelResponse response = runtime.invokeModel(request);

        JSONObject jsonObject = new JSONObject(
                response.body().asString(StandardCharsets.UTF_8)
        );
        JSONArray embeddings = jsonObject.getJSONArray("embedding");

        double[] embeddingsArray = new double[embeddings.length()];
        for(int i=0; i<embeddings.length(); i++){
            embeddingsArray[i] = embeddings.getDouble(i);
        }
        return embeddingsArray;

    }



    public ChatResponse generateResponse(ChatRequest chatRequest) {


        // Generate embeddings for prompt
        double[] embeddingsArray = this.generateEmbeddings(chatRequest.getPrompt());

        // Lookup context in Mongo based on vector search
        List<Chunk> contextEntries = this.vectorQuery(embeddingsArray, chatRequest);

        // Pass prompt to LLM to do query
        StringBuilder builder = new StringBuilder();

        for(Chunk chunk: contextEntries) {
            if ((builder.length() + chunk.getText().length()) <= 3900) {
                builder.append(chunk.getText());
            }
        }

        // Always have these 2, the ones above are for the context found in the DB lookup
        builder.append(" \nBased on the above context, ");
        builder.append(chatRequest.getPrompt());

        JSONObject configObject = new JSONObject()
                .put("maxTokenCount", 4096)
                .put("stopSequences", new JSONArray())
                .put("temperature", 0)
                .put("topP", 1);

        JSONObject jsonBody = new JSONObject()
                .put("inputText", builder.toString())
                .put("textGenerationConfig", configObject);


        SdkBytes body = SdkBytes.fromUtf8String(
                jsonBody.toString()
        );

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("amazon.titan-text-express-v1")
                .contentType("application/json")
                .body(body)
                .build();

        InvokeModelResponse response = runtime.invokeModel(request);

        JSONObject jsonObject = new JSONObject(
                response.body().asString(StandardCharsets.UTF_8)
        );

        String completion = jsonObject.getJSONArray("results").getJSONObject(0).getString("outputText");

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setAnswer(completion);
        chatResponse.setPrompt(chatRequest.getPrompt());
        chatResponse.setLlmInput(builder.toString());

        // Create feedback stub
        Feedback feedback = new Feedback();
        feedback.setAnswer(chatResponse.getAnswer());
        feedback.setPrompt(chatResponse.getPrompt());
        feedback.setEmbeddings(embeddingsArray);
        feedback.setChunkSize(chatRequest.getChunkSize());
        feedback.setLlmInput(chatResponse.getLlmInput());
        Feedback saved = feedbackRepo.save(feedback);

        chatResponse.setFeedbackId(saved.getId());
        return chatResponse;
        

    }


    private List<Chunk> vectorQuery(double[] embeddings, ChatRequest chatRequest) {

        String collectionName = null;
        switch (chatRequest.getChunkSize()){
            case 1000:
                collectionName = "1000_chunks";
                break;
            case 500:
                collectionName = "500_chunks";
                break;
            case 100:
                collectionName = "100_chunks";
                break;
            default:
                collectionName = "100_chunks";
        }

        String indexName = "vector_index";
        switch (chatRequest.getSimilarity()){
            case "euclidean":
                indexName = "vector_index_euclidean";
                break;
            case "dotproduct":
                indexName = "vector_index_dotproduct";
                break;
            default:
                indexName = "vector_index";
                break;
        }

        BsonDocument searchQuery = new BsonDocument();
        BsonArray embeddingsArray = new BsonArray();
        for(double vector: embeddings){
            embeddingsArray.add(new BsonDouble(vector));
        }
        searchQuery.append("queryVector", embeddingsArray);
        searchQuery.append("path",new BsonString("embeddings"));
        searchQuery.append("numCandidates", new BsonInt32(chatRequest.getNumCandidates()));
        searchQuery.append("index", new BsonString(indexName));
        searchQuery.append("limit", new BsonInt32(chatRequest.getLimit()));


        Aggregation agg = newAggregation(
             stage(new BsonDocument().append("$vectorSearch", searchQuery))
        );

        AggregationResults<Chunk> aggResult = template.aggregate(agg,collectionName, Chunk.class);
        List<Chunk> results = aggResult.getMappedResults();


        return results;
    }



}
