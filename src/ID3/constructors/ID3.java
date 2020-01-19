package ID3.constructors;

import ID3.Functionalities.DataHandler;
import ID3.constructors.Tree;

import java.io.IOException;
import java.util.ArrayList;

public class ID3 {

    /**public static ArrayList<Double> fList = new ArrayList<>();
    public static ArrayList<Double> tempgainList;
    static String pathName;
    static int row;
    static int noofattributes;
    static String filename1, filename2, filename3;
    public static ArrayList<ArrayList<Integer>> inputArray = new ArrayList<>();
    public static ArrayList<Integer> inputsubArray = new ArrayList<>();
    public static Hashtable<String, String> partitionList = new Hashtable<>();
    public static ArrayList<ArrayList<Double>> gainList = new ArrayList<>();

    /**
     * @param args
     */

    public static int attributes;
    public static int examples;
    public static Tree decisionTree;
    public static int[] stringCount;
    public static String[][] inputStrings;
    public static String[][] indexedData;
    public static String[][] uniqueStrings;
    public static final String TRAINING_DATA="Training Data.txt";
    public static final String TEST_DATA="Test Data.txt";
    public static void train() {


        try {
            inputStrings=DataHandler.parseData(TRAINING_DATA);
            //inputData= DataHandler.parseData(TRAINING_DATA);
            /**readSourceFile(filename1);
            readPartitionFile(filename2);
             */
        } catch (IOException e) {
            e.printStackTrace();
        }
        train(inputStrings);


        //cal_Entropy();

       // partition();
//
    }
     public static void indexStrings(String[][] inputData) {
        indexedData = inputData;
        examples = indexedData.length;
        attributes = indexedData[0].length;
        stringCount = new int[attributes];
        uniqueStrings = new String[attributes][examples];// might not need all columns
        int index = 0;
        for (int attr = 0; attr < attributes; attr++) {
            stringCount[attr] = 0;
            for (int ex = 1; ex < examples; ex++) {
                for (index = 0; index < stringCount[attr]; index++)
                    if (indexedData[ex][attr].equals(uniqueStrings[attr][index]))
                        break;	// we've seen this String before
                if (index == stringCount[attr])		// if new String found
                    uniqueStrings[attr][stringCount[attr]++] = indexedData[ex][attr];
            } // for each example
        } // for each attribute
    }

    public static void train(String[][] trainingData){
        indexStrings(trainingData);
        decisionTree=createDecisionTree(new ArrayList<Integer>(),new ArrayList<Integer>());
    }

    private static Tree createDecisionTree(ArrayList<Integer> ignoredCols,ArrayList<Integer> ignoredRows){
        if(ignoredRows.size() == examples-1){
            //no rows left
            return null;
        }
        if(ignoredCols.size() >= attributes-1){
            //if no attributes are left
            return new Tree(null, getMostCommon(ignoredRows));
        }
        int cls = isSameClass(ignoredRows);
        if(cls != -1){
            //if all are same class
            return new Tree(null, cls);
        }
        int bestAttr = getBestAttr(ignoredCols, ignoredRows);
        System.out.println("best attr " + bestAttr);
        //add best attribute to ignored columns, because it cannot be reused
        ArrayList<Integer> bic = new ArrayList<Integer>(ignoredCols);
        bic.add(bestAttr);
        //create a branch for each string of this attribute
        Tree[] subsets = new Tree[stringCount[bestAttr]];
        for(int i=0; i<subsets.length; i++){
            //split data for each string of an attribute
            //method creates a shallow copy of the array, to cause data disintegrity
            ArrayList<Integer> bir = addIgnoredRows(bestAttr, i, ignoredRows);
            //pass new ignored columns and rows to the method
            subsets[i] = createDecisionTree(bic, bir);
            if(subsets[i] == null){
                subsets[i] = new Tree(null, getMostCommon(ignoredRows));
            }
        }
        return new Tree(subsets, bestAttr);
    }
    private static ArrayList<Integer> addIgnoredRows(int attr, int sId, ArrayList<Integer> ignoredRows){
        String string = uniqueStrings[attr][sId];
        if(string == null) return ignoredRows;
        ArrayList<Integer> newIgnored = new ArrayList<Integer>(ignoredRows);
        for(int row=1; row<examples; row++){
            //if string is not equal cell value it is added to ignored
            //but if it is already ignored, there is no need for it to be there twice
            if(!string.equals(indexedData[row][attr]) && !isIgnored(row, ignoredRows)){
                newIgnored.add(row);
            }
        }
        return newIgnored;
    }

    private static int isSameClass(ArrayList<Integer> ignoredRows){
        int cls = -1;
        boolean found = false;
        for(int row=1; row<examples || !found; row++){
            //find first not ignored row
            if(isIgnored(row, ignoredRows)) continue;
            //set class string as class of this row
            String clsString = indexedData[row][attributes-1];
            //find index of this class
            for(int clsId=0; clsId<stringCount[attributes-1]; clsId++){
                if(clsString.equals(uniqueStrings[attributes-1][clsId])){
                    cls = clsId;
                    found = true;
                    break;
                }
            }
        }
        //save class string to be reused for comparing
        String clsString = uniqueStrings[attributes-1][cls];
        //check each not ignored row, if all are the same
        for(int row=1; row<examples; row++){
            if(isIgnored(row, ignoredRows)) continue;
            if(!clsString.equals(indexedData[row][attributes-1])){
                //if class string of this row is different
                return -1;
            }
        }
        //return class that is same for all rows;
        return cls;
    }

    private static int getMostCommon(ArrayList<Integer> ignoredRows){
        int cCol = attributes-1;
        //count how many of each class in not ignored data
        int[] clsCount = new int[stringCount[cCol]];
        for(int row=1; row<examples; row++){
            if(isIgnored(row, ignoredRows)) continue;
            String cellClass = indexedData[row][cCol];
            for(int cls=0; cls<clsCount.length; cls++){
                //find class index
                if(cellClass.equals(uniqueStrings[cCol][cls])){
                    clsCount[cls]++;
                }
            }
        }
        //find highest number of all array
        int mostCommonId = 0;
        for(int cls=1; cls<clsCount.length; cls++){
            if(clsCount[cls] > clsCount[mostCommonId]){
                mostCommonId = cls;
            }
        }
        return mostCommonId;
    }

    private static void printCCount(int[][][] cCount){
        for(int[][] attr : cCount){
            System.out.println("Attribute");
            for(int[] string : attr){
                System.out.println("  String");
                for(int cCnt : string){
                    System.out.println("    Class count: " + cCnt);
                }
            }
        }
    }

    private static int getBestAttr(ArrayList<Integer> ignoredCols, ArrayList<Integer> ignoredRows){
        //-1 - because 1 is title row
        double s = getS(ignoredRows);
        int[][][] cCount = getClassCount(ignoredCols, ignoredRows);
        int totalRows = examples - 1 - ignoredRows.size();
        //printCCount(cCount);
        double bestGain = -1;
        int bestAttr = -1;
        //
        for(int attr=0; attr<cCount.length; attr++){
            //calculate for every not ignored attribute
            if(isIgnored(attr, ignoredCols)) continue;
            double gain = s;
            for(int[] string : cCount[attr]){
                //for each string in that attribute
                int stringTotal = 0; //total number of those strings in data
                for(int classSum : string){
                    stringTotal += classSum;
                }
                //calculate H(s)
                double entropy = 0.0;
                for(int classSum : string){
                    //class fraction of a string e.g.:
                    //2/3 = 2 rows are some class out of 3 rows of this string
                    double cfos = (double)classSum/(double)stringTotal;
                    entropy -= xlogx(cfos);
                }
                //gain -= (stringRows / allRows) * entropy
                //ratio of this string to total number of rows in data
                double ratio = ((double)stringTotal/(double)totalRows);
                gain -= ratio * entropy;
            }
            if(gain > bestGain){
                //if gain for this attribute is better than previous - set as new best attribute
                bestGain = gain;
                bestAttr = attr;
            }
        }
        return bestAttr;
    }

    private static boolean isIgnored(int number, ArrayList<Integer> ignoredVals){
        for(Integer ignored : ignoredVals){
            if(number == ignored) return true;
        }
        return false;
    }

    private static int[][][] getClassCount(ArrayList<Integer> ignoredCols, ArrayList<Integer> ignoredRows){
        int cCol = attributes-1; //class column
        int classes = stringCount[cCol];
        //[attribute][string][class]
        int[][][] cCount = new int[cCol][][];
        for(int attr=0; attr<cCol; attr++){
			/*
			find out if the column is to be ignored
			alternatively I had option to remove column from data, but that
			created multiple copies of smaller and smaller data, plus it required
			twice as many iterations
			*/
            if(isIgnored(attr, ignoredCols)) continue;
            //System.out.println("[ATTR]");
            int attrStrings = stringCount[attr];
            cCount[attr] = new int[attrStrings][classes];
            //check each row, without title row (1st row)
            for(int row=1; row<examples; row++){
                //find out if the row should be ignored
                if(isIgnored(row, ignoredRows)) continue;
                //data[row][attr];
                String cell = indexedData[row][attr];
                String rowClass = indexedData[row][cCol];
                int stringId = 0;
                for(String val : uniqueStrings[attr]){
                    if(val == null) continue;
					/*
					1) Try matching each string for this attribute with cell value
						 to find string index
					*/
                    if(cell.equals(val)){
                        //matched cell value with one of the attribute string
                        int classId = 0;
                        for(String cls : uniqueStrings[cCol]){
                            if(cls == null) continue;
							/*
							2) Try matching each string for class with class of this col
								 to find index of this class
							*/
                            if(rowClass.equals(cls)){
                                cCount[attr][stringId][classId]++;
                            }
                            classId++;
                        }
                    }
                    stringId++;
                }
            }
        }
        return cCount;
    }

    private static double getS(ArrayList<Integer> ignoredRows){
        int cCol = attributes-1;
        int[] cCnt = new int[stringCount[cCol]];
        for(int row=1; row<examples; row++){
            //check if row is ignored
            if(isIgnored(row, ignoredRows)) continue;
            String rowCls = indexedData[row][cCol];
            //find which class matches class of a row
            for(int c=0; c<cCnt.length; c++){
                if(rowCls.equals(uniqueStrings[cCol][c])){
                    cCnt[c]++;
                }
            }
        }
        double s = 0.0;
        int totalRows = examples - 1 - ignoredRows.size();
        for(Integer sum : cCnt){
            double fraction = ((double)sum/totalRows);
            s -= xlogx(fraction);
        }
        return s;
    }
    public static void printTree() {
        if (decisionTree == null)
            error("Attempted to print null Tree");
        else
            System.out.println(decisionTree);
    } // printTree()

    /** Print error message and exit. **/
    static void error(String msg) {
        System.err.println("Error: " + msg);
        System.exit(1);
    } // error()

    static final double LOG2 = Math.log(2.0);

    static double xlogx(double x) {
        return x == 0? 0: x * Math.log(x) / LOG2;
    }

    /** Given a 2-dimensional array containing the training data, numbers each
     *  unique value that each attribute has, and stores these Strings in
     *  instance variables; for example, for attribute 2, its first value
     *  would be stored in strings[2][0], its second value in strings[2][1],
     *  and so on; and the number of different values in stringCount[2].
     **/
    ;

    /** For debugging: prints the list of attribute values for each attribute
     *  and their index values.
     **/
    void printStrings() {
        for (int attr = 0; attr < attributes; attr++)
            for (int index = 0; index < stringCount[attr]; index++)
                System.out.println(indexedData[0][attr] + " value " + index +
                        " = " + uniqueStrings[attr][index]);
    } // printStrings()

    public ID3(){}


    public static void classify(String[][] testData) {
        if (decisionTree == null)
            error("Please run training phase before classification");
        // PUT  YOUR CODE HERE FOR CLASSIFICATION
        for(int row=1; row<testData.length; row++){
            int result = classify(testData[row], decisionTree);
            System.out.println(uniqueStrings[attributes-1][result]);
        }
    } // classify()

    private static int classify(String[] testData, Tree tree){
        //only row is passed to this method
        //if reached tree leaf node
        if(tree.children == null){
            return tree.value;
        }
        //get string of selected attribute for this row
        String string = testData[tree.value];
        //find string number, to follow right branch
        for(int branch=0; branch<stringCount[tree.value]; branch++){
            if(string.equals(uniqueStrings[tree.value][branch])){
                return classify(testData, tree.children[branch]);
            }
        }
		/*
		this would happen if training data didn't have a string that was
		in the test data
		*/
        return 0;
    }

    public static void classify(){
        String[][] tempStrings=null;
        try {
            tempStrings=DataHandler.parseData(TEST_DATA);

        } catch (IOException e) {
            e.printStackTrace();
        }
        classify(tempStrings);
    }

}
/*










































    public static void calEntropy(){

    }

    public static void cal_Entropy() {
        int tempRow;

        ArrayList<Double> tempEntropyList;

        for (Map.Entry<String, String> entry : partitionList.entrySet()) {

            tempEntropyList = new ArrayList<>();
            tempgainList = new ArrayList<>();

            String[] temp = entry.getValue().split(" ");
            tempRow = temp.length;
            int nozero = 0, noone = 0;
            for (int i = 0; i < temp.length; i++) {

                if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 1) {
                    noone++;
                } else if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 0) {
                    nozero++;
                }
            }
            double first, second;
            if (nozero == 0 || noone == 0) {
                first = 0;
                second = 0;
            } else {
                first = (double) (((double) nozero / (double) tempRow) * (Math.log10(tempRow / nozero) / Math.log10(2)));
                second = (double) (((double) noone / (double) tempRow) * (Math.log10(tempRow / noone) / Math.log10(2)));
            }
            double entropy = first + second;

            for (int j = 0; j < noofattributes - 1; j++) {
                int subzero = 0, subone = 0, subtwo = 0;
                int sub_zero_target_zero = 0, sub_zero_target_one = 0, sub_zero_target_two, sub_one_target_zero = 0, sub_one_target_one = 0, sub_one_target_two, sub_two_target_zero = 0, sub_two_target_one = 0, sub_two_target_two;
                for (int i = 0; i < temp.length; i++) {
                    if (inputArray.get(j).get(Integer.parseInt(temp[i]) - 1) == 1) {
                        subone++;
                        if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 0) {
                            sub_one_target_zero++;
                        } else if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 1) {
                            sub_one_target_one++;
                        }
                    } else if (inputArray.get(j).get(Integer.parseInt(temp[i]) - 1) == 0) {
                        subzero++;
                        if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 0) {
                            sub_zero_target_zero++;
                        } else if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 1) {
                            sub_zero_target_one++;
                        }
                    } else if (inputArray.get(j).get(Integer.parseInt(temp[i]) - 1) == 2) {
                        subtwo++;
                        if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 0) {
                            sub_two_target_zero++;
                        } else if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 1) {
                            sub_two_target_one++;
                        }
                    }
                }
                double sub_entropy, first_half, second_half, third_half;

                if (subzero == 0 || sub_zero_target_one == 0 || sub_zero_target_zero == 0) {
                    first_half = 0;
                } else {
                    first_half = (double) ((double) subzero / (double) tempRow) * ((((double) sub_zero_target_zero / (double) subzero) * (Math.log10((subzero) / (double) sub_zero_target_zero) / Math.log10(2))) + (((double) sub_zero_target_one / (double) subzero) * (Math.log10((subzero) / (double) sub_zero_target_one) / Math.log10(2))));
                }

                if (subone == 0 || sub_one_target_one == 0 || sub_one_target_zero == 0) {
                    second_half = 0;
                } else {
                    second_half = (double) ((double) subone / (double) tempRow) * ((((double) sub_one_target_zero / (double) subone) * (Math.log10((subone) / (double) sub_one_target_zero) / Math.log10(2))) + (((double) sub_one_target_one / (double) subone) * (Math.log10((subone) / (double) sub_one_target_one) / Math.log10(2))));
                }

                if (subtwo == 0 || sub_two_target_one == 0 || sub_two_target_zero == 0) {
                    third_half = 0;
                } else {
                    third_half = (double) ((double) subtwo / (double) tempRow) * ((((double) sub_two_target_zero / (double) subtwo) * (Math.log10((subtwo) / (double) sub_two_target_zero) / Math.log10(2))) + (((double) sub_two_target_one / (double) subtwo) * (Math.log10((subtwo) / (double) sub_two_target_one) / Math.log10(2))));
                }

                sub_entropy = first_half + second_half + third_half;
                tempgainList.add(entropy - sub_entropy);
            }
            gainList.add(tempgainList);

            double maxGain = 0.0;
            for (int i = 0; i < tempgainList.size(); i++) {
                maxGain = Math.max(maxGain, tempgainList.get(i));
            }
            double f = ((float) tempRow / (float) row) * maxGain;
            fList.add(f);
        }
    }

    private static void partition() {
        double maxF = 0.0;
        int index = 0;
        for (int i = 0; i < fList.size(); i++) {
            if (fList.get(i) > maxF) {
                maxF = fList.get(i);
                index = i;
            }
        }

        double maxGain = 0.0;
        int maxAtrributeIndex = 0;
        for (int j = 0; j < gainList.get(index).size(); j++) {
            if (gainList.get(index).get(j) > maxGain) {
                maxGain = gainList.get(index).get(j);
                maxAtrributeIndex = j;
            }
        }
        String group = partitionList.values().toArray()[index].toString();
        String tempGroup[] = group.split(" ");

        Iterator temp = partitionList.entrySet().iterator();

        String str = "";

        String Final = "";
        int count = 0;


        while (temp.hasNext()) {
            Map.Entry pairs = (Map.Entry) temp.next();

            if (count == index) {
                str = pairs.getKey().toString();
            } else {
                Final += pairs.getKey().toString() + " " + pairs.getValue().toString() + "\n";
            }
            count++;
        }

        String zeroGroup = str + "0 ", oneGroup = str + "1 ", twoGroup = str + "2 ";
        for (int i = 0; i < tempGroup.length; i++) {
            if (inputArray.get(maxAtrributeIndex).get(Integer.parseInt(tempGroup[i]) - 1) == 0) {
                zeroGroup += tempGroup[i] + " ";
            } else if (inputArray.get(maxAtrributeIndex).get(Integer.parseInt(tempGroup[i]) - 1) == 1) {
                oneGroup += tempGroup[i] + " ";
            } else if (inputArray.get(maxAtrributeIndex).get(Integer.parseInt(tempGroup[i]) - 1) == 2) {
                twoGroup += tempGroup[i] + " ";
            }
        }

        String outputString = "Partition " + str + " was replaed with partitions ";

        if (!zeroGroup.equalsIgnoreCase(str + "0")) {
            Final += zeroGroup;
            outputString += str + "0 ";
        }
        if (!oneGroup.equalsIgnoreCase(str + "1")) {
            Final += "\n" + oneGroup;
            outputString += str + "1 ";
        }
        if (!twoGroup.equalsIgnoreCase(str + "2")) {
            Final += "\n" + twoGroup;
            outputString += str + "2 ";
        }

        zeroGroup += "\n";
        oneGroup += "\n";
        twoGroup += "\n";

        outputString += " using Feature " + (maxAtrributeIndex + 1);
        System.out.println(outputString);

        try {
            FileWriter fstream = new FileWriter(pathName + "\\" + filename3);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(Final);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}
*/