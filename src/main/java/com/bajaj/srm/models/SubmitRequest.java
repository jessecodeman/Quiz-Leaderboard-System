package com.bajaj.srm.models;

import java.util.List;

public class SubmitRequest {
    private String regNo;
    private List<LeaderboardEntry> leaderboard;

    public SubmitRequest(String regNo, List<LeaderboardEntry> leaderboard) {
        this.regNo = regNo;
        this.leaderboard = leaderboard;
    }

    public String getRegNo() { return regNo; }
    public List<LeaderboardEntry> getLeaderboard() { return leaderboard; }
}
