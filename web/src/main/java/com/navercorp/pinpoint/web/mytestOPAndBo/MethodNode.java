package com.navercorp.pinpoint.web.mytestOPAndBo;

/**
 * @Auther: yaya
 * @Date: 2019/12/8 16:14
 * @Description:
 */
public class MethodNode {

    private Integer id;

    private String fullname;

    @Override
    public String toString() {
        return "MethodNode{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }
}
