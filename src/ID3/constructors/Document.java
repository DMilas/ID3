/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes.constructors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dmilas
 */
public class Document {

    public Map<String, Integer> tokens;

    /**
     * The class of the document
     */
    public String category;

    /**
     * Document constructor
     */
    public Document() {
        tokens = new HashMap<>();
    }
}

