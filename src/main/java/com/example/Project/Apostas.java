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

    private static Map<String, Bet> bets;
    private static List<Sorteio> sorteio;
    private static Path path;

    public Apostas() throws IOException {
        bets = new HashMap<>();

        String currentDirectory = Paths.get("").toAbsolutePath().toString();
        String fileSeparator = File.separator;
        path = Paths.get(currentDirectory + fileSeparator + "src" + fileSeparator + "Apostas.csv");

        readApostas(path);

        sorteio = new ArrayList<>();

        Div divNome = new Div();
        TextField nome = new TextField("Nome do Apostador: ");
        divNome.add(nome);
        Button enviarAposta = new Button("Salvar Aposta!");
        enviarAposta.getStyle().setMarginLeft("50px");
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

        enviarAposta.addClickListener(e -> {
            Bet bet = new Bet(nome.getValue(), numEscolhidos);
            String sorteio = MainLayout.getSorteio();
            bets.put(sorteio, bet);


            try {
                setBets(sorteio, bet, path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            UI.getCurrent().getPage().reload();
        });

        add(divNome);
        add(text);
        add(divNum);

    }

    public static void getBets(String sorteio) throws IOException {
        bets.remove(sorteio);
        reloadBets(path);
    }

    private static void readApostas(Path path) throws IOException {
        BufferedReader bf = Files.newBufferedReader(path);

        String line = "";
        while((line = bf.readLine()) != null) {
            String sorteio = line;
            String name = bf.readLine();
            Map<Integer, Integer> num = new HashMap<>();
            while ((line = bf.readLine()) != null) {
                if(line.length() == 0)
                    break;
                int n = Integer.parseInt(line);
                num.put(n, n);
            }
            Bet bet = new Bet(name, num);
            bets.put(sorteio, bet);
        }

    }

    private static StringBuilder readApsotas(Path path) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader bf = Files.newBufferedReader(path)) {
            String line;
            while ((line = bf.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result;
    }

    private void setBets(String sorteio, Bet bet, Path path) throws IOException {

        StringBuilder a = readApsotas(path);
        try {
            BufferedWriter bw = Files.newBufferedWriter(path);
            a.append("\n").append(sorteio).append("\n").append(bet.getName());

            List<Integer> values = new ArrayList<>(bet.getNumbers().values());
            for(Integer num : values){
                a.append("\n").append(num);
            }

            bw.write(a.toString());
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reloadBets(Path path){
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            List<String> s = new ArrayList<>(bets.keySet());
            List<Bet> b = new ArrayList<>(bets.values());

            StringBuilder st = new StringBuilder();
            for(String sorteio : s){
                st.append("\n").append(s);
                for(Bet bet : b){
                    st.append("\n").append(bet.getName());
                    for(Integer i : bet.getNumbers().values()){
                        st.append("\n").append(i);
                    }
                }
            }
            bw.write(st.toString());
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createSorteio(Sorteio name){
        sorteio.add(name);
    }

    private static void deleteSorteio(Sorteio name){
        sorteio.remove(name);
    }

}
