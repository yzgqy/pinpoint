package com.navercorp.pinpoint.web.statistics.data;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2019/12/15 19:48
 * @Description:
 */
public class DataSet {
    private Map<String,String> nameMap = new HashMap<>();
    private List<List<String>> tables = new ArrayList<>();
    private List<GitCommitFileEdge> calls = new ArrayList<>();
    private Map<String, Integer> clusterResults = new HashMap<>();

    public DataSet(String nameMapPath,String callsPath,String clusterPath){
        setNameMap(nameMapPath);
        setClusterResults(clusterPath);
        setCalls(callsPath);

    }

    public Map<String, String> getNameMap() {
        return nameMap;
    }

    public List<GitCommitFileEdge> getCalls() {
        return calls;
    }

    private void setNameMap(String path){

        List<String> nameMapLine = new ArrayList<>();
        try {
            nameMapLine = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String node:nameMapLine){
            List<String> table = new ArrayList<>();
            String[] infos = node.split("@@@");
            this.nameMap.put(infos[0],infos[1]);
            String[] tableStr = infos[2].split(",");
            Collections.addAll(table,tableStr);
            System.out.println(table);
            this.tables.add(table);
        }
    }

    private void setCalls(String path){
        List<String> callsLine = new ArrayList<>();
        try {
            callsLine = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String call:callsLine){
            String[] infos = call.split("@@@");
            if(clusterResults.containsKey(infos[0])&&clusterResults.containsKey(infos[1]))
                continue;
            GitCommitFileEdge edge = new GitCommitFileEdge();
            if(clusterResults.containsKey(infos[0]))
                edge.setSourceName(clusterResults.get(infos[0]).toString());
            else
                edge.setSourceName(infos[0]);
            if(clusterResults.containsKey(infos[1]))
                edge.setTargetName(clusterResults.get(infos[1]).toString());
            else
                edge.setTargetName(infos[1]);
            edge.setCount(Integer.valueOf(infos[2]));
            this.calls.add(edge);

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
                this.clusterResults.put(this.nameMap.get(infos[i]),index);
            }
            index++;
        }
    }



}
