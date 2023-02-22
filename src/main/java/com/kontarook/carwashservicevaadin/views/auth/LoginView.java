package com.kontarook.carwashservicevaadin.views.auth;

import CarWashSwaggerApi.api.controller.AuthenticationControllerApi;
import CarWashSwaggerApi.api.model.AuthenticationRequest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;


import javax.servlet.http.Cookie;

@Route("login")
@PageTitle("Login | CarWash Service")
public class LoginView extends VerticalLayout implements BeforeEnterListener {

    private final AuthenticationControllerApi authenticationControllerApi;

    private LoginForm login = new LoginForm();

    public LoginView(AuthenticationControllerApi authenticationControllerApi) {
        this.authenticationControllerApi = authenticationControllerApi;
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        login.addLoginListener((AbstractLogin.LoginEvent e) -> {
            String username = e.getUsername();
            String password = e.getPassword();
            authenticationRequest.setUsername(username);
            authenticationRequest.setPassword(password);
            try {
                String token = authenticationControllerApi.loginUsingPOST(authenticationRequest);
                Cookie cookie = new Cookie("jwt", token);
                cookie.setMaxAge(3600);
                VaadinService.getCurrentResponse().addCookie(cookie);
                UI.getCurrent().getPage().executeJs("location.assign('home')");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        login.setAction("login");
        add(
                new H1("CarWash Service"),
                login
        );

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
