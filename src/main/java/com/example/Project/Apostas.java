package com.example.Project;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
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

@Route(value = "/Apostas", layout = MainLayout.class)
public class Apostas extends VerticalLayout{

    private static List<Sorteio> sorteio;
    private static Path path;

    public Apostas() throws IOException {

        if (sorteio == null)
            sorteio = new ArrayList<>();

        reloadBets();

        Div divNome = new Div();
        TextField nome = new TextField("Nome do Apostador: ");
        divNome.add(nome);
        Button enviarAposta = new Button("Salvar Aposta!");
        Button randomBet = new Button("Surpresa!!!");
        randomBet.getStyle().setMargin("50px");
        divNome.add(randomBet);
        divNome.add(enviarAposta);


        Text text = new Text("Selecione 5 numeros:");

        Map<Integer, Integer> numEscolhidos = new HashMap<>();
        Map<Integer, Boolean> buttonStates = new HashMap<>();

        Div divNum = new Div();
        for(int i = 1; i < 51; i++){
            Button num = new Button("" + i);
            divNum.setMaxWidth("750px");
            num.getStyle().setMargin("10px");
            num.getStyle().setPadding("10px");
            num.getStyle().setBorderRadius("40px");
            num.getStyle().setFontSize("35px");
            num.getStyle().setColor("grey");
            divNum.add(num);


            int n = i;
            buttonStates.put(n,false);

            num.addClickListener(e ->{
                if(!buttonStates.get(n) && numEscolhidos.size() < 5 ) {
                    num.getStyle().setColor("blue");
                    numEscolhidos.put(n,n);
                    buttonStates.put(n, true);
                }
                else if(buttonStates.get(n)){
                    num.getStyle().setColor("grey");
                    numEscolhidos.remove(n);
                    buttonStates.put(n, false);
                }
            });

        }

        randomBet.addClickListener(e -> {
            numEscolhidos.clear();
           if(!nome.getValue().isEmpty()){
               while(numEscolhidos.size() < 5){
                   int n = (int) (Math.random() * 50);
                   if(n != 0)
                       numEscolhidos.put(n, n);
               }
               Bet bet = new Bet(nome.getValue(), numEscolhidos);
               String nameSorteio = MainLayout.getSorteio();
               for (Sorteio s: sorteio){
                   if(s.getName() == nameSorteio){
                       s.addBet(bet);
                   }
               }
               saveBets();
               UI.getCurrent().getPage().reload();
           }
        });

        enviarAposta.addClickListener(e -> {
            Bet bet = new Bet(nome.getValue(), numEscolhidos);
            String nameSorteio = MainLayout.getSorteio();
            for (Sorteio s: sorteio){
                if(s.getName() == nameSorteio){
                    s.addBet(bet);
                }
            }
            saveBets();
            UI.getCurrent().getPage().reload();
        });


        add(divNome);
        add(text);
        add(divNum);

    }


    public static void reloadBets() throws IOException {
        String currentDirectory = Paths.get("").toAbsolutePath().toString();
        String fileSeparator = File.separator;
        path = Paths.get(currentDirectory + fileSeparator + "src" + fileSeparator + "Apostas.csv");

        for(Sorteio s : sorteio)
            s.resetBets();

        BufferedReader bf = Files.newBufferedReader(path);

        String line = "";
        while((line = bf.readLine()) != null) {
            String nameSorteio = line;
            String name = bf.readLine();
            Map<Integer, Integer> num = new HashMap<>();
            while ((line = bf.readLine()) != null) {
                if(line.length() == 0)
                    break;
                int n = Integer.parseInt(line);
                num.put(n, n);
            }
            Bet bet = new Bet(name, num);

            for(Sorteio s : sorteio){
                if(s.getName().equals(nameSorteio)){
                    s.addBet(bet);
                }
            }
        }

    }

    private static StringBuilder readBets(Path path) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader bf = Files.newBufferedReader(path)) {
            String line;
            while ((line = bf.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result;
    }

    private void setBets(String nameSorteio, Bet bet){

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            bw.write(nameSorteio +  "\n");
            bw.write(bet.getName() + "\n");

            for (Integer i : bet.getNumbers().values()) {
                bw.write(i + "\n");
            }
            bw.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void saveBets() {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            Set<String> writtenBets = new HashSet<>();
            for (Sorteio s : sorteio) {
                for (Bet bet : s.getBet()) {
                    String key = s.getName() + bet.getName();
                    if (!writtenBets.contains(key)) {
                        bw.write(s.getName() + "\n");
                        bw.write(bet.getName() + "\n");
                        for (Integer i : bet.getNumbers().values()) {
                            bw.write(i + "\n");
                        }
                        bw.write("\n");
                        writtenBets.add(key);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createSorteio(Sorteio name){
        if (sorteio == null)
            sorteio = new ArrayList<>();
        boolean sorteioExists = sorteio.stream().anyMatch(s -> s.getName().equals(name.getName()));

        if (!sorteioExists)
            sorteio.add(name);
    }

    public static void deleteSorteio(Sorteio name){
        sorteio.remove(name);
        saveBets();
        UI.getCurrent().getPage().reload();
    }


    public static Sorteio getSorteio(String name){
        for (Sorteio s : sorteio){
            if(s.getName().equals(name))
                return s;
        }
        return null;
    }


}
