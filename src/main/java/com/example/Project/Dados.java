package com.example.Project;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;


import java.io.IOException;
import java.util.List;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


@Route(value = "/Dados", layout = MainLayout.class)
public class Dados extends VerticalLayout{

    public Dados(){
        Grid <Bet> grid = new Grid<>(Bet.class,false);
        grid.addColumn(Bet :: getName).setHeader("Name");
        grid.addColumn(Bet ::getNumbers).setHeader("numbers");

        List<Bet> bet = Apostas.getSorteio(MainLayout.getSorteio()).getBet();
        grid.setItems(bet);

        add(grid);
    }
}
