package com.example.Project;


import java.util.Map;

public class Bet {
    private Map<Integer, Integer> numbers;
    private String name;

    public Bet(String name, Map<Integer, Integer> numbers){
        this.numbers = numbers;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Map<Integer, Integer> getNumbers(){
        return numbers;
    }
}
