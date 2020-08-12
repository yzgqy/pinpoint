package com.navercorp.pinpoint.web.statistics.algorithm.kmeans.test;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.EData;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.GraphUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.Kmeans;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2020/4/30 11:13
 * @Description:
 */
public class KmeansMethodTest {

    public static void main(String[] args) throws Exception {
//        String step1Path = "/Users/yaya/Desktop/data/MSTstep1-4.txt";
        String step1Path = "/Users/yaya/Desktop/data/MSTstep1-pet-1.txt";
        List<String> lines = null;
        try {
            lines = FileUtil.readFile02(step1Path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int clusterIndex = 0;
        Map<String ,Integer> clusterMap = new HashMap<>();
        Map<String,List<String>> indexClusterMap = new HashMap<>();
        for(String item : lines){
            if(item.equals("@")){
                clusterIndex++;
                continue;
            }
            clusterMap.put(item,clusterIndex);
            if(indexClusterMap.containsKey(String.valueOf(clusterIndex))){
                List<String> list = indexClusterMap.get(String.valueOf(clusterIndex));
                list.add(item);
                indexClusterMap.put(String.valueOf(clusterIndex),list);
            }else{
                List<String> list = new ArrayList<>();
                list.add(item);
                indexClusterMap.put(String.valueOf(clusterIndex),list);
            }
        }


        String path = "/Users/yaya/Desktop/data/3-包含跟多的类/dynamicNode-W.txt";
        Set<String> nodes = new HashSet<>();

        List<String> lines2 = null;
        try {
            lines2 = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<Node>> rs = new ArrayList<>();

        for(String line:lines2){
            String[] items = line.split("@@@");
            if(items.length==3) {
                List<Node> r = new ArrayList<>();
                System.out.println(items[0]);
                System.out.println(String.valueOf(clusterMap.get(items[0])));
                if(clusterMap.containsKey(items[0])) {
                    r.add(new Node(String.valueOf(clusterMap.get(items[0]))));
                    nodes.add(String.valueOf(clusterMap.get(items[0])));
                }else{
                    r.add(new Node(items[0]));
                    nodes.add(items[0]);
                }
                String[] table = items[2].split(",");
                for(int i=0;i<table.length;i++){
                    if(!table[i].isEmpty()){
                        String[] tableInfos = table[i].split("-");
                        nodes.add(tableInfos[0]);
                        r.add(new Node(tableInfos[0],Integer.valueOf(tableInfos[1]),1));
                    }
                }
                System.out.println(r);
                rs.add(r);
            }
        }

        KmeansMethodTest t = new KmeansMethodTest();
        t.getG2(nodes,rs);

    }

    public void getG2(Set<String> nodes,List<List<Node>> rs){

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


        for(List<Node> myr:rs){
            for(int i=0;i<myr.size();i++){
                for(int j=0;j<myr.size();j++){
                    if(i==0&&j>0) {
                        matrix[tbMap.get(myr.get(i).getName())][tbMap.get(myr.get(j).getName())]=matrix[tbMap.get(myr.get(i).getName())][tbMap.get(myr.get(j).getName())]+myr.get(j).getCount();
                    }
                    matrix[tbMap.get(myr.get(i).getName())][tbMap.get(myr.get(j).getName())]=matrix[tbMap.get(myr.get(i).getName())][tbMap.get(myr.get(j).getName())]+1;
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
//        String[] point = {"ACCOUNT", "ORDERS","PRODUCT"};
        String[] point = {"ACCOUNT", "ORDERS"};
//        String[] point = {"Cargo", "Voyage","Location"};
        Kmeans kmeans = new Kmeans(pG,2,point);
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
