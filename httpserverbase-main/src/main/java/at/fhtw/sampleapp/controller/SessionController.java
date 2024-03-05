package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.SessionService;

public class SessionController implements RestController {
    private final SessionService sessionService;

    public SessionController() { this.sessionService = new SessionService(); }

    @Override
    public Response handleRequest(Request request) {
        // Attempt to log in the user with the provided credentials.
        boolean loginSuccessful = this.sessionService.loginUser(request);
        // Check the outcome of the login attempt.
        if (!loginSuccessful) {
            // If login fails, return an unauthorized response indicating invalid credentials.
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Invalid username/ password provided");
        } else {
            // If login is successful, return an OK response indicating the user was successfully logged in.
            return new Response(HttpStatus.OK, ContentType.JSON, "User login successful");
        }
    }
}
