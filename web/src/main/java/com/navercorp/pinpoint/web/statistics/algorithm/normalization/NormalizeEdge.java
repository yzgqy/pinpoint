package com.navercorp.pinpoint.web.statistics.algorithm.normalization;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class NormalizeEdge {
    private String sourceName;
    private String targetName;
    private double weight;    //int -> double

}
