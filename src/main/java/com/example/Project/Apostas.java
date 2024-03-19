package com.example.Project;

import com.vaadin.flow.component.Text;
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
            String nameSorteio = MainLayout.getSorteio();
            for (Sorteio s: sorteio){
                if(s.getName() == nameSorteio){
                    s.addBet(bet);
                }
            }

                saveBets();
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

    private void setBets(String sorteio, Bet bet, Path path) throws IOException {

        StringBuilder a = readBets(path);
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

    public static void saveBets(){
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            StringBuilder st = new StringBuilder();
            for (Sorteio s : sorteio) {
                st.append(s.getName()).append("\n");
                for(Bet bet : s.getBet()){
                    st.append(bet.getName()).append("\n");

                    for (Integer i : bet.getNumbers().values()) {
                        st.append(i).append("\n");
                    }
                }
            }
            bw.write(st.toString());
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createSorteio(Sorteio name){
        if (sorteio == null)
            sorteio = new ArrayList<>();
        sorteio.add(name);
    }

    public static void deleteSorteio(Sorteio name){
        sorteio.remove(name);
    }

    public static Sorteio getSorteio(String name){
        for (Sorteio s : sorteio){
            if(s.getName() == name)
                return s;
        }
        return null;
    }


}
