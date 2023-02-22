package com.kontarook.carwashservicevaadin.views.appointment;

import CarWashSwaggerApi.api.controller.AppointmentControllerApi;
import CarWashSwaggerApi.api.controller.AssistanceControllerApi;
import CarWashSwaggerApi.api.model.AppointmentDTOReq;
import CarWashSwaggerApi.api.model.AppointmentDTORes;
import CarWashSwaggerApi.api.model.Assistance;
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
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Route(value = "appointment")
@PageTitle("Appointment | CarWash Service")
public class AppointmentView extends VerticalLayout {

    private Grid<AppointmentDTORes> grid = new Grid<>(AppointmentDTORes.class);

    private final AssistanceControllerApi assistanceControllerApi;

    private final AppointmentControllerApi appointmentControllerApi;

    private AppointmentForm appointmentForm;

    public AppointmentView(AssistanceControllerApi assistanceControllerApi,
                           AppointmentControllerApi appointmentControllerApi) {
        this.assistanceControllerApi = assistanceControllerApi;
        this.appointmentControllerApi = appointmentControllerApi;

        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private Component getContent() {

        HorizontalLayout content = new HorizontalLayout(grid, appointmentForm);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, appointmentForm);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureForm() {
        setRequestToken();
        appointmentForm = new AppointmentForm(assistanceControllerApi.getAllUsingGET1());
        appointmentForm.setWidth("25em");

        appointmentForm.addListener(AppointmentForm.AddEvent.class, this::saveAppointment);
        appointmentForm.addListener(AppointmentForm.AddOnTimeEvent.class, this::saveAppointmentOnSpecificTime);
        appointmentForm.addListener(AppointmentForm.DeleteEvent.class, this::deleteAppointment);
        appointmentForm.addListener(AppointmentForm.CloseEvent.class, e -> closeEditor());
    }

    private void addAppointment() {
        grid.asSingleSelect().clear();
        editAppointment(new AppointmentDTOReq());
    }

    private void deleteAppointment(AppointmentForm.DeleteEvent event) {
        appointmentControllerApi.deleteUsingDELETE(event.getAppointment().getId());
        updateList();
        closeEditor();
    }

    private void saveAppointment(AppointmentForm.AddEvent event) {
        appointmentControllerApi.addUsingPOST(event.getAppointment());
        updateList();
        closeEditor();
    }

    private void saveAppointmentOnSpecificTime(AppointmentForm.AddOnTimeEvent event) {
        appointmentControllerApi.addAppointmentOnSpecificTimeUsingPOST(event.getAppointmentOnTime());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolbar() {

        Button addContactButton = new Button("Add appointment");
        addContactButton.addClickListener(e -> addAppointment());

        HorizontalLayout toolbar = new HorizontalLayout(addContactButton);
        toolbar.addClassName("appointmentToolbar");
        return toolbar;
    }

    private void editAppointment(AppointmentDTOReq appointmentDTO) {
        if (appointmentDTO == null) {
            closeEditor();
        } else {
            appointmentForm.setVisible(true);
            appointmentForm.setAppointment(appointmentDTO);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        appointmentForm.setAppointment(null);
        appointmentForm.setVisible(false);
        removeClassName("editing");
    }

    private void configureGrid() {
        grid.addClassNames("appointment-grid");
        grid.setSizeFull();

        grid.setColumns("startTime", "totalPrice", "tilStart");
        grid.addColumn(appointment -> {
            StringJoiner joiner = new StringJoiner(", ");
            for (Assistance assistance : appointment.getAssistances()) {
                joiner.add(assistance.getName());
            }
            return joiner.toString();
        }).setHeader("Assistances");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(e -> {
            AppointmentDTOReq appointmentDTOReq = new AppointmentDTOReq();
            appointmentDTOReq.setId(e.getValue().getId());
            appointmentDTOReq.setStartTime(e.getValue().getStartTime());
            appointmentDTOReq.setAssistancesIds(e.getValue().getAssistances().stream().map(Assistance::getId)
                    .collect(Collectors.toList()));
            editAppointment(appointmentDTOReq);
        });
    }

    private void updateList() {
        setRequestToken();
        List<AppointmentDTORes> listOfAssistance = null;
        try {
            listOfAssistance = appointmentControllerApi.getWaitingAssistanceByUserUsingGET();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listOfAssistance != null && !listOfAssistance.isEmpty()) {
            grid.setItems(listOfAssistance);
        } else {
            grid.setItems(Collections.emptyList());
        }
    }

//    private void updateList() {
//        setRequestToken();
//        List<AppointmentDTORes> listOfAssistance = appointmentControllerApi
//                .getWaitingAssistanceByUserUsingGET();
//
//        if (!listOfAssistance.isEmpty()) {
//            grid.setItems(listOfAssistance);
//        } else {
//            grid.setItems(Collections.emptyList());
//        }
//    }

    public void setRequestToken() {
        VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
        if (vaadinRequest instanceof VaadinServletRequest) {
            VaadinServletRequest servletRequest = (VaadinServletRequest) vaadinRequest;
            HttpServletRequest httpServletRequest = servletRequest.getHttpServletRequest();
            String token = getTokenFromCookie(httpServletRequest);
            appointmentControllerApi.getApiClient().setApiKey(token);
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
