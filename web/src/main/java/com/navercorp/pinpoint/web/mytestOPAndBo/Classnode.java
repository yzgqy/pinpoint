package com.navercorp.pinpoint.web.mytestOPAndBo;

/**
 * @Auther: yaya
 * @Date: 2019/12/8 16:16
 * @Description:
 */
public class Classnode {

    private Integer id;


    private String name;

    private String fullname;

    private Integer calleetimes;

    private Integer callertimes;

    @Override
    public String toString() {
        return "Classnode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullname='" + fullname + '\'' +
                ", calleetimes=" + calleetimes +
                ", callertimes=" + callertimes +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setCalleetimes(Integer calleetimes) {
        this.calleetimes = calleetimes;
    }

    public void setCallertimes(Integer callertimes) {
        this.callertimes = callertimes;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullname() {
        return fullname.replace("/",".");
    }

    public Integer getCalleetimes() {
        return calleetimes;
    }

    public Integer getCallertimes() {
        return callertimes;
    }
}
