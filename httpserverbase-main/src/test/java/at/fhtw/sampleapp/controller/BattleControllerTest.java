package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.service.BattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class BattleControllerTest {

    private BattleController battleController;
    private BattleService battleService;

    // Set up the testing environment before each test case
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Mock the BattleService to isolate the test environment
        battleService = Mockito.mock(BattleService.class);
        // Use Mockito.spy to allow partial mocking of the BattleController object
        battleController = Mockito.spy(new BattleController());

        // Access the private 'battleService' field of BattleController class to inject the mock service
        Field battleServiceField = BattleController.class.getDeclaredField("battleService");
        battleServiceField.setAccessible(true);
        battleServiceField.set(battleController, battleService);

        // Stub the 'checkToken' method to return "validUsername" when "validToken" is passed
        Mockito.doReturn("validUsername").when(battleController).checkToken("validToken");
    }

    // Helper method to create a mock Request object with given method, token, and pathname
    private Request createMockRequest(Method method, String token, String pathname) {
        Request mockRequest = Mockito.mock(Request.class);
        HeaderMap mockHeaderMap = Mockito.mock(HeaderMap.class);

        // Stub the request and header map to return the specified method, pathname, and token
        Mockito.when(mockRequest.getMethod()).thenReturn(method);
        Mockito.when(mockRequest.getPathname()).thenReturn(pathname);
        Mockito.when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        Mockito.when(mockHeaderMap.getHeader("Authorization")).thenReturn(token);

        return mockRequest;
    }

    // Helper method to assert that two Response objects are equal
    private void assertResponseEquals(Response expected, Response actual) {
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getContent(), actual.getContent());
    }

    // Test case for handling a POST request with a valid token and "/battles" path
    @Test
    void handleRequest_POST_ValidToken_Battles() {
        // Create a mock POST request with a valid token
        Request postRequest = createMockRequest(Method.POST, "validToken", "/battles");
        // Stub the battleService to return a specific message when a battle is started
        when(battleService.startBattle(anyString(), anyString())).thenReturn("Battle started");
        // Define the expected response
        Response expectedResponse = new Response(HttpStatus.OK, ContentType.JSON, "Waiting for opponent\n");
        // Call the method under test and get the actual response
        Response actualResponse = battleController.handleRequest(postRequest);
        // Assert that the expected and actual responses are equal
        assertResponseEquals(expectedResponse, actualResponse);
    }

    // Test case for handling a POST request with an invalid token and "/battles" path
    @Test
    void handleRequest_POST_InvalidToken_Battles() {
        // Create a mock POST request with no token
        Request postRequest = createMockRequest(Method.POST, null, "/battles");
        // Stub the 'checkToken' method to return null for "invalidToken"
        doReturn(null).when(battleController).checkToken("invalidToken");
        // Define the expected response for an unauthorized access attempt
        Response expectedResponse = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
        // Call the method under test and get the actual response
        Response actualResponse = battleController.handleRequest(postRequest);
        // Assert that the expected and actual responses are equal
        assertResponseEquals(expectedResponse, actualResponse);
    }
}