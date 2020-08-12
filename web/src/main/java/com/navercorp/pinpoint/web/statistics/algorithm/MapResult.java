package com.navercorp.pinpoint.web.statistics.algorithm;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import org.apache.hadoop.util.hash.Hash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2020/4/27 10:43
 * @Description:
 */
public class MapResult {
    public static void main(String[] args) {
        List<String> data1Line = null;
        List<String> data2Line = null;
        try {
            data1Line = FileUtil.readFile02("/Users/yaya/Desktop/data/cargo-2/result.txt");
            data2Line = FileUtil.readFile02("/Users/yaya/Desktop/data/cargo-2/indexName.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Integer ,String > indexNameMap = new HashMap<>();
        for(String mapLine:data2Line){
            String[] items = mapLine.split(" ");
            indexNameMap.put(Integer.valueOf(items[0]),items[1]);
        }

        List<Map> data = new ArrayList<>();
        for(String result :data1Line){
            Map<String ,List<String>> custers = new HashMap<>();
            String[] items = result.split(" ");
            for(int i=0; i<items.length;i++){
                if(custers.containsKey(items[i])){
                    List<String> custer = custers.get(items[i]);
                    custer.add(indexNameMap.get(i));
                    custers.put(items[i],custer);
                }else{
                    List<String> custer = new ArrayList<>();
                    custer.add(indexNameMap.get(i));
                    custers.put(items[i],custer);
                }

            }

            data.add(custers);
        }

        System.out.println("ok");
    }
}
