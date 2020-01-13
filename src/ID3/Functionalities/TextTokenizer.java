/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes.Functionalities;

import naivebayes.constructors.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author dmilas
 */
public class TextTokenizer {


    public static String preprocess(String text) {
        return text.replaceAll("[^a-zA-z]", "").toLowerCase();
    }

    public static Document tokenize(String filename, String category) {
        Document doc = new Document();
        doc.category = category;
        try {
            Scanner filein = new Scanner(new File(filename));


            while (filein.hasNext()) {
                String temp = preprocess(filein.next());
                if (doc.tokens.containsKey(temp)) {
                    doc.tokens.replace(temp, doc.tokens.get(temp) + 1);
                } else {
                    doc.tokens.put(temp, 1);
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!!!");
        }

        return doc;
    }
}
