package com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.demo;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.ClassNode;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.Component;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MST;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MSTCluster;
import com.navercorp.pinpoint.web.statistics.data.DataSet2;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Auther: yaya
 * @Date: 2020/5/4 07:16
 * @Description:
 */
public class MSTmethodStep1 {
    public static void main(String[] args) {
//        动态调用
        String callsPathD="/Users/yaya/Desktop/data/3-包含跟多的类/dynamicClassCall-2.txt";
        List<String> lines = null;
        try {
            lines = FileUtil.readFile02(callsPathD);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<String> nodeSet = new HashSet<>();
        for(String line :lines){
            String[] items = line.split("@@@");
            nodeSet.add(items[0]);
            nodeSet.add(items[1]);
         }

         for(String node :nodeSet){
             System.out.println(node);
         }

        DataSet2 dataSet = new DataSet2(callsPathD);
        List<GitCommitFileEdge> edges = dataSet.getCalls();
        Set<Component> components = new HashSet<>(MSTCluster.clusterWithSplit(MST.calcMST(edges), 10,4));
        System.out.println("components.size = " + components.size());
        for (Component cpt : components){
            System.out.println("*******************************one components "+ cpt.getNodes().size() +"******************************");
            for (ClassNode node: cpt.getNodes()){
                System.out.println(node.getClassName());
            }
        }
    }
}
