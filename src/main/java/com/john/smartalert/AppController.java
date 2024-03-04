package com.john.smartalert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class AppController {


    private final ObjectMapper objectMapper;

    public AppController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping("/emergence")
    public HashMap<String,String> calcEmergence(@RequestBody String body) throws JsonProcessingException {//Map<String, String> body
        //create the 2 hashMaps. One for the alerts and 1 for the reported  incidents (new emergence)
        Emergencies emergencies = objectMapper.readValue(body,Emergencies.class);
        //remove from the new emergence the incidents that already been alerts
        List<String> oldEmergencies = UpdateList.removeOldEmergencies(emergencies.getNewEmerg());
        oldEmergencies.forEach(s -> emergencies.getNewEmerg().remove(s));
        emergencies.getAlerts().forEach((s, stringStringHashMap) -> {
            String group = stringStringHashMap.get("Group");
            String[] strings = group.split(",");
            for (String s1 : strings){
                emergencies.getNewEmerg().remove(s1);
            }
        });
        if (emergencies.getNewEmerg().isEmpty()){
            return new HashMap<>();
        }
        //check if any new emergence can grouped with the already alerts and remove from the new emergence
        UpdateList.updateAlerts(emergencies.getNewEmerg(),emergencies.getAlerts()).forEach(s -> emergencies.getNewEmerg().remove(s));

        HashMap<String, String> results = new HashMap<>();
        if (!emergencies.getNewEmerg().isEmpty()) {
            //create a list of list with the groups of emergencies
            List<List<String>> groups = groupEmergencies(emergencies.getNewEmerg());

            List<List<String>> groupsScore = new ArrayList<>();
            for (List<String> g : groups) {
                //add the score for every group
                groupsScore.add(groupDegree(g, emergencies.getNewEmerg()));
            }
            //sort the groups with the first have the bigger score
            List<List<String>> sortedEmergencies = SortList.sortAllEmergencies(groupsScore);
            //create the HashMap that will return to the android app
            int j = 0;
            for (List<String> g : sortedEmergencies) {
                StringBuilder builder = new StringBuilder();
                //add some information for the group
                builder.append("Group=");
                for (int i = 0; i < g.size() - 4; i++) {
                    builder.append(g.get(i)).append(",");
                }
                builder.append("|").append("Center=").append(g.get(g.size() - 4)).append(",").append(g.get(g.size() - 3)).append("|")
                        .append("Time=").append(g.get(g.size() - 2)).append("|")
                        .append("Reports=").append(g.size() - 4).append("|")
                        .append("Category=").append(emergencies.getNewEmerg().get(sortedEmergencies.get(j).get(0)).get("Category"));
                results.put(String.valueOf(j), builder.toString());
                j++;
            }
        }
        //add the updated groups for the alerts
        StringBuilder updateAlerts = new StringBuilder();
        emergencies.getAlerts().forEach((s, a) -> updateAlerts.append(s).append("=").append(a.get("Group")).append("|"));
        results.put("updateAlerts",updateAlerts.toString());
        //add the incidents that is more than 24 hours and have to move to the AllEmergencies (in the FireBase)
        StringBuilder old = new StringBuilder();
        oldEmergencies.forEach(s -> old.append(s).append(","));
        results.put("oldEmergencies",old.toString());
        return results;
    }

    private List<List<String>> groupEmergencies(HashMap<String,HashMap<String,String>> emergencies){
        //create Lists for every category
        List<String> fire = new ArrayList<>();
        List<String> flood = new ArrayList<>();
        List<String> earthquake = new ArrayList<>();
        List<String> thunderstorm = new ArrayList<>();
        List<String> heatwave = new ArrayList<>();
        List<String> tornado = new ArrayList<>();
        List<String> blizzard = new ArrayList<>();
        //split the emergencies by the category
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
        //create groups for every category with the incidents that are near (based on the location of the incident)
        groups.addAll(groupByLocation(fire,emergencies));
        groups.addAll(groupByLocation(flood,emergencies));
        groups.addAll(groupByLocation(earthquake,emergencies));
        groups.addAll(groupByLocation(thunderstorm,emergencies));
        groups.addAll(groupByLocation(heatwave,emergencies));
        groups.addAll(groupByLocation(tornado,emergencies));
        groups.addAll(groupByLocation(blizzard,emergencies));
        //for every group find the first report and add it the end of the list of the group
        for (List<String> g:groups) {
            g.add(Calculations.BeginOfEvent(g,emergencies));
        }
        return groups;
    }

    //return a list with the lists of the groups and in the end of every list there is the center (lat,long) of the group
    private List<List<String>> groupByLocation(List<String> group, HashMap<String,HashMap<String,String>> emergencies){
        List<List<String>> result = new ArrayList<>();//the group is a list with the keys from FireBase of every reported incident
        List<Double> centers = new ArrayList<>();
        for (int k=0;k<=1;k++) {
            result.clear();
            for (String s : group) {
                //get the lat and long of the incident
                String loc = emergencies.get(s).get("Location");
                String[] temp = loc.split(",");
                double lat = Double.parseDouble(temp[0]);
                double lon = Double.parseDouble(temp[1]);
                //if there isn't any center yet, create one
                if (centers.isEmpty()) {
                    centers.add(lat);
                    centers.add(lon);
                    result.add(new ArrayList<>());
                    result.get(0).add(s);
                    continue;
                }
                //get the first center
                double clat = centers.get(0);
                double clon = centers.get(1);
                //calculate the distance between the center of the group and the incident
                double distance = Calculations.calculateDistance2(lat, lon, clat, clon);
                //check if the distance (for the first loop) is less than 15km and (for the second loop) is less than 10km
                if (distance <= 15-(k*5)) {
                    if(result.isEmpty())
                        result.add(new ArrayList<>());
                    result.get(0).add(s);
                }
                else {
                    //if there is only one group then create a new group with center the location of the incident
                    if (centers.size() == 2) {
                        centers.add(lat);
                        centers.add(lon);
                        result.add(new ArrayList<>());
                        result.get(1).add(s);
                    }
                    else {
                        //search if the incident can be added in an existing group
                        for (int i = 2; i < centers.size(); i += 2) {
                            clat = centers.get(i);
                            clon = centers.get(i+1);
                            distance = Calculations.calculateDistance2(lat, lon, clat, clon);
                            if (distance <= 15 - (k * 5)) {
                                if (result.size() <= i/2){
                                    result.add(new ArrayList<>());
                                }
                                result.get(i / 2).add(s);
                                break;
                            }
                            //if there isn't any group that is near, create a new one
                            else if (distance > 15 - (k * 5) && i == centers.size() - 2) {
                                result.add(new ArrayList<>());
                                result.get((i / 2) + 1).add(s);
                                centers.add(lat);
                                centers.add(lon);
                                break;
                            }
                        }
                    }
                }
            }
            //after find the groups, for every group calculate the true center
            int j = 0;
            for (List<String> l1 : result) {
                List<Double> newCenter = Calculations.calculateCenter(l1, emergencies);
                centers.remove(j);
                centers.remove(j);
                centers.add(j, newCenter.get(0));
                centers.add(j + 1, newCenter.get(1));
                j += 2;
            }
            //if we are in the second loop (the last) we have to add in the end of the group the center
            if (k == 1){
                for (int i = 0; i < result.size(); i ++) {
                    result.get(i).add(result.get(i).size(),centers.get(2*i).toString());
                    result.get(i).add(result.get(i).size(),centers.get((2*i)+1).toString());
                }
            }
        }
        return result;
    }

//for every group calculate the score of the group
    static List<String> groupDegree(List<String> group,HashMap<String,HashMap<String,String>> emergencies){
        /* Score
        * for photo in the incident +1.75
        * if the location of the incident is les than 5 km form the center then +1.75
        * if the location of the incident is between 5 km and 7 km form the center then +1.5
        * if the location of the incident is more than 7 km form the center then +1.25
        * if the time of the incident is less than 10 minutes from the first report then +2
        * if the time of the incident is more than 10 minutes from the first report then +1
        * */
        List<List<String>> score = new ArrayList<>();
        double fscore =0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        LocalDateTime date = LocalDateTime.parse(group.getLast(),formatter);
        for (int i=0; i<group.size()-3;i++) {
            List<String> temp1 = new ArrayList<>();
            double tscore =0;
            String s = group.get(i);
            temp1.add(s);
            HashMap<String,String> e = emergencies.get(s);

            if (!e.get("Photo").equals("-")){
                tscore += 1.75;
            }

            String loc = emergencies.get(s).get("Location");
            String[] temp = loc.split(",");
            double lat = Double.parseDouble(temp[0]);
            double lon = Double.parseDouble(temp[1]);
            double dist =Calculations.calculateDistance2(Double.parseDouble(group.get(group.size()-2)),Double.parseDouble(group.get(group.size()-3)),lat,lon);
            if (dist <= 5){
                tscore += 1.75;
            } else if (dist <= 7) {
                tscore += 1.5;
            }
            else {
                tscore += 1.25;
            }

            Duration duration = Duration.between(date,LocalDateTime.parse(e.get("Time"),formatter));
            if (duration.toSeconds() <= 600){//10 minutes
                tscore +=2;
            }
            else {
                tscore+=1;
            }
            temp1.add(String.valueOf(tscore));
            score.add(temp1);
            fscore += tscore;
        }
        //for every sorted group add in the end the location (lat,long), the date and the score
        List<String> sortedList = SortList.sortL(score);
        sortedList.add(group.get(group.size()-3));
        sortedList.add(group.get(group.size()-2));
        sortedList.add(date.format(formatter));
        sortedList.add(String.valueOf(fscore));
        return sortedList;
    }






}