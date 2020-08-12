package com.navercorp.pinpoint.web.statistics.algorithm.louvain;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.louvain.utils.LouvainUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.InitialEdges;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.MST;
import com.navercorp.pinpoint.web.statistics.algorithm.mstcluster.PartitionInfoFromDB;
import com.navercorp.pinpoint.web.statistics.data.DataSet;
import com.navercorp.pinpoint.web.statistics.data.DataSet3;
import com.navercorp.pinpoint.web.statistics.git.GitDataUtil;
import com.navercorp.pinpoint.web.statistics.git.GitUtil;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;
import org.springframework.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2020/1/6 00:42
 * @Description:
 */
public class Test {


    public static void main(String[] args) throws Exception {
        String callsPath="/Users/yaya/Desktop/bs-project/data/jpetstore/dynamicClassCall.txt";
        String clusterPath="/Users/yaya/Desktop/bs-project/data/jpetstore/A2-Step1-Cluster.txt";
        String nodes="/Users/yaya/Desktop/bs-project/data/jpetstore/packagenames.txt";
        DataSet3 dataSet3 = new DataSet3(callsPath,clusterPath,nodes);
        Map<String, Integer> nodeMap =dataSet3.getNodeMap();
        Map<String, GitCommitFileEdge>  edgeMap = dataSet3.getCalls();
        Map<String, Integer> clusterResults = dataSet3.getClusterResults();
        Map<Integer, String> indexMap = dataSet3.getIndexMap();
        List<String> map = new ArrayList<>();
        for(Map.Entry<String, Integer> entry:clusterResults.entrySet()){
            map.add(entry.getKey()+"  "+entry.getValue());
        }
        FileUtil.writeFile02(map,"/Users/yaya/Desktop/map.txt");

        Map<String,Integer> nodePatitionMap = excutLouvain(nodeMap, edgeMap,indexMap);

    }

    /**
     * 执行louvain算法
     *
     * @param nodeMap
     * @param edgeMap
     * @throws Exception
     */
    public static Map<String,Integer> excutLouvain(Map<String, Integer> nodeMap, Map<String, GitCommitFileEdge> edgeMap,Map<Integer, String> indexMap) throws Exception {

        String edgePath = "/Users/yaya/Desktop/";
        String edgeFileName = System.currentTimeMillis() + "_edge.txt";
        FileUtil.creatFile(edgePath, edgeFileName);
        String filePath = edgePath + edgeFileName;
        List<String> lines = new ArrayList<>();
//        String count = (nodeMap.size() + 1) + " " + edgeMap.size();
        String count = nodeMap.size() + " " + edgeMap.size();
        lines.add(count);
        for (Map.Entry<String, GitCommitFileEdge> entry : edgeMap.entrySet()) {
            GitCommitFileEdge edge = entry.getValue();
            String source = edge.getSourceName();
            String target = edge.getTargetName();
            if(nodeMap.get(source)!=null&&nodeMap.get(target)!=null) {
                String line = nodeMap.get(source) + " " + nodeMap.get(target) + " " + edge.getCount();
                lines.add(line);
            }
        }
        FileUtil.writeFile02(lines, filePath);
        String outputPath = edgePath + System.currentTimeMillis() + "_louvain.txt";
        LouvainUtil.execute(filePath, outputPath);

        String classPath = System.currentTimeMillis() + "_community.txt";
        Map<String,Integer> nodePartitionMap = keyToClass(indexMap, outputPath, edgePath + classPath);
        return nodePartitionMap;
    }

    /**
     * key对应于classname输出
     *
     * @param inputPath
     * @param outputPath
     * @throws Exception
     */
    public static Map<String,Integer> keyToClass(Map<Integer, String> indexMap, String inputPath, String outputPath) throws Exception {
        Map<String,Integer> nodePartitionMap = new HashMap<>();

        List<String> readLines = FileUtil.readFile02(inputPath);
        List<String> writelines = new ArrayList<>();
        for (int i = 0; i < readLines.size(); i++) {
            String[] keys = readLines.get(i).split(" ");
            for (int j = 0; j < keys.length; j++) {
                String className = indexMap.get(Integer.valueOf(keys[j]));
                System.out.println(keys[j] + "    " + className);
                writelines.add(className);
                nodePartitionMap.put(className,i);
            }
            writelines.add(" ");
        }
        FileUtil.writeFile02(writelines, outputPath);
        return nodePartitionMap;
    }
}
