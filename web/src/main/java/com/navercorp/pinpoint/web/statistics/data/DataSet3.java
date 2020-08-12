package com.navercorp.pinpoint.web.statistics.data;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2020/1/6 01:02
 * @Description:
 */
public class DataSet3 {
    private  Map<String, GitCommitFileEdge>  calls = new HashMap<>();
    private Map<String, Integer> clusterResults = new HashMap<>();
    private Map<String, Integer> nodeMap = new HashMap<>();
    private Map<Integer, String> indexMap = new HashMap<>();

    public DataSet3(String callsPath,String clusterPath,String nodes){
        setClusterResults(clusterPath);
        setNodeMap(nodes);
        setCalls(callsPath);
    }

    public Map<String, GitCommitFileEdge> getCalls() {
        return calls;
    }

    public Map<String, Integer> getClusterResults() {
        return clusterResults;
    }

    public Map<String, Integer> getNodeMap() {
        return nodeMap;
    }

    public Map<Integer, String> getIndexMap() {
        return indexMap;
    }

    private void setNodeMap(String path){
        List<String> nodesLine = new ArrayList<>();
        try {
            nodesLine = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int index = 0;
        for(String node:nodesLine){
            if(clusterResults.containsKey(node)){
                int c = clusterResults.get(node);
                if(!nodeMap.containsKey(String.valueOf(c))){
                    nodeMap.put(String.valueOf(c), index);
                    indexMap.put(index, String.valueOf(c));
                    index++;
                }
            }else {
                nodeMap.put(node, index);
                indexMap.put(index, node);
                index++;
            }
//            System.out.println(index+"  "+node);
//            System.out.println(index+"  "+indexMap.get(index));

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
            String callee = infos[0].replace("/",".");
            String caller = infos[1].replace("/",".");
            if(clusterResults.containsKey(callee)&&clusterResults.containsKey(caller))
                continue;
            GitCommitFileEdge edge = new GitCommitFileEdge();
            if(clusterResults.containsKey(callee))
                edge.setSourceName(clusterResults.get(callee).toString());
            else
                edge.setSourceName(callee);
            if(clusterResults.containsKey(caller))
                edge.setTargetName(clusterResults.get(caller).toString());
            else
                edge.setTargetName(caller);
            edge.setCount(Integer.valueOf(infos[2]));
//            this.calls.add(edge);
            this.calls.put(edge.getSourceName()+"@@@"+edge.getTargetName(),edge);

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
            index++;
        }
    }
}
