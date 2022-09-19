package com.leonidaslewy.view;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * A class that interect with the user, outputing text and receiving parameters to be used in the program.
 */
public class Menu {
    
    /**
     * Print in the console a list of options and return the number of the option choosed.
     * Only accept int as input.
     * @param options array of String
     * @return int with the option choosed
     */
    public static int printMenu(String[] options) {        
        var sc = new Scanner(System.in);

        while(true) {
            System.out.println("=========================");
            for(int i = 0; i < options.length; i++) {
                System.out.printf("[%d] - %s\n", i, options[i]);
            }
            System.out.println("=========================");

            try {
                var inp = sc.nextInt();
                if(inp < options.length && inp > -1) {
                    return inp;
                } else {
                    System.out.println("---==INSIRA UM NUMERO VALIDO==---");
                    continue;
                }
            } catch (InputMismatchException ime) {
                System.out.println("---==INSIRA UM INPUT VALIDO==---");
                continue;
            }
        }
    }

    /**
     * Ask for the user which directory he wants to use in the program and return the directory.
     * @return File directory inputed.
     */
    public static File getDiretorio() {
        while(true) {
            System.out.println("=========================");
            System.out.println("Insira o caminho do diretorio: ");
            var dirPath = System.console().readLine();
            var dir = new File(dirPath);
            
            if(!dir.isDirectory()) {
                System.out.println("---==INSIRA UM DIRETORIO VALIDO==---");
                continue;
            } else {
                return dir;
            }
        }
    }

}
