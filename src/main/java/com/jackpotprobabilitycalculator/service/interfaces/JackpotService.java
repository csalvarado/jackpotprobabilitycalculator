package com.jackpotprobabilitycalculator.service.interfaces;

import com.jackpotprobabilitycalculator.model.Bet;

import java.util.List;

public interface JackpotService {

    Bet play(double betAmount);

    List<Bet> getHistory();
}
