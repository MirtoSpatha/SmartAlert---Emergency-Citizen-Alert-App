package com.john.smartalert;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class Emergencies {
    private HashMap<String,HashMap<String, String>> alerts = new HashMap<>();
    private HashMap<String,HashMap<String, String>> newEmerg = new HashMap<>();

    @SuppressWarnings("unchecked")
    @JsonProperty("alerts")
    private void unpackNested(HashMap<String,Object> al) {
        al.forEach((s, o) -> {
            HashMap<String, String> temp = (HashMap<String,String>) o;
            alerts.put(s,temp);
        });
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("newEmergencies")
    private void unpackNestedEm(HashMap<String,Object> em) {
        em.forEach((s, o) -> {
            HashMap<String, String> temp = (HashMap<String,String>) o;
            newEmerg.put(s,temp);
        });
    }

    public HashMap<String, HashMap<String, String>> getAlerts() {
        return alerts;
    }

    public void setAlerts(HashMap<String, HashMap<String, String>> alerts) {
        this.alerts = alerts;
    }

    public HashMap<String, HashMap<String, String>> getNewEmerg() {
        return newEmerg;
    }

    public void setNewEmerg(HashMap<String, HashMap<String, String>> newEmerg) {
        this.newEmerg = newEmerg;
    }
}
