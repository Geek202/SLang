package me.geek.tom.slang.sourcefile;

import me.geek.tom.slang.sourcefile.nodes.ClsNode;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceFile {

    private static final char INDENT = '\t';

    private final List<ClsNode> classes;

    public static final List<String> ACCESS_ATTRIBS = Arrays.asList(
            "PRIVATE",
            "PROTECTED",
            "PUBLIC",
            "STATIC",
            "FINAL",
            "ABSTRACT",
            "NATIVE"
    );

    public SourceFile() {
        classes = new ArrayList<>();
    }

    public void read(File file) throws IOException {
        try (Source source = Okio.source(file); BufferedSource bufferedSource = Okio.buffer(source)) {
            String ln;
            while ((ln = bufferedSource.readUtf8Line()) != null) {
                String line = ln.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("c")) {
                    String[] pts = line.split(" ");
                    if (pts.length != 2)
                        throw new IOException("Illegal slang file on line: '" + ln + "'!");

                    if (classes.stream().anyMatch(n -> n.getName().equals(pts[1])))
                        throw new IOException("Two classes called " + pts[1] + " declared in the same file!");
                    ClsNode cls = ClsNode.readClass(pts[1], bufferedSource);
                    this.classes.add(cls);
                } else {
                    throw new IOException("Invalid slang file on line: '" + ln + "'!");
                }
            }
        }
    }

    public List<ClsNode> getClasses() {
        return classes;
    }

    @Override
    public String toString() {
        return "SourceFile{\n" +
                "\tclasses=" + classes.toString().replace("\n", "\n\t") +
                "\n}";
    }
}
