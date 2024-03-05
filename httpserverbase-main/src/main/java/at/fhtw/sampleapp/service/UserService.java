package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.UserRepository;
import at.fhtw.sampleapp.persistence.repository.UserRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserService extends AbstractService {

    private UserRepository getUserRepository(UnitOfWork unitOfWork) {
        return new UserRepositoryImpl(unitOfWork);
    }

    // Retrieves a User object by their username.
    public User getUserByUsername(String username) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = getUserRepository(unitOfWork);
            // Fetch and return the user by their username.
            return userRepository.searchByUsername(username);
        }
    }

    // Adds a new user.
    public void addUser(Request request) throws JsonProcessingException {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = getUserRepository(unitOfWork);
            String body = request.getBody(); // Extract the body of the request.

            ObjectMapper objectMapper = new ObjectMapper(); // Create an ObjectMapper to parse the JSON.
            JsonNode jsonNode = objectMapper.readTree(body); // Parse the body to a JSON node.

            // Extract username and password from the JSON node.
            String username = jsonNode.get("Username").asText();
            String password = jsonNode.get("Password").asText();

            // Create a new User object with the provided details.
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            // Add the user to the database.
            userRepository.addUser(user);
            // Initialize user's deck and stats entries.
            userRepository.insertUsernameToDeck(username);
            userRepository.insertUsernameToStats(username);
        }
    }

    // Edits an existing user's details.
    public void editUser(String username, Request request) throws JsonProcessingException {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = getUserRepository(unitOfWork);
            String body = request.getBody(); // Extract the body of the request.

            ObjectMapper objectMapper = new ObjectMapper(); // Use ObjectMapper to parse the JSON.
            JsonNode jsonNode = objectMapper.readTree(body); // Parse the body to a JSON node.

            // Extract name, bio, and image URL from the JSON node.
            String name = jsonNode.get("Name").asText();
            String bio = jsonNode.get("Bio").asText();
            String image = jsonNode.get("Image").asText();

            // Create a new User object with the updated details.
            User user = new User();
            user.setUsername(username); // Username remains unchanged.
            user.setName(name);
            user.setBio(bio);
            user.setImage(image);

            // Update the user's details in the database.
            userRepository.editUser(username, user);
        }
    }
}
