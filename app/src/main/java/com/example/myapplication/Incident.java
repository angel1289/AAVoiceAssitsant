package com.example.myapplication;

public class Incident {

    private String number;
    private String state;
    private String urgency;
    private String short_description;
    private String response;

    private String speech_text;

    public Incident(String number, String state, String urgency, String short_description, String response, String speech_text) {
        this.number = number;
        this.state = state;
        this.urgency = urgency;
        this.short_description = short_description;
        this.response = response;
        this.speech_text = speech_text;
    }


    public Incident(String speech_text) {
        this.speech_text = speech_text;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSpeech_text() {
        return speech_text;
    }

    public void setSpeech_text(String speech_text) {
        this.speech_text = speech_text;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "number='" + number + '\'' +
                ", state='" + state + '\'' +
                ", urgency='" + urgency + '\'' +
                ", short_description='" + short_description + '\'' +
                ", response='" + response + '\'' +
                ", speech_text='" + speech_text + '\'' +
                '}';
    }
}
