package com.navercorp.pinpoint.web.statistics.algorithm.kmeans.test;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.MySQLUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.EData;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.GraphUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.Kmeans;

import java.io.IOException;
import java.util.*;

public class KmeansTest {

    public void getG1(String path){
        Set<String> tables = new HashSet<>();
        List<String> tline = null;
        try {
            tline = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String line:tline){
            String[] items = line.split(",");
            for(int i=0;i<items.length;i++){
                if(!items[i].isEmpty()){
                    tables.add(items[i]);
                }
            }
        }

        Map<String,Integer> tbMap =new HashMap<>();
        Map<Integer,String> indexMap =new HashMap<>();
        int index = 0;
        for(String tb:tables){
            tbMap.put(tb,index);
            indexMap.put(index,tb);
            index++;
        }

        int[][]  matrix = new int[tables.size()][tables.size()];
        for(int i=0;i<tables.size();i++){
            for(int j=0;j<tables.size();j++){
                matrix[i][j] = 0;
            }
        }

        for(String line:tline){
            String[] items = line.split(",");
            for(int i=0;i<items.length;i++){
                if(!items[i].isEmpty()){
                    for(int j=0;j<items.length;j++){
                        if(!items[j].isEmpty()){
                            matrix[tbMap.get(items[i])][tbMap.get(items[j])]=matrix[tbMap.get(items[i])][tbMap.get(items[j])]+1;
                        }
                    }
                }
            }
        }

        for(Map.Entry<String,Integer> entry:tbMap.entrySet()){
            System.out.println(entry.getKey()+"   "+entry.getValue());
        }
        for(int i=0;i<tables.size();i++){
            for(int j=0;j<tables.size();j++){
                System.out.print(matrix[i][j]);
                System.out.print(" ");
            }
            System.out.println(" ");
        }
        String[] vexs = new String[tables.size()];
        index =0;
        for(String t:tables){
            vexs[index] = t;
            index++;
        }
        List<EData> edgsList =new ArrayList<>();
        for(int i=0; i<tables.size();i++){
            for(int j=0;j<tables.size();j++){
//                System.out.print(matrix[i][j]);
//                System.out.print(" ");
//                if(matrix[i][j] == 0)
//                    continue;
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
//            System.out.println(" ");
        }

        EData[] edges = new EData[edgsList.size()];
        for(int i=0;i<edges.length;i++){
            edges[i] = edgsList.get(i);
        }

        GraphUtil pG;

        // 采用已有的"图"
        pG = new GraphUtil(vexs, edges);
        String[] point = {"ACCOUNT", "ORDERS","PRODUCT"};
        Kmeans kmeans = new Kmeans(pG,3,point);
        List<Set<String>> graphs = kmeans.run();
        System.out.println("");
        System.out.println("打印结果：");
        int i = 1;
        for(Set<String> graphUtil:graphs){
            System.out.println("第"+i+"类：");
            for(String mypoint : graphUtil)
                System.out.println(mypoint);
            i++;
        }

    }
    public static void main(String[] args) throws Exception {
        KmeansTest t = new KmeansTest();
//        t.getG1("/Users/yaya/Desktop/bs-project/data/jpetstore/dynamicTable.txt");
        t.getG2("/Users/yaya/Desktop/data/3-包含跟多的类/dynamicNode.txt");
//        t.getG2("/Users/yaya/Desktop/data/1/dynamicNode.txt");


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
                r.add(items[0]);
                nodes.add(items[0]);
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

//        MySQLUtil mySQLUtil = new MySQLUtil();
//        List<List<String>> fkys = mySQLUtil.getForeignKeyFromDDL("/Users/yaya/Desktop/bs-project/jpetstore-6/spring-jpetstore/src/main/resources/database/H2-schema.sql");
//        for (List<String> list:fkys){
//            for (String t:list)
//                nodes.add(t);
//        }
//        rs.addAll(fkys);

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
            FileUtil.writeFile02(clusters,"/Users/yaya/Desktop/bs-project/data/jpetstore/A2-Step1-Cluster.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
