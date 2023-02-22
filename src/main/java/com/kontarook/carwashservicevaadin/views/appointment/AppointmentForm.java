package com.kontarook.carwashservicevaadin.views.appointment;

import CarWashSwaggerApi.api.model.AppointmentDTOReq;
import CarWashSwaggerApi.api.model.AppointmentOnTimeRequest;
import CarWashSwaggerApi.api.model.AssistanceDTO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class AppointmentForm extends FormLayout {

    private Binder<AppointmentDTOReq> binder = new Binder<>(AppointmentDTOReq.class);
    private AppointmentDTOReq appointmentDTO;
    private AppointmentOnTimeRequest appointmentOnTimeRequest;
    ComboBox<AssistanceDTO> assistanceListBox = new ComboBox<>("Assistance");
    private DateTimePicker startTime = new DateTimePicker();
    private Button addOnTime = new Button("Add on time");
    private Button addOnFreeTime = new Button("Add on free time");
    private Button delete = new Button("Delete");
    private Button close = new Button("Cancel");

    public AppointmentForm(List<AssistanceDTO> assistances) {
        addClassName("appointment-form");
        assistanceListBox.setItems(assistances);
        add(    assistanceListBox,
                startTime,
                createButtonsLayout());
    }

    public void setAppointment(AppointmentDTOReq appointmentDTO) {
        this.appointmentDTO = appointmentDTO;
        binder.readBean(appointmentDTO);
    }

    private HorizontalLayout createButtonsLayout() {
        addOnTime.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addOnFreeTime.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        addOnTime.addClickListener(event -> validateAndAddOnTime());
        addOnFreeTime.addClickListener(event -> validateAndAddOnFreeTime());

        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, appointmentDTO)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        addOnTime.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(addOnTime, addOnFreeTime, delete, close);
    }

    private void validateAndAddOnFreeTime() {
        try {
            binder.writeBean(appointmentDTO);
            appointmentDTO.setAssistancesIds(Collections.singletonList(assistanceListBox.getValue().getId()));
            fireEvent(new AddEvent(this, appointmentDTO));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private void validateAndAddOnTime() {
        try {
            binder.writeBean(appointmentDTO);
            appointmentDTO.setAssistancesIds(Collections.singletonList(assistanceListBox.getValue().getId()));
            appointmentOnTimeRequest = new AppointmentOnTimeRequest();
            appointmentOnTimeRequest.setAppointmentDTO(appointmentDTO);
            appointmentOnTimeRequest.setAppointmentTime(LocalDateTime.from(startTime.getValue())
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            fireEvent(new AddOnTimeEvent(this, appointmentOnTimeRequest));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class AppointmentFormEvent extends ComponentEvent<AppointmentForm> {
        private AppointmentDTOReq appointmentDTO;

        private AppointmentOnTimeRequest appointmentOnTimeRequest;

        protected AppointmentFormEvent(AppointmentForm source, AppointmentDTOReq appointmentDTO) {
            super(source, false);
            this.appointmentDTO = appointmentDTO;
        }

        public AppointmentFormEvent(AppointmentForm source, AppointmentOnTimeRequest appointmentOnTimeDTO) {
            super(source, false);
            this.appointmentOnTimeRequest = appointmentOnTimeDTO;
        }

        public AppointmentDTOReq getAppointment() {
            return appointmentDTO;
        }

        public AppointmentOnTimeRequest getAppointmentOnTime() {
            return appointmentOnTimeRequest;
        }
    }

    public static class AddEvent extends AppointmentForm.AppointmentFormEvent {
        AddEvent(AppointmentForm source, AppointmentDTOReq appointmentDTO) {
            super(source, appointmentDTO);
        }
    }

    public static class AddOnTimeEvent extends AppointmentForm.AppointmentFormEvent {
        AddOnTimeEvent(AppointmentForm source, AppointmentOnTimeRequest appointmentDTO) {
            super(source, appointmentDTO);
        }
    }

    public static class DeleteEvent extends AppointmentForm.AppointmentFormEvent {
        DeleteEvent(AppointmentForm source, AppointmentDTOReq appointmentDTO) {
            super(source, appointmentDTO);
        }
    }

    public static class CloseEvent extends AppointmentForm.AppointmentFormEvent {
        CloseEvent(AppointmentForm source) {
            super(source, (AppointmentDTOReq) null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
