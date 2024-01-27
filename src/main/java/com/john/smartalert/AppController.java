package com.john.smartalert;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class AppController {

    @PostMapping("/newemergance")
    public String addMovieRating(@RequestBody Map<String, String> body) {
        //System.out.println(body);
        HashMap<String, HashMap<String, String>> emergence = new HashMap<>();
        body.forEach((s, map) -> {
            HashMap<String, String> temp1 = new HashMap<>();
            //System.out.println(s.toString());
            map = map.replace("{","");
            map = map.replace("}","");
            String[] temp = map.split(",");
            //System.out.println(temp.length);
            for (int i =0; i<temp.length;i++) {
                if (i==temp.length-2){
//                    System.out.println(temp[i]);
                    String[] t1 = temp[i].split("=");
                    temp1.put(t1[0].strip(),t1[1]+","+temp[6]);
                    break;
                }

                String[] t = temp[i].split("=");
                temp1.put(t[0].strip(),t[1]);
            }
            emergence.put(s,temp1);
        });
        List<List<String>> groups = groupEmergencies(emergence);
        List<List<String>> sortedGroups = new ArrayList<>();
        for (List<String> g:groups) {
            sortedGroups.add(groupDegree(g,emergence));
        }
        System.out.println(groups);
        System.out.println(sortedGroups);
        String res = "ok";
        return res;
    }

    private List<List<String>> groupEmergencies(HashMap<String,HashMap<String,String>> emergencies){
        List<String> fire = new ArrayList<>();
        List<String> flood = new ArrayList<>();
        List<String> earthquake = new ArrayList<>();
        List<String> thunderstorm = new ArrayList<>();
        List<String> heatwave = new ArrayList<>();
        List<String> tornado = new ArrayList<>();
        List<String> blizzard = new ArrayList<>();
        emergencies.forEach((s, map) ->{
            switch (map.get("Category")){
                case ("Fire"):{
                    fire.add(s);
                    break;
                }
                case ("Flood"):{
                    flood.add(s);
                    break;
                }
                case ("Earthquake"):{
                    earthquake.add(s);
                    break;
                }
                case ("Thunderstorm"):{
                    thunderstorm.add(s);
                    break;
                }
                case ("Heatwave"):{
                    heatwave.add(s);
                    break;
                }
                case ("Tornado"):{
                    tornado.add(s);
                    break;
                }case ("Blizzard"):{
                    blizzard.add(s);
                    break;
                }
            }
        } );
        List<List<String>> groups= new ArrayList<>();
        groups.addAll(groupByLocation(fire,emergencies));
        groups.addAll(groupByLocation(flood,emergencies));
        groups.addAll(groupByLocation(earthquake,emergencies));
        groups.addAll(groupByLocation(thunderstorm,emergencies));
        groups.addAll(groupByLocation(heatwave,emergencies));
        groups.addAll(groupByLocation(tornado,emergencies));
        groups.addAll(groupByLocation(blizzard,emergencies));
        return groups;
    }

    private List<List<String>> groupByLocation(List<String> group, HashMap<String,HashMap<String,String>> emergencies){
        List<List<String>> result = new ArrayList<>();;
        List<Double> centers = new ArrayList<>();
        for (int k=0;k<=1;k++) {
            result.clear();
            for (String s : group) {
                String loc = emergencies.get(s).get("Location");
                String[] temp = loc.split(",");
                double lon = Double.parseDouble(temp[0]);
                double lat = Double.parseDouble(temp[1]);
                if (centers.isEmpty()) {
                    centers.add(lon);
                    centers.add(lat);
                    result.add(new ArrayList<>());
                    result.get(0).add(s);
                    continue;
                }
                double clon = centers.get(0);
                double clat = centers.get(1);
                double distance = calculateDistance2(lat, lon, clat, clon);
                if (distance <= 15-(k*5)) {
                    if(result.isEmpty())
                        result.add(new ArrayList<>());
                    result.get(0).add(s);
                }
                else {
                    if (centers.size() == 2) {
                        centers.add(lon);
                        centers.add(lat);
                        result.add(new ArrayList<>());
                        result.get(1).add(s);
                    }
                    else {
                        for (int i = 2; i < centers.size(); i += 2) {
                            clon = centers.get(i);
                            clat = centers.get(i + 1);
                            distance = calculateDistance2(lat, lon, clat, clon);
                            if (distance <= 15 - (k * 5)) {
                                if (result.size() <= i/2){
                                    result.add(new ArrayList<>());
                                }
                                result.get(i / 2).add(s);
                            }
                            else if (distance > 15 - (k * 5) && i == centers.size() - 1) {
                                result.add(new ArrayList<>());
                                result.get((i / 2) + 1).add(s);
                            }
                        }
                    }
                }
            }
            int j = 0;
            for (List<String> l1 : result) {
                List<Double> newCenter = calculateCenter(l1, emergencies);
                centers.remove(j);
                centers.remove(j);
                centers.add(j, newCenter.get(0));
                centers.add(j + 1, newCenter.get(1));
                j += 2;
            }
            if (k == 1){
                for (int i = 0; i < result.size(); i ++) {
                    result.get(i).add(result.get(i).size(),centers.get(2*i).toString());
                    result.get(i).add(result.get(i).size(),centers.get((2*i)+1).toString());
                }
            }
        }
        return result;
    }

    static List<Double> calculateCenter(List<String> l,HashMap<String,HashMap<String,String>> emergencies){
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
                double lon = Double.parseDouble(temp[0]);
                double lat = Double.parseDouble(temp[1]);
                c1+=lon;
                c2+=lat;
            }
            c1/=size;
            c2/=size;
            center.add(c1);
            center.add(c2);
            return center;
        }
    }

    static double EARTH_RADIUS = 6371;
    static double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
    static double calculateDistance2(double startLat, double startLong, double endLat, double endLong) {

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    static List<String> groupDegree(List<String> group,HashMap<String,HashMap<String,String>> emergencies){
        double score =0;
        List<String> sortedList = new ArrayList<>();
        LocalDateTime date = null;
        int count1 =0;
        int count2 =0;
        for (int i=0; i<group.size()-2;i++) {
            String s = group.get(i);
            HashMap<String,String> e = emergencies.get(s);
            if (!e.get("Photo").equals("-")){
                score += 1.75;
                sortedList.add(1,s);
            }
            else {
                sortedList.add(s);
            }
            String loc = emergencies.get(s).get("Location");
            String[] temp = loc.split(",");
            double lon = Double.parseDouble(temp[0]);
            double lat = Double.parseDouble(temp[1]);
            double dist =calculateDistance2(Double.parseDouble(group.get(group.size()-1)),Double.parseDouble(group.get(group.size()-2)),lat,lon);

            if (dist <= 5){
                score += 1.75;
                count1++;
            } else if (dist <= 7) {
                score += 1.5;
            }
            else {
                score += 1.25;
                count2++;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
            if (date == null){
                date = LocalDateTime.parse(e.get("Time"),formatter);
                continue;
            }
            Duration duration = Duration.between(date,LocalDateTime.parse(e.get("Time"),formatter));
            if (duration.toSeconds() <= 600){
                score +=2;
            }
            else {
                score+=1;
            }
        }

        sortedList.add(group.get(group.size()-2));
        sortedList.add(group.get(group.size()-1));
        sortedList.add(String.valueOf(score));
        return sortedList;
    }

}
/*jdslajflasfaf
* f
* akfasfk
* asfkasfkas;kf;asjfg
* asgajg*/