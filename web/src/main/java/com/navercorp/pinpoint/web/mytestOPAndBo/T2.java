package com.navercorp.pinpoint.web.mytestOPAndBo;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import org.apache.hadoop.hbase.shaded.org.apache.commons.configuration.tree.NodeAddData;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2020/1/5 17:16
 * @Description:
 */
public class T2 {

    private Map<String,Integer> nodeAndIndexMap = new HashMap<>();
    private Map<String,Integer> tableAndBoMap = new HashMap<>();

    public static void main(String[] args) {
        T2 t = new T2();
        t.mapBo();
        HashMap<String,List> data = t.mapNodeAndBo("/Users/yaya/Desktop/bs-project/data/dynamicNode.txt");
        List<String> calls = t.mapCall("/Users/yaya/Desktop/bs-project/data/staticMethodCall.txt");
        List nodes = data.get("nodes");
        List bos = data.get("bos");
        List allmap = data.get("allMaps");
        StringBuilder sb = new StringBuilder();
        for(Object item:nodes){
            sb.append(item);
            sb.append(",");
        }
        sb.append("@@@");
        for(Object item:bos){
            sb.append(item);
            sb.append(",");
        }
        sb.append("@@@");
        for(Object item:calls){
            sb.append(item);
            sb.append(",");
        }
        List<String> line = new ArrayList<>();
        line.add(sb.toString());

        List<String> nodeMapLine = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : t.nodeAndIndexMap.entrySet()){
            nodeMapLine.add(entry.getValue()+":"+entry.getKey());

        }
        try {
            FileUtil.writeFile02(line,"/Users/yaya/Desktop/bs-project/data/A1-nsgaIIData.txt");
            FileUtil.writeFile02(nodeMapLine,"/Users/yaya/Desktop/bs-project/data/A1-nodeMap.txt");
            FileUtil.writeFile02(allmap,"/Users/yaya/Desktop/bs-project/data/A1-step3-allMap.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    public void mapBo(){
        tableAndBoMap.put("visits",1);
        tableAndBoMap.put("specialties",2);
        tableAndBoMap.put("vets",2);
        tableAndBoMap.put("vet_specialties",2);
        tableAndBoMap.put("pets",3);
        tableAndBoMap.put("types",3);
        tableAndBoMap.put("owners",3);
    }

    //处理dynamicNode.txt文件
//    0,1,2,3,4,5,6,7,8,9,10,11,12,@@@1-,1-2-,1-,1-,1-,1-,1-2-,1-,1-,1-,1-,1-2-,1-,@@@12-1-205,5-8-2,4-1-268,2-6-60,4-11-268,5-1-2,
    public HashMap<String,List> mapNodeAndBo(String path){
        HashMap<String,List> data = new HashMap<>();
        List<Integer> nodeIndexs = new ArrayList<>();
        List<String> boIndexs = new ArrayList<>();
        List<String> allmap = new ArrayList<>();

        List<String> nodeList = null;
        try {
            nodeList = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int index = 0;
        for(String node:nodeList){
//            System.out.println(node);
            String[] items = node.split("@@@");
            String nodeName = items[1];
            if(items.length==3) {
                String tables = items[2];
                //node处理
                nodeAndIndexMap.put(nodeName, index);
                nodeIndexs.add(index);

                //bo处理
                StringBuilder sbTables = new StringBuilder();
                List<String> tableList = getTable(tables);
                Set<Integer> boSet = new HashSet<>();
                for (String table : tableList) {
                    sbTables.append(table);
                    sbTables.append(",");
                    boSet.add(tableAndBoMap.get(table));
                }
                List<Integer> boList = new ArrayList<>(boSet);
                StringBuilder sb = new StringBuilder();
                for (Integer bo : boList) {
                    sb.append(bo);
                    sb.append("-");
                }
                boIndexs.add(sb.toString());

                allmap.add(index+"@@@"+nodeName+"@@@"+sbTables.toString());
                index++;
            }

        }
        data.put("nodes",nodeIndexs);
        data.put("bos",boIndexs);
        data.put("allMaps",allmap);
        return data;
    }
//staticMethodCall.txt
    public List<String> mapCall(String path){

        List<String> calls = new ArrayList<>();
        List<String> callList = null;
        try {
            callList = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String call:callList){
            String[] items = call.split("@@@");
            String caller = items[0];
            String callee = items[1];
            String count = items[2];

            if(nodeAndIndexMap.containsKey(callee)&&nodeAndIndexMap.containsKey(caller)){
                calls.add(nodeAndIndexMap.get(callee)+"-"+nodeAndIndexMap.get(caller)+"-"+nodeAndIndexMap.get(count));
            }

        }

        return calls;
    }


    private List<String> getTable(String tables){
        List<String> data = new ArrayList<>();
        String[] items = tables.split(",");
        for(int i=0;i<items.length;i++){
            if(!items[i].isEmpty())
                data.add(items[i]);
        }
        return data;
    }
}
