package com.mongodb.api.services;

import net.minidev.json.JSONArray;

import org.springframework.stereotype.Service;
import org.json.JSONObject;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.StandardCharsets;

@Service
public class StabilityService {

    private BedrockRuntimeClient runtime;

    public StabilityService(){
        runtime = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

    }



    public String generateJson(String prompt) {

        JSONArray array = new JSONArray();
        array.add(new JSONObject().put("text", prompt));

        JSONObject jsonBody = new JSONObject()
                .put("text_prompts", array)
                .put("cfg_scale", 10)
                .put("seed", 0)
                .put("steps", 50);

        System.out.println(jsonBody.toString());

        SdkBytes body = SdkBytes.fromUtf8String(
                jsonBody.toString()
        );

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("stability.stable-diffusion-xl-v0")
                .contentType("application/json")
                .body(body)
                .build();

        InvokeModelResponse response = runtime.invokeModel(request);

        JSONObject jsonObject = new JSONObject(
                response.body().asString(StandardCharsets.UTF_8)
        );
        System.out.println(jsonObject.toString());

        String completion = jsonObject.getJSONArray("artifacts").getJSONObject(0).getString("base64");

        return completion;

    }



}
