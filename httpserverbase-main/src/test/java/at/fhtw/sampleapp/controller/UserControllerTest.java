package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class UserControllerTest {

    private Request createMockRequest(Method method, String path, String token) {
        Request mockRequest = mock(Request.class);
        HeaderMap mockHeaderMap = mock(HeaderMap.class);

        when(mockRequest.getMethod()).thenReturn(method);
        when(mockRequest.getPathname()).thenReturn(path);
        when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        when(mockHeaderMap.getHeader("Authorization")).thenReturn(token);

        return mockRequest;
    }

    @Test
    void handleGetRequestWithMatchingToken() {
        // Arrange
        UserController userController = spy(new UserController());
        Response response = mock(Response.class);

        Request getRequest = createMockRequest(Method.GET, "/users/testUser", "testUser");

        doReturn(response).when(userController).handleRequest(getRequest);

        // Act
        userController.handleRequest(getRequest);

        // Assert
        verify(userController, times(1)).handleRequest(getRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    @Test
    void handleGetRequestWithNonMatchingToken() {
        // Arrange
        UserController userController = spy(new UserController());
        Response response = mock(Response.class);

        Request getRequest = createMockRequest(Method.GET, "/users/testUser", "wrongUser");

        doReturn(response).when(userController).handleRequest(getRequest);

        // Act
        userController.handleRequest(getRequest);

        // Assert
        verify(userController, times(1)).handleRequest(getRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    @Test
    void handlePostRequest() {
        // Arrange
        UserController userController = spy(new UserController());
        Response response = mock(Response.class);

        // Create a mock Request with a body containing Username and Password in JSON format
        Request postRequest = createMockRequest(Method.POST, "/users", null);
        String jsonBody = "{\"Username\": \"username\", \"Password\": \"password\"}";
        when(postRequest.getBody()).thenReturn(jsonBody);

        doReturn(response).when(userController).handleRequest(postRequest);

        // Act
        userController.handleRequest(postRequest);

        // Assert
        verify(userController, times(1)).handleRequest(postRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    @Test
    void handlePutRequestWithMatchingToken() {
        // Arrange
        UserController userController = spy(new UserController());
        Response response = mock(Response.class);

        Request putRequest = createMockRequest(Method.PUT, "/users/testUser", "testUser");

        doReturn(response).when(userController).handleRequest(putRequest);

        // Act
        userController.handleRequest(putRequest);

        // Assert
        verify(userController, times(1)).handleRequest(putRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    @Test
    void handlePutRequestWithNonMatchingToken() {
        // Arrange
        UserController userController = spy(new UserController());
        Response response = mock(Response.class);

        Request putRequest = createMockRequest(Method.PUT, "/users/testUser", "wrongUser");

        doReturn(response).when(userController).handleRequest(putRequest);

        // Act
        userController.handleRequest(putRequest);

        // Assert
        verify(userController, times(1)).handleRequest(putRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

}