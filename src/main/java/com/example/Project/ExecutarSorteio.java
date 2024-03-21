package com.example.Project;



import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
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
    private int timesRandomized;
    private Path path;
    public ExecutarSorteio() throws IOException {
        if(MainLayout.getSorteio() == null){
            UI.getCurrent().navigate("");
            Apostas.dialog("Nenhum Sorteio Selecionado");
            return;
        }
        Dados.readWinners();
        if(Dados.hasFinished(MainLayout.getSorteio()) == true){
            UI.getCurrent().navigate("/Apostas");
            Apostas.dialogSuccess("Sorteio ja finalizado   ");
        }
        Apostas.reloadBets();
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

        //Pergunta se deseja realizar o sorteio
        execute.addClickListener(e -> {
           youSure();
        });
    }

    //Verifica se o sorteio teve vencedores
    private void check(Sorteio sorteio){
        hasWinner = false;
        for(Bet bet : sorteio.getBet()){
            List <Integer> a = bet.getNumber();
            Collections.sort(a);

            if(a.equals(n)){
                hasWinner = true;
            }
        }
    }

    //Le o arquivo csv
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

    //salva as variaveis em um arquivo csv
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

    //Caixa de dialogo que pergunta se deseja finalizar o sorteio
    private void youSure(){
            Dialog dialog = new Dialog();
            Span sure = new Span("Tem certeza que deseja finalizar o sorteio?\n Não sera mais possivel editalo");
            Button no = new Button("Não");
            Button yes = new Button("Sim");
            no.addClickListener(e -> {
                dialog.close();
            });

            //Acao do botao yes, realiza o sorteio
            yes.addClickListener(e -> {
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
                dialog.close();
            });
            sure.getStyle().setColor("grey");
            yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            yes.getStyle().setMargin("25px");
            no.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            VerticalLayout div = new VerticalLayout();

            Div div2 = new Div();
            div2.add(no, yes);

            div.add(sure,div2);

            dialog.add(div);
            dialog.open();
    }


}
