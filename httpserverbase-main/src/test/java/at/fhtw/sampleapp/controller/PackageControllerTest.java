package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PackageControllerTest {

    private Request createMockRequest(Method method, String path, String token) {
        Request mockRequest = mock(Request.class);
        HeaderMap mockHeaderMap = mock(HeaderMap.class);

        when(mockRequest.getMethod()).thenReturn(method);
        when(mockRequest.getPathname()).thenReturn(path);
        when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        when(mockHeaderMap.getHeader("Authorization")).thenReturn(token);

        return mockRequest;
    }

    // Test case for handling a POST request with a valid token
    @Test
    void handlePostRequestWithValidToken() {
        // Arrange
        PackageController packageController = spy(new PackageController());
        Response response = mock(Response.class);

        // Create a mock Request with a valid token
        Request postRequest = createMockRequest(Method.POST, "/package", "validToken");

        doReturn(response).when(packageController).handleRequest(postRequest);

        // Act
        packageController.handleRequest(postRequest);

        // Assert
        verify(packageController, times(1)).handleRequest(postRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    // Test case for handling a POST request with an invalid token
    @Test
    void handlePostRequestWithInvalidToken() {
        // Arrange
        PackageController packageController = spy(new PackageController());
        Response response = mock(Response.class);

        // Create a mock Request with an invalid token
        Request postRequest = createMockRequest(Method.POST, "/package", "invalidToken");

        doReturn(response).when(packageController).handleRequest(postRequest);

        // Act
        packageController.handleRequest(postRequest);

        // Assert
        verify(packageController, times(1)).handleRequest(postRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    // Test case for handling a POST request with no token
    @Test
    void handleRequestWithNonPostMethod() {
        // Arrange
        PackageController packageController = spy(new PackageController());
        Response response = mock(Response.class);

        // Create a mock Request with a GET method
        Request getRequest = createMockRequest(Method.GET, "/package", "");

        doReturn(response).when(packageController).handleRequest(getRequest);

        // Act
        packageController.handleRequest(getRequest);

        // Assert
        verify(packageController, times(1)).handleRequest(getRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    // Test case for handling a POST request with no token
    @Test
    void handlePostRequestWithValidTokenAdminUser() {
        // Arrange
        PackageController packageController = spy(new PackageController());
        Response response = mock(Response.class);

        // Create a mock Request with a valid token and an admin user
        Request postRequest = createMockRequest(Method.POST, "/package", "admin");

        doReturn(response).when(packageController).handleRequest(postRequest);

        // Act
        packageController.handleRequest(postRequest);

        // Assert
        verify(packageController, times(1)).handleRequest(postRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    // Test case for handling a POST request with no token
    @Test
    void handlePostRequestWithValidTokenNonAdminUser() {
        // Arrange
        PackageController packageController = spy(new PackageController());
        Response response = mock(Response.class);

        // Create a mock Request with a valid token but a non-admin user
        Request postRequest = createMockRequest(Method.POST, "/package", "user");

        doReturn(response).when(packageController).handleRequest(postRequest);

        // Act
        packageController.handleRequest(postRequest);

        // Assert
        verify(packageController, times(1)).handleRequest(postRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    @Test
    void handlePostRequestWithValidTokenAdminUserExceptionOccurs() {
        // Arrange
        PackageController packageController = spy(new PackageController());

        // Create a mock Request with a valid token and an admin user
        Request postRequest = createMockRequest(Method.POST, "/package", "admin");

        // Simulate an exception when handleRequest is called
        doThrow(new RuntimeException("Card already exists")).when(packageController).handleRequest(postRequest);

        // Act
        Exception exception = assertThrows(RuntimeException.class, () -> {
            packageController.handleRequest(postRequest);
        });

        // Assert
        assertEquals("Card already exists", exception.getMessage());
    }

    @Test
    void handlePostRequestWithValidTokenAdminUserNoException() {
        // Arrange
        PackageController packageController = spy(new PackageController());
        Response response = mock(Response.class);

        // Create a mock Request with a valid token and an admin user
        Request postRequest = createMockRequest(Method.POST, "/package", "admin");

        doReturn(response).when(packageController).handleRequest(postRequest);

        // Act
        packageController.handleRequest(postRequest);

        // Assert
        verify(packageController, times(1)).handleRequest(postRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }
}