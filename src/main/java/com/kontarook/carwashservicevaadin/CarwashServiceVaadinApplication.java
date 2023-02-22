package com.kontarook.carwashservicevaadin;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "CarwashServiceVaadin")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class CarwashServiceVaadinApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(CarwashServiceVaadinApplication.class, args);
    }

}
