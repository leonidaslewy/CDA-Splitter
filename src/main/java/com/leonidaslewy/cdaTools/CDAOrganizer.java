package com.leonidaslewy.cdaTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Stack;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Class made to agregate te functions to organize the CDA in folders.
 * @author Leônidas Lewy
 */
public class CDAOrganizer {
    private final static String REPLACE = ("[/\\\\:*?\"<>|]");
    private final static String SEP = System.getProperty("file.separator");
    private final String parentFolder;
    private Stack<PDDocument> fileList = new Stack<>();
    
    
    /**
     * Construct the object with the Stack used for the organization defined.
     * @param fileList Stack<File> with only .pdf files
     * @throws IOException
     */
    public CDAOrganizer(Stack<File> fileList) throws IOException {
        parentFolder = fileList.peek().getParent();
        while (!fileList.empty()) {
            PDDocument pdf = Loader.loadPDF(fileList.pop());
            Splitter splitter = new Splitter();
            PDFMergerUtility merger = new PDFMergerUtility();

            List<PDDocument> documentList = splitter.split(pdf);

            int documentMainIndex = 0;
            for (int i = 0; i < documentList.size(); i++) {
                var text = new PDFTextStripper().getText(documentList.get(i));

                if(text.contains("Petição Inicial")) {
                    continue;
                } else if(text.startsWith("Estado do Rio Grande do Sul")) {
                    if (i !=0) {
                        this.fileList.add(documentList.get(documentMainIndex));
                    }
                    documentMainIndex = i;
                } else if (text.startsWith("VERIFICAÇÃO DAS")) {
                    var rename = String.format("004 ASSINATURA %s.pdf", text.substring(text.indexOf("verificação:")+13, text.indexOf("verificação:")+32));
                    try {
                        documentList.get(i).save(DirAnalyser.createImportantDir(new File(parentFolder)).getAbsolutePath()+SEP+rename);
                    } catch (Exception e) {}
                } else {
                    merger.appendDocument(documentList.get(documentMainIndex), documentList.get(i));
                }
            }
        }
        
    }

    /**
     * Organize in parallel all the files in the Stack, renaming and moving then when necessary.
     */
    public void organizeAllFiles() {
        if (!fileList.empty()) {
            new File(parentFolder+SEP+"ORGANIZADO").mkdir();
            fileList.forEach(pdf -> {
                try {
                    organizeFile(pdf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    //Organize by name and CDA number.
    private void organizeFile(PDDocument pdf) throws IOException {
        //gets the text
        var text = new PDFTextStripper().getText(pdf);
        if(text.contains("os livros de Dívida Ativa")) {
            text = text.replace(".", "").replace("\n", ".").replace("\r", "");
            //Gets the initial index for the name
            var fromIndex = text.indexOf("-", text.indexOf(".", text.indexOf("CPF/CNPJ:")))+1;
            while((text.charAt(fromIndex)+"").equals(" "))
                fromIndex++;
            
            //Gets the final index for the name
            var toIndex = text.indexOf(".", fromIndex);
            while((text.charAt(toIndex-1)+"").equals(" "))
                toIndex--;
 
            //Creates the substring for the name and the directory
            var CDAName = text.substring(fromIndex, toIndex).replaceAll(REPLACE, "-");
            var CDADir = new File(parentFolder+SEP+"ORGANIZADO"+SEP+CDAName+SEP);
            CDADir.mkdir();

            //Formats the string to rename the file
            fromIndex = text.indexOf("CERTIDÃO DE DÍVIDA ATIVA Nº")+27;
            while((text.charAt(fromIndex-1)+"").equals(" "))
                fromIndex++;
                
            toIndex = text.indexOf(".", fromIndex);
            while((text.charAt(toIndex-1)+"").equals(" "))
                toIndex--;

            var CDANumber = text.substring(fromIndex, toIndex).replace("/", "-");
            var rename = String.format("002 CDA %s.pdf", CDANumber);
            pdf.save(CDADir.getAbsolutePath()+SEP+rename);

            //Copy signature to the directory
            text = text.replace(".", "");
            var signaturePath = String.format("004 ASSINATURA %s.pdf", text.substring(text.indexOf("informe o código")+17, text.indexOf("informe o código")+36));
            File signature = new File(parentFolder+SEP+"IMPORTANTE"+SEP+signaturePath);
            if(signature.exists() && !new File(CDADir.getAbsolutePath()+SEP+signaturePath).exists()) {
                try {
                    Files.copy(signature.toPath(), new File(CDADir.getAbsolutePath()+SEP+signaturePath).toPath());
                } catch (Exception e){}
            }
        }
    }
    
}
