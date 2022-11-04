package com.leonidaslewy;

import java.io.File;
import java.io.IOException;

import com.leonidaslewy.cdaTools.CDAOrganizer;
import com.leonidaslewy.cdaTools.DirAnalyser;
import com.leonidaslewy.cdaTools.ExtratoOrganizer;
import com.leonidaslewy.cdaTools.PeticaoOrganizer;
import com.leonidaslewy.view.Menu;

public class App {
    public static void main(String[] args) throws IOException {
        //This array defines the main options in the program
        String[] options = {"Sair", "Definir Diretorio", "Organizar CDAs", "Organizar Peticoes", "Organizar Extratos"};
        
        var dir = new File("null");
        while(true) {
            var input = Menu.printMenu(options);
            switch(input) {
                //Sair
                case 0:
                    System.exit(0);
                    break;
                
                //Definir Diretorio
                case 1:
                    dir = Menu.getDiretorio();
                    break;

                //Organizar CDAs    
                case 2:
                    if(dir.getName().equals("null")) {
                        System.out.println("---==SELECIONE UM DIRETORIO PRIMEIRO==---");
                        break;
                    } else {
                        var CDAO = new CDAOrganizer(DirAnalyser.getFiles(dir));
                        CDAO.organizeAllFiles();
                        break;
                    }
                
                //Organizar Peticoes
                case 3:
                    if(dir.getName().equals("null")) {
                        System.out.println("---==SELECIONE UM DIRETORIO PRIMEIRO==---");
                        break;
                    } else {
                        var PO = new PeticaoOrganizer(DirAnalyser.getFiles(dir));
                        PO.organizeAllFiles();
                        break;
                    }

                case 4:
                    if(dir.getName().equals("null")) {
                        System.out.println("---==SELECIONE UM DIRETORIO PRIMEIRO==---");
                        break;
                    } else {
                        var EO = new ExtratoOrganizer(DirAnalyser.getFiles(dir));
                        EO.organizeAllFiles();
                        break;
                    }
            }
        }
    }
}
