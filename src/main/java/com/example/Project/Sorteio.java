package com.example.Project;

import java.util.List;

public class Sorteio {

    private List<Bet> bets;
    public Sorteio(Bet bet){
        bets.add(bet);
    }

    public Sorteio(){

    }

    public void addBet(Bet bet){
        bets.add(bet);
    }

    public void removeBet(Bet bet){
        bets.remove(bet);
    }
}
