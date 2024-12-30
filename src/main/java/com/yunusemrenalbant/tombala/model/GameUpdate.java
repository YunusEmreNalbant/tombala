package com.yunusemrenalbant.tombala.model;

import lombok.Data;

@Data
public class GameUpdate {
    private String gameId;
    private Game game;
    private String message;
    private String type;

    public GameUpdate(String gameId, Game game, String message, String type) {
        this.gameId = gameId;
        this.game = game;
        this.message = message;
        this.type = type;
    }
} 