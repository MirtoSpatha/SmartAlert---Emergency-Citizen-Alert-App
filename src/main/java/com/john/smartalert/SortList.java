package com.john.smartalert;

import java.util.ArrayList;
import java.util.List;

public class SortList {

    public static List<String> sortL(List<List<String>> l){
        List<String> sortedLIst =new ArrayList<>();
        sortedLIst.add(l.get(0).get(0));
        for (int i =1; i<l.size();i++){
            double score = Double.parseDouble(l.get(i).get(1));
            int j = i-1;
            while (j>=0 && score > Double.parseDouble(l.get(j).get(1))){
                j--;
            }
            sortedLIst.add(j+1,l.get(i).get(0));
        }
        return sortedLIst;
    }

    public static List<List<String>> sortAllEmergencies(List<List<String>> groups){
        List<List<String>> sortedLIst =new ArrayList<>();
        sortedLIst.add(groups.get(0));
        for (int i = 1; i<groups.size(); i++){
            List<String> temp = groups.get(i);
            double score = Double.parseDouble(temp.get(temp.size()-1));
            int j = i-1;
            while (j >=0 && score > Double.parseDouble(sortedLIst.get(j).get(sortedLIst.get(j).size()-1))){
                j--;
            }
            sortedLIst.add(j+1,temp);
        }
        return sortedLIst;
    }
}
