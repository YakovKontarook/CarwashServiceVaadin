package com.kontarook.carwashservicevaadin.views.assistance;

import CarWashSwaggerApi.api.controller.AssistanceControllerApi;
import CarWashSwaggerApi.api.model.AssistanceDTO;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Route(value = "assistance")
@PageTitle("Assistances | CarWash Service")
public class AssistanceView extends VerticalLayout {
    private Grid<AssistanceDTO> grid = new Grid<>(AssistanceDTO.class);
    private AssistanceForm assistanceForm;
    private final AssistanceControllerApi assistanceControllerApi;

    public AssistanceView(AssistanceControllerApi assistanceControllerApi) {
        this.assistanceControllerApi = assistanceControllerApi;

        addClassName("assistance-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("assistance-grid");
        grid.setSizeFull();
        grid.setColumns("name", "description", "price", "duration");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(e -> editAssistance(e.getValue()));
    }

    private void configureForm() {
        assistanceForm = new AssistanceForm();
        assistanceForm.setWidth("25em");

        assistanceForm.addListener(AssistanceForm.SaveEvent.class, this::saveAssistance);
        assistanceForm.addListener(AssistanceForm.DeleteEvent.class, this::deleteAssistance);
        assistanceForm.addListener(AssistanceForm.CloseEvent.class, e -> closeEditor());
    }

    private void updateList() {
        setRequestToken();
        grid.setItems(assistanceControllerApi.getAllUsingGET1());
    }

    private Component getContent() {

        HorizontalLayout content = new HorizontalLayout(grid, assistanceForm);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, assistanceForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private HorizontalLayout getToolbar() {

        Button addContactButton = new Button("Add assistance");
        addContactButton.addClickListener(e -> addAssistance());

        HorizontalLayout toolbar = new HorizontalLayout(addContactButton);
        toolbar.addClassName("assistanceToolbar");
        return toolbar;
    }

    private void closeEditor() {
        assistanceForm.setAssistance(null);
        assistanceForm.setVisible(false);
        removeClassName("editing");
    }

    private void saveAssistance(AssistanceForm.SaveEvent event) {
        assistanceControllerApi.addUsingPOST1(event.getAssistance());
        updateList();
        closeEditor();
    }

    private void editAssistance(AssistanceDTO assistanceDTO) {
        if (assistanceDTO == null) {
            closeEditor();
        } else {
            assistanceForm.setVisible(true);
            assistanceForm.setAssistance(assistanceDTO);
            addClassName("editing");
        }
    }

    private void addAssistance() {
        grid.asSingleSelect().clear();
        editAssistance(new AssistanceDTO());
    }

    private void deleteAssistance(AssistanceForm.DeleteEvent event) {
        assistanceControllerApi.deleteUsingDELETE1(event.getAssistance().getId());
        updateList();
        closeEditor();
    }

    public void setRequestToken() {
        VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
        if (vaadinRequest instanceof VaadinServletRequest) {
            VaadinServletRequest servletRequest = (VaadinServletRequest) vaadinRequest;
            HttpServletRequest httpServletRequest = servletRequest.getHttpServletRequest();
            String token = getTokenFromCookie(httpServletRequest);
            assistanceControllerApi.getApiClient().setApiKey(token);
        }
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}

