package com.navercorp.pinpoint.web.statistics.git;



import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitFileEdge;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitInfo;
import com.navercorp.pinpoint.web.statistics.git.entity.GitCommitRetn;

import java.io.IOException;
import java.util.*;

/**
 * git数据处理工具
 */
public class GitDataUtil {

    public static Map<String, GitCommitFileEdge> getCommitFileGraph(GitCommitRetn gitCommitRetn, String path) {
        Map<String, GitCommitFileEdge> gitCommitFileEdgeMap = new HashMap<String, GitCommitFileEdge>();
        List<GitCommitInfo> gitCommitInfos = gitCommitRetn.getGitCommitInfos();
        HashSet<String> nowFiles = new HashSet<String>();
        nowFiles.addAll(new GetAllFiles().getNowAllFiles(path));
//        Iterator<String> it = nowFiles.iterator();
//        while (it.hasNext()){
//            System.out.println(it.next());
//        }
        for (GitCommitInfo gitCommitInfo : gitCommitInfos) {
            Set<String> files = gitCommitInfo.getFiles();
            for (String file1 : files) {
                if (file1.endsWith(".java") && !file1.endsWith("Test.java")&& nowFiles.contains(file1))
                    for (String file2 : files) {
                        if (file2.endsWith(".java") && !file2.endsWith("Test.java") && nowFiles.contains(file2))
                            if (file1 != file2) {
                                String class1 = toClassName(file1);
                                String class2 = toClassName(file2);
                                String key = class1 + "-!-" + class2;
                                GitCommitFileEdge oldGitCommitFileEdge = gitCommitFileEdgeMap.get(key);
                                if (oldGitCommitFileEdge == null) {
                                    GitCommitFileEdge gitCommitFileEdge = new GitCommitFileEdge();
                                    gitCommitFileEdge.setCount(1);
                                    gitCommitFileEdge.setSourceName(class1);
                                    gitCommitFileEdge.setTargetName(class2);
                                    gitCommitFileEdgeMap.put(key, gitCommitFileEdge);

                                } else {
                                    int count = oldGitCommitFileEdge.getCount() + 1;
                                    oldGitCommitFileEdge.setCount(count);
                                    gitCommitFileEdgeMap.put(key, oldGitCommitFileEdge);
                                }
                            }
                    }
            }
        }
        return gitCommitFileEdgeMap;
    }

    //文件名转类名
    private static String toClassName(String fileName) {
        String className = "";
        if (fileName.endsWith(".java")) {
            int index = fileName.lastIndexOf(".");
            className = fileName.replace("/", ".").substring(0, index);
        }
        return className;
    }

    public static void main(String[] args) throws Exception {
        Map<String, GitCommitFileEdge> map = getCommitFileGraph(GitUtil.getLocalCommit("/Users/yaya/Desktop/test/"), "/Users/yaya/Desktop/test/");
        System.out.println(map.size());
        for (Map.Entry<String, GitCommitFileEdge> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }

    }

}
