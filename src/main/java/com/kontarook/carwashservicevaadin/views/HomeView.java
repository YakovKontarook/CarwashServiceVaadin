package com.kontarook.carwashservicevaadin.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Home")
@Route(value = "home")
public class HomeView extends VerticalLayout {

    public HomeView() {
        Button addAppointment = new Button("Add appointment");
        Button addAssistance = new Button("Add assistance");

        HorizontalLayout horizontalLayout = new HorizontalLayout(addAppointment, addAssistance);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        addAppointment.addClickListener(click -> UI.getCurrent().getPage().executeJs("location.assign('appointment')"));
        addAssistance.addClickListener(click -> UI.getCurrent().getPage().executeJs("location.assign('assistance')"));

        add(horizontalLayout);
    }
}
