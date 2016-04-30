package com.jfxbe;

public interface Roarable {
    default void roar() {
        System.out.println("Roar!!");
    } 
}
