# Quiz Leaderboard System - Internship Assignment

This is a backend integration project that consumes API responses from an external system representing a quiz show, processes the data, handles duplications, and submits a final aggregated leaderboard.

## Features

- **Polls the Validator API**: Polls 10 times to collect events.
- **Enforces Rate Limiting**: Ensures a mandatory 5-second delay between polls.
- **Handles Deduplication**: In distributed systems, events might be delivered multiple times. Duplicate responses are ignored using `roundId` and `participant`.
- **Aggregates Scores**: Computes total scores per participant.
- **Generates Leaderboard**: Creates a leaderboard sorted by total score descending.
- **Submits Final Result**: Automatically posts the finalized JSON payload to the API.

## Requirements

- **Java 11 or higher** (Uses the modern `java.net.http.HttpClient`)
- **Maven** (For dependency management - Uses Gson for JSON serialization/deserialization)

## How to Configure

1. Open `src/main/java/com/bajaj/srm/QuizApplication.java`
2. Update the `REG_NO` constant with your actual registration number:

   ```java
   private static final String REG_NO = "YOUR_REG_NO_HERE";
   ```

## How to Build and Run

You can build and run this project using Maven.

### 1. Build the Project
In the root directory of the project (where the `pom.xml` is located), run:
```sh
mvn clean install
```

### 2. Run the Application
You can use `mvn exec:java` to run the main class directly:
```sh
mvn exec:java -Dexec.mainClass="com.bajaj.srm.QuizApplication"
```

Or you can run it via your IDE (IntelliJ IDEA, Eclipse, VS Code) by running the `main` method in `QuizApplication.java`.

## Submission Instructions

1. Push this code to a public GitHub repository.
2. Ensure you have updated the `REG_NO` before submitting.
3. Submit the GitHub URL via the provided Google Form link in the assignment instructions.
