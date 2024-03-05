package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.TransactionService;

import java.util.List;
import java.util.Map;

public class TransactionController implements RestController {

    private final TransactionService transactionService;

    public TransactionController() { this.transactionService = new TransactionService(); }

    @Override
    public Response handleRequest(Request request){
        // Retrieve the Authorization token from the request header.
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check the token for POST requests to ensure the request is authorized.
        if (request.getMethod() == Method.POST) {
            result = checkToken(token);
            // If the token check returns a response, it means there was an error (e.g., invalid token), so return that response.
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        // Extract the username from the token, if available.
        String usernameFromToken = result != null ? (String) result : null;

        try {
            // Handle requests for acquiring card packages or selecting cards based on the requested path.
            if(request.getPathname().equals("/transactions/packages")) {
                if (request.getMethod() == Method.POST) {
                    // Attempt to buy a package using the transaction service and return a success response if successful.
                    this.transactionService.acquirePackages(usernameFromToken);
                    return new Response(HttpStatus.OK, ContentType.JSON, "A package has been successfully bought");
                } else if (request.getMethod() == Method.GET) {
                    // Retrieve available cards for selection and return them in JSON format.
                    List<Map<String, Object>> cards = this.transactionService.selectCards();
                    String json = getObjectMapper().writeValueAsString(cards);
                    return new Response(HttpStatus.OK, ContentType.JSON, json);
                }
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes.
            e.printStackTrace();
            // Handle specific exceptions, such as when no packages are available or the user lacks sufficient funds.
            if (e.getMessage().equals("No packages found")) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "No card package available for buying");
            } else if (e.getMessage().equals("Not enough coins")) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "Not enough money for buying a card package");
            } else {
                // For all other exceptions, return an internal server error response.
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }
        }

        // If the request method does not match expected methods or paths, or an exception was not caught, return a bad request response.
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
