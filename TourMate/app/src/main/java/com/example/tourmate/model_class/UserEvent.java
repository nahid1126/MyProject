package com.example.tourmate.model_class;

public class UserEvent {
    private String eventId;
    private String eventName;
    private String eventStartLocation;
    private String eventDestination;
    private String eventStartDate;
    private String eventEndDate;
    private String eventBudget;

    public UserEvent() {
    }

    public UserEvent(String eventId, String eventName, String eventStartLocation, String eventDestination, String eventStartDate, String eventEndDate, String eventBudget) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventStartLocation = eventStartLocation;
        this.eventDestination = eventDestination;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventBudget = eventBudget;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventStartLocation() {
        return eventStartLocation;
    }

    public void setEventStartLocation(String eventStartLocation) {
        this.eventStartLocation = eventStartLocation;
    }

    public String getEventDestination() {
        return eventDestination;
    }

    public void setEventDestination(String eventDestination) {
        this.eventDestination = eventDestination;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventBudget() {
        return eventBudget;
    }

    public void setEventBudget(String eventBudget) {
        this.eventBudget = eventBudget;
    }
}
