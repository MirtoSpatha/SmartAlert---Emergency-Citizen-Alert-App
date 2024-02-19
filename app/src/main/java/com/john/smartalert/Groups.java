package com.john.smartalert;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class Groups {
    private HashMap<String,HashMap<String,String>> groups = new HashMap<>();
    private HashMap<String,HashMap<String,String>> alerts = new HashMap<>();

    @SuppressWarnings("unchecked")
    @JsonProperty("groups")
    private void unpackNestedGroups(HashMap<String,Object> g) {
        g.forEach((s, o) -> {
            HashMap<String, String> temp = (HashMap<String,String>) o;
            groups.put(s,temp);
        });
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("updateAlerts")
    private void unpackNested(HashMap<String,Object> al) {
        al.forEach((s, o) -> {
            HashMap<String, String> temp = (HashMap<String,String>) o;
            alerts.put(s,temp);
        });
    }

    public HashMap<String, HashMap<String, String>> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<String, HashMap<String, String>> groups) {
        this.groups = groups;
    }

    public HashMap<String, HashMap<String, String>> getAlerts() {
        return alerts;
    }

    public void setAlerts(HashMap<String, HashMap<String, String>> alerts) {
        this.alerts = alerts;
    }
}
