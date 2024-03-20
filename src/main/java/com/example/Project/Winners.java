package com.example.Project;

import java.util.Collections;
import java.util.List;

public class Winners {

    private String nameSorteio;
    private boolean winner;
    private List<Integer> numbers;
    private int timesRandomized;
    public Winners(String nameSorteio, boolean winner, List<Integer> numbers, int timesRandomized){
        this.nameSorteio = nameSorteio;
        this.winner = winner;
        this.numbers = numbers;
        this.timesRandomized = timesRandomized;
    }

    public boolean hasWinner(){
        return winner;
    }

    public List<Integer> getNumbers(){
        Collections.sort(numbers);
        return numbers;
    }

    public int getTimesRandomized(){
        return timesRandomized;
    }

    public String getNameSorteio(){
        return nameSorteio;
    }
}
