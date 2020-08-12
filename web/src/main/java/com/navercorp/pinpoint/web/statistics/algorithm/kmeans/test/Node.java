package com.navercorp.pinpoint.web.statistics.algorithm.kmeans.test;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther: yaya
 * @Date: 2020/5/10 19:13
 * @Description:
 */
@Setter
@Getter
public class Node {
    private String name ;
    private int count;
    private int type; //0-类结点，1-表结点


    public Node(String name) {
        this.name = name;
        this.count = 1;
        this.type = 0;
    }

    public Node(String name, int count, int type) {
        this.name = name;
        this.count = count;
        this.type = type;
    }
}
