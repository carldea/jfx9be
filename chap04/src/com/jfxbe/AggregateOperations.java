package com.jfxbe;

import java.util.*;

/**
 * Aggregate Operations.
 * @author cdea
 */
public class AggregateOperations {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // create a list of values
        List<Integer> values = Collections.synchronizedList(Arrays.asList(23, 84, 74, 85, 54, 60));
        System.out.println("values: " + values.toString());

        // non-local variable to be used in lambda expression.
        int threshold = 54;
        System.out.println("Values greater than " + threshold + " converted to hex:");
        //java.util.stream.Stream<Integer> stream = values.parallelStream();
        java.util.stream.Stream<Integer> stream = values.stream();

        // using aggregate functions filter() and forEach()
        stream
            .filter(val -> val > threshold) // Predicate functional interface
            .sorted()
            .map(dec -> Integer.toHexString(dec).toUpperCase() ) // Consumer functional interface
            //.forEachOrdered(val -> System.out.println(val)); // for each output values for parallel stream
            .forEach(val -> System.out.println(val)); // for each output values.
    }    
}
