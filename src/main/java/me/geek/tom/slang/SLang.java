package me.geek.tom.slang;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import kotlin.NotImplementedError;
import me.geek.tom.slang.output.BytecodeCompiler;
import me.geek.tom.slang.sourcefile.SourceFile;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SLang {

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        JCommander.newBuilder()
                .addObject(options)
                .build()
                .parse(args);

        if (options.mode == Mode.ASSEMBLE) {
            SourceFile source = new SourceFile();
            source.read(options.input);
            System.out.println(source);
            BytecodeCompiler compiler = new BytecodeCompiler();
            compiler.readSrc(source);
            List<ClassNode> classes = compiler.getClasses();
            if (!classes.isEmpty()) {
                ClassNode node = classes.get(0);
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                FileUtils.writeByteArrayToFile(options.output, writer.toByteArray());
            }
        } else if (options.mode == Mode.DISASSEMBLE) {
            throw new NotImplementedError();
        }
    }

    public static class Options {
        @Parameter(names = { "--input", "-in", "-i" }, required = true, description = "Input file")
        public File input;
        @Parameter(names = { "--output", "-out", "-o" }, required = true, description = "Output file")
        public File output;
        @Parameter(names = { "--mode", "-m" }, required = true, description = "Mode")
        public Mode mode;
    }

    public enum Mode {
        ASSEMBLE, DISASSEMBLE;
    }
}
