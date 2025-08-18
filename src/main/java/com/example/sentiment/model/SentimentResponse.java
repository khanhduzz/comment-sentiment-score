package com.example.sentiment.model;

public class SentimentResponse {
    private double score;
    private String sentiment;
    private String emotion;
    private String intent;
    private String broadcast;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    @Override
    public String toString() {
        return "SentimentResponse{" +
                "score=" + score +
                ", sentiment='" + sentiment + '\'' +
                ", emotion='" + emotion + '\'' +
                ", intent='" + intent + '\'' +
                ", broadcast='" + broadcast + '\'' +
                '}';
    }
}

