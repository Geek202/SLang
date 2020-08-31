package me.geek.tom.slang.sourcefile.nodes;

import me.geek.tom.slang.sourcefile.SourceFile;
import okio.BufferedSource;
import okio.Source;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MethodNode {

    private final String name;
    private final String desc;

    private final List<String> access;
    private final List<String> insns;

    public MethodNode(String name, String desc, List<String> access, List<String> insns) {
        this.name = name;
        this.desc = desc;
        this.access = access;
        this.insns = insns;
    }

    public static MethodNode readMethod(String name, String desc, BufferedSource source) throws IOException {
        List<String> attribs = new ArrayList<>();
        List<String> instructions = new ArrayList<>();

        String ln = source.readUtf8Line();

        while (ln != null) {
            String line = ln.trim();
            if (line.equals("em")) break;

            if (!line.isEmpty() && !line.startsWith("#")) {

                if (line.startsWith("a ")) {
                    String[] pts = line.split(" ");
                    if (pts.length != 2) {
                        throw new IOException("Invalid slang file on line: '" + line + "'!");
                    }

                    String access = pts[1].toUpperCase(Locale.getDefault());
                    if (!SourceFile.ACCESS_ATTRIBS.contains(access))
                        throw new IOException("Invalid access modifier: " + access + " for method " + name);

                    attribs.add(access);
                } else if (line.startsWith("i")) {
                    String[] pts = line.split(" ", 2);

                    if (pts.length != 2) {
                        throw new IOException("Invalid slang file on line: '" + line + "'!");
                    }

                    instructions.add(pts[1]);
                }
            }

            ln = source.readUtf8Line();
        }

        return new MethodNode(name, desc, attribs, instructions);
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<String> getAccess() {
        return access;
    }

    public List<String> getInsns() {
        return insns;
    }

    @Override
    public String toString() {
        return "MethodNode{" + "\n" +
                "\tname='" + name + '\'' + "\n" +
                "\t, desc='" + desc + '\'' + "\n" +
                "\t, access=" + access + "\n" +
                "\t, insns=[\n\t\t" + String.join("\n\t\t", insns) + "\n\t]\n" +
                '}';
    }
}
