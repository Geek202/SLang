package me.geek.tom.slang.output;

import me.geek.tom.slang.Util;

import java.util.HashMap;
import java.util.Map;

public class AccessLookup {
    public static final Map<String, Integer> NAME_TO_CODE_LOOKUP = Util.make(new HashMap<>(), m -> {
        m.put("PUBLIC", 0x0001);
        m.put("PRIVATE", 0x0002);
        m.put("PROTECTED", 0x0004);
        m.put("STATIC", 0x0008);
        m.put("FINAL", 0x0010);
        m.put("NATIVE", 0x0100);
        m.put("ABSTRACT", 0x0400);
    });
}
