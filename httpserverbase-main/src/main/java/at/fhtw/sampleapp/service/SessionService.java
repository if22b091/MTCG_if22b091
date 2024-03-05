package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.SessionRepository;
import at.fhtw.sampleapp.persistence.repository.SessionRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionService extends AbstractService {

    // loginUser method attempts to authenticate user.
    public boolean loginUser(Request request) {
        // Use try-with-resources to ensure that UnitOfWork resources are properly closed.
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            // Initialize a session repository to interact with session data.
            SessionRepository sessionRepository = new SessionRepositoryImpl(unitOfWork);
            String body = request.getBody(); // Extract the request body.
            ObjectMapper objectMapper = new ObjectMapper(); // Create an ObjectMapper for JSON processing.
            JsonNode jsonNode = objectMapper.readTree(body); // Parse the request body to a JSON node.

            // Extract username and password from the JSON node.
            String username = jsonNode.get("Username").asText();
            String password = jsonNode.get("Password").asText();
            // Instantiate a User object with extracted credentials.
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            // Use the session repository to find and authenticate the user.
            return sessionRepository.findUser(user); // Return the authentication result.
        } catch (Exception e) {
            e.printStackTrace(); // Log exceptions.
            return false; // Return false in case of any exceptions.
        }
    }
}
