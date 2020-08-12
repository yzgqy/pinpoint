package com.navercorp.pinpoint.web.statistics.algorithm.kmeans.test;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.EData;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.GraphUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.Kmeans;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2020/5/4 07:19
 * @Description:
 */
public class KmeansStep2 {

    public static void main(String[] args) throws Exception {
        KmeansStep2 t = new KmeansStep2();
        t.getG2("/Users/yaya/Desktop/data/3-包含跟多的类/dynamicNode.txt");

    }

    public void getG2(String path){
        Set<String> nodes = new HashSet<>();

        List<String> lines = null;
        try {
            lines = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<String>> rs = new ArrayList<>();

        for(String line:lines){
            String[] items = line.split("@@@");
            if(items.length==3) {
                List<String> r = new ArrayList<>();
                r.add(items[1]);
                nodes.add(items[1]);
                String[] table = items[2].split(",");
                for(int i=0;i<table.length;i++){
                    if(!table[i].isEmpty()){
                        nodes.add(table[i]);
                        r.add(table[i]);
                    }
                }
                System.out.println(r);
                rs.add(r);
            }
        }

        Map<String,Integer> tbMap =new HashMap<>();
        Map<Integer,String> indexMap =new HashMap<>();
        int index = 0;
        for(String node:nodes){
            tbMap.put(node,index);
            indexMap.put(index,node);
            index++;
        }

        int[][]  matrix = new int[nodes.size()][nodes.size()];
        for(int i=0;i<nodes.size();i++){
            for(int j=0;j<nodes.size();j++){
                matrix[i][j] = 0;
            }
        }


        for(List<String> myr:rs){
            for(int i=0;i<myr.size();i++){
                for(int j=0;j<myr.size();j++){
                    matrix[tbMap.get(myr.get(i))][tbMap.get(myr.get(j))]=matrix[tbMap.get(myr.get(i))][tbMap.get(myr.get(j))]+1;
                }
            }
        }

        for(Map.Entry<String,Integer> entry:tbMap.entrySet()){
            System.out.println(entry.getKey()+"   "+entry.getValue());
        }
        for(int i=0;i<nodes.size();i++){
            for(int j=0;j<nodes.size();j++){
                System.out.print(matrix[i][j]);
                System.out.print(" ");
            }
            System.out.println(" ");
        }

        String[] vexs = new String[nodes.size()];
        index =0;
        for(String t:nodes){
            vexs[index] = t;
            index++;
        }
        List<EData> edgsList =new ArrayList<>();
        for(int i=0; i<nodes.size();i++){
            for(int j=0;j<nodes.size();j++){
                if(i==j)
                    continue;
                if(matrix[i][j]==0){
                    edgsList.add(new EData(indexMap.get(i),indexMap.get(j),Integer.MAX_VALUE));
//                    System.out.println("原weight："+matrix[i][j]+"   倒数："+Integer.MAX_VALUE);
                }else{
                    edgsList.add(new EData(indexMap.get(i),indexMap.get(j),1.0/matrix[i][j]));
//                    System.out.println("原weight："+matrix[i][j]+"   倒数："+1.0/matrix[i][j]);
                }
            }
        }

        EData[] edges = new EData[edgsList.size()];
        for(int i=0;i<edges.length;i++){
            edges[i] = edgsList.get(i);
        }

        GraphUtil pG;

        // 采用已有的"图"
        pG = new GraphUtil(vexs, edges);
//        String[] point = {"owners", "visits","vets"};
        String[] point = {"ACCOUNT", "ORDERS","PRODUCT"};
        Kmeans kmeans = new Kmeans(pG,3,point);
        List<Set<String>> graphs = kmeans.run();
        System.out.println("");
        System.out.println("打印结果：");
        int i = 1;
        List<String> clusters = new ArrayList<>();
        for(Set<String> graphUtil:graphs){
            System.out.println("第"+i+"类：");
            StringBuilder sb = new StringBuilder();
            for(String mypoint : graphUtil) {
                System.out.println(mypoint);
                sb.append(mypoint);
                sb.append(",");
            }
            i++;
            clusters.add(sb.toString());
        }

        try {
            FileUtil.writeFile02(clusters,"/Users/yaya/Desktop/data/A2-Step1-Cluster.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
