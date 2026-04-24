package com.bajaj.srm.models;

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private String participant;
    private int totalScore;

    public LeaderboardEntry(String participant, int totalScore) {
        this.participant = participant;
        this.totalScore = totalScore;
    }

    public String getParticipant() { return participant; }
    public int getTotalScore() { return totalScore; }

    @Override
    public int compareTo(LeaderboardEntry other) {
        // Sort in descending order (highest score first)
        return Integer.compare(other.totalScore, this.totalScore);
    }
}
