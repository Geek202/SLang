package me.geek.tom.slang.disassembler;

import me.geek.tom.slang.Helper;
import me.geek.tom.slang.output.OpcodeLookup;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ClassParser {

    private final ClassNode classNode;

    public ClassParser() {
        classNode = new ClassNode();
    }

    public void load(File file) {
        try (InputStream is = new FileInputStream(file)) {
            new ClassReader(is).accept(classNode, 0);
        } catch (IOException e) {
            throw new RuntimeException("disassembling: " + file, e);
        }
    }

    public void disassemble(Consumer<String> output) {
        output.accept("c " + classNode.name + "\n");
        for (String a : Helper.accessList(classNode.access)) {
            output.accept("\ta " + a);
        }
        output.accept("\te " + classNode.superName);
        for (String iface :classNode.interfaces){
            output.accept("\ti " + iface);
        }
        for (FieldNode field : classNode.fields) {
            output.accept("\tf " + field.desc + " " + field.name);
        }
        for (MethodNode method : classNode.methods) {
            disassemble(method, output);
        }
        output.accept("ec");
    }

    private void disassemble(MethodNode method, Consumer<String> output) {
        output.accept("\tm " + method.name + " " + method.desc);

        for (String a : Helper.accessList(method.access)) {
            output.accept("\t\ta " + a);
        }

        int labelCount = 0;
        Map<Label, Integer> labels = new HashMap<>();
        for (AbstractInsnNode insn : method.instructions) {
            String start = "\t\ti " + OpcodeLookup.OPCODE_TO_NAME_LOOKUP.get(insn.getOpcode()) + " ";
            if (insn instanceof LdcInsnNode) {
                output.accept(start + Helper.ldcToString((LdcInsnNode) insn));
            } else if (insn instanceof FieldInsnNode) {
                FieldInsnNode fNode = (FieldInsnNode) insn;
                output.accept(start + fNode.owner + " " + fNode.name + " " + fNode.desc);
            } else if (insn instanceof MethodInsnNode) {
                MethodInsnNode mNode = (MethodInsnNode) insn;
                output.accept(start + mNode.owner + " " + mNode.name + " " + mNode.desc);
            } else if (insn instanceof TypeInsnNode) {
                output.accept(start + ((TypeInsnNode) insn).desc);
            } else if (insn instanceof VarInsnNode) {
                output.accept(start + ((VarInsnNode) insn).var);
            } else if (insn instanceof IntInsnNode) {
                output.accept(start + ((IntInsnNode) insn).operand);
            } else if (insn instanceof IincInsnNode) {
                output.accept(start + ((IincInsnNode) insn).var);
            } else if (insn instanceof LabelNode) {
                Label lbl = ((LabelNode) insn).getLabel();
                labels.put(lbl, labelCount);
                output.accept(start + labelCount);
                labelCount++;
            } else if (insn instanceof JumpInsnNode) {
                Label lbl = ((JumpInsnNode) insn).label.getLabel();
                if (labels.containsKey(lbl))
                    output.accept(start + labels.get(lbl));
                else {
                    output.accept(start + labelCount);
                    labels.put(lbl, labelCount);
                    labelCount++;
                }
            } else if (insn instanceof InsnNode) {
                output.accept("\t\t" + start.trim());
            }
        }

        output.accept("\tem");
    }
}
