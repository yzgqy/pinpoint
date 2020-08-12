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
 * @Date: 2020/2/4 23:26
 * @Description:
 */
public class TestAAA {
    public static void main(String[] args) {
        List<GitCommitFileEdge> edges = new ArrayList<>();
        List<String> callsLine = new ArrayList<>();
        Set<String> nodes = new HashSet<>();
        try {
            callsLine = FileUtil.readFile02("/Users/yaya/Desktop/test.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String call:callsLine){

            String[] items =call.split("@@@");
            GitCommitFileEdge edge = new GitCommitFileEdge();
            edge.setSourceName(items[0]);
            edge.setTargetName(items[1]);
            edge.setCount(Integer.valueOf(items[2]));
            edges.add(edge);
            nodes.add(items[0]);
            nodes.add(items[1]);
        }

        System.out.println("===============================================================");
        for(String n:nodes){
            System.out.println(n);
        }
        System.out.println("===============================================================");
        Set<Component> components = new HashSet<>(MSTCluster.clusterWithSplit(MST.calcMST(edges), 1000,8));
        System.out.println("components.size = " + components.size());
        for (Component cpt : components){
            System.out.println("*******************************one components "+ cpt.getNodes().size() +"******************************");
            for (ClassNode node: cpt.getNodes()){
                System.out.println(node.getClassName());
            }
        }
    }
}
