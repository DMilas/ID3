package naivebayes.Functionalities;

import naivebayes.constructors.Stats;

import java.util.HashMap;
import java.util.Map;

public class StatCalculation {

    //Calculates the prior probability of an email belonging to either category
    public static HashMap<String, Double> calcPriors(Stats stats) {
        HashMap<String, Double> temp = new HashMap<>();
        for (Map.Entry<String, Integer> entry : stats.categoryCounts.entrySet()) {

            temp.putIfAbsent(entry.getKey(), entry.getValue() / (double) stats.n);
        }
        return temp;

    }

    public static HashMap<String, HashMap<String, Double>> calcLikelihood(Stats stats, Map<String, Double> priors) {
        HashMap<String, HashMap<String, Double>> temp = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : stats.categoryJointCount.entrySet()) {
            HashMap<String, Double> tempValue = new HashMap<>();
            for (Map.Entry<String, Integer> entryValue : entry.getValue().entrySet()) {

                tempValue.putIfAbsent(entryValue.getKey(),(entryValue.getValue()/(double)stats.wordcount)*priors.get(entry.getKey()));
            }
            temp.putIfAbsent(entry.getKey(),tempValue);
        }

        return temp;
    }
}
