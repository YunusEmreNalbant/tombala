package com.yunusemrenalbant.tombala.controller;

import com.yunusemrenalbant.tombala.model.Game;
import com.yunusemrenalbant.tombala.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/create")
    public Game createGame() {
        return gameService.createGame();
    }

    @PostMapping("/{gameId}/join")
    public Game joinGame(@PathVariable String gameId, @RequestParam String playerName) {
        return gameService.joinGame(gameId, playerName);
    }

    @PostMapping("/{gameId}/draw")
    public Integer drawNumber(@PathVariable String gameId, @RequestParam String playerId) {
        return gameService.drawNumber(gameId, playerId);
    }

    @GetMapping("/{gameId}")
    public Game getGame(@PathVariable String gameId) {
        return gameService.getGame(gameId);
    }
} 