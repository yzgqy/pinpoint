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
 * @Date: 2020/2/4 23:37
 * @Description:
 */
public class DataSet4 {
    private Map<String, GitCommitFileEdge> calls = new HashMap<>();
//    private Map<String, Integer> clusterResults = new HashMap<>();
    private Map<String, Integer> nodeMap = new HashMap<>();
    private Map<Integer, String> indexMap = new HashMap<>();

    public DataSet4(String callsPath,String nodes){
        setNodeMap(nodes);
        setCalls(callsPath);
    }

    public Map<String, GitCommitFileEdge> getCalls() {
        return calls;
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
                nodeMap.put(node, index);
                indexMap.put(index, node);
                index++;
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
            GitCommitFileEdge edge = new GitCommitFileEdge();
            edge.setSourceName(callee);
            edge.setTargetName(caller);
            edge.setCount(Integer.valueOf(infos[2]));
//            this.calls.add(edge);
            this.calls.put(edge.getSourceName()+"@@@"+edge.getTargetName(),edge);

        }


    }


}
