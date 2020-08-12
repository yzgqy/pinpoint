package com.navercorp.pinpoint.web.statistics.data;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2020/1/6 00:24
 * @Description:
 */
public class DataSet2 {
    private Map<String,String> nameMap = new HashMap<>();
    private List<List<String>> tables = new ArrayList<>();
    private List<GitCommitFileEdge> calls = new ArrayList<>();
    private Map<String, Integer> clusterResults = new HashMap<>();
    private Map<Integer, String> indexMap = new HashMap<>();
    private Map<String,Integer> edgeMap = new HashMap<>();

    public DataSet2(String callsPath,String clusterPath){
        setClusterResults(clusterPath);
        setCalls2(callsPath);

    }

    public DataSet2(String callsPath){
//        setClusterResults(clusterPath);
        setCalls2(callsPath);

    }

    public DataSet2(String callsPathS,String callsPathD,String clusterPath){
        setClusterResults(clusterPath);
        setCalls2(callsPathS);
        setCalls2(callsPathD);

    }

    private void setCalls2(String path){
        List<String> callsLine = new ArrayList<>();
        try {
            callsLine = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String call:callsLine){
//            System.out.println("call:  "+call);
            String[] infos = call.split("@@@");
            String callee = infos[0].replace("/",".");
            String caller = infos[1].replace("/",".");

            String sourceName;
            String targetName;
            if(clusterResults.containsKey(callee)&&clusterResults.containsKey(caller))
                continue;

            if(clusterResults.containsKey(callee))
                sourceName = clusterResults.get(callee).toString();
            else
                sourceName = callee;
            if(clusterResults.containsKey(caller))
                targetName=clusterResults.get(caller).toString();
            else
                targetName=caller;

//            System.out.println("sourceName:  "+sourceName);
//            System.out.println("targetName:  "+targetName);
//            System.out.println("count:  "+infos[2]);

            if(sourceName.equals(targetName)) {
//                System.out.println("N");
                continue;
            }
//            System.out.println("Y");

            String key = sourceName+"@@@"+targetName;
            int newCount = Double.valueOf(infos[2]).intValue();
            if(edgeMap.containsKey(key)) {
//                System.out.println("+");
                int oldCount = edgeMap.get(key)+newCount;
                edgeMap.put(key,oldCount);
            }else {
                edgeMap.put(key,newCount);
            }
        }


    }


    public List<GitCommitFileEdge> getCalls() {
        for(Map.Entry<String,Integer> entry:edgeMap.entrySet()){
            String key = entry.getKey();
            String[] keyStrs = key.split("@@@");
            GitCommitFileEdge edge = new GitCommitFileEdge();
            edge.setSourceName(keyStrs[0]);
            edge.setTargetName(keyStrs[1]);
            edge.setCount(entry.getValue());
//            System.out.println(edge.getSourceName()+"@@@"+edge.getTargetName()+"@@@"+edge.getCount());
            calls.add(edge);
        }
        return calls;
    }

    public void printInde(){
        for(Map.Entry<Integer,String> entry:indexMap.entrySet()){
            System.out.println(entry.getKey()+":  "+entry.getValue());
        }
    }

    private void setClusterResults(String path){
        List<String> cluster = new ArrayList<>();
        try {
            cluster = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = 0;
        for(String item:cluster){
            String[] infos = item.split(",");
            for(int i=0;i<infos.length;i++){
                this.clusterResults.put(infos[i],index);
            }
            indexMap.put(index,item);
            index++;
        }
    }
}
