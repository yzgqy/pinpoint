package com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.demo;

import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.ClassNode;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.Component;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MST;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MSTCluster;
import com.navercorp.pinpoint.web.statistics.data.DataSet2;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Auther: yaya
 * @Date: 2020/4/30 11:18
 * @Description:
 */
public class MSTmethod {
    public static void main(String[] args) {
//        动态调用
        String callsPathD="/Users/yaya/Desktop/data/3-包含跟多的类/dynamicClassCall.txt";
//        String callsPathD="/Users/yaya/Desktop/data/3-包含跟多的类/dynamicMethodCall.txt";
//        kmeans聚类结果
//        String clusterPath="/Users/yaya/Desktop/bs-project/data/jpetstore/A2-Step1-Cluster.txt";
        String clusterPath="/Users/yaya/Desktop/data/A2-Step1-Cluster.txt";
        DataSet2 dataSet = new DataSet2(callsPathD,clusterPath);
//        dataSet.printInde();
        List<GitCommitFileEdge> edges = dataSet.getCalls();
        Set<Component> components = new HashSet<>(MSTCluster.clusterWithSplit(MST.calcMST(edges), 99,4));
        System.out.println("components.size = " + components.size());
        for (Component cpt : components){
            System.out.println("*******************************one components "+ cpt.getNodes().size() +"******************************");
            for (ClassNode node: cpt.getNodes()){
                System.out.println(node.getClassName());
            }
        }
    }
}
