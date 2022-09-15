package com.leonidaslewy;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import com.leonidaslewy.cdaTools.CDAOrganizer;
import com.leonidaslewy.cdaTools.DirAnalyser;

public class Tests {
    public static void main(String[] args) throws IOException {
        Stack<File> test = DirAnalyser.getFiles(new File("/home/leonidaslewy/Documents/test/"));
        CDAOrganizer CDAO = new CDAOrganizer(test);
        CDAO.organizeAllFiles();
    }
}
