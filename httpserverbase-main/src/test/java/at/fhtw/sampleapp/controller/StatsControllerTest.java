package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StatsControllerTest {

    private StatsController statsController;
    private StatsService statsService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        statsService = mock(StatsService.class);
        statsController = spy(new StatsController());

        Field statsServiceField = StatsController.class.getDeclaredField("statsService");
        statsServiceField.setAccessible(true);
        statsServiceField.set(statsController, statsService);

        // Stub the checkToken method to return a valid username when given a valid token
        doReturn("validUsername").when(statsController).checkToken("validToken");
    }

    private Request createMockRequest(Method method, String token, String pathname) {
        Request mockRequest = mock(Request.class);
        HeaderMap mockHeaderMap = mock(HeaderMap.class);

        when(mockRequest.getMethod()).thenReturn(method);
        when(mockRequest.getPathname()).thenReturn(pathname);
        when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        when(mockHeaderMap.getHeader("Authorization")).thenReturn(token);

        return mockRequest;
    }

    @Test
    void handleRequest_GET_ValidToken_Stats() {
        // Arrange
        Request getRequest = createMockRequest(Method.GET, "validToken", "/stats");
        Map<String, Object> stats = new HashMap<>();
        stats.put("wins", 10);
        stats.put("losses", 5);
        when(statsService.getStats("validUsername")).thenReturn(stats);
        Response expectedResponse = new Response(HttpStatus.OK, ContentType.JSON, "The stats could be retrieved successfully.\n{\"wins\":10,\"losses\":5}");

        // Act
        Response actualResponse = statsController.handleRequest(getRequest);

        // Assert
        verify(statsService, times(1)).getStats("validUsername");
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    @Test
    void handleRequest_GET_ValidToken_Scoreboard() {
        // Arrange
        Request getRequest = createMockRequest(Method.GET, "validToken", "/scoreboard");
        when(statsService.getScoreboard()).thenReturn(Collections.emptyList());
        Response expectedResponse = new Response(HttpStatus.OK, ContentType.JSON, "The scoreboard could be retrieved successfully.\n[]");

        // Act
        Response actualResponse = statsController.handleRequest(getRequest);

        // Assert
        verify(statsService, times(1)).getScoreboard();
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    @Test
    void handleRequest_GET_InvalidToken_Stats() {
        // Arrange
        Request getRequest = createMockRequest(Method.GET, "invalidToken", "/stats");
        doReturn(new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid")).when(statsController).checkToken("invalidToken");
        Response expectedResponse = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");

        // Act
        Response actualResponse = statsController.handleRequest(getRequest);

        // Assert
        verify(statsService, times(0)).getStats(anyString());
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    @Test
    void handleRequest_GET_InvalidToken_Scoreboard() {
        // Arrange
        Request getRequest = createMockRequest(Method.GET, "invalidToken", "/scoreboard");
        doReturn(new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid")).when(statsController).checkToken("invalidToken");
        Response expectedResponse = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");

        // Act
        Response actualResponse = statsController.handleRequest(getRequest);

        // Assert
        verify(statsService, times(0)).getScoreboard();
        assertEquals(expectedResponse.get(), actualResponse.get());
    }
}