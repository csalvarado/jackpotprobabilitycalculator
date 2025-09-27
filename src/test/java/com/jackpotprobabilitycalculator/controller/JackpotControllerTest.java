package com.jackpotprobabilitycalculator.controller;

import com.jackpotprobabilitycalculator.model.Bet;
import com.jackpotprobabilitycalculator.service.interfaces.JackpotService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JackpotController}.
 *
 * <p>These tests exercise the controller behavior by mocking the {@link JackpotService}
 * and verifying responses and interactions. Each test corresponds to a specific
 * behavior or exception scenario from the service.</p>
 */
@RunWith(MockitoJUnitRunner.class)
public class JackpotControllerTest {

    // the mocked service used by the controller in tests
    @Mock
    private JackpotService jackpotService;

    // the controller instance under test
    private JackpotController controller;

    /**
     * Setup method executed before each test to create a controller instance
     * and inject the mocked service.
     *
     * @throws Exception if reflection fails when injecting the mock into the controller
     */
    @Before
    public void setUp() throws Exception {
        controller = new JackpotController();
        Field f = JackpotController.class.getDeclaredField("jackpotService");
        f.setAccessible(true);
        f.set(controller, jackpotService);
    }

    /**
     * When the service indicates a win, controller should return an OK response
     * with the congratulations message
     * and call the service exactly once with the provided bet amount.
     */
    @Test
    public void testPlay_whenWon_returnsCongratulationsMessageAndCallsService() {
        double betAmount = 100.0;
        Bet mockBet = mock(Bet.class);
        when(mockBet.isWon()).thenReturn(true);
        when(jackpotService.play(betAmount)).thenReturn(mockBet);

        ResponseEntity<String> response = controller.play(betAmount);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Congratulations! You've won the Jackpot.", response.getBody());
        verify(jackpotService, times(1)).play(betAmount);
    }

    /**
     * When the service indicates a loss, controller should
     * return an OK response with the retry message
     * and call the service exactly once with the provided bet amount.
     */
    @Test
    public void testPlay_whenLost_returnsTryAgainMessageAndCallsService() {
        double betAmount = 50.5;
        Bet mockBet = mock(Bet.class);
        when(mockBet.isWon()).thenReturn(false);
        when(jackpotService.play(betAmount)).thenReturn(mockBet);

        ResponseEntity<String> response = controller.play(betAmount);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sorry, you didn't win this time. Try again.", response.getBody());
        verify(jackpotService, times(1)).play(betAmount);
    }
    /**
     * When the service throws IllegalArgumentException, the controller should
     * return a 400 Bad Request
     * containing the exception message, and ensure the service was invoked.
     */
    @Test
    public void testPlay_whenIllegalArgumentException_returnsBadRequestWithMessage() {
        double betAmount = -10.0;
        IllegalArgumentException ex = new IllegalArgumentException("Invalid bet amount");
        when(jackpotService.play(betAmount)).thenThrow(ex);

        ResponseEntity<String> response = controller.play(betAmount);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid bet amount", response.getBody());
        verify(jackpotService, times(1)).play(betAmount);
    }

    /**
     * When the service throws IllegalStateException, the controller should
     * return a 500 Internal Server Error
     * with a generic internal error message and ensure the service was invoked.
     */
    @Test
    public void testPlay_whenIllegalStateException_returnsInternalServerErrorGenericMessage() {
        double betAmount = 20.0;
        IllegalStateException ex = new IllegalStateException("Internal server error. Please try again later.");
        when(jackpotService.play(betAmount)).thenThrow(ex);

        ResponseEntity<String> response = controller.play(betAmount);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error. Please try again later.", response.getBody());
        verify(jackpotService, times(1)).play(betAmount);
    }

    /**
     * When the service throws an unexpected RuntimeException,
     * the controller should return a 500 Internal Server Error
     * with a generic unexpected error message and ensure the service was invoked.
     */
    @Test
    public void testPlay_whenUnexpectedException_returnsUnexpectedErrorMessage() {
        double betAmount = 5.0;
        RuntimeException ex = new RuntimeException("Unexpected error. Please contact support.");
        when(jackpotService.play(betAmount)).thenThrow(ex);

        ResponseEntity<String> response = controller.play(betAmount);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error. Please contact support.", response.getBody());
        verify(jackpotService, times(1)).play(betAmount);
    }

    /**
     * Verifies that controller.getHistory() delegates
     * to the service and returns the same list instance.
     */
    @Test
    public void testGetHistory_delegatesToServiceAndReturnsList() {
        Bet b1 = mock(Bet.class);
        Bet b2 = mock(Bet.class);
        List<Bet> history = Arrays.asList(b1, b2);
        when(jackpotService.getHistory()).thenReturn(history);

        List<Bet> result = controller.getHistory();

        assertSame(history, result);
        verify(jackpotService, times(1)).getHistory();
    }

    /**
     * Uses static mocking to ensure that when a win occurs the controller
     * invokes ResponseEntity.ok(...) static
     * method and returns the exact ResponseEntity instance provided by the static call.
     */
    @Test
    public void testPlay_invokesResponseEntityOkStatic_whenWon() {
        double betAmount = 200.0;
        Bet mockBet = mock(Bet.class);
        when(mockBet.isWon()).thenReturn(true);
        when(jackpotService.play(betAmount)).thenReturn(mockBet);

        ResponseEntity<String> dummy = new ResponseEntity<>("dummy", HttpStatus.OK);

        try (MockedStatic<ResponseEntity> mockedStatic = Mockito.mockStatic(ResponseEntity.class)) {
            mockedStatic.when(() -> ResponseEntity.ok("Congratulations! You've won the Jackpot.")).thenReturn(dummy);

            ResponseEntity<String> response = controller.play(betAmount);

            assertSame(dummy, response);
            mockedStatic.verify(() -> ResponseEntity.ok("Congratulations! You've won the Jackpot."), times(1));
            verify(jackpotService, times(1)).play(betAmount);
        }
    }

    /**
     * Uses static mocking to ensure that when the service
     * throws IllegalArgumentException the controller builds a BadRequest
     * ResponseEntity via ResponseEntity.badRequest().body(...) and returns
     * the produced ResponseEntity instance.
     */
    @Test
    public void testPlay_invokesResponseEntityBadRequestStatic_whenIllegalArgument() {
        double betAmount = -1.0;
        IllegalArgumentException ex = new IllegalArgumentException("Bet must be positive");
        when(jackpotService.play(betAmount)).thenThrow(ex);

        ResponseEntity<String> dummy = new ResponseEntity<>("bad", HttpStatus.BAD_REQUEST);
        ResponseEntity.BodyBuilder bodyBuilderMock = mock(ResponseEntity.BodyBuilder.class);
        when(bodyBuilderMock.body("Bet must be positive")).thenReturn(dummy);

        try (MockedStatic<ResponseEntity> mockedStatic = Mockito.mockStatic(ResponseEntity.class)) {
            mockedStatic.when(ResponseEntity::badRequest).thenReturn(bodyBuilderMock);

            ResponseEntity<String> response = controller.play(betAmount);

            assertSame(dummy, response);
            mockedStatic.verify(ResponseEntity::badRequest, times(1));
            verify(bodyBuilderMock, times(1)).body("Bet must be positive");
        }
    }

    /**
     * Uses static mocking to ensure that when the service
     * throws IllegalStateException the controller uses
     * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(...) to build the response.
     */
    @Test
    public void testPlay_invokesResponseEntityStatusInternalError_whenIllegalState() {
        double betAmount = 10.0;
        IllegalStateException ex = new IllegalStateException("service down");
        when(jackpotService.play(betAmount)).thenThrow(ex);

        ResponseEntity<String> dummy = new ResponseEntity<>("internal", HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseEntity.BodyBuilder bodyBuilderMock = mock(ResponseEntity.BodyBuilder.class);
        when(bodyBuilderMock.body("Internal server error. Please try again later.")).thenReturn(dummy);

        try (MockedStatic<ResponseEntity> mockedStatic = Mockito.mockStatic(ResponseEntity.class)) {
            mockedStatic.when(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)).thenReturn(bodyBuilderMock);

            ResponseEntity<String> response = controller.play(betAmount);

            assertSame(dummy, response);
            mockedStatic.verify(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR), times(1));
            verify(bodyBuilderMock, times(1)).body("Internal server error. Please try again later.");
        }
    }

    /**
     * Uses static mocking to ensure that when the service throws a generic
     * RuntimeException the controller uses
     * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(...)
     * to provide a generic unexpected error message.
     */
    @Test
    public void testPlay_invokesResponseEntityStatusInternalError_whenGenericException() {
        double betAmount = 11.0;
        RuntimeException ex = new RuntimeException("unexpected");
        when(jackpotService.play(betAmount)).thenThrow(ex);

        ResponseEntity<String> dummy = new ResponseEntity<>("unexpected", HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseEntity.BodyBuilder bodyBuilderMock = mock(ResponseEntity.BodyBuilder.class);
        when(bodyBuilderMock.body("Unexpected error. Please contact support.")).thenReturn(dummy);

        try (MockedStatic<ResponseEntity> mockedStatic = Mockito.mockStatic(ResponseEntity.class)) {
            mockedStatic.when(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)).thenReturn(bodyBuilderMock);

            ResponseEntity<String> response = controller.play(betAmount);

            assertSame(dummy, response);
            mockedStatic.verify(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR), times(1));
            verify(bodyBuilderMock, times(1)).body("Unexpected error. Please contact support.");
        }
    }
}
