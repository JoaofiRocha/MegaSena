package com.example.Project;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "/Dados", layout = MainLayout.class)
public class Dados extends HorizontalLayout {

    List<Winners> winners;
    Sorteio s;
    Winners w;

    public Dados() throws IOException {
        winners = new ArrayList<>();
        readWinners();

        if(winners.size() == 0){
            UI.getCurrent().navigate("/Apostas");
            dialog("Nenhum Sorteio Finalizado");
            return;
        }

        String sorteio = MainLayout.getSorteio();
        for(Winners win : winners){
            if(win.getNameSorteio().equals(sorteio)){
                s = Apostas.getSorteio(sorteio);
                w = win;
                break;
            }
        }


        TextArea listNumbers = new TextArea();
        listNumbers.setReadOnly(true);
        listNumbers.setLabel("Numeros Sorteados");
        listNumbers.setValue(w.getNumbers().toString());
        listNumbers.getStyle().setFontSize("30px");

        Span timesRandomized = new Span("Rodadas Realizadas: " + w.getTimesRandomized());
        timesRandomized.getStyle().setFont("roboto");
        timesRandomized.getStyle().setFontSize("25px");
        timesRandomized.getStyle().setColor("grey");


        int numberOfWinners = 0;
        List<Bet> listWinners = new ArrayList<>();
        Map<Integer, Integer> numbers = new HashMap<>();
        for(Bet bet : s.getBet()){
            if(bet.getNumber().equals(w.getNumbers())) {
                numberOfWinners++;
                listWinners.add(bet);
            }
            for(int i : bet.getNumber()) {
                if (numbers.containsKey(i)){
                    numbers.replace(i, numbers.get(i) + 1);
                }
                else
                    numbers.put(i, 1);
            }
        }

        numbers = numbers.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        listWinners.sort(new Comparator<Bet>() {
            @Override
            public int compare(Bet bet01, Bet bet02){
                return bet01.getName().compareTo(bet02.getName());
            }
        });




        Span textNumberOfWinners = new Span("Vencedores: " + numberOfWinners);
        textNumberOfWinners.getStyle().setFont("roboto");
        textNumberOfWinners.getStyle().setFontSize("25px");
        textNumberOfWinners.getStyle().setColor("grey");

        Grid <Bet> gridListWinners = new Grid<>(Bet.class, false);
        gridListWinners.addColumn(Bet :: getName).setHeader("Nome do Apostador");
        gridListWinners.addColumn(Bet :: getNumber).setHeader("Números Apostados");
        gridListWinners.setItems(listWinners);

        Grid<Map.Entry<Integer, Integer>> gridNumbers = new Grid<>();
        gridNumbers.addColumn(entry -> entry.getKey()).setHeader("Numeros");
        gridNumbers.addColumn(entry -> entry.getValue()).setHeader("Quantas Vezes Apareceu");
        gridNumbers.setItems(numbers.entrySet());



        VerticalLayout div = new VerticalLayout();
        div.setMaxWidth("355px");
        div.add(listNumbers, timesRandomized, textNumberOfWinners);

        VerticalLayout divTables = new VerticalLayout();
        divTables.add(gridNumbers);

        if(listWinners.size() == 0){
            Span noWinners = new Span("Não Houve Vencedores");
            noWinners.getStyle().setFont("roboto");
            noWinners.getStyle().setFontSize("35px");
            noWinners.getStyle().setColor("grey");
            divTables.add(noWinners);
        }
        else
            divTables.add(gridListWinners);


        add(div, divTables);
    }

    private void readWinners() throws IOException {

        String currentDirectory = Paths.get("").toAbsolutePath().toString();
        String fileSeparator = File.separator;
        Path path = Paths.get(currentDirectory + fileSeparator + "src" + fileSeparator + "Winners.csv");


        BufferedReader bf = Files.newBufferedReader(path);

        String line = "";
        while((line = bf.readLine()) != null) {
            String nameSorteio = line;
            Boolean winner;
            if (bf.readLine().equals("true"))
                winner = true;
            else
                winner = false;
            if(nameSorteio.equals(""))
                break;

            int timesRandomized = Integer.parseInt(bf.readLine());
            List<Integer> num= new ArrayList<>();
            while ((line = bf.readLine()) != null) {
                if(line.length() == 0 || line.equals(""))
                    break;
                int n = Integer.parseInt(line);
                num.add(n);
            }

            winners.add(new Winners(nameSorteio, winner, num, timesRandomized));
        }
    }

    public void dialog(String error){
        ConfirmDialog dialog = new ConfirmDialog();

        Button cancelButton = new Button(error, (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.add(cancelButton);
        dialog.open();
    }

}
