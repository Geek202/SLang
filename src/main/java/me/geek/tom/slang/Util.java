package me.geek.tom.slang;

import java.util.function.Consumer;

public class Util {

    public static <T> T make(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

}
