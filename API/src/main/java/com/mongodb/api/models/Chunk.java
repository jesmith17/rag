package com.mongodb.api.models;

import org.springframework.data.annotation.Id;

public class Chunk {

    @Id
    private String id;
    private String text;
    private double[] embeddings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double[] getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(double[] embeddings) {
        this.embeddings = embeddings;
    }
}
