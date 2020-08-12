package cn.edu.nju.software.pinpoint.statistics.entity;

import java.io.Serializable;
import java.util.Date;

public class StaticCallInfo implements Serializable {
    private String id;

    private String caller;

    private String callee;

    private Integer count;

    private String appid;

    private Date createdat;

    private Date updatedat;

    private Integer flag;

    private Integer type;

    private String desc;

    private Object callerObj;
    private Object calleeObj;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller == null ? null : caller.trim();
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee == null ? null : callee.trim();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
    }

    public Date getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Date createdat) {
        this.createdat = createdat;
    }

    public Date getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(Date updatedat) {
        this.updatedat = updatedat;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc == null ? null : desc.trim();
    }

    public Object getCallerObj() {
        return callerObj;
    }

    public void setCallerObj(Object callerObj) {
        this.callerObj = callerObj;
    }

    public Object getCalleeObj() {
        return calleeObj;
    }

    public void setCalleeObj(Object calleeObj) {
        this.calleeObj = calleeObj;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", caller=").append(caller);
        sb.append(", callee=").append(callee);
        sb.append(", count=").append(count);
        sb.append(", appid=").append(appid);
        sb.append(", createdat=").append(createdat);
        sb.append(", updatedat=").append(updatedat);
        sb.append(", flag=").append(flag);
        sb.append(", type=").append(type);
        sb.append(", desc=").append(desc);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}