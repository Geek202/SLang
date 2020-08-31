package me.geek.tom.slang.sourcefile.nodes;

public class FieldNode {

    private final String desc;
    private final String name;

    public FieldNode(String desc, String name) {
        this.desc = desc;
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FieldNode{" + "\n" +
                "\tdesc='" + desc + '\'' + "\n" +
                "\t, name='" + name + '\'' + "\n" +
                '}';
    }
}
