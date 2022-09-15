package com.leonidaslewy.cdaTools;

import java.io.File;
import java.util.Stack;

/**
 * Class made with the purpose of an agregation of functions related with get archives and folders for the program.
 * @author Leônidas Lewy
 */
public class DirAnalyser {

    /**
     * Return the pdf files in the directory and move to a directory all the files that isnt pdf.
     * @param dir File with the path of the directory.
     * @return Stack<File> with the list of the pdf in the directory.
     */
    public static Stack<File> getFiles(File dir) {
        File[] templist = dir.listFiles();
        Stack<File> fileStack = new Stack<>();
        for (File file : templist) {
            if(file.getName().endsWith(".pdf")) {
                fileStack.add(file);
            } else if (!file.isDirectory()){
                File notUsedDir = new File(dir.getAbsolutePath() + "/NAO USADO");
                notUsedDir.mkdir();
                file.renameTo(new File(notUsedDir.getAbsolutePath()+"/"+file.getName()));
            }
        }
        return fileStack;
    }
}
