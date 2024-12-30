package com.yunusemrenalbant.tombala.model;

import lombok.Data;
import java.util.List;

@Data
public class TombalaCard {
    private String playerId;
    private List<Integer> numbers;
    private List<Boolean> marked;
    private boolean cinko;
    private boolean tombala;
    
    public TombalaCard(String playerId, List<Integer> numbers) {
        this.playerId = playerId;
        this.numbers = numbers;
        this.marked = List.of(false, false, false, false, false, 
                             false, false, false, false, false,
                             false, false, false, false, false);
        this.cinko = false;
        this.tombala = false;
    }
} 