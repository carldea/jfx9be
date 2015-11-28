package com.jfxbe;

/**
 * Functional Interfaces.
 * @author cdea
 */
public class FunctionalInterfaces {
    @FunctionalInterface
    interface MyFormula {
        double compute(double val1, double val2);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // assigning variables to functional interfaces
        MyFormula area = (height, width) -> height * width;
        MyFormula perimeter = (height, width) -> 2*height + 2*width;
        
        System.out.println("Area = " + area.compute(3, 4));       
        System.out.println("Perimeter = " + perimeter.compute(3, 4)); 
    }
    
}
