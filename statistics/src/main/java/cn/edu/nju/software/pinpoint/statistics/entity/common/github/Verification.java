package cn.edu.nju.software.pinpoint.statistics.entity.common.github;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Verification {
    private boolean verified;
    private String reason;
    private String signature;
    private String payload;
}
