package com.kontarook.carwashservicevaadin.views.auth;

import CarWashSwaggerApi.api.controller.AuthenticationControllerApi;
import CarWashSwaggerApi.api.model.SignUpRequest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("signup")
@PageTitle("Signup | CarWash Service")
public class SignupView extends VerticalLayout {

    private final AuthenticationControllerApi authenticationControllerApi;

    private TextField username = new TextField("Username");
    private TextField email = new TextField("Email");
    private PasswordField password = new PasswordField("Password");
    private PasswordField confirmPassword = new PasswordField("Confirm Password");

    private Button signup = new Button("Sign up");

    public SignupView(AuthenticationControllerApi authenticationControllerApi) {
        this.authenticationControllerApi = authenticationControllerApi;

        addClassName("signup-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        signup.addClickListener(e -> {
            String enteredUsername = username.getValue();
            String enteredPassword = password.getValue();
            String enteredEmail = email.getValue();
            String confirmedPassword = confirmPassword.getValue();

            if (!enteredPassword.equals(confirmedPassword)) {
                Notification.show("Passwords do not match", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            SignUpRequest signUpRequest = new SignUpRequest();
            signUpRequest.setUsername(enteredUsername);
            signUpRequest.setEmail(enteredEmail);
            signUpRequest.setPassword(enteredPassword);

            try {
                authenticationControllerApi.signUpUsingPOST(signUpRequest);
                Notification.show("Signup successful", 3000, Notification.Position.TOP_CENTER);
                UI.getCurrent().getPage().executeJs("location.assign('login')");
            } catch (Exception ex) {
                Notification.show("Signup failed: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        add(
                new H1("CarWah Service"),
                username,
                email,
                password,
                confirmPassword,
                signup
        );
    }
}
