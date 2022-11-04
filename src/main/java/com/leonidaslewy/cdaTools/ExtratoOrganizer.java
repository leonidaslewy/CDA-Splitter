package com.leonidaslewy.cdaTools;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ExtratoOrganizer {
    private final static String REPLACE = ("[/\\\\:*?\"<>|]");
    private final static String SEP = System.getProperty("file.separator");
    private Stack<File> fileList;
    
    /**
     * Construct the object with the Stack used for the organization defined.
     * @param fileList Stack<File> with only .pdf files
     */
    public ExtratoOrganizer(Stack<File> fileList) {
        this.fileList = fileList;
    }

    /**
     * Organize in parallel all the files in the Stack, renaming and moving then when necessary.
     */
    public void organizeAllFiles() {
        if (!fileList.empty()) {
            new File(fileList.peek().getParent()+SEP+"ORGANIZADO").mkdir();
            fileList.parallelStream().forEach(file -> {
                try {
                    organizeFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        
    }

    private void organizeFile(File file) throws IOException {
        //Loads the file
        PDDocument pdf = Loader.loadPDF(file);
        var text = new PDFTextStripper().getText(pdf);
        text = text.replace(".", "").replace("\n", ".").replace("\r", "");
        pdf.close();

        if(text.contains("Relatório Extrato do Contribuinte")) {
            var extratoDir = new File(file.getParent()+SEP+"ORGANIZADO"+SEP+"EXTRATOS");
            extratoDir.mkdir();

            //Formats the string to rename the file
            var label = text.substring(text.indexOf("Imóvel")+8, text.indexOf(".", text.indexOf("Imóvel")+8));
            var rename = String.format("005 EXTRATO IMOVEL %s.pdf", label);
            
            int i = 0;
            boolean renamed = false;
            while (!renamed) {
                if (i == 0) {
                    renamed = file.renameTo(new File(extratoDir.getAbsolutePath()+SEP+rename));
                } else {
                    rename = String.format("005 EXTRATO IMOVEL %s (%d).pdf", label, i);
                    renamed = file.renameTo(new File(extratoDir.getAbsolutePath()+SEP+rename));
                }

                if(!renamed)
                    i++;
            }
        }
    }
}
