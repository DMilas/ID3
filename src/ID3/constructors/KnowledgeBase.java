/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes.constructors;

import naivebayes.Functionalities.StatCalculation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dmilas
 */
public class KnowledgeBase {
    //Number of training observations
    public int n = 0;
    //Number of Categories
    public int c = 0;
    //Number of Features
    public int f = 0;
    //Log priors  for log(P(c))
    public Map<String, Double> logPriors = new HashMap<>();
    //Log likelihood for P(x|c)
    public Map<String, HashMap<String, Double>> logLikelihood = new HashMap<>();

    public KnowledgeBase(Stats stats) {
        this.n = stats.n;
        this.c = stats.categoryCounts.size();
        this.logPriors = StatCalculation.calcPriors(stats);
        this.logLikelihood=StatCalculation.calcLikelihood(stats,logPriors);
    }

    public KnowledgeBase() {
    }
}
