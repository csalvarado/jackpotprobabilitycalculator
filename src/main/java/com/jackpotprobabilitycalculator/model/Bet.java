package com.jackpotprobabilitycalculator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "bets")
@Getter // Genera todos los getters
@Setter // Genera todos los setters
@NoArgsConstructor // Genera un constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double betAmount;
    private boolean won;
    private double winningProbability;
    private LocalDateTime timestamp;

}