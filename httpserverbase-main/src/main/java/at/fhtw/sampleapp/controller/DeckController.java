package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.DeckService;

import java.util.List;

public class DeckController implements RestController {

    private final DeckService deckService;

    public DeckController() { deckService = new DeckService(); }

    @Override
    public Response handleRequest(Request request){

        // Retrieve the Authorization token from the request header.
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check the token for GET and PUT requests to ensure the request is authorized.
        if (request.getMethod() == Method.GET || request.getMethod() == Method.PUT) {
            result = checkToken(token);
            // If the token check returns a response, it means there was an error (e.g., invalid token), so return that response.
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        // Extract the username from the token, if available.
        String usernameFromToken = result != null ? (String) result : null;

        try {
            // Handle GET requests to retrieve a user's deck.
            if (request.getMethod() == Method.GET) {
                // Extract the 'format' parameter from the request query parameters.
                String format = null;
                String params = request.getParams();
                if (params != null) {
                    String[] queryParams = params.split("&");
                    for (String param : queryParams) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2 && "format".equals(keyValue[0])) {
                            format = keyValue[1];
                            break;
                        }
                    }
                }
                // Retrieve the deck based on the username and format.
                List<?> deck = deckService.getDeck(usernameFromToken, format);
                if ("plain".equals(format)) {
                    // Return the deck in plain text format if specified.
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "The deck has cards, the response contains these: \n" + String.join("\n", (List<String>) deck));
                } else {
                    // Return the deck in JSON format by default.
                    String json = this.getObjectMapper().writeValueAsString(deck);
                    return new Response(HttpStatus.OK, ContentType.JSON, "The deck has cards, the response contains these: \n" + json);
                }
            } else if (request.getMethod() == Method.PUT) {
                // Handle PUT requests to update a user's deck.
                deckService.updateDeck(usernameFromToken, request);
                return new Response(HttpStatus.OK, ContentType.JSON, "The deck has been successfully configured");
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes.
            e.printStackTrace();
            // Handle specific exceptions, such as when no cards are found in the deck, or the provided deck is invalid.
            if (e.getMessage().equals("No cards found")) {
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "The request was fine, but the deck doesn't have any cards");
            } else if (e.getMessage().equals("not enough cards provided")) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "The provided deck did not include the required amount of cards");
            } else if (e.getMessage().equals("user does not own all cards")) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "At least one of the provided cards does not belong to the user or is not available.");
            } else {
                // For all other exceptions, return an internal server error response.
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }
        }

        // If the request method is not GET or PUT, or an exception was not caught, return a bad request response.
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}