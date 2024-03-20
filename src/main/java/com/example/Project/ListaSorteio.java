package com.example.Project;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;


import java.util.List;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


@Route(value = "/Lista", layout = MainLayout.class)
public class ListaSorteio extends VerticalLayout{

    public ListaSorteio(){
        Grid <Bet> grid = new Grid<>(Bet.class,false);
        grid.addColumn(Bet :: getName).setHeader("Nome do Apostador");
        grid.addColumn(Bet :: getNumber).setHeader("Números Apostados");

        List<Bet> bet = Apostas.getSorteio(MainLayout.getSorteio()).getBet();
        grid.setItems(bet);

        add(grid);
    }
}
