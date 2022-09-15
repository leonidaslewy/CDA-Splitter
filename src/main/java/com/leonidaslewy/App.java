package com.leonidaslewy;

import java.io.File;

import com.leonidaslewy.view.Menu;

public class App {
    public static void main(String[] args) {
        //This array defines the main options in the program
        String[] options = {"Sair", "Definir Diretorio", "Organizar CDAs", "Organizar Peticoes"};
        
        var dir = new File("null");
        while(true) {
            var input = Menu.printMenu(options);
            switch(input) {
                default:
                    case 0:
                        System.exit(0);
                        break;
                    
                    case 1:
                        Menu.getDiretorio();
                        break;
                    case 2:
                    if(dir.getName().equals("null")) {
                        System.out.println("---==SELECIONE UM DIRETORIO PRIMEIRO==---");
                        break;
                    } else {

                    }
                    case 3:
                    if(dir.getName().equals("null")) {
                        System.out.println("---==SELECIONE UM DIRETORIO PRIMEIRO==---");
                        break;
                    } else {

                    }
            }
        }
    }
}
