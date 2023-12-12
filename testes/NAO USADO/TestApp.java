package com.leonidaslewy;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class TestApp {
    public static void main(String[] args) throws IOException {


        File pdfPath = new File("/home/leonidaslewy/Documents/CDA-Splitter/testes/002 CDA 0268-2023.pdf");
        PDDocument pdf = Loader.loadPDF(pdfPath);
        var text = new PDFTextStripper().getText(pdf);
        pdf.close();
        System.out.println(text);


    }
}
