package com.jackpotprobabilitycalculator.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Bet} model class.
 * <p>
 * Tests cover:
 * - default values on instantiation,
 * - basic setter/getter behavior,
 * - boundary and special double values handling,
 * - preservation of object references timestamp,
 * - static mocking and verification of LocalDateTime.now().
 * </p>
 */
@RunWith(MockitoJUnitRunner.class)
public class BetTest {

    // field that will hold the Bet instance used in each test
    private Bet bet;

    /**
     * Set up a fresh Bet instance before each test.
     */
    @Before
    public void setUp() {
        bet = new Bet();
    }

    /**
     * Verify that a newly created Bet has the expected default values.
     */
    @Test
    public void testDefaultValues() {
        assertNull(bet.getId());
        assertEquals(0.0d, bet.getBetAmount(), 0d);
        assertFalse(bet.isWon());
        assertNull(bet.getTimestamp());
        assertEquals(0.0d, bet.getWinningProbability(), 0d);
    }

    /**
     * Verify that basic setters and getters correctly store and return values.
     */
    @Test
    public void testSetAndGetBasicProperties() {
        Long id = 123L;
        double amount = 250.75;
        boolean won = true;
        LocalDateTime now = LocalDateTime.of(2025, 1, 2, 3, 4, 5);
        double prob = 0.357;

        bet.setId(id);
        bet.setBetAmount(amount);
        bet.setWon(won);
        bet.setTimestamp(now);
        bet.setWinningProbability(prob);

        assertEquals(id, bet.getId());
        assertEquals(amount, bet.getBetAmount(), 0d);
        assertTrue(bet.isWon());
        assertSame(now, bet.getTimestamp());
        assertEquals(prob, bet.getWinningProbability(), 0d);
    }

    /**
     * Verify that the id property can be set to null and retrieved as null.
     */
    @Test
    public void testIdCanBeNull() {
        bet.setId(null);
        assertNull(bet.getId());
    }

    /**
     * Verify behavior of betAmount when setting boundary and special double values.
     * This test intentionally sets values that may be unusual (negative, MAX_VALUE, infinity, NaN).
     */
    @Test
    public void testBoundaryBetAmounts() {
        bet.setBetAmount(-100.0);
        assertEquals(-100.0, bet.getBetAmount(), 0d);

        bet.setBetAmount(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, bet.getBetAmount(), 0d);

        bet.setBetAmount(Double.POSITIVE_INFINITY);
        assertTrue(Double.isInfinite(bet.getBetAmount()));
        assertEquals(Double.POSITIVE_INFINITY, bet.getBetAmount(), 0d);

        bet.setBetAmount(Double.NaN);
        assertTrue(Double.isNaN(bet.getBetAmount()));
    }

    /**
     * Verify behavior of winningProbability when setting boundary and special double values.
     * This includes negative values, values outside [0,1], NaN, and negative infinity.
     */
    @Test
    public void testWinningProbabilityBoundaries() {
        bet.setWinningProbability(-1.0);
        assertEquals(-1.0, bet.getWinningProbability(), 0d);

        bet.setWinningProbability(0.0);
        assertEquals(0.0, bet.getWinningProbability(), 0d);

        bet.setWinningProbability(1.0);
        assertEquals(1.0, bet.getWinningProbability(), 0d);

        bet.setWinningProbability(2.0);
        assertEquals(2.0, bet.getWinningProbability(), 0d);

        bet.setWinningProbability(Double.NaN);
        assertTrue(Double.isNaN(bet.getWinningProbability()));

        bet.setWinningProbability(Double.NEGATIVE_INFINITY);
        assertTrue(Double.isInfinite(bet.getWinningProbability()));
        assertEquals(Double.NEGATIVE_INFINITY, bet.getWinningProbability(), 0d);
    }

    /**
     * Verify that LocalDateTime.now() can be statically mocked, that the mock is used,
     * and that the timestamp is set to the mocked value. Also verifies the static
     * method was called.
     *
     * Uses a try-with-resources to ensure the mocked static is closed after use.
     */
    @Test
    public void testTimestampStaticMockingAndVerification() {
        LocalDateTime fixed = LocalDateTime.of(2000, 12, 31, 23, 59, 59);
        try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(fixed);
            LocalDateTime ts = LocalDateTime.now();
            bet.setTimestamp(ts);
            mocked.verify(LocalDateTime::now, Mockito.times(1));
        }
        assertEquals(LocalDateTime.of(2000, 12, 31, 23, 59, 59), bet.getTimestamp());
    }

    /**
     * Verify that when a LocalDateTime instance is set as the timestamp,
     * the exact reference is preserved.
     */
    @Test
    public void testSetTimestampReferencePreserved() {
        LocalDateTime ts = LocalDateTime.of(2023, 6, 15, 10, 20, 30);
        bet.setTimestamp(ts);
        assertSame(ts, bet.getTimestamp());
    }

    /**
     * Verify toggling the won flag between true and false behaves as expected.
     */
    @Test
    public void testToggleWonFlag() {
        bet.setWon(false);
        assertFalse(bet.isWon());
        bet.setWon(true);
        assertTrue(bet.isWon());
        bet.setWon(false);
        assertFalse(bet.isWon());
    }
}
