package com.mongodb.api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("feedback")
public class Feedback {

    @Id
    private String id;
    private String prompt;
    private String answer;
    private String llmInput;

    private int chunkSize;

    private boolean thumbsUp;
    private String feedback;

    private double[] embeddings;

    private Justification justification;

    public Justification getJustification() {
        return justification;
    }

    public void setJustification(Justification justification) {
        this.justification = justification;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getchunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {this.chunkSize = chunkSize;}

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getLlmInput() {
        return llmInput;
    }

    public void setLlmInput(String llmInput) {
        this.llmInput = llmInput;
    }

    public boolean isThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(boolean thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public double[] getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(double[] embeddings) {
        this.embeddings = embeddings;
    }


}
