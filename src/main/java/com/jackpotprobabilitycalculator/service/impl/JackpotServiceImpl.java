package com.jackpotprobabilitycalculator.service.impl;

import com.jackpotprobabilitycalculator.model.Bet;
import com.jackpotprobabilitycalculator.repository.BetRepository;
import com.jackpotprobabilitycalculator.service.interfaces.JackpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Service
public class JackpotServiceImpl implements JackpotService {

    public static final double DOUBLE_ZERO = 0.0;

    @Autowired
    private BetRepository betRepository;

    //logarithmic function
    // functional field that computes winning probability from a bet amount using a specific formula
    private final Function<Double, Double> probabilityCalculator = betAmount -> Math.log(1 + betAmount) / 10;

    /**
     * Play the jackpot once with the provided bet amount.
     *
     * @param betAmount the amount of the bet; must be greater than zero
     * @return the created Bet instance containing details about the play (amount, win flag, probability, timestamp)
     * @throws IllegalArgumentException if the bet amount is less than or equal to zero
     * @throws IllegalStateException    if the probability calculation or bet creation fails unexpectedly
     */
    @Override
    public Bet play(double betAmount) {

        if (betAmount <= DOUBLE_ZERO) { // validate the bet amount is positive
            throw new IllegalArgumentException("The bet amount must be greater than zero."); // throw exception for invalid input
        }

        // declare local variable to hold calculated winning probability
        final double winningProbability;
        try {
            // apply the probability function to the bet amount
            winningProbability = probabilityCalculator.apply(betAmount);

        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to calculate winning probability for betAmount: " + betAmount, e); // wrap and rethrow as IllegalStateException
        }

        // generate random double and compare to probability to decide win/lose
        final boolean won = ThreadLocalRandom.current().nextDouble() < winningProbability;

        try {
            // create, persist, and return the new Bet
            return createBet(betAmount, won, winningProbability);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to create Bet", e);
        }
    }

    // helper method to construct and persist a Bet entity with provided details
    // create a new Bet instance and save it
    private Bet createBet(double betAmount, boolean won, double winningProbability) {
        Bet newBet = new Bet(); // instantiate a new Bet object
        newBet.setBetAmount(betAmount); // set the bet amount on the Bet
        newBet.setWon(won); // set whether this bet resulted in a win
        newBet.setWinningProbability(winningProbability); // set the calculated winning probability on the Bet
        newBet.setTimestamp(LocalDateTime.now()); // set the current timestamp on the Bet
        betRepository.save(newBet); // persist the Bet using the injected repository
        return newBet; // return the persisted Bet
    }

    /**
     * Retrieve the history of all bets.
     *
     * The results of this method are cached under the "history" cache name to avoid repeated database hits.
     *
     * @return a list of all Bet records persisted so far
     */
    @Cacheable("history")
    @Override
    public List<Bet> getHistory() {
        // return all Bet records retrieved from the repository
        return betRepository.findAll();
    }
}
