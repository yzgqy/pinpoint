package com.navercorp.pinpoint.web.statistics;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2019/12/15 10:15
 * @Description:
 */
public class FormatData {

    Map<String, Method> methodMap = new HashMap<>();//所有的方法结点
    Map<String, Call> callMap = new HashMap<>();//所有的调用关系

    //bo聚类结果
    Map<String,Integer> boMap = new HashMap<>();//表名->bo编号

    public FormatData(Map<String, Method> methodMap, Map<String, Call> callMap, Map<String, Integer> boMap) {
        this.methodMap = methodMap;
        this.callMap = callMap;
        this.boMap = boMap;
    }

    //可对应染色体长度
    public int getMethodNum(){
        int count = 0;
        for (Map.Entry<String, Method> entry: methodMap.entrySet()){
            if(check(entry.getValue()))
                count++;
        }
        System.out.println("个数："+count);
        return count;
    }

    //生成对应算法输入文件
//    0,1,2,3,4,5,6,7,8,@@@1,2,2,2,3,2,3,3,3,@@@
// 0-1-5,1-2-6,1-3-7,2-5-4,2-4-3,3-3-5,4-6-2,5-6-4,6-7-4,7-8-3,
    public void getFile(String path){
        int[] metIndex = new int[getMethodNum()];
        String[] boIndex = new String[getMethodNum()];
        int i =0;
        List<String> methodInfo = new ArrayList<>();
        for (Map.Entry<String, Method> entry: methodMap.entrySet()){
            List<String> list = new ArrayList<>(entry.getValue().getTables());
            if(check(entry.getValue())) {
                entry.getValue().setIndex(i);
                metIndex[i] = i;
                Set<Integer> bos = new HashSet<>();
                String tables = "";
                for(String tableName:list){
                    tables =tables+tableName+",";
                    bos.add(boMap.get(tableName));
                }
                String boItem = "";
                for(Integer bo :bos){
                    boItem = boItem+bo+"-";
                }
                boIndex[i] = boItem;
                for(String table:list){
                    String line  = table+"-"+boMap.get(table);
                    System.out.println(line);
                }

                String info = i+"@@@"+entry.getKey()+"@@@"+tables;
                methodInfo.add(info);
                System.out.println("-----");
                i++;
            }
        }

        StringBuilder sb = new StringBuilder();
        for(int j=0;j<metIndex.length;j++){
            sb.append(metIndex[j]);
            sb.append(",");
        }
        sb.append("@@@");
        for(int j=0;j<boIndex.length;j++){
            sb.append(boIndex[j]);
            sb.append(",");
        }
        sb.append("@@@");

        List<String> allCall = new ArrayList<>();
        for (Map.Entry<String, Call> entry: callMap.entrySet()){
            Call call = entry.getValue();
            String callStr = call.getCaller().getName()+"@@@"+call.getCallee().getName()+"@@@"+call.getCount();
            allCall.add(callStr);
            if(check(call.getCaller())&&check(call.callee)) {
                String callString = call.getCaller().getIndex() + "-" + call.getCallee().getIndex() + "-" + call.getCount();
                sb.append(callString);
                sb.append(",");
            }
        }

        List<String> line = new ArrayList<>();
        line.add(sb.toString());
        try {
            FileUtil.writeFile02(line,path);
            FileUtil.writeFile02(methodInfo,"/Users/yaya/Desktop/nameMap.txt");
            FileUtil.writeFile02(allCall,"/Users/yaya/Desktop/allCall.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        FileUtil.writeFile02(line,"/Users/yaya/Desktop/ClusteringInformation.txt");
    }



    public boolean check(Method method){
        List<String> list = new ArrayList<>(method.getTables());
        if(list.size()>0)
            return true;
        else return false;
    }





}
