package com.jackpotprobabilitycalculator.controller;

import com.jackpotprobabilitycalculator.model.Bet;
import com.jackpotprobabilitycalculator.service.interfaces.JackpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller that exposes endpoints for playing the jackpot and retrieving play history.
 *
 * <p>This controller delegates business logic to a JackpotService implementation and
 * translates service results and exceptions into appropriate HTTP responses.</p>
 */
@RestController
@Tag(name = "Jackpot Probability Calculator", description = "Operations for placing bets and managing probability.")
public class JackpotController {

    /**
     * The jackpot service that contains the business logic for playing and history retrieval.
     */
    @Autowired
    private JackpotService jackpotService;

    /**
     * Endpoint to play the jackpot draw with a specified bet amount.
     *
     * @param betAmount the amount placed for the jackpot draw, provided as a request parameter
     * @return a ResponseEntity containing a success/failure message or an error description
     */
    @PostMapping("/jackpot-draw")
    @Operation(summary = "Place a new bet",
            description = "Dynamically calculates the probability of winning and records the result.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Draw result (win or lose)."),
                    @ApiResponse(responseCode = "400", description = "Invalid bet amount.")
            })
    public ResponseEntity<String> play( @Parameter(description = "Bet amount. Must be a positive value.")
                                        @RequestParam double betAmount) {

        try {
            // call service to play and capture the result in a Bet object
            Bet result = jackpotService.play(betAmount);
            if (result.isWon()) {
                // return 200 OK with win message
                return ResponseEntity.ok("Congratulations! You've won the Jackpot.");
            } else {
                // return 200 OK with lose message
                return ResponseEntity.ok("Sorry, you didn't win this time. Try again.");
            }
        } catch (IllegalArgumentException e) {
            // return 400 Bad Request with the exception message
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            // prepare 500 Internal Server Error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error. Please try again later.");
        } catch (Exception e) {
            // prepare 500 Internal Server Error for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error. Please contact support.");
        }
    }

    /**
     * Endpoint to retrieve the history of bets/plays.
     *
     * @return a list of Bet objects representing the play history
     */
    @GetMapping("/history")
    @Operation(summary = "Displays complete betting history",
            description = "Retrieves all bets placed and their results. This endpoint is cached.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bet list successfully retrieved.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Bet.class)))
            })
    public List<Bet> getHistory() {
        // delegate to the service to fetch and return the history
        return jackpotService.getHistory();
    }
}
