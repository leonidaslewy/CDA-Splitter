package com.leonidaslewy.cdaTools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Class made with the purpose of an agregation of functions related with get archives and folders for the program.
 * @author Leônidas Lewy
 */
public class DirAnalyser {
    private final static String SEP = System.getProperty("file.separator");

    /**
     * Return the pdf files in the directory and move to a directory all the files that isnt pdf.
     * @param dir File with the path of the directory.
     * @return Stack<File> with the list of the pdf in the directory.
     * @throws IOException
     */
    public static Stack<File> getFiles(File dir) {
        Stack<File> fileStack = new Stack<>();
        List<File> tempList = Arrays.asList(dir.listFiles());

        tempList.parallelStream().forEach(file -> {
            File newFile = null;
            try {
                newFile = analyseFile(file, dir);
            } catch (IOException e) {e.printStackTrace();}
            if (newFile != null)
                fileStack.add(newFile);
        });

        return fileStack;
    }

    private static File createImportantDir(File dir) {
        var importantDir = new File(dir.getAbsolutePath()+SEP+"IMPORTANTE");
        importantDir.mkdir();
        return importantDir;
    }

    private static String getText(File file) throws IOException {
        PDDocument pdf = Loader.loadPDF(file);
        var text = new PDFTextStripper().getText(pdf);
        pdf.close();
        return text;
    }

    private static File analyseFile(File file, File dir) throws IOException {
        //if is a directory
        if (file.isDirectory()) {
            return null;
        //if its a pdf and starting with 003 
        } else if (file.getName().startsWith("003") && file.getName().endsWith(".pdf")) {
            file.renameTo(new File(createImportantDir(dir).getAbsolutePath()+SEP+"003 PROCURACAO.pdf"));
            return null;
        //if its a sign
        } else if (file.getName().endsWith(".pdf") && getText(file).contains("Código para verificação:")) {
            //rename the file with the code of the signature
            var text = getText(file);
            var rename = String.format("004 ASSINATURA %s.pdf", text.substring(text.indexOf("verificação:")+13, text.indexOf(" ", text.indexOf("verificação:")+13)));
            file.renameTo(new File(createImportantDir(dir).getAbsolutePath()+SEP+rename));
            return null;
        //if its a pdf
        } else if (file.getName().endsWith(".pdf")) {
            return file;
        //if its a pdf starting with
        } else {
            var notUsedDir = new File(dir.getAbsolutePath()+SEP+"NAO USADO");
            notUsedDir.mkdir();
            file.renameTo(new File(notUsedDir.getAbsolutePath()+SEP+file.getName()));
            return null;
        }
    }
}
