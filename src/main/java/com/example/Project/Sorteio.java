package com.example.Project;

import java.util.ArrayList;
import java.util.List;

public class Sorteio {

    private List<Bet> bets;
    private String name;
    public Sorteio(String name, Bet bet){
        bets = new ArrayList<>();
        bets.add(bet);
        this.name = name;
    }

    public Sorteio(String name){
        bets = new ArrayList<>();
        this.name = name;
    }

    public void addBet(Bet bet){
        bets.add(bet);
        }

    public void removeBet(Bet bet){
        bets.remove(bet);
    }

    public String getName(){
        return name;
    }

    public List<Bet> getBet(){
        return bets;
    }

    public void resetBets(){
        bets = new ArrayList<>();
    }
}
