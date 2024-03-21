package com.example.Project;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.util.List;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


@Route(value = "/Lista", layout = MainLayout.class)
public class ListaSorteio extends VerticalLayout{

    //Lista as Apostas Realizadas no sorteio
    public ListaSorteio() throws IOException {

        if(MainLayout.getSorteio() == null){
            UI.getCurrent().navigate("");
            Apostas.dialog("Nenhum Sorteio Selecionado");
            return;
        }

        Apostas.reloadBets();

        Grid <Bet> grid = new Grid<>(Bet.class,false);
        grid.addColumn(Bet :: getName).setHeader("Nome do Apostador");
        grid.addColumn(Bet :: getNumber).setHeader("Números Apostados");

        List<Bet> bet = Apostas.getSorteio(MainLayout.getSorteio()).getBet();
        grid.setItems(bet);

        add(grid);
    }
}
