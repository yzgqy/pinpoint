package com.navercorp.pinpoint.web.statistics.algorithm.kmeans;


import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.ClassNode;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.Component;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MST;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MSTCluster;
import com.navercorp.pinpoint.web.statistics.git.GitUtil;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@ToString

public class Initialization {

    //寻找中心点
    public static String[] findCenter(String path, int splitThreshold, int numServices) throws Exception{
//        Map<String, GitCommitFileEdge> map = getCommitFileGraph(GitUtil.getLocalCommit(path), path);
//
//        Set<Component> components = new HashSet<>(MSTCluster.clusterWithSplit(MST.calcMST(MST.getEdges(map)), splitThreshold,numServices));
//
//        List<String> list = new ArrayList<>();
//        for (Component cmp : components){
//            list.add(calcCenter(cmp));
//        }
//        String[] centerPoints = new String[list.size()];
//        for (int i=0; i<list.size(); i++){
//            String pth = new String(list.get(i));
//            String temp[] = pth.trim().split("\\.");
//            int t = temp.length;
//            centerPoints[i] = new String(temp[(t-3)]+"."+temp[(t-2)]+"."+temp[(t-1)]);
//        }
//        return centerPoints;
        return null;
    }

    //计算中心点
    private static String calcCenter(Component cmp){
        int max = 0;
        int flag = 0;
        List<ClassNode> nodes = cmp.getNodes();
        for (int i=0; i<nodes.size();i++){
            if(nodes.get(i).getDegree() > max){
                max = nodes.get(i).getDegree();
                flag = i;
            }
        }
        return nodes.get(flag).getClassName();
    }

}
