package com.john.smartalert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Calculations {
    private static final double EARTH_RADIUS = 6371;

    public static double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public static double calculateDistance2(double startLat, double startLong, double endLat, double endLong) {

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static List<Double> calculateCenter(List<String> l, HashMap<String, HashMap<String,String>> emergencies){
        List<Double> center = new ArrayList<>();
        if (l.size() == 1) {
            String loc = emergencies.get(l.get(0)).get("Location");
            String[] temp = loc.split(",");
            center.add(Double.parseDouble(temp[0]));
            center.add(Double.parseDouble(temp[1]));
            return center;
        }
        else {
            final int size = l.size();
            double c1=0,c2=0;
            for (String s : l) {
                String loc = emergencies.get(s).get("Location");
                String[] temp = loc.split(",");
                double lat = Double.parseDouble(temp[0]);
                double lon = Double.parseDouble(temp[1]);
                c1+=lat;
                c2+=lon;
            }
            c1/=size;
            c2/=size;
            center.add(c1);
            center.add(c2);
            return center;
        }
    }

    public static String BeginOfEvent(List<String> g, HashMap<String,HashMap<String,String>> emergencies){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        LocalDateTime date = LocalDateTime.parse(emergencies.get(g.get(0)).get("Time"),formatter);
        for (int i = 1; i<g.size()-2;i++){
            LocalDateTime l = LocalDateTime.parse(emergencies.get(g.get(i)).get("Time"),formatter);
            int r = date.compareTo(l);
            if (r > 0){
                date =l;
            }
        }
        return date.format(formatter);
    }
}
