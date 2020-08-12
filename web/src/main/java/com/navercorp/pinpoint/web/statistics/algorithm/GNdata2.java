package com.navercorp.pinpoint.web.statistics.algorithm;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.data.DataSet2;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2020/4/30 16:37
 * @Description:
 */
public class GNdata2 {

    public static void main(String[] args) {
        //        动态调用
        String callsPathD="/Users/yaya/Desktop/data/cargo-2/dynamicMethodCall.txt";
//        kmeans聚类结果
        String clusterPath="/Users/yaya/Desktop/data/A2-Step1-Cluster.txt";
        DataSet2 dataSet = new DataSet2(callsPathD,clusterPath);
        List<GitCommitFileEdge> edges = dataSet.getCalls();

        Map<String ,Integer> nameIndexMap = new HashMap<>();
        Map<Integer ,String> indexNameMap = new HashMap<>();

        int index = 0;

        for(GitCommitFileEdge edge:edges){
            if(!nameIndexMap.containsKey(edge.getSourceName())){
                nameIndexMap.put(edge.getSourceName(), index);
                indexNameMap.put(index, edge.getSourceName());
                index++;
            }

            if(!nameIndexMap.containsKey(edge.getTargetName())) {
                nameIndexMap.put(edge.getTargetName(), index);
                indexNameMap.put(index, edge.getTargetName());
                index++;
            }
        }

        System.out.println("ok  "+index+"  "+nameIndexMap.size());

        int[][] matrix = new int[index][index];
        for(int i=0;i<index;i++){
            for(int j=0;j<index;j++){
                matrix[i][j] = 0;
            }
        }


        for(GitCommitFileEdge edge:edges) {
//            String[] items = data2.split("@@@");
            if(nameIndexMap.get(edge.getSourceName())!=nameIndexMap.get(edge.getTargetName())){
                matrix[nameIndexMap.get(edge.getSourceName())][nameIndexMap.get(edge.getTargetName())] = matrix[nameIndexMap.get(edge.getSourceName())][nameIndexMap.get(edge.getTargetName())]+Integer.valueOf(edge.getCount());
            }
        }


        List<String> line = new ArrayList<>();
        for(int i = 0;i<matrix.length;i++){

            for(int j=0;j<matrix.length;j++){
                if(matrix[i][j]!=0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(i);
                    sb.append(",");
                    sb.append(j);
                    sb.append(",");
                    sb.append(matrix[i][j]);
                    line.add(sb.toString());
                }
            }
        }

        List<String> nameLine = new ArrayList<>();
        for(Map.Entry<String,Integer> entry:nameIndexMap.entrySet()){
            nameLine.add(entry.getValue()+" "+entry.getKey());
        }
        try {
            FileUtil.writeFile02(line, "/Users/yaya/Desktop/data/GN/graphData.txt");
            FileUtil.writeFile02(nameLine, "/Users/yaya/Desktop/data/GN/indexName.txt");
        }catch (Exception e){
            e.printStackTrace();
        }


    }


}
