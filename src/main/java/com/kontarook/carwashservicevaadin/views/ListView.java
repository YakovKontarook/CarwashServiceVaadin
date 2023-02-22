package com.kontarook.carwashservicevaadin.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("")
@Route(value = "")
public class ListView extends VerticalLayout {

    public ListView() {
        Button signUp = new Button("Signup");
        Button logIn = new Button("Login");

        HorizontalLayout horizontalLayout = new HorizontalLayout(signUp, logIn);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        signUp.addClickListener(click -> UI.getCurrent().getPage().executeJs("location.assign('signup')"));
        logIn.addClickListener(click -> UI.getCurrent().getPage().executeJs("location.assign('login')"));

        add(horizontalLayout);
    }
}