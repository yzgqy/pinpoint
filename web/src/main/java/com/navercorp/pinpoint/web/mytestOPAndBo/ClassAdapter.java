package com.navercorp.pinpoint.web.mytestOPAndBo;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;

import java.util.HashMap;

/**
 * @Auther: yaya
 * @Date: 2019/12/8 16:07
 * @Description:
 */
public class ClassAdapter extends ClassVisitor implements Opcodes {

    private String owner;
    private boolean isInterface;

    public static HashMap<String ,Classnode> classnoedes = new HashMap<String ,Classnode>();
    public static HashMap<String, MethodNode> methodNodes = new HashMap<String, MethodNode>();

    public ClassAdapter() {
        super(ASM6);
    }

    // 该方法是当扫描类时第一个拜访的方法，主要用于类声明使用：visit( 类版本 ,修饰符 , 类名 , 泛型信息 , 继承的父类 , 实现的接口)
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (cv != null) {
            cv.visit(version, access, name, signature, superName, interfaces);

        }
        owner = name;
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        System.out.println(name);
        Classnode cnode = classnoedes.get(name);
//		List<Classnode> cnode = cm.selectByfullname(name);
        if (cnode == null) {
            Classnode cnode1 = new Classnode();
            cnode1.setName(name);
            cnode1.setFullname(name);
//			cnode1.setCalleetimes(1);
//			cm.insert(cnode1);
            classnoedes.put(name, cnode1);
        }
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
                                     final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (!isInterface) {
            mv = new MethodAdapter(mv, owner, access, name, desc, signature, exceptions);
        }

        String MethodName = (this.owner+"."+name+desc)
                .replace("/",".")
                .replace(";",",")
                .replace("(L","(")
                .replace(",)",")")
                .replace(",L",",");
        int index = MethodName.lastIndexOf(")");
        MethodName = MethodName.substring(0,index+1);

        MethodNode sourceMethodNode = new MethodNode();
        sourceMethodNode.setFullname(MethodName);
        methodNodes.put(MethodName,sourceMethodNode);

        return mv;
    }
}