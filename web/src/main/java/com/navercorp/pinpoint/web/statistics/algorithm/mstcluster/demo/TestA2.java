package com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.demo;

import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.ClassNode;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.Component;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MST;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MSTCluster;
import com.navercorp.pinpoint.web.statistics.data.DataSet;
import com.navercorp.pinpoint.web.statistics.data.DataSet2;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Auther: yaya
 * @Date: 2020/1/6 00:22
 * @Description:
 */
public class TestA2 {
    public static void main(String[] args) {
//        String nameMapPath="/Users/yaya/Desktop/bs-project/data/A1-step3-allMap.txt";
//        动态调用
        String callsPathD="/Users/yaya/Desktop/data/cargo-2/dynamicClassCall.txt";
//        静态调用
        String callsPathS="/Users/yaya/Desktop/bs-project/data/jpetstore/staticClassCall.txt";
        String clusterPath="/Users/yaya/Desktop/bs-project/data/jpetstore/A2-Step1-Cluster.txt";
//        DataSet2 dataSet = new DataSet2(callsPathS,clusterPath);
        DataSet2 dataSet = new DataSet2(callsPathD,clusterPath);
//        DataSet2 dataSet = new DataSet2(callsPathS,callsPathD,clusterPath);
        dataSet.printInde();
        List<GitCommitFileEdge> edges = dataSet.getCalls();
        Set<Component> components = new HashSet<>(MSTCluster.clusterWithSplit(MST.calcMST(edges), 99,10));
        System.out.println("components.size = " + components.size());
        for (Component cpt : components){
            System.out.println("*******************************one components "+ cpt.getNodes().size() +"******************************");
            for (ClassNode node: cpt.getNodes()){
                System.out.println(node.getClassName());
            }
        }
    }
}
