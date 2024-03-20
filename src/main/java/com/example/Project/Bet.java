package com.example.Project;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public List<Integer> getNumber(){
        List<Integer> a = new ArrayList<>(numbers.values());
        Collections.sort(a);
        return a;
    }
}
