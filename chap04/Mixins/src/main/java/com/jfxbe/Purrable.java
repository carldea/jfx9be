package com.jfxbe;

public interface Purrable {
    default void purr() {
        System.out.println("Purrrrrrr...");
    }
}
