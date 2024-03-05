package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TransactionControllerTest {

    private TransactionController transactionController;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        transactionService = mock(TransactionService.class);
        transactionController = spy(new TransactionController());

        Field transactionServiceField = TransactionController.class.getDeclaredField("transactionService");
        transactionServiceField.setAccessible(true);
        transactionServiceField.set(transactionController, transactionService);

        // Stub the checkToken method to return a valid username when given a valid token
        doReturn("validUsername").when(transactionController).checkToken("validToken");
    }

    private Request createMockRequest(String token) {
        Request mockRequest = mock(Request.class);
        HeaderMap mockHeaderMap = mock(HeaderMap.class);

        when(mockRequest.getMethod()).thenReturn(Method.POST);
        when(mockRequest.getPathname()).thenReturn("/transactions/packages");
        when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        when(mockHeaderMap.getHeader("Authorization")).thenReturn(token);

        return mockRequest;
    }

    @Test
    void handleRequest_POST_ValidToken() {
        // Arrange
        Request postRequest = createMockRequest("validToken");
        Response expectedResponse = new Response(HttpStatus.OK, ContentType.JSON, "A package has been successfully bought");

        // Act
        Response actualResponse = transactionController.handleRequest(postRequest);

        // Assert
        verify(transactionService, times(1)).acquirePackages(anyString());
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    @Test
    void handleRequest_POST_InvalidToken() {
        // Arrange
        Request postRequest = createMockRequest("invalidToken");
        Response expectedResponse = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");

        // Act
        Response actualResponse = transactionController.handleRequest(postRequest);

        // Assert
        verify(transactionService, times(0)).acquirePackages(anyString());
        assertEquals(expectedResponse.get(), actualResponse.get());
    }
}