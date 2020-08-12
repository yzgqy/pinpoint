package com.navercorp.pinpoint.web.mytestOPAndBo;

/**
 * @Auther: yaya
 * @Date: 2019/12/8 16:19
 * @Description:
 */
public class MethodEdge {
    private Integer id;
    private Integer sourceid;
    private String source;
    private Integer targetid;
    private String target;
    private Integer weight;

    @Override
    public String toString() {
        return "MethodEdge{" +
                "id=" + id +
                ", sourceid=" + sourceid +
                ", source='" + source + '\'' +
                ", targetid=" + targetid +
                ", target='" + target + '\'' +
                ", weight=" + weight +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSourceid() {
        return sourceid;
    }

    public void setSourceid(Integer sourceid) {
        this.sourceid = sourceid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getTargetid() {
        return targetid;
    }

    public void setTargetid(Integer targetid) {
        this.targetid = targetid;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
