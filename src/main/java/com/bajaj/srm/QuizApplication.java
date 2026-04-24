package com.bajaj.srm;

import com.bajaj.srm.models.LeaderboardEntry;
import com.bajaj.srm.models.QuizEvent;
import com.bajaj.srm.models.QuizResponse;
import com.bajaj.srm.models.SubmitRequest;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuizApplication {

    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    
    // IMPORTANT: Replace with your actual registration number provided for the assignment.
    private static final String REG_NO = "2024CS101"; 
    
    private static final int TOTAL_POLLS = 10;
    private static final int POLL_DELAY_MS = 5000;

    public static void main(String[] args) {
        System.out.println("Starting Quiz Leaderboard Application");
        System.out.println("Registration Number: " + REG_NO);
        
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        
        // Use a set to deduplicate by roundId_participant
        Set<String> processedEvents = new HashSet<>();
        // Map to keep track of total scores per participant
        Map<String, Integer> participantScores = new HashMap<>();
        
        for (int poll = 0; poll < TOTAL_POLLS; poll++) {
            System.out.println("\n--- Polling API [" + poll + "/" + (TOTAL_POLLS - 1) + "] ---");
            try {
                String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + poll;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    QuizResponse quizResponse = gson.fromJson(response.body(), QuizResponse.class);
                    System.out.println("Received Response - Set ID: " + quizResponse.getSetId());
                    
                    if (quizResponse.getEvents() != null) {
                        for (QuizEvent event : quizResponse.getEvents()) {
                            // Deduplication condition (roundId + participant)
                            String uniqueEventKey = event.getRoundId() + "_" + event.getParticipant();
                            if (!processedEvents.contains(uniqueEventKey)) {
                                processedEvents.add(uniqueEventKey);
                                int currentScore = participantScores.getOrDefault(event.getParticipant(), 0);
                                participantScores.put(event.getParticipant(), currentScore + event.getScore());
                                System.out.println("  [New Event] " + event.getParticipant() + " scored " + event.getScore() + " in round " + event.getRoundId());
                            } else {
                                System.out.println("  [Duplicate Event] Ignoring score for " + event.getParticipant() + " in round " + event.getRoundId());
                            }
                        }
                    } else {
                        System.out.println("  No events in this poll.");
                    }
                } else {
                    System.err.println("Error: API returned status code " + response.statusCode());
                }
                
                // Wait for the mandatory 5 seconds between polls
                if (poll < TOTAL_POLLS - 1) {
                    System.out.println("Waiting 5 seconds before next poll...");
                    Thread.sleep(POLL_DELAY_MS);
                }
                
            } catch (Exception e) {
                System.err.println("Exception occurred during poll " + poll + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Step 2: Generate the leaderboard sorted by totalScore descending
        System.out.println("\n--- Generating Leaderboard ---");
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : participantScores.entrySet()) {
            leaderboard.add(new LeaderboardEntry(entry.getKey(), entry.getValue()));
        }
        
        // The compareTo method in LeaderboardEntry handles descending sort
        Collections.sort(leaderboard);
        
        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            System.out.println((i + 1) + ". " + entry.getParticipant() + " : " + entry.getTotalScore());
        }
        
        // Step 3: Submit Results
        System.out.println("\n--- Submitting Results ---");
        try {
            SubmitRequest submitRequest = new SubmitRequest(REG_NO, leaderboard);
            String requestBody = gson.toJson(submitRequest);
            System.out.println("Submit Payload: " + requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/quiz/submit"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Submit Status Code: " + response.statusCode());
            System.out.println("Submit Response: " + response.body());
            
        } catch (Exception e) {
            System.err.println("Exception occurred during submission: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Done.");
    }
}
