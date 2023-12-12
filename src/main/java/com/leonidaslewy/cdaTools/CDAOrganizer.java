package com.leonidaslewy.cdaTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Stack;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Class made to agregate te functions to organize the CDA in folders.
 * @author Leônidas Lewy
 */
public class CDAOrganizer {
    private final static String REPLACE = ("[/\\\\:*?\"<>|]");
    private final static String SEP = System.getProperty("file.separator");
    private Stack<File> fileList;
    
    /**
     * Construct the object with the Stack used for the organization defined.
     * @param fileList Stack<File> with only .pdf files
     */
    public CDAOrganizer(Stack<File> fileList) {
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

    //Organize by name and CDA number.
    private void organizeFile(File file) throws IOException {
        //Loads the file
        PDDocument pdf = Loader.loadPDF(file);
        var text = new PDFTextStripper().getText(pdf);
        pdf.close();
        if(text.contains("os livros de Dívida Ativa")) {
            text = text.replace(".", "").replace("\n", ".").replace("\r", "");
            //Gets the initial index for the name
            var fromIndex = text.indexOf("-", text.indexOf(".", text.indexOf("PREFEITURA MUNICIPAL DE CAPÃO DA CANOA")))+1;
            while((text.charAt(fromIndex)+"").equals(" "))
                fromIndex++;
            
            //Gets the final index for the name
            var toIndex = text.indexOf(".", fromIndex);
            while((text.charAt(toIndex-1)+"").equals(" "))
                toIndex--;
 
            //Creates the substring for the name and the directory
            var CDAName = text.substring(fromIndex, toIndex).replaceAll(REPLACE, "-");
            var CDADir = new File(file.getParent()+SEP+"ORGANIZADO"+SEP+CDAName+SEP);
            CDADir.mkdir();

            //Formats the string to rename the file
            fromIndex = text.indexOf("CDA Nº")+6;
            while((text.charAt(fromIndex-1)+"").equals(" "))
                fromIndex++;
                
            toIndex = text.indexOf(".", fromIndex);
            while((text.charAt(toIndex-1)+"").equals(" "))
                toIndex--;

            var CDANumber = text.substring(fromIndex, toIndex).replace("/", "-");
            var originalDir = file.getParent();
            var rename = String.format("002 CDA %s.pdf", CDANumber);
            file.renameTo(new File(CDADir.getAbsolutePath()+SEP+rename));

            //Copy signature to the directory
            text = text.replace(".", "");
            var signaturePath = String.format("004 ASSINATURA %s.pdf", text.substring(text.indexOf("informe o código")+17, text.indexOf("informe o código")+36));
            File signature = new File(originalDir+SEP+"IMPORTANTE"+SEP+signaturePath);
            if(signature.exists() && !new File(CDADir.getAbsolutePath()+SEP+signaturePath).exists()) {
                try {
                    Files.copy(signature.toPath(), new File(CDADir.getAbsolutePath()+SEP+signaturePath).toPath());
                } catch (Exception e){}
            }
        }
    }
    
}
