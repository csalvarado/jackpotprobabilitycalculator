package com.jackpotprobabilitycalculator.repository;

import com.jackpotprobabilitycalculator.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  BetRepository extends JpaRepository<Bet, Long> {
}
