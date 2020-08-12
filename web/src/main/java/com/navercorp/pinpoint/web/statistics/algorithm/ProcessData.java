package com.navercorp.pinpoint.web.statistics.algorithm;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.kmeans.EData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2020/4/26 19:24
 * @Description:
 */
public class ProcessData {
    public static void main(String[] args) {
        List<String> data1Line = null;
        List<String> data2Line = null;
        try {
            data1Line = FileUtil.readFile02("/Users/yaya/Desktop/data/dynamicNode.txt");
            data2Line = FileUtil.readFile02("/Users/yaya/Desktop/data/dynamicMethodCall.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String ,Integer> nameIndexMap = new HashMap<>();
        Map<Integer ,String> indexNameMap = new HashMap<>();

        int index = 0;

        List<List<String>> groupRelationList = new ArrayList<>();
        for(String data1 :data1Line){
            String[] items = data1.split("@@@");
            if(items.length==3) {
                List<String> groupRelation = new ArrayList<>();
                groupRelation.add(items[1]);
                if(!nameIndexMap.containsKey(items[1])) {
                    nameIndexMap.put(items[1], index);
                    indexNameMap.put(index, items[1]);
                    index++;
                }

                String[] table = items[2].split(",");
                for(int i=0;i<table.length;i++){
                    if(!table[i].isEmpty()) {
                        groupRelation.add(table[i]);
                        if (!nameIndexMap.containsKey(table[i])) {
                            nameIndexMap.put(table[i], index);
                            indexNameMap.put(index, table[i]);
                            index++;
                        }
                    }
                }
                groupRelationList.add(groupRelation);
            }
        }

        for(String data2 :data2Line){
            String[] items = data2.split("@@@");
            if(!nameIndexMap.containsKey(items[0])) {
                nameIndexMap.put(items[0], index);
                indexNameMap.put(index, items[0]);
                index++;
            }

            if(!nameIndexMap.containsKey(items[1])) {
                nameIndexMap.put(items[1], index);
                indexNameMap.put(index, items[1]);
                index++;
            }

        }

        System.out.println("ok  "+index+"  "+nameIndexMap.size());

        double[][] matrix = new double[index][index];
        for(int i=0;i<index;i++){
            for(int j=0;j<index;j++){
                matrix[i][j] = 0;
            }
        }

        for(List<String> gr:groupRelationList){
            for(int i=0;i<gr.size();i++){
                for(int j=0;j<gr.size();j++){
                    if(nameIndexMap.get(gr.get(i))!=nameIndexMap.get(gr.get(j)))
                        matrix[nameIndexMap.get(gr.get(i))][nameIndexMap.get(gr.get(j))]=matrix[nameIndexMap.get(gr.get(i))][nameIndexMap.get(gr.get(j))]+1;
                }
            }
        }

        for(String data2 :data2Line) {
            String[] items = data2.split("@@@");
            if(nameIndexMap.get(items[0])!=nameIndexMap.get(items[1])){
                matrix[nameIndexMap.get(items[0])][nameIndexMap.get(items[1])] = matrix[nameIndexMap.get(items[0])][nameIndexMap.get(items[1])]+Integer.valueOf(items[2]);
                matrix[nameIndexMap.get(items[1])][nameIndexMap.get(items[0])] = matrix[nameIndexMap.get(items[1])][nameIndexMap.get(items[0])]+Integer.valueOf(items[2]);
            }
        }


        List<String> line = new ArrayList<>();
        for(int i = 0;i<matrix.length;i++){
            StringBuilder sb = new StringBuilder();
            for(int j=0;j<matrix.length;j++){
                if(matrix[i][j]==0){
                    matrix[i][j] = 1000;
                }else{
                    matrix[i][j] = 1.0/matrix[i][j];
                }
                System.out.print(matrix[i][j]+" ");

//                BigDecimal bg = new BigDecimal(matrix[i][j]);
//                double f1 = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                sb.append(matrix[i][j]);
//                sb.append(f1);
                if(j<matrix.length-1)
                    sb.append(",");
            }
            System.out.println();
            line.add(sb.toString());
        }

        List<String> nameLine = new ArrayList<>();
        for(Map.Entry<String,Integer> entry:nameIndexMap.entrySet()){
            nameLine.add(entry.getValue()+" "+entry.getKey());
        }
        try {
            FileUtil.writeFile02(line, "/Users/yaya/Desktop/data/matrix.txt");
            FileUtil.writeFile02(nameLine, "/Users/yaya/Desktop/data/indexName.txt");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
