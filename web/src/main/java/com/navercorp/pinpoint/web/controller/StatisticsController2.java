package com.navercorp.pinpoint.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.util.TransactionId;
import com.navercorp.pinpoint.web.calltree.span.CallTreeIterator;
import com.navercorp.pinpoint.web.filter.Filter;
import com.navercorp.pinpoint.web.filter.FilterBuilder;
import com.navercorp.pinpoint.web.filter.FilterChain;
import com.navercorp.pinpoint.web.service.*;
import com.navercorp.pinpoint.web.statistics.DynamicInfo;
import com.navercorp.pinpoint.web.statistics.Method;
import com.navercorp.pinpoint.web.statistics.Statistics;
import com.navercorp.pinpoint.web.statistics.StatisticsOpsAndBos;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController2 {
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
    @RequestMapping(value = "/statistcsallcall2")
    @ResponseBody
    public JSONResult statistcsallcall(@RequestParam Map<String, String> requestParam) {
        //app的name
        String an = (String) requestParam.get("an");
        //筛选的类
        String pnListStr = requestParam.get("pnList");

        ObjectMapper MAPPER = new ObjectMapper();
        List<String> pnList=null;
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
//        Statistics statistics = new Statistics(pnList);
        StatisticsOpsAndBos statistics = new StatisticsOpsAndBos(pnList);
        for (int i=0; i<spanResultList.size(); i++){
            SpanResult spanResult = spanResultList.get(i);
            final CallTreeIterator callTreeIterator = spanResult.getCallTree();
            RecordSet recordSet = this.transactionInfoService.createRecordSet(callTreeIterator, dotList.get(i).getAcceptedTime(), dotList.get(i).getAgentId(), -1);
            statistics.statisticsRecord(recordSet);
        }

//        String mode = requestParam.get("mode");
//        if ("remote".equals(mode)){
//            String api= requestParam.get("api");
//            statistics.sendMethod(api);
//        }else {
//            statistics.saveClass();
//            statistics.saveMethod();
//        }


//        DynamicInfo dynamicInfo = statistics.allCallsForDynamicInfo(requestParam.get("analysisId"));
        DynamicInfo dynamicInfo = statistics.allCallsForDynamicInfo("-1");
        HashMap<String ,Object> result = new HashMap<>();
        result.put("calls",dynamicInfo.getDynamicCallInfoOpsAndBos());
        result.put("nodes",dynamicInfo.getMethods());
        result.put("flag",0);
        return JSONResult.ok(result);
    }

    @RequestMapping(value = "/statistics2/{page}")
    @ResponseBody
    public ModelAndView statistics(@PathVariable(value="page") String page, @RequestParam Map<String, String> requestParam) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(page);

        return modelAndView;
    }

    @RequestMapping(value = "/querystatistics2")
    @ResponseBody
    public List<Statistics.Call> queryStatistics(@RequestParam Map<String, String> requestParam) {
        List<Statistics.Call> callList = new ArrayList<>();
        try {
//            FileReader fr = new FileReader("E:\\workspace\\project\\pinpoint\\statics-method-com.hand.hap.txt");
            FileReader fr = new FileReader("/Users/yaya/Desktop/pinpoint/statics-method-com.hand.hap.txt");
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                String[] strArray = str.split(",");
                if (strArray.length!=3)
                    continue;
                Statistics statistics = new Statistics(new ArrayList<String>());
                Statistics.Call call = statistics.new Call( statistics.new Method(strArray[0]), statistics.new Method(strArray[1]), Long.parseLong(strArray[2]));
                callList.add(call);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return callList;
    }
}
