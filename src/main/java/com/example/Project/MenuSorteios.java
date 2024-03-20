package com.example.Project;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Route ("a")
public class MenuSorteios extends VerticalLayout {

    private List<String> sorteios;

    public MenuSorteios() throws IOException{

        String currentDirectory = Paths.get("").toAbsolutePath().toString();
        String fileSeparator = File.separator;
        Path path = Paths.get(currentDirectory + fileSeparator + "src" + fileSeparator + "Sorteios.csv");

        if (sorteios == null)
            sorteios = readSorteio(path);

        TextField sorteioName = new TextField("Nome do Sorteio:");
        Button newSorteioButton = new Button("Criar Novo Sorteio", event -> {
            try {
                createNewSorteio(path, sorteioName.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Div bar = new Div();
        bar.getStyle().setBackgroundColor("lightgrey");
        bar.setHeight("3px");
        bar.getStyle().setMarginTop("30px");

        Div create = new Div(sorteioName, newSorteioButton, bar);
        add(create);

        for (String sorteio : sorteios) {
            Div buttonArea = new Div();

            buttonArea.add(new Button(sorteio, event -> {
                MainLayout.setSorteio(sorteio);
                UI.getCurrent().getPage().reload();
            }));



            Button delete = new Button(VaadinIcon.TRASH.create());
            delete.getStyle().setMarginLeft("15px");
            delete.addClickListener(e ->{
                    buttonArea.removeAll();
                    sorteios.remove(sorteio);
                    Apostas.deleteSorteio(Apostas.getSorteio(sorteio));

                try {
                    reloadSorteio(path);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            });
            buttonArea.add(delete);

            add(buttonArea);
        }
    }

    private void createNewSorteio(Path path, String name) throws IOException{
        if (!sorteios.contains(name)) {
            Sorteio n = new Sorteio(name);
            Apostas.createSorteio(n);
            sorteios.add(name);
        }

        reloadSorteio(path);
        UI.getCurrent().getPage().reload();
    }


    private List<String> readSorteio(Path path) throws IOException {
        BufferedReader bf = Files.newBufferedReader(path);

        String line = "";
        ArrayList<String> list = new ArrayList<>();
        while ((line = bf.readLine()) != null){
            list.add(line);
            Sorteio n = new Sorteio(line);
            Apostas.createSorteio(n);
        }
        return list;
    }

    private void reloadSorteio (Path path) throws IOException{

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (String sorteio : sorteios) {
                bw.write(sorteio + "\n");
            }
        }

    }

}