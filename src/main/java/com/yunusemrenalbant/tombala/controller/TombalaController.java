package com.yunusemrenalbant.tombala.controller;

import com.yunusemrenalbant.tombala.model.Game;
import com.yunusemrenalbant.tombala.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TombalaController {

    @Autowired
    private GameService gameService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/create")
    public String createGame(Model model) {
        Game game = gameService.createGame();
        return "redirect:/game/" + game.getGameId();
    }

    @GetMapping("/game/{gameId}")
    public String gameDetail(@PathVariable String gameId, 
                           @RequestParam(required = false) String playerId,
                           Model model) {
        Game game = gameService.getGame(gameId);
        if (game == null) {
            return "redirect:/";
        }
        model.addAttribute("game", game);
        return "game";
    }

    @PostMapping("/game/{gameId}/join")
    public String joinGame(@PathVariable String gameId, 
                         @RequestParam String playerName, 
                         RedirectAttributes redirectAttributes) {
        try {
            Game game = gameService.joinGame(gameId, playerName);

            String playerId = game.getCards().stream()
                .filter(card -> game.getPlayers().get(card.getPlayerId()).equals(playerName))
                .findFirst()
                .map(card -> card.getPlayerId())
                .orElse("");

            return "redirect:/game/" + gameId + "?playerId=" + playerId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/game/" + gameId;
        }
    }

    @PostMapping("/game/{gameId}/draw")
    public String drawNumber(@PathVariable String gameId, 
                           @RequestParam String playerId, 
                           RedirectAttributes redirectAttributes) {
        try {
            gameService.drawNumber(gameId, playerId);

            return "redirect:/game/" + gameId + "?playerId=" + playerId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/game/" + gameId + "?playerId=" + playerId;
        }
    }
} 