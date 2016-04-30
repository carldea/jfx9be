package com.jfxbe;

/**
 * This represents an abstract Cat containing default methods common to all cats. 
 * @author carldea
 */
public interface Cat {
    String getCatKind();
    String getFurDescription();
    
    default void growl() {
        System.out.println("Grrrrowl!!");
    }
    default void walk() {
        System.out.println(getCatKind() + " is walking.");
    }
    default void eat() {
        System.out.println(getCatKind() + " is eating.");
    }
    default void sleep() {
        System.out.println(getCatKind() + " is sleeping.");
    }
}
