package com.navercorp.pinpoint.web.statistics;

import com.navercorp.pinpoint.web.vo.callstacks.Record;
import com.navercorp.pinpoint.web.vo.callstacks.RecordSet;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @Auther: yaya
 * @Date: 2019/12/7 21:40
 * @Description:
 */
public class StatisticsOpsAndBos {

    private Set<String> packagenameSet = new HashSet<>();

    Map<String, Method> methodMap = new HashMap<>();
    Map<String, Call> callMap = new HashMap<>();

    public Map<String, Method> getMethodMap() {
        return methodMap;
    }

    public Map<String, Call> getCallMap() {
        return callMap;
    }

    public StatisticsOpsAndBos(List<String> packagenameList) {
        this.packagenameSet.addAll(packagenameList);
    }

    public void statisticsRecord (RecordSet recordSet){
        List<Record> recordList = recordSet.getRecordList();
        Map<Integer, Record> recordMap = new HashMap<>();
        for(Record record: recordList){
            recordMap.put(record.getId(),record);
        }

        for(int i =0 ;i<recordList.size();i++){
            Record record = recordList.get(i);

            //如果是执行sql的，寻找其父方法，将sql语句加入
            if(record.getTitle().equals("executeQuery()")||(record.getTitle().equals("execute()"))){
                Record parentRecord = getParentRecord(recordMap,record);
                if (parentRecord==null)
                    continue;
                getMethod(getMethodName(parentRecord),recordList.get(i+1).getArguments());
                continue;
            }

            //检测方法名是否为需要的方法
            if(!check(getClassName(record)))
                continue;
            getMethod(getMethodName(record),null);

            Record parentRecord = getParentRecord(recordMap,record);
            if (parentRecord==null)
                continue;
            this.call(record,parentRecord);
        }
    }

    // 查找父类（在包中的）
    private Record getParentRecord(Map<Integer, Record> recordMap,Record record){
        Record parentRecord = recordMap.get(record.getParentId());
        Boolean tag = check(getClassName(parentRecord));//调用方是否是需要找的类
        //调用方法不在需要找的类中，则
        while (!tag){
            if (parentRecord==null)
                break;
            parentRecord = recordMap.get(parentRecord.getParentId());
            if (parentRecord==null)
                break;
            tag = check(getClassName(parentRecord));
        }
        return parentRecord;
    }

    //想methodMap中添加method
    private Method getMethod(String api,String SQL){
        Method method = methodMap.get(api);
        if(method!=null) {
            if(SQL!=null)
                method.addSQL(SQL);
            return method;
        }
        method = new Method(getMethodName(api));
        if(SQL!=null)
            method.addSQL(SQL);
        methodMap.put(api, method);
        return method;
    }


    //在callMap中增加调用
    public void call(Record record, Record parentRecord){
        String api = record.getFullApiDescription();
        String parentApi = null;
        if (parentRecord!=null)
            parentApi = parentRecord.getFullApiDescription();

        addCall(api,parentApi);
    }
    //在callMap中增加调用，名字格式化
    public void addCall(String api, String parentApi){
        if(api==null)
            api = "";
        if(parentApi==null)
            parentApi = "";
        String[] list = api.split(":");
        if (list.length==1||list.length>=3)
            return;
        String[] list2 = parentApi.split(":");
        if (list2.length==1||list2.length>=3)
            return;
        api = list[0];
        parentApi = list2[0];
        getCall(api,parentApi).call();
    }
    //在callMap中增加调用，获取已有调用，没有就创建
    private Call getCall(String api, String parentApi){
        Call call = callMap.get(api+"&&"+parentApi);
        if(call!=null)
            return call;
        call = new Call(getMethod(api,null),getMethod(parentApi,null));
        callMap.put(api+"&&"+parentApi, call);
        return call;
    }

    //获取类名
    private String getClassName(Record record){
        String methodName = getMethodName(record);
        int bracketIndex = methodName.indexOf("(");
        if(bracketIndex!=-1){
            int dotIndex = methodName.lastIndexOf(".", bracketIndex);
            if(dotIndex!=-1) {
                return methodName.substring(0, dotIndex);
            }
        }
        return methodName;
    }

//    获取方法名
    private String getMethodName(Record record){
        if (record==null)
            return "";
        String api = record.getFullApiDescription();
        if (api==null)
            return "";
        String[] list = api.split(":");
        if (list.length==1||list.length>=3)
            return "";
        return list[0];
    }

    //检测该类名是否在包中
    private Boolean check(String className){
        if(this.packagenameSet.contains(className)){
            return true;
        }
        return false;
    }

    // 增加动态调用id
    public List<DynamicCallInfoOpsAndBos> allCallsForDynamicCallInfo(String analysisId){
        List<DynamicCallInfoOpsAndBos> list = new ArrayList<>();
        Map<String , Method> dynamicNodes = new HashMap<>();
        for (Map.Entry<String, Call> entry: callMap.entrySet()){
            list.add(entry.getValue().toDynamicCallInfoForClass(analysisId));
            Method caller = entry.getValue().getCaller();
            Method callee = entry.getValue().getCallee();
            String callerNodeName = caller.getName();
            String calleeNodeName = callee.getName();
            if(!dynamicNodes.containsKey(callerNodeName)){
                dynamicNodes.put(callerNodeName,caller);
            }
            if(!dynamicNodes.containsKey(calleeNodeName)){
                dynamicNodes.put(calleeNodeName,callee);
            }
        }

        return list;
    }

    // 增加动态调用id
    public DynamicInfo allCallsForDynamicInfo(String analysisId){
        List<DynamicCallInfoOpsAndBos> list = new ArrayList<>();
        Map<String , Method> dynamicNodes = new HashMap<>();
        for (Map.Entry<String, Call> entry: callMap.entrySet()){
            list.add(entry.getValue().toDynamicCallInfoForClass(analysisId));
            Method caller = entry.getValue().getCaller();
            Method callee = entry.getValue().getCallee();
            String callerNodeName = caller.getName();
            String calleeNodeName = callee.getName();
            if(!dynamicNodes.containsKey(callerNodeName)){
                dynamicNodes.put(callerNodeName,caller);
            }
            if(!dynamicNodes.containsKey(calleeNodeName)){
                dynamicNodes.put(calleeNodeName,callee);
            }
        }

        Set<Method> methods = new HashSet<>();
        for(Map.Entry<String , Method> entry:dynamicNodes.entrySet()){
            methods.add(entry.getValue());
        }

        return new DynamicInfo(list,methods);
    }

    //格式化打印
    public List<String> printDataClass(){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Call> entry: callMap.entrySet()){
            list.add(entry.getValue().getCaller().getClassName()+"@@@"+entry.getValue().getCallee().getClassName()
            +"@@@"+entry.getValue().getCount());
        }
        return list;
    }
    //格式化打印
    public List<String> printDataMethod(){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Call> entry: callMap.entrySet()){
            list.add(entry.getValue().getCaller().getName()+"@@@"+entry.getValue().getCallee().getName()
                    +"@@@"+entry.getValue().getCount());
        }
        return list;
    }

    //格式化打印
    public List<String> printData(){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Call> entry: callMap.entrySet()){
            list.add(entry.getValue().getCaller().getName()+"@@@"+entry.getValue().getCallee().getName()
                    +"@@@"+entry.getValue().getCount());
        }
        return list;
    }

    //获取方法名
    private String getMethodName(String fullName){
        String[] list = fullName.split("\\(");
        if(list.length!=2 || !list[1].endsWith(")"))
            return fullName;
        String[] list2 = list[1].substring(0,list[1].length()-1).split(", ");
        StringBuilder sb = new StringBuilder();
        for(String str: list2){
            String[] para = str.split(" ");
            sb.append(para[0]).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return list[0]+"("+sb+")";
    }

    public Map<String,Method> getAllClass(){
        Map<String,Method> classMap = new HashMap<>();
        for (Map.Entry<String, Method> entry: methodMap.entrySet()){
            String className = entry.getValue().getClassName();
            if(!classMap.containsKey(className)){
                classMap.put(className,entry.getValue());
            }else {
                Set<String> SQLs = entry.getValue().getSQLs();
                classMap.get(className).addSQLs(SQLs);
//                classMap.put(className,);
            }

        }
        return classMap;
    }
}
