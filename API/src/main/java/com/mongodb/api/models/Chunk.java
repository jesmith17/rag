package com.mongodb.api.models;

public interface Chunk {


    public void setId(String id);

    public String getText();

    public void setText(String text);

    public double[] getEmbeddings();

    public void setEmbeddings(double[] embeddings);
}
