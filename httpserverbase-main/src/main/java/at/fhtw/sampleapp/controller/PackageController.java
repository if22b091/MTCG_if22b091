package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.service.PackageService;

import java.util.List;

public class PackageController implements RestController {

    private final PackageService packageService;

    public PackageController() { this.packageService = new PackageService(); }

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
            // Handle POST requests to add new card packages.
            if (request.getMethod() == Method.POST) {
                // Check if the user is an admin before allowing them to add cards.
                if (!"admin".equals(usernameFromToken)) {
                    // Return a forbidden response if the user is not an admin.
                    return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "Provided user is not 'admin'");
                } else {
                    // Add cards to the package using the package service and return a successful creation response.
                    this.packageService.addCards(request);
                    return new Response(HttpStatus.CREATED, ContentType.JSON, "Package and cards successfully created");
                }
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes.
            e.printStackTrace();
            // Handle specific exceptions, such as when a card in the package already exists.
            if (e.getMessage().equals("Card already exists")) {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "At least one card in the packages already exists");
            } else {
                // For all other exceptions, return an internal server error response.
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Card not added");
            }
        }

        // If the request method is not POST, or an exception was not caught, return a bad request response.
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
