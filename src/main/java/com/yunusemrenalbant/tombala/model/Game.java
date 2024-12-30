package com.yunusemrenalbant.tombala.model;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Data
public class Game {
    private String gameId;
    private List<TombalaCard> cards;
    private List<Integer> remainingNumbers;
    private List<Integer> drawnNumbers;
    private Map<String, String> players;
    private String currentTurn;
    private String winner;
    private GameStatus status;
    
    public Game(String gameId) {
        this.gameId = gameId;
        this.cards = new ArrayList<>();
        this.remainingNumbers = new ArrayList<>();
        this.drawnNumbers = new ArrayList<>();
        this.players = new HashMap<>();
        this.status = GameStatus.WAITING;
        
        for (int i = 1; i <= 90; i++) {
            remainingNumbers.add(i);
        }
    }
} 