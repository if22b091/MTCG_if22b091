package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

public interface RestController {

    default Object checkToken(String token) {
        // Check if the token is null
        if (token == null) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "Access token is missing or invalid"
            );
        }

        // Remove the "Bearer " part from the token
        token = token.replace("Bearer ", "");

        // Check if the token is valid
        if (!token.contains("-") || !token.split("-")[1].equals("mtcgToken")) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "Access token is missing or invalid"
            );
        }

        // Split the token around the "-" character
        String[] parts = token.split("-");
        // The first part is the username
        String usernameFromToken = parts[0];

        return usernameFromToken;
    }

    ObjectMapper objectMapper = new ObjectMapper();

    default ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    Response handleRequest(Request request);

}
