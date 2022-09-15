package com.leonidaslewy.cdaTools;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Class made to agregate te functions to organize the CDA in folders.
 * @author Leônidas Lewy
 */
public class CDAOrganizer {
    private final String REPLACE = ("[/\\\\:*?\"<>|]");
    private final String SEP = System.getProperty("file.separator");
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
        new File(fileList.peek().getParent()+SEP+"CDA").mkdir();
        fileList.parallelStream().forEach(file -> {
            try {
                organizeFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //Organize by name and CDA number.
    private void organizeFile(File file) throws IOException {
        //Loads the file
        PDDocument pdf = Loader.loadPDF(file);
        var text = new PDFTextStripper().getText(pdf);
        pdf.close();
        text = text.replace("\n", "").replace("\r", ""); 
        if(text.contains("Certidão de Dívida AtivaESTADO DO RIO GRANDE DO SULPrefeitura Municipal de Capão da Canoa")) {
            //Formats the string to rename the file
            var CDANumber = text.substring(text.indexOf("Número:")+8, text.indexOf(" ", text.indexOf("/20"))).replace("/", "-");
            var rename = String.format("002 CDA %s.pdf", CDANumber);

            file.renameTo(new File(file.getParent()+SEP+rename));
            
            //Formats the string and move to the apropiated dir
            var CDAName = text.substring(text.indexOf("/Devedor:")+10, text.indexOf("Responsável:", text.indexOf("/Devedor:"))).replaceAll(REPLACE, "-");
            var CDADir = new File(file.getParent()+SEP+"CDA"+SEP+CDAName+SEP);
            System.out.println(file.getParent()+SEP+"CDA"+SEP+CDAName+SEP); 
            System.out.println(CDADir.mkdir());

            file.renameTo(new File(CDADir.getAbsolutePath()+SEP+rename));
        }
    }
    
}
