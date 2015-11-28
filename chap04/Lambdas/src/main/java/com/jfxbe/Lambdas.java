package com.jfxbe;

import java.util.Arrays;
import java.util.List;

/**
 * Lambdas
 * @author cdea
 */
public class Lambdas {
    
    @FunctionalInterface
    interface MyFormula {
        double compute(double val1, double val2);
    }

    public static void main(String[] args) {
        
        // assigning variables to functional interfaces
        MyFormula area = (height, width) -> height * width;
        MyFormula perimeter = (height, width) -> 2*height + 2*width;
        
        System.out.println("Area = " + area.compute(3, 4));       
        System.out.println("Perimeter = " + perimeter.compute(3, 4));   
        
        // create a list of values
        List<Integer> values = Arrays.asList(23, 84, 74, 85, 54, 60);
        System.out.println("values: " + values.toString());

        // non-local variable to be used in lambda expression.
        int threshold = 54;
        System.out.println("Values greater than " + threshold + " converted to hex:");
        java.util.stream.Stream<Integer> stream = values.stream();
        // using aggregate functions filter() and forEach()
        values.stream()
            .filter(val -> val > threshold) // Predicate functional interface
            .sorted()
            .map(dec -> Integer.toHexString(dec) ) // Consumer functional interface
            .forEach(val -> System.out.println(val)); // for each output values.
    }
    
}
