package com.example.Project;


import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.IOException;

@Route ("")
public class MainLayout extends AppLayout {

    private static String sorteioSelecionado;

    public MainLayout() throws IOException {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1(sorteioSelecionado);
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        getSideNav();


        Icon apostasIcon = VaadinIcon.INVOICE.create();
        apostasIcon.setSize("50px");
        Icon listaIcon = VaadinIcon.MODAL_LIST.create();
        listaIcon.setSize("50px");
        Icon executarIcon = VaadinIcon.EXCLAMATION.create();
        executarIcon.setSize("50px");
        Icon dadosIcon = VaadinIcon.BAR_CHART.create();
        dadosIcon.setSize("50px");
        Icon premioIcon = VaadinIcon.COIN_PILES.create();
        premioIcon.setSize("50px");


        Tab nothing = new Tab(new Span(" "));
        nothing.getStyle().setMarginRight("100px");

        Tab apostas = new Tab(apostasIcon, new Anchor("/Apostas", "Apostas"));
        Tab lista = new Tab(listaIcon, new Anchor("/Lista","Lista das Apostas"));
        Tab executarSorteio = new Tab(executarIcon, new Anchor("/Executar","Executar Sorteio"));
        Tab dados = new Tab(dadosIcon, new Anchor("/Dados", "Dados do Sorteio"));
        Tab premio = new Tab(premioIcon, new Anchor("","Premio"));

        for (Tab tab : new Tab[] { apostas, lista, executarSorteio, dados, premio }) {
            tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        }


        Tabs tabs = new Tabs( nothing,apostas, lista, executarSorteio, dados, premio );

        addToNavbar(toggle, title,tabs);
    }



    private void getSideNav() throws IOException {
        VerticalLayout sideNav = new VerticalLayout();

        MenuSorteios sorteio = new MenuSorteios();
        sideNav.add(sorteio);

        Scroller scroller = new Scroller(sideNav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
    }

    public static void setSorteio(String sorteio){
        sorteioSelecionado = sorteio.replaceAll(" ", "_");
    }

    public static String getSorteio(){
        return sorteioSelecionado;
    }

}
