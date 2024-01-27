package com.john.smartalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
public class SmartAlertApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartAlertApplication.class, args);

       /* double lat1 = 38.022459910025994; // New York
        double lon1 = 23.728185264252634;
        double lat2 = 37.922459910025994; // Los Angeles
        double lon2 = 23.728185264252634;

        double haversineDistance = AppController.calculateDistance2(lat1, lon1, lat2, lon2);

        double expectedDistance = 12;
        System.out.println(Math.abs(haversineDistance - expectedDistance) );
        System.out.println(haversineDistance <=12);*/

    }


}



