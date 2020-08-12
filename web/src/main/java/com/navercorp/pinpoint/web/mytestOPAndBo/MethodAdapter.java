package com.navercorp.pinpoint.web.mytestOPAndBo;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;

import java.util.HashMap;

/**
 * @Auther: yaya
 * @Date: 2019/12/8 16:15
 * @Description:
 */
public class MethodAdapter extends MethodVisitor implements Opcodes {
    protected String className = null;
    protected int access = -1;
    protected String name = null;
    protected String desc = null;
    protected String signature = null;
    protected String[] exceptions = null;

    public static HashMap<String, Edge> edges = new HashMap<String, Edge>();

    public static HashMap<String, MethodEdge> methodEdges = new HashMap<String, MethodEdge>();



    public MethodAdapter(final MethodVisitor mv, final String className, final int access, final String name,
                         final String desc, final String signature, final String[] exceptions) {
        // super(ASM5, mv, access, name, desc);
        super(ASM6, mv);
        this.className = className;
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;

    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        System.out.println(this.className + " " + this.name + "   call  " + owner + " " + name);
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }

        if (!owner.equals(this.className)) {
            // Classnode sourcenode = classnoedes.get(this.className);
            // Classnode targetnode = classnoedes.get(owner);
            String edgeKey = this.className + "_" + owner;
            Edge myedge = edges.get(edgeKey);
            if (myedge != null) {
                int weight = myedge.getWeight();
                myedge.setWeight(weight + 1);
                edges.put(edgeKey, myedge);
            } else {
                Edge newedge = new Edge();
                newedge.setSource(this.className);
                newedge.setTarget(owner);
                newedge.setWeight(1);
                edges.put(edgeKey, newedge);
            }
        }

//        String sourceMethodName = this.className+"."+this.name+this.desc;
        String sourceMethodName = (this.className+"."+this.name+this.desc).replace("/",".")
                .replace(";",",")
                .replace("(L","(")
                .replace(",)",")")
                .replace(",L",",");
        int index1 = sourceMethodName.lastIndexOf(")");
        sourceMethodName = sourceMethodName.substring(0,index1+1);

//        String targetMethodName = owner+"."+name+desc;
        String targetMethodName = (owner+"."+name+desc).replace("/",".")
                .replace(";",",")
                .replace("(L","(")
                .replace(",)",")")
                .replace(",L",",");
        int index2 = targetMethodName.lastIndexOf(")");
        targetMethodName = targetMethodName.substring(0,index2+1);

        if (!sourceMethodName.equals(targetMethodName)) {
            String edgeKey = sourceMethodName+ "_" + targetMethodName;

            MethodEdge myedge = methodEdges.get(edgeKey);
            if (myedge != null) {
                int weight = myedge.getWeight();
                myedge.setWeight(weight + 1);
                methodEdges.put(edgeKey, myedge);
            } else {
                MethodEdge newedge = new MethodEdge();
                newedge.setSource(sourceMethodName);
                newedge.setTarget(targetMethodName);
                newedge.setWeight(1);
                methodEdges.put(edgeKey, newedge);
            }

        }
    }
}