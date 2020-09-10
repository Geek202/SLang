package me.geek.tom.slang.disassembler;

import me.geek.tom.slang.Helper;
import me.geek.tom.slang.output.OpcodeLookup;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class JavaClassDisassembler {

    public static void main(String[] args) throws IOException {
        ClassReader reader = new ClassReader(new BufferedInputStream(
                new FileInputStream(new File("Test.class"))));

        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        System.out.print(Helper.access(node.access) + " " + node.name + " extends "+ node.superName);
        if (node.interfaces.size() > 0) {
            System.out.println(" implements " + String.join(", ", node.interfaces));
        } else {
            System.out.println();
        }

        System.out.println("==============================");
        System.out.println("Fields:");
        for (FieldNode field : node.fields) {
            System.out.println("\t" + Helper.access(field.access) + " " + field.desc + " " + field.name + " (sig: " + field.signature + ")");
        }
        System.out.println("==============================");
        System.out.println("Methods:");
        for (MethodNode method : node.methods) {
            disassembleMethod(method);
        }
    }

    private static void disassembleMethod(MethodNode node) {
        System.out.println("\t" + Helper.access(node.access) + " " + node.name + node.desc + " (sig: " + node.signature + ")");
        for (AbstractInsnNode insn : node.instructions) {
            disassembleInsn(insn);
        }
    }

    private static void disassembleInsn(AbstractInsnNode node) {
        if (node.getOpcode() < 0) {
            String ending = "";
            if (node instanceof LabelNode)
                ending = " " + ((LabelNode) node).getLabel().toString();

            System.out.println("\t\t" + node.getClass().getSimpleName() + ending);
            return;
        }
        System.out.println("\t\t" + OpcodeLookup.OPCODE_TO_NAME_LOOKUP.get(node.getOpcode()) + " " + Helper.nodeDetails(node));
    }
}
