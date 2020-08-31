package me.geek.tom.slang.disassembler;

import me.geek.tom.slang.output.CompilerHelper;
import me.geek.tom.slang.output.OpcodeLookup;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

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

        System.out.print(CompilerHelper.access(node.access) + " " + node.name + " extends "+ node.superName);
        if (node.interfaces.size() > 0) {
            System.out.println(" implements " + String.join(", ", node.interfaces));
        } else {
            System.out.println();
        }

        System.out.println("==============================");
        System.out.println("Fields:");
        for (FieldNode field : node.fields) {
            System.out.println("\t" + CompilerHelper.access(field.access) + " " + field.desc + " " + field.name + " (sig: " + field.signature + ")");
        }
        System.out.println("==============================");
        System.out.println("Methods:");
        for (MethodNode method : node.methods) {
            disassembleMethod(method);
        }
    }

    private static void disassembleMethod(MethodNode node) {
        System.out.println("\t" + CompilerHelper.access(node.access) + " " + node.name + node.desc + " (sig: " + node.signature + ")");
        for (AbstractInsnNode insn : node.instructions) {
            disassembleInsn(insn);
        }
    }

    private static void disassembleInsn(AbstractInsnNode node) {
        if (node.getOpcode() < 0) return;
        System.out.println("\t\t" + OpcodeLookup.OPCODE_TO_NAME_LOOKUP.get(node.getOpcode()) + " " + CompilerHelper.nodeDetails(node));
    }
}
