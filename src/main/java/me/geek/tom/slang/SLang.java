package me.geek.tom.slang;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import me.geek.tom.slang.disassembler.ClassParser;
import me.geek.tom.slang.embedded.SLangClassLoader;
import me.geek.tom.slang.output.BytecodeCompiler;
import me.geek.tom.slang.sourcefile.SourceFile;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class SLang {

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Options options = new Options();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(options)
                .build();
        jCommander.parse(args);

        if (options.mode != Mode.EXECUTE && options.output == null) {
            jCommander.usage();
            return;
        }

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
            try (BufferedSink sink = Okio.buffer(Okio.sink(options.output))) {
                ClassParser parser = new ClassParser();
                parser.load(options.input);
                parser.disassemble(s -> {
                    try {
                        sink.writeUtf8(s + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException("disassembling", e);
                    }
                });
            }
        } else if (options.mode == Mode.EXECUTE) {
            SLangClassLoader loader = new SLangClassLoader("Hello", options.input);
            Class<?> cls = Class.forName("Hello", true, loader);
            Method main = cls.getDeclaredMethod("main", String[].class);
            main.invoke(null, (Object) new String[0]);
        }
    }

    public static class Options {
        @Parameter(names = { "--input", "-in", "-i" }, required = true, description = "Input file")
        public File input;
        @Parameter(names = { "--output", "-out", "-o" }, description = "Output file")
        public File output;
        @Parameter(names = { "--mode", "-m" }, required = true, description = "Mode")
        public Mode mode;
    }

    public enum Mode {
        ASSEMBLE, DISASSEMBLE, EXECUTE
    }
}
