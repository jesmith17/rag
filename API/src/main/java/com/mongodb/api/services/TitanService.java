package com.mongodb.api.services;


import com.mongodb.api.models.ChatResponse;
import com.mongodb.api.models.Chunk;
import com.mongodb.api.repository.HundredEmbeddingsRepository;
import com.mongodb.api.repository.ThousandEmbeddingsRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class TitanService {

    private BedrockRuntimeClient runtime;

    @Autowired
    private ThousandEmbeddingsRepository thousandRepository;
    @Autowired
    private HundredEmbeddingsRepository hundredRepository;

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



    public ChatResponse generateResponse(String prompt, int chunkSize) {



        // Generate embeddings for prompt
        double[] embeddingsArray = this.generateEmbeddings(prompt);

        List<Chunk> contextEntries = new ArrayList<>();
        // Lookup context in Mongo based on vector search
        if (chunkSize == 1000) {
            contextEntries = thousandRepository.vectorSearch(embeddingsArray);
        } else if (chunkSize == 100) {
            contextEntries = hundredRepository.vectorSearch(embeddingsArray);
        }


        // Pass prompt to LLM to do query
        StringBuilder builder = new StringBuilder();

        for(Chunk chunk: contextEntries) {
            builder.append(chunk.getText());
        }

        // Always have these 2, the ones above are for the context found in the DB lookup
        builder.append("Based on the above context");
        builder.append(prompt);

        String llmInput = builder.toString();
        if (llmInput.length() > 4000) {
            llmInput = llmInput.substring(llmInput.length() - 3999);
        }


        JSONObject configObject = new JSONObject()
                .put("maxTokenCount", 4096)
                .put("stopSequences", new JSONArray())
                .put("temperature", 0)
                .put("topP", 1);

        JSONObject jsonBody = new JSONObject()
                .put("inputText", llmInput)
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
        chatResponse.setPrompt(prompt);

        return chatResponse;
        

    }



}
