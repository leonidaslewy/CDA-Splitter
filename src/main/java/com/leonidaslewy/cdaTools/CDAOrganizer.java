package com.leonidaslewy.cdaTools;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class CDAOrganizer {
    private Stack<File> fileList;
    
    public CDAOrganizer(Stack<File> fileList) {
        this.fileList = fileList;
    }

    public void organizeAllFiles() {
        fileList.parallelStream().forEach(file -> {
            try {
                organizeFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void organizeFile(File file) throws IOException {
        PDDocument pdf = Loader.loadPDF(file);
        String text = new PDFTextStripper().getText(pdf);
        pdf.close();
        System.out.println(text);
    }
    
}
