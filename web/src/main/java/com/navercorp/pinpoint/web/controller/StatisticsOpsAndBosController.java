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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2019/12/7 21:40
 * @Description:
 */
@Controller
public class StatisticsOpsAndBosController {
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
    @RequestMapping(value = "/statistcsPoAndBo")
    @ResponseBody
    public JSONResult statistcsallcall(@RequestParam Map<String, String> requestParam) {
        String an = requestParam.get("an");//"Cargo"  "Petclinic"
        String path = requestParam.get("path");
        String bopath = requestParam.get("boPath");
        List<String> pnList = null;
        List<String> boList = null;
        try {
            pnList = FileUtil.readFile02(path);
            boList = FileUtil.readFile02(bopath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //bo聚类结果
        Map<String,Integer> boMap = new HashMap<>();
        for(String bo:boList){
            String[] boInfo = bo.split("-");
            boMap.put(boInfo[0],Integer.valueOf(boInfo[1]));
        }

        StringBuilder sb = new StringBuilder();
        for(String line : pnList){
            sb.append(line);
        }
//        String pnListStr = requestParam.get("pnList");
        String pnListStr = sb.toString();
        ObjectMapper MAPPER = new ObjectMapper();
        try {
            pnList = MAPPER.readValue(pnListStr, List.class);
        } catch (IOException e) {
            e.printStackTrace();
            return JSONResult.errorMsg("pnList wrong for:"+pnListStr);
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
        HashMap<String ,Object> result = new HashMap<>();
        result.put("list",list);
        result.put("flag",0);

        FormatData formatData =new FormatData(statistics.getMethodMap(),statistics.getCallMap(),boMap);
        formatData.getFile("/Users/yaya/Desktop/ClusteringInformation.txt");
//        toFile(statistics);

        return JSONResult.ok(result);
    }

    public void toFile(StatisticsOpsAndBos statistics){
        List<String> printData = statistics.printData();
        try {
            FileUtil.writeFile02(printData,"/Users/yaya/Desktop/OPsAndBOs.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


        Map<String,Method> classMap = statistics.getMethodMap();
        List<String> allSQL = new ArrayList<>();
        List<String> classLine = new ArrayList<>();
        for (Map.Entry<String, Method> entry: classMap.entrySet()){
            classLine.add(entry.getValue().toString());
            allSQL.addAll(entry.getValue().getSQLs());
        }

        try {
            FileUtil.writeFile02(classLine,"/Users/yaya/Desktop/classAll.txt");
            FileUtil.writeFile02(allSQL,"/Users/yaya/Desktop/allSQL.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    @RequestMapping(value = "/statistics/{page}")
//    @ResponseBody
//    public ModelAndView statistics(@PathVariable(value="page") String page, @RequestParam Map<String, String> requestParam) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName(page);
//        return modelAndView;
//    }
//
//    @RequestMapping(value = "/querystatistics")
//    @ResponseBody
//    public List<Call> queryStatistics(@RequestParam Map<String, String> requestParam) {
//        List<Call> callList = new ArrayList<>();
//        try {
////            FileReader fr = new FileReader("E:\\workspace\\project\\pinpoint\\statics-method-com.hand.hap.txt");
//            FileReader fr = new FileReader("/Users/yaya/Desktop/pinpoint/statics-method-com.hand.hap.txt");
//            BufferedReader bf = new BufferedReader(fr);
//            String str;
//            // 按行读取字符串
//            while ((str = bf.readLine()) != null) {
//                String[] strArray = str.split(",");
//                if (strArray.length!=3)
//                    continue;
//                Call call = new Call( new Method(strArray[0]), new Method(strArray[1]), Long.parseLong(strArray[2]));
//                callList.add(call);
//            }
//            bf.close();
//            fr.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return callList;
//    }
}
