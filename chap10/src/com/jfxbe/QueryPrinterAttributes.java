package com.jfxbe;

import javafx.print.Printer;
import javafx.print.PrinterAttributes;

/**
 * Static utility class to dump out the
 * default printer's attributes
 */
public class QueryPrinterAttributes {

    public static void main(String[] args) {
        dumpAttributes();
    }

    public static void dumpAttributes() {

        Printer.getAllPrinters().forEach(System.out::println);
        Printer defaultPrinter = Printer.getDefaultPrinter();
        PrinterAttributes attributes = defaultPrinter.getPrinterAttributes();
        System.out.println("--------------------------");
        System.out.println("Default Print Layout : ");
        System.out.printf(" %s%n", defaultPrinter.getDefaultPageLayout() );
        System.out.printf(" printable width: %f%n", defaultPrinter.getDefaultPageLayout().getPrintableWidth() );
        System.out.printf(" printable height: %f%n", defaultPrinter.getDefaultPageLayout().getPrintableHeight() );
        System.out.println("--------------------------");
        System.out.println("Supported Orientations : ");
        attributes.getSupportedPageOrientations()
                .forEach(System.out::println);

        System.out.println("--------------------------");
        System.out.println("Supported Collations : ");
        attributes.getSupportedCollations()
                .forEach( collation -> System.out.printf(" %s%n", collation));

        System.out.println("--------------------------");
        System.out.println("Supported Paper types : ");
        attributes.getSupportedPapers()
                .forEach( paper -> System.out.printf(" %s%n", paper));

        System.out.println("--------------------------");
        System.out.println("Supported Paper Sources : ");
        attributes.getSupportedPaperSources()
                .forEach( paperSource -> System.out.printf(" %s%n", paperSource));

        System.out.println("--------------------------");
        System.out.println("Supported Print Colors : ");
        attributes.getSupportedPrintColors()
                .forEach( paperColor -> System.out.printf(" %s%n", paperColor));

        System.out.println("--------------------------");
        System.out.println("Supported Print Quality types : ");
        attributes.getSupportedPrintQuality()
                .forEach( printQuality -> System.out.printf(" %s%n", printQuality));

        System.out.println("--------------------------");
        System.out.println("Supported Print Resolutions : ");
        attributes.getSupportedPrintResolutions()
                .forEach( printRez -> System.out.printf(" %s%n", printRez));

        System.out.println("--------------------------");
        System.out.println("Supported Print Sides: ");
        attributes.getSupportedPrintSides()
                .forEach( printSize -> System.out.printf(" %s%n", printSize));
    }

}
