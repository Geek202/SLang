package me.geek.tom.slang.output;

import org.apache.commons.lang.StringEscapeUtils;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class InstructionCompiler {

    public static AbstractInsnNode compileInsn(String insn, Map<Integer, LabelNode> labels) throws IOException {
        String[] split = insn.split(" ", 2);
        String opcodeName = split[0];
        int opcode = OpcodeLookup.NAME_TO_OPCODE_LOOKUP.get(opcodeName);

        switch (opcode) {
            case GETSTATIC:
            case PUTSTATIC:
            case GETFIELD:
            case PUTFIELD:
                return compileFieldInsn(opcode, split[1].split(" "), insn);
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case INVOKEINTERFACE:
                return compileMethodInsn(opcode, split[1].split(" "), insn);
            case NEW:
            case ANEWARRAY:
            case CHECKCAST:
            case INSTANCEOF:
                return compileTypeInsn(opcode, split[1]);
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
            case RET:
                return compileVarInsn(opcode, split[1], insn);
            case BIPUSH:
            case SIPUSH:
            case NEWARRAY:
                return compileIntInsn(opcode, split[1], insn);
            case LDC:
                return compileLdc(split[1].split(" ", 2));
            case IINC:
                return compileIinc(split[1].split(" "), insn);
            case -1:
                return compileLabelInsn(insn, split[1], labels);
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case GOTO:
            case JSR:
            case IFNULL:
            case IFNONNULL:
                return compileJumpInsn(insn, opcode, split[1], labels);
            case RETURN:
                return new InsnNode(opcode);
            default: {
                return new InsnNode(opcode);
            }
        }
    }

    private static AbstractInsnNode compileJumpInsn(String line, int opcode, String name, Map<Integer, LabelNode> labels) throws IOException {
        try {
            int id = Integer.parseInt(name);
            LabelNode lbl = labels.computeIfAbsent(id, __ -> new LabelNode());
            return new JumpInsnNode(opcode, lbl);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number on line: " + line);
        }
    }

    private static AbstractInsnNode compileLabelInsn(String line, String name, Map<Integer, LabelNode> labels) throws IOException {
        try {
            int id = Integer.parseInt(name);
            return labels.computeIfAbsent(id, __ -> new LabelNode());
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number on line: " + line);
        }
    }

    private static AbstractInsnNode compileIntInsn(int opcode, String num, String line) throws IOException {
        try {
            int val = Integer.parseInt(num);
            return new IntInsnNode(opcode, val);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number on line: " + line);
        }
    }

    private static AbstractInsnNode compileIinc(String[] pts, String line) throws IOException {
        if (pts.length != 2) {
            throw new IOException("Invalid method instruction: " + line);
        }
        try {
            int var = Integer.parseInt(pts[0]);
            int incr = Integer.parseInt(pts[1]);
            return new IincInsnNode(var, incr);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number on line: " + line);
        }
    }

    private static AbstractInsnNode compileVarInsn(int opcode, String v, String line) throws IOException {
        try {
            int var = Integer.parseInt(v);
            return new VarInsnNode(opcode, var);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number on line: " + line);
        }
    }

    private static AbstractInsnNode compileTypeInsn(int opcode, String name) {
        return new TypeInsnNode(opcode, name);
    }

    private static AbstractInsnNode compileMethodInsn(int opcode, String[] pts, String line) throws IOException {
        if (pts.length != 3) {
            throw new IOException("Invalid method instruction: " + line);
        }

        String owner = pts[0];
        String name = pts[1];
        String desc = pts[2];
        return new MethodInsnNode(opcode, owner, name, desc);
    }

    private static AbstractInsnNode compileLdc(String[] c) {
        switch (c[0]) {
            case "s":
                return new LdcInsnNode(StringEscapeUtils.unescapeJava(c[1]));
            case "i":
                return new LdcInsnNode(Integer.parseInt(c[1]));
            case "f":
                return new LdcInsnNode(Float.parseFloat(c[1]));
            default:
                return new LdcInsnNode(String.join(" ", c));
        }
    }

    private static AbstractInsnNode compileFieldInsn(int opcode, String[] pts, String line) throws IOException {
        if (pts.length != 3) {
            throw new IOException("Invalid field instruction: " + line);
        }

        String owner = pts[0];
        String name = pts[1];
        String desc = pts[2];
        return new FieldInsnNode(opcode, owner, name, desc);
    }
}
