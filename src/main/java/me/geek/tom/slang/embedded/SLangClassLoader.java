package me.geek.tom.slang.embedded;

import me.geek.tom.slang.output.BytecodeCompiler;
import me.geek.tom.slang.sourcefile.SourceFile;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SLangClassLoader extends ClassLoader {

    private final String className;
    private final File classFile;

    public SLangClassLoader(String className, File classFile) {
        this.className = className;
        this.classFile = classFile;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (!Objects.equals(name, className)) throw new ClassNotFoundException(name);
        try {
            byte[] cls = parseClass(classFile);
            return defineClass(name, cls, 0, cls.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    private byte[] parseClass(File classFile) throws IOException {
        SourceFile source = new SourceFile();
        source.read(classFile);
        BytecodeCompiler compiler = new BytecodeCompiler();
        compiler.readSrc(source);
        try {
            ClassNode cls = compiler.getClasses().get(0);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cls.accept(writer);
            return writer.toByteArray();
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("no classes!", e);
        }
    }
}
