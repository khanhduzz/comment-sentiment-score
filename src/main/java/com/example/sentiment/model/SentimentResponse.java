package com.example.sentiment.model;

public class SentimentResponse {
    private double score;
    private String sentiment;

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
}
