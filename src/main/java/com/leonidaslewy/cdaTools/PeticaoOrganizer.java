package com.leonidaslewy.cdaTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Stack;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PeticaoOrganizer {
    private final static String REPLACE = ("[/\\\\:*?\"<>|]");
    private final static String SEP = System.getProperty("file.separator");
    private Stack<File> fileList;
    
    /**
     * Construct the object with the Stack used for the organization defined.
     * @param fileList Stack<File> with only .pdf files
     */
    public PeticaoOrganizer(Stack<File> fileList) {
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
        pdf.close();
        if(text.contains("Petição Inicial")) {
            text = text.replace(".", "").replace("\n", ".").replace("\r", ".");

            //Gets the initial index for the name
            var fromIndex = text.indexOf("Executado:")+11;
            while((text.charAt(fromIndex)+"").equals(" "))
                fromIndex++;
            
            //Gets the final index for the name
            var toIndex = text.indexOf(".", fromIndex);
            while((text.charAt(toIndex-1)+"").equals(" "))
                toIndex--;

            var peticao = text.substring(fromIndex, toIndex).replaceAll(REPLACE, "-");
            var peticaoDir = new File(file.getParent()+SEP+"ORGANIZADO"+SEP+peticao);
            peticaoDir.mkdir();
            
            //Formats the string to rename the file
            var originalDir = file.getParent();
            var rename = String.format("001 PETICAO %s.pdf", peticao);
            file.renameTo(new File(peticaoDir.getAbsolutePath()+SEP+rename));
            
            //Copy procuracao to the directory
            File procuracao = new File(originalDir+SEP+"IMPORTANTE"+SEP+"003 PROCURACAO.pdf");
            if(procuracao.exists() && !new File(peticaoDir.getAbsolutePath()+SEP+"003 PROCURACAO.pdf").exists()) {
                try {
                    Files.copy(procuracao.toPath(), new File(peticaoDir.getAbsolutePath()+SEP+"003 PROCURACAO.pdf").toPath());
                } catch (Exception e){}
            }
        }
    }
}
