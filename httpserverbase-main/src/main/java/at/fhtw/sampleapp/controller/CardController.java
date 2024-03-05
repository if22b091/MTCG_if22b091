package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.CardService;

import java.util.List;
import java.util.Map;

public class CardController implements RestController {

    private final CardService cardService;

    public CardController() { this.cardService = new CardService(); }

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
            // If the request is a GET request, handle retrieving the user's cards.
            if (request.getMethod() == Method.GET) {
                // Call the card service to get the cards associated with the username.
                List<Map<String, Object>> cards = cardService.getCards(usernameFromToken);
                // Convert the list of cards to a JSON string.
                String json = this.getObjectMapper().writeValueAsString(cards);
                // Return a successful response containing the JSON string of cards.
                return new Response(HttpStatus.OK, ContentType.JSON, "The user has cards, the response contains these: \n" + json);
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes.
            e.printStackTrace();
            // Handle specific exceptions, such as when no cards are found for the user.
            if (e.getMessage().equals("No cards found")) {
                // Return an ACCEPTED status to indicate the request was fine, but no cards exist for the user.
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "The request was fine, but the user doesn't have any cards");
            } else {
                // For all other exceptions, return an internal server error response.
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }
        }

        // If the request method is not GET or an exception was not caught, return a bad request response.
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}