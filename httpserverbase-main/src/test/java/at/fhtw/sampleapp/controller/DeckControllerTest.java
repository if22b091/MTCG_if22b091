package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.service.DeckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DeckControllerTest {

    private DeckController deckController;
    private DeckService deckService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        deckService = mock(DeckService.class);
        deckController = spy(new DeckController());

        Field deckServiceField = DeckController.class.getDeclaredField("deckService");
        deckServiceField.setAccessible(true);
        deckServiceField.set(deckController, deckService);

        // Stub the checkToken method to return a valid username when given a valid token
        doReturn("validUsername").when(deckController).checkToken("validToken");
    }

    private Request createMockRequest(Method method, String token) {
        Request mockRequest = mock(Request.class);
        HeaderMap mockHeaderMap = mock(HeaderMap.class);

        when(mockRequest.getMethod()).thenReturn(method);
        when(mockRequest.getPathname()).thenReturn("/deck");
        when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        when(mockHeaderMap.getHeader("Authorization")).thenReturn(token);

        return mockRequest;
    }

    //Test case for handling a GET request where the user has a deck
    @Test
    void handleRequest_GET_NoDeck() {
        // Arrange
        Request getRequest = createMockRequest(Method.GET, "validToken");
        when(deckService.getDeck(eq("validUsername"), isNull())).thenThrow(new IllegalArgumentException("No cards found"));
        Response expectedResponse = new Response(HttpStatus.ACCEPTED, ContentType.JSON, "The request was fine, but the deck doesn't have any cards");

        // Act
        Response actualResponse = deckController.handleRequest(getRequest);

        // Assert
        verify(deckService, times(1)).getDeck(eq("validUsername"), isNull());
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    //Test case for handling a GET request where the user has a deck
    @Test
    void handleRequest_PUT_ValidToken() {
        // Arrange
        Request putRequest = createMockRequest(Method.PUT, "validToken");
        try {
            doNothing().when(deckService).updateDeck(eq("validUsername"), any(Request.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Response expectedResponse = new Response(HttpStatus.OK, ContentType.JSON, "The deck has been successfully configured");

        // Act
        Response actualResponse = null;
        try {
            actualResponse = deckController.handleRequest(putRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Assert
        try {
            verify(deckService, times(1)).updateDeck(eq("validUsername"), any(Request.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert actualResponse != null;
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    //Test case for handling a PUT request where the user does not have enough cards
    @Test
    void handleRequest_PUT_NotCorrectAmountOfCards() {
        // Arrange
        Request putRequest = createMockRequest(Method.PUT, "validToken");
        try {
            doThrow(new IllegalArgumentException("not enough cards provided")).when(deckService).updateDeck(eq("validUsername"), any(Request.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Response expectedResponse = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "The provided deck did not include the required amount of cards");

        // Act
        Response actualResponse = null;
        try {
            actualResponse = deckController.handleRequest(putRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Assert
        try {
            verify(deckService, times(1)).updateDeck(eq("validUsername"), any(Request.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert actualResponse != null;
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    //Test case for handling a PUT request where the user does not own all cards
    @Test
    void handleRequest_PUT_UserDoesNotOwnAllCards() {
        // Arrange
        Request putRequest = createMockRequest(Method.PUT, "validToken");
        try {
            doThrow(new IllegalArgumentException("user does not own all cards")).when(deckService).updateDeck(eq("validUsername"), any(Request.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Response expectedResponse = new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "At least one of the provided cards does not belong to the user or is not available.");

        // Act
        Response actualResponse = null;
        try {
            actualResponse = deckController.handleRequest(putRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            verify(deckService, times(1)).updateDeck(eq("validUsername"), any(Request.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert actualResponse != null;
        assertEquals(expectedResponse.get(), actualResponse.get());
    }
}