package com.yunusemrenalbant.tombala.service;

import com.yunusemrenalbant.tombala.model.*;
import com.yunusemrenalbant.tombala.model.Game;
import com.yunusemrenalbant.tombala.model.GameStatus;
import com.yunusemrenalbant.tombala.model.GameUpdate;
import com.yunusemrenalbant.tombala.model.TombalaCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GameService {
    private Map<String, Game> games = new HashMap<>();
    private Random random = new Random();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Game createGame() {
        String gameId = UUID.randomUUID().toString();
        Game game = new Game(gameId);
        games.put(gameId, game);
        return game;
    }

    public Game joinGame(String gameId, String playerName) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new RuntimeException("Oyun bulunamadı");
        }
        
        if (game.getPlayers().size() >= 2) {
            throw new RuntimeException("Oyun dolu");
        }

        String playerId = UUID.randomUUID().toString();
        game.getPlayers().put(playerId, playerName);
        
        TombalaCard card = createCard();
        card.setPlayerId(playerId);
        game.getCards().add(card);

        if (game.getPlayers().size() == 2) {
            game.setStatus(GameStatus.PLAYING);
            game.setCurrentTurn(game.getPlayers().keySet().iterator().next());
            String firstPlayerName = game.getPlayers().get(game.getCurrentTurn());
            
            sendGameUpdate(game, "Oyun başladı! İlk sıra " + firstPlayerName + " adlı oyuncuda.", "GAME_START");
        } else {
            sendGameUpdate(game, playerName + " oyuna katıldı. İkinci oyuncu bekleniyor...", "PLAYER_JOIN");
        }

        return game;
    }

    public Integer drawNumber(String gameId, String playerId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new RuntimeException("Oyun bulunamadı");
        }

        if (!game.getCurrentTurn().equals(playerId)) {
            throw new RuntimeException("Sıra sizde değil");
        }

        if (game.getRemainingNumbers().isEmpty()) {
            throw new RuntimeException("Çekilecek sayı kalmadı");
        }

        int index = random.nextInt(game.getRemainingNumbers().size());
        Integer drawnNumber = game.getRemainingNumbers().remove(index);
        game.getDrawnNumbers().add(drawnNumber);

        String nextPlayerId = null;

        for (String pid : game.getPlayers().keySet()) {
            if (!pid.equals(playerId)) {
                nextPlayerId = pid;
                break;
            }
        }
        game.setCurrentTurn(nextPlayerId);
        System.out.println("Sıra " + game.getPlayers().get(nextPlayerId) + " adlı oyuncuya geçti.");

        checkCards(game, drawnNumber);

        String playerName = game.getPlayers().get(playerId);
        sendGameUpdate(game, playerName + " " + drawnNumber + " numarayı çekti", "NUMBER_DRAWN");

        return drawnNumber;
    }

    private TombalaCard createCard() {
        List<Integer> numbers = new ArrayList<>();
        while (numbers.size() < 15) {
            int num = random.nextInt(90) + 1;
            if (!numbers.contains(num)) {
                numbers.add(num);
            }
        }
        Collections.sort(numbers);
        return new TombalaCard(null, numbers);
    }

    private void checkCards(Game game, Integer drawnNumber) {
        for (TombalaCard card : game.getCards()) {
            int index = card.getNumbers().indexOf(drawnNumber);
            if (index != -1) {
                List<Boolean> marked = new ArrayList<>(card.getMarked());
                marked.set(index, true);
                card.setMarked(marked);

                String playerName = game.getPlayers().get(card.getPlayerId());

                if (!card.isCinko() && marked.stream().filter(m -> m).count() >= 5) {
                    card.setCinko(true);
                    sendGameUpdate(game, playerName + " çinko yaptı!", "CINKO");
                }

                if (!card.isTombala() && marked.stream().filter(m -> m).count() == 15) {
                    card.setTombala(true);
                    game.setWinner(card.getPlayerId());
                    game.setStatus(GameStatus.FINISHED);
                    sendGameUpdate(game, playerName + " tombala yaptı! Oyun bitti.", "TOMBALA");
                }
            }
        }
    }

    private void sendGameUpdate(Game game, String message, String type) {
        try {
            GameUpdate update = new GameUpdate(game.getGameId(), game, message, type);

            String destination = "/topic/game/" + game.getGameId();

            System.out.println("WebSocket mesajı gönderiliyor - Destination: " + destination);

            messagingTemplate.convertAndSend(destination, update);
            
            System.out.println("WebSocket mesajı başarıyla gönderildi");
        } catch (Exception e) {
            System.err.println("WebSocket mesajı gönderilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }
} 