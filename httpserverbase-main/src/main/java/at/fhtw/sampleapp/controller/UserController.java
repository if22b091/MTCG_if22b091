package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserController implements RestController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController() {
        this.userService = new UserService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Response handleRequest(Request request){
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for GET and PUT requests
        if (request.getMethod() == Method.GET || request.getMethod() == Method.PUT) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

        try {
            if (request.getMethod() == Method.GET && request.getPathParts().size() > 1 && request.getPathParts().get(0).equals("users")) {
                String usernameFromPath = request.getPathParts().get(1);
                if (!usernameFromPath.equals(usernameFromToken)) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "Access token does not match the username"
                    );
                }
                User user = this.userService.getUserByUsername(usernameFromToken);
                return new Response(HttpStatus.OK, ContentType.JSON, objectMapper.writeValueAsString(user));
            } else if (request.getMethod() == Method.POST) {
                this.userService.addUser(request);
                return new Response(HttpStatus.CREATED, ContentType.JSON, "User successfully created");

            } else if (request.getMethod() == Method.PUT && request.getPathParts().size() > 1 && request.getPathParts().get(0).equals("users")) {
                String usernameFromPath = request.getPathParts().get(1);
                if (!usernameFromPath.equals(usernameFromToken)) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "Access token does not match the username"
                    );
                }
                this.userService.editUser(usernameFromToken, request);
                return new Response(HttpStatus.OK, ContentType.JSON, "User sucessfully updated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Username already taken")) {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "User with same username already registered");
            } else if (e.getMessage().equals("No user found")) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "User not found.");
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}