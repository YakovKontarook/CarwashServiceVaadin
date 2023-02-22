package com.kontarook.carwashservicevaadin.views.assistance;

import CarWashSwaggerApi.api.model.AssistanceDTO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class AssistanceForm extends FormLayout {

    private Binder<AssistanceDTO> binder = new Binder<>(AssistanceDTO.class);

    private AssistanceDTO assistanceDTO;

    private TextField name = new TextField("Name");
    private TextField description = new TextField("Description");
    private NumberField price = new NumberField("Price");
    private IntegerField duration = new IntegerField("Duration");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Button close = new Button("Cancel");

    public AssistanceForm() {
        addClassName("assistance-form");
        binder.bindInstanceFields(this);

        add(    name,
                description,
                price,
                duration,
                createButtonsLayout());
    }

    public void setAssistance(AssistanceDTO assistanceDTO) {
        this.assistanceDTO = assistanceDTO;
        binder.readBean(assistanceDTO);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, assistanceDTO)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(assistanceDTO);
            fireEvent(new SaveEvent(this, assistanceDTO));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class AssistanceFormEvent extends ComponentEvent<AssistanceForm> {
        private AssistanceDTO assistanceDTO;

        protected AssistanceFormEvent(AssistanceForm source, AssistanceDTO assistanceDTO) {
            super(source, false);
            this.assistanceDTO = assistanceDTO;
        }

        public AssistanceDTO getAssistance() {
            return assistanceDTO;
        }
    }

    public static class SaveEvent extends AssistanceFormEvent {
        SaveEvent(AssistanceForm source, AssistanceDTO assistanceDTO) {
            super(source, assistanceDTO);
        }
    }

    public static class DeleteEvent extends AssistanceFormEvent {
        DeleteEvent(AssistanceForm source, AssistanceDTO assistanceDTO) {
            super(source, assistanceDTO);
        }
    }

    public static class CloseEvent extends AssistanceFormEvent {
        CloseEvent(AssistanceForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
