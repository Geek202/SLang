package me.geek.tom.slang;

import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class Helper {

    public static MethodNode createVoidMethod(int access, String name, String descriptor,
                                          String signature, String[] exceptions, Consumer<MethodNode> compiler) {
        MethodNode node = new MethodNode(access, name, descriptor, signature, exceptions);
        compiler.accept(node);
        node.instructions.add(new InsnNode(RETURN));
        return node;
    }

    /**
     * Constructs a Java access string from an access int
     * @param access The access int
     * @return The Java string representation.
     */
    public static List<String> accessList(int access) {
        List<String> builder = new ArrayList<>();

        if ((access & ACC_PRIVATE) != 0)
            builder.add("private");
        if ((access & ACC_PROTECTED) != 0)
            builder.add("protected");
        if ((access & ACC_PUBLIC) != 0)
            builder.add("public");
        if ((access & ACC_STATIC) != 0)
            builder.add("static");
        if ((access & ACC_ABSTRACT) != 0)
            builder.add("abstract");
        if ((access & ACC_FINAL) != 0)
            builder.add("final");
        if ((access & ACC_NATIVE) != 0)
            builder.add("native");

        return builder;
    }

    public static String access(int access) {
        return String.join(" ", accessList(access));
    }

    public static String nodeDetails(AbstractInsnNode node) {
        if (node instanceof MethodInsnNode) {
            return ((MethodInsnNode) node).name + ((MethodInsnNode) node).desc + " on " + ((MethodInsnNode) node).owner;
        } else if (node instanceof VarInsnNode) {
            return "" + ((VarInsnNode) node).var;
        } else if (node instanceof FieldInsnNode) {
            FieldInsnNode insn = (FieldInsnNode) node;
            return insn.desc + " " + insn.name + " on " + insn.owner;
        } else if (node instanceof LdcInsnNode) {
            return String.valueOf(((LdcInsnNode) node).cst);
        } else if (node instanceof InsnNode) {
            return "";
        }
        return node.getClass().getSimpleName();
    }

    public static String ldcToString(LdcInsnNode insn) {
        if (insn.cst instanceof String)
            return "s " + insn.cst;
        if (insn.cst instanceof Float)
            return "f " + insn.cst;
        if (insn.cst instanceof Integer)
            return "i "+ insn.cst;
        return "<invalid instruction>";
    }
}
