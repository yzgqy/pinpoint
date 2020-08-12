package com.navercorp.pinpoint.web.statistics.algorithm.louvain;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import com.navercorp.pinpoint.web.statistics.algorithm.louvain.utils.LouvainUtil;
import com.navercorp.pinpoint.web.statistics.data.DataSet3;
import com.navercorp.pinpoint.web.statistics.data.DataSet4;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2020/2/4 23:36
 * @Description:
 */
public class TestBBB {

    public static void main(String[] args) throws Exception {
        String callsPath="/Users/yaya/Desktop/DS合并测试/calls.txt";
        String nodes="/Users/yaya/Desktop/DS合并测试/nodes.txt";
        DataSet4 dataSet3 = new DataSet4(callsPath,nodes);
        Map<String, Integer> nodeMap =dataSet3.getNodeMap();
        Map<String, GitCommitFileEdge>  edgeMap = dataSet3.getCalls();
        Map<Integer, String> indexMap = dataSet3.getIndexMap();
        List<String> map = new ArrayList<>();
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
