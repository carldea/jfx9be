package com.jfxbe;

/**Demonstrates default methods in Java 8.
 * Mixins.java 
 * 
 * @author carldea
 */
public class Mixins {
    public static void main(String[] args) {
        Tiger bigCat = new Tiger();
        Cheetah mediumCat = new Cheetah();
        HouseCat smallCat = new HouseCat();
        
        System.out.printf("%s with %s fur.\n", bigCat.getCatKind(), bigCat.getFurDescription());
        bigCat.eat();
        bigCat.sleep();
        bigCat.walk();
        bigCat.roar();
        bigCat.growl();
        System.out.println("------------------");
        
        System.out.printf("%s with %s fur.\n", mediumCat.getCatKind(), mediumCat.getFurDescription());
        mediumCat.eat();
        mediumCat.sleep();
        mediumCat.walk();
        mediumCat.growl();
        mediumCat.purr();
        System.out.println("------------------");
        
        System.out.printf("%s with %s fur.\n", smallCat.getCatKind(), smallCat.getFurDescription());
        smallCat.eat();
        smallCat.sleep();
        smallCat.walk();
        smallCat.growl();
        smallCat.purr();
        smallCat.meow();
        System.out.println("------------------");
        
    }
}
