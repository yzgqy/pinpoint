package cn.edu.nju.software.pinpoint.statistics.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "`git`")
public class Git {

    @Id
    private String id;

    private String appId;

    private String path;

    private Date createDate;

    private Date updateDate;

    @Column(name = "`desc`")
    private String desc;

    private Integer logicCouplingFactor;

    private Integer modifyFrequencyFactor;

    private Integer reliabilityFactor;

    @Column(name = "`status`")
    private Integer status;

}
