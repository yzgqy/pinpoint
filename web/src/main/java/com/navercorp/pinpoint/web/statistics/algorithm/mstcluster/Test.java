package com.navercorp.pinpoint.web.statistics.algorithm.mstcluster;

import com.navercorp.pinpoint.web.statistics.data.DataSet;
import com.navercorp.pinpoint.web.statistics.git.GitUtil;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.util.*;


public class Test {
    public static void main(String[] args) throws Exception{
        String nameMapPath="/Users/yaya/Desktop/bs-project/data/A1-step3-allMap.txt";
        String callsPath="/Users/yaya/Desktop/bs-project/data/staticMethodCall.txt";
        String clusterPath="/Users/yaya/Desktop/bs-project/data/A1-step2Cluster.txt";
        DataSet dataSet = new DataSet(nameMapPath,callsPath,clusterPath);
        List<GitCommitFileEdge> edges = dataSet.getCalls();
        Set<Component> components = new HashSet<>(MSTCluster.clusterWithSplit(MST.calcMST(edges), 1000,3));
        System.out.println("components.size = " + components.size());
        for (Component cpt : components){
            System.out.println("*******************************one components "+ cpt.getNodes().size() +"******************************");
            for (ClassNode node: cpt.getNodes()){
                System.out.println(node.getClassName());
            }
        }

    }
}
