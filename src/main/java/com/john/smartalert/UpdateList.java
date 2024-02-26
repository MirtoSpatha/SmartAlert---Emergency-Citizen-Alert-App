package com.john.smartalert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdateList {

    public static List<String> removeOldEmergencies(HashMap<String, HashMap<String,String>> emergencies){
        List<String> oldEmerg = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        emergencies.forEach((s, stringStringHashMap) -> {
            Duration duration = Duration.between(LocalDateTime.parse(stringStringHashMap.get("Time"),formatter), LocalDateTime.now());
            if (duration.toHours()>=48){
                oldEmerg.add(s);
            }
        });
        return oldEmerg;
    }

    public static List<String> updateAlerts(HashMap<String,HashMap<String,String>> emergencies, HashMap<String,HashMap<String,String>> alerts){
        List<String> delete = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        alerts.forEach((s, map) ->{
            String[] loc = map.get("Location").split(",");
            double alat = Double.parseDouble(loc[0]);
            double alon = Double.parseDouble(loc[1]);
            emergencies.forEach((s1, e) ->{
                String[] t = e.get("Location").split(",");
                double elat = Double.parseDouble(t[0]);
                double elon = Double.parseDouble(t[1]);
                double dist = Calculations.calculateDistance2(alat, alon, elat, elon);
                Duration duration = Duration.between(LocalDateTime.parse(map.get("Time"),formatter), LocalDateTime.parse(e.get("Time"),formatter));
                if (dist<=10 && map.get("Category").equals(e.get("Category")) && duration.toHours() <= 48){
                    String g = map.remove("Group");
                    g = g.concat(s1+",");
                    map.put("Group",g);
                    delete.add(s1);
                }
            } );
        } );
        return delete;
    }
}
