package com.mongodb.api.models;

public class ChatResponse {

    public String prompt;
    public String answer;

    public String llmInput;


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
}
