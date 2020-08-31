package me.geek.tom.slang.output;

import me.geek.tom.slang.HelloWorldCompiler;
import me.geek.tom.slang.sourcefile.SourceFile;
import me.geek.tom.slang.sourcefile.nodes.ClsNode;
import me.geek.tom.slang.sourcefile.nodes.FieldNode;
import me.geek.tom.slang.sourcefile.nodes.MethodNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class BytecodeCompiler {

    private List<ClassNode> classes;

    public BytecodeCompiler() {
        this.classes = new ArrayList<>();
    }

    public void readSrc(SourceFile src) throws IOException {
        for (ClsNode cls : src.getClasses()) {
            ClassNode node = new ClassNode();
            node.version = V1_8;

            for (String attrib : cls.getAttribs()) {
                node.access += AccessLookup.NAME_TO_CODE_LOOKUP.get(attrib);
            }
            node.name = cls.getName();
            node.superName = cls.getSuperClass();
            for (FieldNode field : cls.getFields()) {
                node.fields.add(compileField(field));
            }

            node.interfaces.addAll(cls.getImplemented());

            for (MethodNode method : cls.getMethods()) {
                if (method.getName().equals("<init>") && method.getDesc().equals("()V") && !method.getAccess().contains("STATIC")) {
                    node.methods.add(HelloWorldCompiler.compileEmptyConstructor());
                } else {
                    node.methods.add(compileMethod(method));
                }
            }
            classes.add(node);
        }
    }

    private org.objectweb.asm.tree.MethodNode compileMethod(MethodNode method) throws IOException {
        org.objectweb.asm.tree.MethodNode node = new org.objectweb.asm.tree.MethodNode();
        node.name = method.getName();
        node.desc = method.getDesc();
        for (String access : method.getAccess()) {
            node.access += AccessLookup.NAME_TO_CODE_LOOKUP.get(access);
        }
        for (String insn : method.getInsns()) {
            node.instructions.add(InstructionCompiler.compileInsn(insn));
        }
        return node;
    }

    public List<ClassNode> getClasses() {
        return classes;
    }

    private org.objectweb.asm.tree.FieldNode compileField(FieldNode field) {
        return new org.objectweb.asm.tree.FieldNode(ACC_PUBLIC, field.getName(), field.getDesc(), null, null);
    }

}
