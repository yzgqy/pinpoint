package com.navercorp.pinpoint.web.statistics.algorithm.dfd;

import com.navercorp.pinpoint.web.statistics.FileUtil;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2020/5/5 10:31
 * @Description:
 */
public class Evaluation {
    public static void main(String[] args) {
        String clusterPath = "/Users/yaya/Desktop/data/cuseter-pet-2.txt";
        String callPath = "/Users/yaya/Desktop/data/3-包含跟多的类/dynamicClassCall.txt";
        List<String> clusterlines = null;
        List<String> calllines = null;
        try {
            clusterlines = FileUtil.readFile02(clusterPath);
            calllines = FileUtil.readFile02(callPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = 0;
        Map<String,Integer> nodePartitionMap = new HashMap<>();
        Map<Integer,List<String>> partitionNodeMap = new HashMap<>();
        for(String item:clusterlines){
            if(item.equals("@"))
                index++;
            else{
                nodePartitionMap.put(item,index);
                if(partitionNodeMap.containsKey(index)){
                    List<String> nodes = partitionNodeMap.get(index);
                    nodes.add(item);
                    partitionNodeMap.put(index,nodes);
                }else{
                    List<String> nodes = new ArrayList<>();
                    nodes.add(item);
                    partitionNodeMap.put(index,nodes);
                }
            }
        }

        Set<String> calls = new HashSet<>();
        for(String item:calllines){
            String[] call = item.split("@@@");
            String caller = call[0];
            String callee = call[1];
            String count = call[2];
            String line =  caller+"@@@"+callee;
            calls.add(line);
        }

        Map<Integer, Set<String>> caMap =new HashMap<>();
        Map<Integer,Set<String>> ceMap =new HashMap<>();
        Map<Integer,Integer> relationNumMap =new HashMap<>();
//        Map<Integer,Integer> classNumMap =new HashMap<>();

        for(String item:calls){
            String[] call = item.split("@@@");
            String caller = call[0];
            String callee = call[1];

            Integer callerartitionId = nodePartitionMap.get(caller);
            Integer calleeartitionId = nodePartitionMap.get(callee);

//            if(callerartitionId!=null && calleeartitionId!=null){
                if(callerartitionId==calleeartitionId){
                    if(calleeartitionId!=null) {
                        if (relationNumMap.containsKey(callerartitionId)) {
                            Integer rNum = relationNumMap.get(callerartitionId);
                            relationNumMap.put(callerartitionId, rNum + 1);
                        } else {
                            relationNumMap.put(callerartitionId, 1);
                        }
                    }
                }else{

                    if(callerartitionId!=null) {
                        if (ceMap.containsKey(callerartitionId)) {
                            Set<String> ceSet = ceMap.get(callerartitionId);
                            ceSet.add(caller);
                            ceMap.put(callerartitionId, ceSet);
                        } else {
                            Set<String> ceSet = new HashSet<>();
                            ceSet.add(caller);
                            ceMap.put(callerartitionId, ceSet);
                        }
                    }

                    if(calleeartitionId!=null) {
                        if (caMap.containsKey(calleeartitionId)) {
                            Set<String> caSet = caMap.get(calleeartitionId);
                            caSet.add(callee);
                            caMap.put(calleeartitionId, caSet);
                        } else {
                            Set<String> caSet = new HashSet<>();
                            caSet.add(callee);
                            caMap.put(calleeartitionId, caSet);
                        }
                    }
                }
//            }
        }

        for(int i=0;i<partitionNodeMap.size();i++){
//            System.out.println("第"+i+"类：ce="+ceMap.get(i).size()+" , " +
//                    "ca="+caMap.get(i).size()+" , RC关系数="+relationNumMap.get(i)+
//                    " , RC类数="+partitionNodeMap.get(i).size());

            System.out.println("第"+i+"类");
            if(ceMap.containsKey(i)) {
                System.out.println("ce="+ceMap.get(i).size());
            }else {
                System.out.println("ce=0");
            }

            if(caMap.containsKey(i)) {
                System.out.println("ca="+caMap.get(i).size());
            }else {
                System.out.println("ca=0");
            }

            if(relationNumMap.containsKey(i)) {
                System.out.println("RC关系数="+relationNumMap.get(i));
            }else {
                System.out.println("RC关系数=0");
            }


            if(partitionNodeMap.containsKey(i)) {
                System.out.println("RC类数="+partitionNodeMap.get(i).size());
            }else {
                System.out.println("RC类数=0");
            }





        }



    }
}
