package com.smartshelf.dto;

public class HandwrittenListTextRequest {
    private String text;
    private String apiKey;
    private String model;

    public HandwrittenListTextRequest() {}

    public HandwrittenListTextRequest(String text, String apiKey, String model) {
        this.text = text;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
