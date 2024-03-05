package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.BattleService;

import java.util.*;
import java.util.concurrent.*;

public class BattleController implements RestController {

    // Declare necessary variables: a service for battle logic, lists for managing requests,
    // a map for request to username, and a scheduled executor for timeouts.
    private final BattleService battleService;
    private List<Request> pendingRequests;
    private Map<Request, String> requestToUsernameMap;
    private String currentBattleLog;
    private final ScheduledExecutorService scheduledTaskExecutor = Executors.newScheduledThreadPool(1);

    public BattleController() {
        this.battleService = new BattleService();
        this.pendingRequests = Collections.synchronizedList(new ArrayList<>());
        this.requestToUsernameMap = new HashMap<>();
        this.currentBattleLog = null;
    }

    // Method to handle POST requests; adds the request to pending requests and schedules a timeout task.
    public void postRequest(Request request, String usernameFromToken) {
        synchronized (pendingRequests) {
            pendingRequests.add(request);
            requestToUsernameMap.put(request, usernameFromToken);
            // Schedule a task to remove the request if it times out after 1 minute.
            scheduledTaskExecutor.schedule(() -> {
                synchronized (pendingRequests) {
                    if (pendingRequests.contains(request)) {
                        pendingRequests.remove(request);
                        requestToUsernameMap.remove(request);
                        System.out.println("Request timed out");
                    }
                }
            }, 1, TimeUnit.MINUTES);
            // If there are two players ready, start the battle.
            if (pendingRequests.size() == 2) {
                // Fetch both requests and usernames, remove them from pending lists, and start the battle.
                Request player1 = pendingRequests.get(0);
                Request player2 = pendingRequests.get(1);
                String username1 = requestToUsernameMap.get(player1);
                String username2 = requestToUsernameMap.get(player2);

                if (username1 != null && username2 != null) {
                    pendingRequests.remove(0);
                    pendingRequests.remove(0);
                    requestToUsernameMap.remove(player1);
                    requestToUsernameMap.remove(player2);
                    currentBattleLog = "Starting battle between " + username1 + " and " + username2 + "\n";
                    currentBattleLog += battleService.startBattle(username1, username2);
                }
            }
        }
    }

    // Main method to handle incoming requests, checks authorization, and calls the appropriate method based on the HTTP method.
    public Response handleRequest(Request request){
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check the token for POST requests.
        if (request.getMethod() == Method.POST) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

        // Handle POST requests by adding them to the queue and returning appropriate responses.
        if (request.getMethod() == Method.POST) {
            postRequest(request, usernameFromToken);

            if (pendingRequests.isEmpty()) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        currentBattleLog != null ? currentBattleLog : ""
                );
            } else {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "Waiting for opponent\n"
                );
            }
        }

        // If the method is not POST, return a bad request response.
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]");
    }

}