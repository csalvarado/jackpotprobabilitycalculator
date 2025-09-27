package com.jackpotprobabilitycalculator.service;

import com.jackpotprobabilitycalculator.model.Bet;
import com.jackpotprobabilitycalculator.repository.BetRepository;
import com.jackpotprobabilitycalculator.service.impl.JackpotServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JackpotServiceImpl} using Mockito to mock dependencies and behaviors.
 * Each test validates a different behavior of the play method and the getHistory method.
 */
@RunWith(MockitoJUnitRunner.class)
public class JackpotServiceImplTest {

    // instance of the service under test, dependencies will be injected
    @InjectMocks
    private JackpotServiceImpl jackpotService;

    // mocked repository used by JackpotServiceImpl
    @Mock
    private BetRepository betRepository;

    /**
     * Setup method executed before each test to reset interactions with mocks.
     */
    @Before
    public void setup() {
        Mockito.reset(betRepository);
    }

    /**
     * Tests that calling play with a zero bet amount throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void play_withZeroBet_throwsIllegalArgumentException() {
        jackpotService.play(0.0);
    }

    /**
     * Tests that calling play with a negative bet amount throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void play_withNegativeBet_throwsIllegalArgumentException() {
        jackpotService.play(-5.0);
    }

    /**
     * Tests that when the random value is less than the winning probability the bet
     * is marked as won
     * and that the bet is persisted via the repository.
     */
    @Test
    public void play_whenRandomLessThanProbability_returnsWonBet_andPersists() {
        double betAmount = 1000.0;
        ThreadLocalRandom mockRandom = mock(ThreadLocalRandom.class);
        when(mockRandom.nextDouble()).thenReturn(0.5);

        try (MockedStatic<ThreadLocalRandom> mocked = mockStatic(ThreadLocalRandom.class)) {
            mocked.when(ThreadLocalRandom::current).thenReturn(mockRandom);

            ArgumentCaptor<Bet> captor = ArgumentCaptor.forClass(Bet.class);
            when(betRepository.save(any(Bet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Bet result = jackpotService.play(betAmount);

            mocked.verify(ThreadLocalRandom::current);
            verify(betRepository, times(1)).save(captor.capture());

            Bet saved = captor.getValue();
            assertNotNull(saved);
            assertEquals(betAmount, saved.getBetAmount(), 0.0);
            assertTrue(saved.isWon());
            assertEquals(result, saved);
            assertEquals(saved.getWinningProbability(), result.getWinningProbability(), 0.0);

            LocalDateTime now = LocalDateTime.now();
            assertNotNull(saved.getTimestamp());
            Duration diff = Duration.between(saved.getTimestamp(), now).abs();
            assertTrue(diff.toSeconds() < 5);
        }
    }

    /**
     * Tests that when the random value is greater than or equal to the winning
     * probability the bet is marked as lost
     * and that the bet is persisted via the repository.
     */
    @Test
    public void play_whenRandomGreaterOrEqualProbability_returnsLostBet_andPersists() {
        double betAmount = 1.0;
        ThreadLocalRandom mockRandom = mock(ThreadLocalRandom.class);
        when(mockRandom.nextDouble()).thenReturn(0.1);

        try (MockedStatic<ThreadLocalRandom> mocked = mockStatic(ThreadLocalRandom.class)) {
            mocked.when(ThreadLocalRandom::current).thenReturn(mockRandom);

            ArgumentCaptor<Bet> captor = ArgumentCaptor.forClass(Bet.class);
            when(betRepository.save(any(Bet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Bet result = jackpotService.play(betAmount);

            mocked.verify(ThreadLocalRandom::current);
            verify(betRepository, times(1)).save(captor.capture());

            Bet saved = captor.getValue();
            assertNotNull(saved);
            assertEquals(betAmount, saved.getBetAmount(), 0.0);
            assertFalse(saved.isWon());
            assertEquals(result, saved);
            assertEquals(saved.getWinningProbability(), result.getWinningProbability(), 0.0);

            LocalDateTime now = LocalDateTime.now();
            assertNotNull(saved.getTimestamp());
            Duration diff = Duration.between(saved.getTimestamp(), now).abs();
            assertTrue(diff.toSeconds() < 5);
        }
    }

    /**
     * Tests that if repository.save throws an exception when creating a Bet,
     * the service wraps it in an IllegalStateException.
     */
    @Test
    public void play_whenCreateBetFails_throwsIllegalStateException() {
        double betAmount = 50.0;
        ThreadLocalRandom mockRandom = mock(ThreadLocalRandom.class);
        when(mockRandom.nextDouble()).thenReturn(0.0);

        try (MockedStatic<ThreadLocalRandom> mocked = mockStatic(ThreadLocalRandom.class)) {
            mocked.when(ThreadLocalRandom::current).thenReturn(mockRandom);

            when(betRepository.save(any(Bet.class))).thenThrow(new RuntimeException("persist-failure"));

            try {
                jackpotService.play(betAmount);
                fail("Expected IllegalStateException due to createBet failure");
            } catch (IllegalStateException e) {
                assertTrue(e.getMessage().contains("Failed to create Bet"));
                assertNotNull(e.getCause());
                assertEquals(RuntimeException.class, e.getCause().getClass());
                assertEquals("persist-failure", e.getCause().getMessage());
            }

            mocked.verify(ThreadLocalRandom::current);
            verify(betRepository, times(1)).save(any(Bet.class));
        }
    }

    /**
     * Tests that getHistory delegates to the repository's findAll method and returns the repository result directly.
     */
    @Test
    public void getHistory_invokesRepositoryFindAll_andReturnsList() {
        List<Bet> mockedList = Arrays.asList(new Bet(), new Bet());
        when(betRepository.findAll()).thenReturn(mockedList);

        List<Bet> result = jackpotService.getHistory();

        verify(betRepository, times(1)).findAll();
        assertSame(mockedList, result);
    }
}
