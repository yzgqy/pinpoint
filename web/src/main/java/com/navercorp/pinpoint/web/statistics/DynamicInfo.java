package com.navercorp.pinpoint.web.statistics;

import java.util.List;
import java.util.Set;

/**
 * @Auther: yaya
 * @Date: 2020/2/19 14:17
 * @Description:
 */
public class DynamicInfo {
    private List<DynamicCallInfoOpsAndBos> dynamicCallInfoOpsAndBos;
    private Set<Method> methods;

    public List<DynamicCallInfoOpsAndBos> getDynamicCallInfoOpsAndBos() {
        return dynamicCallInfoOpsAndBos;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public void setDynamicCallInfoOpsAndBos(List<DynamicCallInfoOpsAndBos> dynamicCallInfoOpsAndBos) {
        this.dynamicCallInfoOpsAndBos = dynamicCallInfoOpsAndBos;
    }

    public void setMethods(Set<Method> methods) {
        this.methods = methods;
    }

    public DynamicInfo(List<DynamicCallInfoOpsAndBos> dynamicCallInfoOpsAndBos, Set<Method> methods) {
        this.dynamicCallInfoOpsAndBos = dynamicCallInfoOpsAndBos;
        this.methods = methods;
    }
}
