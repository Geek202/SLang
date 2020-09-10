package me.geek.tom.slang;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import static org.objectweb.asm.Opcodes.*;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HelloWorldCompiler {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting 'Hello, World!' compiler");
        System.out.println("  Copyright (c) 2020 Tom_The_Geek");
        Options options = new Options();
        JCommander.newBuilder()
                .addObject(options)
                .build()
                .parse(args);

        System.out.println();
        System.out.println("Compiling 'Hello, World!' to " + options.output);

        File output = options.output.get(0);

        ClassNode node = new ClassNode();
        node.version = V1_8;
        node.access = ACC_PUBLIC;
        node.name = output.getName().split("\\.", 2)[0];
        node.superName = "java/lang/Object";
        node.sourceFile = "HelloWorld.java";

        node.methods.add(compileMain());
        node.methods.add(compileEmptyConstructor());

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        FileUtils.writeByteArrayToFile(output, writer.toByteArray());
    }

    private static MethodNode compileMain() {
        return Helper.createVoidMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null, null,
                node -> {
                    node.instructions.add(
                            new FieldInsnNode(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;")
                    );
                    node.instructions.add(
                            new LdcInsnNode("Hello, World!")
                    );
                    node.instructions.add(
                            new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V")
                    );
                }
        );
    }

    public static MethodNode compileEmptyConstructor() {
        return Helper.createVoidMethod(
                ACC_PUBLIC,
                "<init>",
                "()V",
                null, null,
                node -> {
                    node.instructions.add(
                            new VarInsnNode(ALOAD, 0)
                    );
                    node.instructions.add(
                            new MethodInsnNode(
                                    INVOKESPECIAL,
                                    "java/lang/Object",
                                    "<init>",
                                    "()V"
                            )
                    );
                }
        );
    }

    private static class Options {
        @Parameter(description = "File to compile to", required = true)
        public List<File> output;
    }

}
