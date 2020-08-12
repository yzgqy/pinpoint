package com.navercorp.pinpoint.web.mytestOPAndBo;

import com.navercorp.pinpoint.web.statistics.FileUtil;
import org.springframework.asm.ClassReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2019/12/8 16:06
 * @Description:
 */
public class T1 {
    public static void main(String[] args) throws Exception{
        HashMap<String, Object> result = new HashMap<String, Object>();
        String path = "/Users/yaya/Desktop/bs-project/jpetstore-6/spring-jpetstore/target/classes";
        ArrayList<String> myfiles = new ArrayList<String>();
        traverseFolder2(path, myfiles);
        System.out.println("class文件数：" + myfiles.size());
        for (String file : myfiles) {
            if (file.endsWith(".class")) {
                InputStream inputstream = new FileInputStream(new File(file));
                ClassReader cr = new ClassReader(inputstream);
                ClassAdapter ca = new ClassAdapter();
                cr.accept(ca, ClassReader.EXPAND_FRAMES);
            }
        }
        System.out.println("++++++++++++++");
        System.out.println(ClassAdapter.methodNodes.size());
        System.out.println(MethodAdapter.methodEdges.size());
        System.out.println(ClassAdapter.classnoedes.size());
        System.out.println(MethodAdapter.edges.size());
        System.out.println("++++++++++++++");

        HashMap<String ,Classnode> classnodes =ClassAdapter.classnoedes;
        List<String> classData = new ArrayList<>();
        for (Map.Entry<String, Classnode> entry : classnodes.entrySet()) {
            classData.add(entry.getValue().getFullname().replace("/","."));
        }
        FileUtil.writeFile02(classData,"/Users/yaya/Desktop/bs-project/data/jpetstore/packagenames.txt");

        HashMap<String ,MethodNode> methodnodes =ClassAdapter.methodNodes;
        List<String> methodData = new ArrayList<>();
        for (Map.Entry<String, MethodNode> entry : methodnodes.entrySet()) {
            methodData.add(entry.getValue().getFullname().replace("/","."));
        }
        FileUtil.writeFile02(methodData,"/Users/yaya/Desktop/bs-project/data/jpetstore/staticMethodNode.txt");


        HashMap<String, MethodEdge> methodEdge = MethodAdapter.methodEdges;
        List<String> allMethodCalls = new ArrayList<>();
        for (Map.Entry<String, MethodEdge> entry : methodEdge.entrySet()) {
            MethodEdge edge = entry.getValue();
            String call = edge.getSource().replace("/",".")+"@@@"+edge.getTarget().replace("/",".")+"@@@"+edge.getWeight();
            allMethodCalls.add(call);
        }
        FileUtil.writeFile02(allMethodCalls,"/Users/yaya/Desktop/bs-project/data/jpetstore/staticMethodCall.txt");

        HashMap<String, Edge> classEdge = MethodAdapter.edges;
        List<String> allClassCalls = new ArrayList<>();
        for (Map.Entry<String, Edge> entry : classEdge.entrySet()) {
            Edge edge = entry.getValue();
            if(classnodes.containsKey(edge.getSource())&&classnodes.containsKey(edge.getTarget())) {
                String caller = edge.getSource().replace("/", ".");
                String callee = edge.getTarget().replace("/", ".");
                String call = caller + "@@@" + callee + "@@@" + edge.getWeight();
                allClassCalls.add(call);
            }else {
                System.out.println(edge.getSource()+"-->"+edge.getTarget());
            }
        }
        FileUtil.writeFile02(allClassCalls,"/Users/yaya/Desktop/bs-project/data/jpetstore/staticClassCall.txt");

//        List<String> allClassNames = new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
//        sb.append("[");
//        for (Map.Entry<String, Classnode> entry : classnodes.entrySet()) {
//            String line = entry.getValue().getFullname().replace("/",".");
//            System.out.println(line);
//            sb.append("\"");
//            sb.append(line);
//            sb.append("\",");
//        }
//        sb.append("]");
//        T1 t1=new T1();
//        allClassNames.add(sb.toString());
//        t1.writeFile02(allClassNames,"/Users/yaya/Desktop/allClass.txt");
    }

    public static void traverseFolder2(String path, ArrayList<String> myfiles) {

        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                // System.out.println("文件夹是空的!");
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        traverseFolder2(file2.getAbsolutePath(), myfiles);
                    } else {

                        if (file2.getName().endsWith(".class")) {
                            myfiles.add(file2.getAbsolutePath());
                            System.out.println("文件:" + file2.getAbsolutePath());
                        }

                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }

}
