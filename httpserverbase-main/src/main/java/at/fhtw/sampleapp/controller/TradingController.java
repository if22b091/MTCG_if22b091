package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;

public class TradingController implements RestController {

    // Method to handle incoming HTTP requests related to trading operations.
    public Response handleRequest(Request request) {
        // Retrieve the Authorization token from the request header.
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check the token for GET, POST, and DELETE requests to ensure the request is authorized.
        if (request.getMethod() == Method.GET || request.getMethod() == Method.POST || request.getMethod() == Method.DELETE) {
            result = checkToken(token);
            // If the token check returns a response, it means there was an error (e.g., invalid token), so return that response.
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        // Provide placeholder responses for GET, POST, and DELETE methods indicating that these features are not yet implemented.
        if (request.getMethod() == Method.GET) {
            return new Response(HttpStatus.OK, ContentType.JSON, "Not implemented yet");
        } else if (request.getMethod() == Method.POST) {
            return new Response(HttpStatus.OK, ContentType.JSON, "Not implemented yet");
        } else if (request.getMethod() == Method.DELETE) {
            return new Response(HttpStatus.OK, ContentType.JSON, "Not implemented yet");
        }

        // If the request method is not recognized (i.e., not GET, POST, or DELETE), return a bad request response.
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
