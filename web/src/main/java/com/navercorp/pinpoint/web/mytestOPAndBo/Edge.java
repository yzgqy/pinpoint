package com.navercorp.pinpoint.web.mytestOPAndBo;

/**
 * @Auther: yaya
 * @Date: 2019/12/8 16:20
 * @Description:
 */
public class Edge {
    private  String source;
    private String target;
    private Integer id;
    private Integer sourceid;
    private Integer targetid;
    private Integer weight;

    @Override
    public String toString() {
        return "Edge{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", id=" + id +
                ", sourceid=" + sourceid +
                ", targetid=" + targetid +
                ", weight=" + weight +
                '}';
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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

    public Integer getTargetid() {
        return targetid;
    }

    public void setTargetid(Integer targetid) {
        this.targetid = targetid;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
