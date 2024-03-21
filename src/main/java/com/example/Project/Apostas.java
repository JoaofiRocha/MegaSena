package com.example.Project;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
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

        //Verifica se a lista sorteio ja foi gerada
        if (sorteio == null)
            sorteio = new ArrayList<>();

        //Verifica se Algum sorteio esta selecionado
        if(MainLayout.getSorteio() == null){
            UI.getCurrent().navigate("");
            dialog("Nenhum Sorteio Selecionado    ");
            return;
        }

        //Verifica se o sorteio foi finalizado
        Dados.readWinners();
        if(Dados.hasFinished(MainLayout.getSorteio()) == true) {
            UI.getCurrent().navigate("");
            dialogSuccess("Sorteio ja finalizado");
        }

        reloadBets();

        //cria a UI
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

        //adiciona os botoes de numeros
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

            //Adiciona acao nos botoes
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

        //cria o botao surpresa
        randomBet.addClickListener(e -> {
            if(nome.getValue().isEmpty()){
                dialog("Nome Invalido!");
                return;
            }

            numEscolhidos.clear();

            while(numEscolhidos.size() < 5){
                int n = (int) (Math.random() * 50);
                if(n != 0)
                    numEscolhidos.put(n, n);
            }
            Bet bet = new Bet(nome.getValue(), numEscolhidos);
            String nameSorteio = MainLayout.getSorteio();
            for (Sorteio s: sorteio){
                if(s.getName().equals(nameSorteio)){
                    s.addBet(bet);
                }
            }
            saveBets();
            dialogSuccess("Sorteio Adicionado com Sucesso!   ");
        });

        //Adiciona acao no botao enviar aposta
        enviarAposta.addClickListener(e -> {
            if(nome.getValue().isEmpty()){
                dialog("Nome Invalido!");
                return;
            }
            else if(numEscolhidos.size() < 4){
                dialog("Adicione 5 Numeros!!!");
                return;
            }
            Bet bet = new Bet(nome.getValue(), numEscolhidos);
            String nameSorteio = MainLayout.getSorteio();
            for (Sorteio s: sorteio){
                if(s.getName().equals(nameSorteio)){
                    s.addBet(bet);
                }
            }
            saveBets();
            dialogSuccess("Sorteio Adicionado com Sucesso!   ");
        });


        add(divNome);
        add(text);
        add(divNum);

    }


    //Ler o arquivo csv e salva as variaveis
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

    //Ler o csv
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

    //Adiciona as apostas no csv
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

    //Salva todas apostas no arquivo csv
    public static void saveBets() {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {

            for (Sorteio s : sorteio) {
                for (Bet bet : s.getBet()) {
                    bw.write(s.getName() + "\n");
                    bw.write(bet.getName() + "\n");
                    for (Integer i : bet.getNumbers().values()) {
                        bw.write(i + "\n");
                    }
                    bw.write("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //cria o sorteio com o nome do parametro
    public static void createSorteio(Sorteio name){
        if (sorteio == null)
            sorteio = new ArrayList<>();
        boolean sorteioExists = sorteio.stream().anyMatch(s -> s.getName().equals(name.getName()));

        if (!sorteioExists)
            sorteio.add(name);
    }

    //apaga um sorteio criado utilizando o nome do parametro
    public static void deleteSorteio(Sorteio name){
        sorteio.remove(name);
        saveBets();
        UI.getCurrent().getPage().reload();
    }

    //retorna um sorteio que tem o mesmo nome que o parametro
    public static Sorteio getSorteio(String name){
        for (Sorteio s : sorteio){
            if(s.getName().equals(name))
                return s;
        }
        return null;
    }

    //Cria uma caixa de dialogo
    public static void dialog(String error){
        ConfirmDialog dialog = new ConfirmDialog();

        Button cancelButton = new Button(error, (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.add(cancelButton);
        dialog.open();
    }

    //cria uma caixa de dialogo
    public static void dialogSuccess(String message) {
        Dialog dialog = new Dialog();
        Button ok = new Button("OK");
        ok.addClickListener(event -> {
            dialog.close();
            UI.getCurrent().getPage().reload();
        });
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.setModal(false);
        dialog.add(new Text(message), ok);
        dialog.open();
    }

}
