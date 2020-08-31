package me.geek.tom.slang.sourcefile.nodes;

import me.geek.tom.slang.sourcefile.SourceFile;
import okio.BufferedSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClsNode {

    private final String name;
    private final String superClass;
    private final List<String> implemented;

    private final List<String> attribs;
    private final List<FieldNode> fields;
    private final List<MethodNode> methods;

    public ClsNode(String name, String superClass, List<String> implemented, List<String> attribs, List<FieldNode> fields, List<MethodNode> methods) {
        this.name = name;
        this.superClass = superClass;
        this.implemented = implemented;
        this.attribs = attribs;
        this.fields = fields;
        this.methods = methods;
    }

    public static ClsNode readClass(String name, BufferedSource bufferedSource) throws IOException {
        List<String> implemented = new ArrayList<>();
        List<String> attribs = new ArrayList<>();
        List<FieldNode> fields = new ArrayList<>();
        List<MethodNode> methods = new ArrayList<>();
        String superClass = "java/lang/Object";

        String ln = bufferedSource.readUtf8Line();
        while (ln != null) {
            String line = ln.trim();
            if (line.equals("ec")) break;

            if (!line.isEmpty() && !line.startsWith("#")) {
                String[] pts = line.split(" ");
                if (line.startsWith("a ")) {
                    if (pts.length != 2) {
                        throw new IOException("Invalid slang file on line: '" + line + "'!");
                    }

                    String access = pts[1].toUpperCase(Locale.getDefault());
                    if (!SourceFile.ACCESS_ATTRIBS.contains(access))
                        throw new IOException("Invalid access modifier: " + access + " for class " + name);

                    attribs.add(access);
                } else if (line.startsWith("e ")) {
                    if (pts.length != 2) {
                        throw new IOException("Invalid slang file on line: '" + line + "'!");
                    }

                    superClass = pts[1];
                } else if (line.startsWith("i ")) {
                    if (pts.length != 2) {
                        throw new IOException("Invalid slang file on line: '" + line + "'!");
                    }

                    String iface = pts[1];
                    if (!implemented.contains(iface)) {
                        implemented.add(iface);
                    }
                } else if (line.startsWith("f ")) {
                    if (pts.length != 3) {
                        throw new IOException("Invalid slang file on line: '" + line + "'!");
                    }

                    String type = pts[1];
                    String fieldName = pts[2];

                    if (fields.stream().anyMatch(f -> f.getName().equals(fieldName))) {
                        throw new IOException("Two fields called " + fieldName + " declared in the same class (" + name + ")!");
                    }

                    fields.add(new FieldNode(type, name));
                } else if (line.startsWith("m ")) {
                    if (pts.length != 3) {
                        throw new IOException("Invalid slang file on line: '" + line + "'!");
                    }

                    String methodName = pts[1];
                    String desc = pts[2];

                    if (methods.stream().anyMatch(f -> f.getName().equals(methodName))) {
                        throw new IOException("Two methods called " + methodName + " declared in the same class (" + name + ")!");
                    }

                    methods.add(MethodNode.readMethod(methodName, desc, bufferedSource));
                } else {
                    System.out.println("Unknown line: '" + line + "', ignoring...");
                }
            }

            ln = bufferedSource.readUtf8Line();
        }

        return new ClsNode(name, superClass, implemented, attribs, fields, methods);
    }

    public List<String> getAttribs() {
        return attribs;
    }

    public List<FieldNode> getFields() {
        return fields;
    }

    public List<MethodNode> getMethods() {
        return methods;
    }

    public String getSuperClass() {
        return superClass;
    }

    public String getName() {
        return name;
    }

    public List<String> getImplemented() {
        return implemented;
    }

    @Override
    public String toString() {
        return "ClsNode{" + "\n" +
                "\tname='" + name.replace("\n", "\n\t\t") + '\'' + "\n" +
                "\t, superClass='" + superClass.replace("\n", "\n\t\t") + '\'' + "\n" +
                "\t, implemented=" + implemented.toString().replace("\n", "\n\t\t") + "\n" +
                "\t, attribs=" + attribs.toString().replace("\n", "\n\t\t") + "\n" +
                "\t, fields=" + fields.toString().replace("\n", "\n\t\t") + "\n" +
                "\t, methods=" + methods.toString().replace("\n", "\n\t\t") + "\n" +
                '}';
    }
}
