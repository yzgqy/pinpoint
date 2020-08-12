package com.navercorp.pinpoint.web.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2019/12/7 22:04
 * @Description:
 */
public class Call {
    Method caller;
    Method callee;
    String id;
    Long count = 0l;
    boolean isSQL;

    public Call(Method callee, Method caller) {
        this.caller = caller;
        this.callee = callee;
        this.id = caller.getName()+";"+callee.getName();
        this.isSQL = callee.getSQLs()!=null || caller.getSQLs()!=null;
    }
    public Call(Method callee, Method caller, Long count) {
        this.caller = caller;
        this.callee = callee;
        this.id = caller.getName()+";"+callee.getName();
        this.count = count;
        this.isSQL = callee.getSQLs()!=null;
    }

    public String toClass(){return caller.getClassName() + "," + callee.getClassName() + "," + count;}

    public String toMethod(){return caller.getName() + "," + callee.getName() + "," + count;}

    public Map<String , Method> toDynamicNodeInfoForClass(String analysisId){
        Map<String , Method> dynamicNodes = new HashMap<>();
        dynamicNodes.put(this.caller.getName(),this.caller);
        dynamicNodes.put(this.callee.getName(),this.callee);
        return dynamicNodes;
    }
    public DynamicCallInfoOpsAndBos toDynamicCallInfoForClass(String analysisId){
        DynamicCallInfoOpsAndBos dynamicCallInfo = new DynamicCallInfoOpsAndBos();
        dynamicCallInfo.setDynamicanalysisinfoid(analysisId);
        dynamicCallInfo.setType(0);
        dynamicCallInfo.setCaller(caller.getName());
        dynamicCallInfo.setCallee(callee.getName());
        dynamicCallInfo.setCount(Math.toIntExact(count));
        dynamicCallInfo.setIsSQL(this.isSQL);
        return dynamicCallInfo;
    }

    public void call(){
        count++;
    }

    public Method getCaller() {
        return caller;
    }

    public Method getCallee() {
        return callee;
    }

    public String getId() {
        return id;
    }

    public Long getCount() {
        return count;
    }
}
