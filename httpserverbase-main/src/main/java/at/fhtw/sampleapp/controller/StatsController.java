package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.StatsService;

import java.util.List;
import java.util.Map;

public class StatsController implements RestController {

    private StatsService statsService;

    public StatsController() { statsService = new StatsService(); }

    @Override
    public Response handleRequest(Request request){

        // Retrieve the Authorization token from the request header.
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check the token for GET requests to ensure the request is authorized.
        if (request.getMethod() == Method.GET) {
            result = checkToken(token);
            // If the token check returns a response, it means there was an error (e.g., invalid token), so return that response.
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        // Extract the username from the token, if available.
        String usernameFromToken = result != null ? (String) result : null;

        try {
            // Handle GET requests based on the requested path.
            if (request.getMethod() == Method.GET) {
                // Handle requests for user statistics.
                if (request.getPathname().equals("/stats")) {
                    // Retrieve the user's statistics from the stats service.
                    Map<String, Object> stats = statsService.getStats(usernameFromToken);
                    // Convert the statistics to a JSON string.
                    String json = this.getObjectMapper().writeValueAsString(stats);
                    // Return a successful response containing the user's statistics in JSON format.
                    return new Response(HttpStatus.OK, ContentType.JSON, "The stats could be retrieved successfully.\n" + json);
                } else if (request.getPathname().equals("/scoreboard")) {
                    // Handle requests for the game scoreboard.
                    // Retrieve the scoreboard from the stats service.
                    List<Map<String, Object>> scoreboard = statsService.getScoreboard();
                    // Convert the scoreboard to a JSON string.
                    String json = this.getObjectMapper().writeValueAsString(scoreboard);
                    // Return a successful response containing the scoreboard in JSON format.
                    return new Response(HttpStatus.OK, ContentType.JSON, "The scoreboard could be retrieved successfully.\n" + json);
                }
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes.
            e.printStackTrace();
            // Return an internal server error response if an exception is caught.
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
        }

        // If the request method is not GET, or the pathname does not match any known routes, return a bad request response.
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
