package com.navercorp.pinpoint.web.statistics.algorithm.dfd;

import com.navercorp.pinpoint.web.statistics.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2020/5/5 19:36
 * @Description:
 */
public class Process {
    public static void main(String[] args) throws Exception{
        String callPath = "/Users/yaya/Desktop/data/3-包含跟多的类/dynamicClassCall.txt";
        List<String> calllines = null;
        try {
            calllines = FileUtil.readFile02(callPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String ,Integer> fan = new HashMap<>();

        for(String item :calllines){
            String[] call = item.split("@@@");
            String caller = call[0];
            String callee = call[1];
            int count = Integer.valueOf(call[2]);
            String key =  caller+"@@@"+callee;
            if(fan.containsKey(key)){
                int oldCount = fan.get(key);
                fan.put(key,oldCount+count);
            }else {
                fan.put(key,count);
            }
        }

        List<String> lines = new ArrayList<>();
        for(Map.Entry<String ,Integer> entry: fan.entrySet()){
            String line  = entry.getKey()+"@@@"+entry.getValue();
            lines.add(line);
        }
        FileUtil.writeFile02(lines,"/Users/yaya/Desktop/data/3-包含跟多的类/dynamicClassCall-2.txt");

    }
}
