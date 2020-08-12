package com.navercorp.pinpoint.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.util.TransactionId;
import com.navercorp.pinpoint.web.calltree.span.CallTreeIterator;
import com.navercorp.pinpoint.web.filter.Filter;
import com.navercorp.pinpoint.web.filter.FilterBuilder;
import com.navercorp.pinpoint.web.filter.FilterChain;
import com.navercorp.pinpoint.web.service.*;
import com.navercorp.pinpoint.web.statistics.*;
import com.navercorp.pinpoint.web.vo.LimitedScanResult;
import com.navercorp.pinpoint.web.vo.callstacks.RecordSet;
import com.navercorp.pinpoint.web.vo.scatter.Dot;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2020/1/5 13:42
 * @Description:
 */
@Controller
public class StatisticsOpsAndBosController2 {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScatterChartService scatter;

    @Autowired
    private SpanService spanService;

    @Autowired
    private TransactionInfoService transactionInfoService;

    @Autowired
    private FilteredMapService filteredMapService;

    @Autowired
    private FilteredMapService flow;

    @Autowired
    private FilterBuilder filterBuilder;

    private static final String PREFIX_TRANSACTION_ID = "I";
    private static final String PREFIX_TIME = "T";
    private static final String PREFIX_RESPONSE_TIME = "R";

    /**
     * selected points from scatter chart data query
     *
     * @param requestParam
     * @return
     */
    @RequestMapping(value = "/statistcsPoAndBo2")
    @ResponseBody
    public JSONResult statistcsallcall(@RequestParam Map<String, String> requestParam) {
        String an = requestParam.get("an");//"Cargo"  "Petclinic"
        String path = requestParam.get("path");
        List<String> pnList = null;
        try {
            pnList = FileUtil.readFile02(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LimitedScanResult<List<TransactionId>> limitedScanResult = flow.selectTraceIdsFromApplicationTraceIndex(an);
        Filter filter = new FilterChain();
        List<Dot> dotList = scatter.selectScatterData(limitedScanResult.getScanData(),an,filter);
        List<SpanResult> spanResultList =  spanService.selectSpans(dotList);

        StatisticsOpsAndBos statistics = new StatisticsOpsAndBos(pnList);
        for (int i=0; i<spanResultList.size(); i++){
            SpanResult spanResult = spanResultList.get(i);
            final CallTreeIterator callTreeIterator = spanResult.getCallTree();
            RecordSet recordSet = this.transactionInfoService.createRecordSet(callTreeIterator, dotList.get(i).getAcceptedTime(), dotList.get(i).getAgentId(), -1);
            statistics.statisticsRecord(recordSet);
        }

        List list = statistics.allCallsForDynamicCallInfo(requestParam.get("analysisId"));
        toFile(statistics);

        DynamicInfo dynamicInfo = statistics.allCallsForDynamicInfo(requestParam.get("analysisId"));
        HashMap<String ,Object> result = new HashMap<>();
        result.put("calls",dynamicInfo.getDynamicCallInfoOpsAndBos());
        result.put("nodes",dynamicInfo.getMethods());
        result.put("flag",0);

        return JSONResult.ok(result);
    }

    public void toFile(StatisticsOpsAndBos statistics){
        List<String> printDataC = statistics.printDataClass();
        List<String> printDataM = statistics.printDataMethod();
        try {
            FileUtil.writeFile02(printDataC,"/Users/yaya/Desktop/data/dynamicClassCall.txt");
            FileUtil.writeFile02(printDataM,"/Users/yaya/Desktop/data/dynamicMethodCall.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String,String> countMap = new HashMap<>();
        for(String str:printDataM){
            String[] items = str.split("@@@");
            countMap.put(items[1],items[2]);
        }


        Map<String, Method> classMap = statistics.getMethodMap();
        List<String> resultDA = new ArrayList<>();
        List<String> resultNodeN = new ArrayList<>();
//        List<String> fkeyLine = new ArrayList<>();
        List<String> tablesLine = new ArrayList<>();
        Map<String, Set<String>> classTables=new HashMap<>();
        for (Map.Entry<String, Method> entry: classMap.entrySet()){
            String key = entry.getValue().getClassName();
            Set<String> value;
            if(classTables.containsKey(key))
                value = classTables.get(key);
            else value = new HashSet<>();

            StringBuilder sb = new StringBuilder();
            sb.append(entry.getValue().getClassName());
            sb.append("@@@");
            sb.append(entry.getValue().getName());
            sb.append("@@@");
            List<String> tables =entry.getValue().getTables();
            StringBuilder sbTbl = new StringBuilder();
            if(tables!=null) {
                for (String t : tables) {
                    sbTbl.append(t);
                    sbTbl.append(",");
                    value.add(t);
                }
            }
            if(tables!=null) {
                tablesLine.add(sbTbl.toString());
            }
            sb.append(sbTbl.toString());
//            List<String> fkeys =entry.getValue().getFkeys();
//            if(fkeys!=null) {
//                fkeyLine.addAll(fkeys);
//            }
            resultDA.add(sb.toString());
            classTables.put(key,value);

            if(!sb.toString().endsWith("@@@")){
                sb.append("@@@");
            }
            if(countMap.get(entry.getValue().getName())!=null){
                sb.append(countMap.get(entry.getValue().getName()));
            }else {
                sb.append("1");
            }
            resultNodeN.add(sb.toString());
        }

        List<String> classTlbs = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry: classTables.entrySet()){
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey());
            sb.append("@@@");
            Set<String> value = entry.getValue();
            for(String x:value){
                sb.append(x);
                sb.append(",");
            }
            classTlbs.add(sb.toString());
        }

        List<String> frequency = new ArrayList<>();
        for(String x:resultNodeN){
            String[] items = x.split("@@@");
            if(items.length == 4){
                int count = Integer.valueOf(items[3]);
                for(int i=0;i<count;i++){
                    frequency.add(x);
                }
            }

        }
        try {
            FileUtil.writeFile02(resultDA,"/Users/yaya/Desktop/data/dynamicNode.txt");
            FileUtil.writeFile02(classTlbs,"/Users/yaya/Desktop/data/dynamicClassTables.txt");
//            FileUtil.writeFile02(fkeyLine,"/Users/yaya/Desktop/data/dynamicFkey.txt");
            FileUtil.writeFile02(tablesLine,"/Users/yaya/Desktop/datadynamicTable.txt");
            FileUtil.writeFile02(frequency,"/Users/yaya/Desktop/data/dynamicNodeFrequency.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
