package com.example.Project;


import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

@Route(value = "/Executar", layout = MainLayout.class)
public class ExecutarSorteio extends VerticalLayout {

    private List <Integer> n;
    private boolean hasWinner;
    private List <Bet> winner;
    private int timesRandomized;
    private Path path;
    public ExecutarSorteio(){
        String currentDirectory = Paths.get("").toAbsolutePath().toString();
        String fileSeparator = File.separator;
        path = Paths.get(currentDirectory + fileSeparator + "src" + fileSeparator + "Winners.csv");

        Button execute = new Button("Executar Sorteio");
        Span text = new Span("Apos executar o sorteio não sera mais possivel adicionar nenhuma aposta.");

        text.getStyle().setColor("grey");
        text.getStyle().setFont("roboto");
        text.getStyle().setFontSize("35px");

        execute.getStyle().setMarginTop("55px");
        execute.getStyle().setFontSize("35px");
        execute.getStyle().setPadding("35px");
        execute.getStyle().setBorderRadius("40px");

        add(execute);
        add(text);

        execute.addClickListener(e -> {
           Sorteio sorteio = Apostas.getSorteio(MainLayout.getSorteio());

           int i;
           for (i = 0; i < 25; i++){
               n = new ArrayList<>();

               while(n.size() < 5){
                   n.add((int) (Math.random() * 50));
                   Collections.sort(n);
               }

               check(sorteio);
           }
           timesRandomized = i;
            try {
                saveWinners(sorteio.getName());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void check(Sorteio sorteio){
        hasWinner = false;
        for(Bet bet : sorteio.getBet()){
            List <Integer> a = bet.getNumber();
            Collections.sort(a);

            if(a.equals(n)){
                winner.add(bet);
                hasWinner = true;
            }
        }
    }

    private StringBuilder readWinners() throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader bf = Files.newBufferedReader(path)) {
            String line;
            while ((line = bf.readLine()) != null) {
                result.append(line + "\n");
            }
            if(!result.toString().equals(""))
                result.append("\n");
        }
        return result;
    }

    public void saveWinners(String nameSorteio) throws IOException {
        StringBuilder st = readWinners();
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            st.append(nameSorteio + "\n");
            st.append(hasWinner + "\n");
            st.append(timesRandomized + "\n");
            for(int i : n){
                st.append(i + "\n");
            }
            bw.write(st.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
