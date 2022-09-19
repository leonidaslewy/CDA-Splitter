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
        text = text.replace(".", "").replace("\n", ".").replace("\r", "");
        if(text.contains("os livros de Dívida Ativa")) {
            //Formats the string and creates the dir
            var finalIndex = text.indexOf(".", text.indexOf("/Devedor:")+10);
            while((text.charAt(finalIndex-1)+"").equals(" ")) {
                finalIndex--;
            }
            var CDAName = text.substring(text.indexOf("/Devedor:")+10, finalIndex).replaceAll(REPLACE, "-");
            var CDADir = new File(file.getParent()+SEP+"ORGANIZADO"+SEP+CDAName+SEP);
            CDADir.mkdir();
            //Formats the string to rename the file
            var CDANumber = text.substring(text.indexOf("Número:")+8, text.indexOf(".", text.indexOf("/20"))).replace("/", "-");
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
