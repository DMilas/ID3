package ID3.Functionalities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class DataHandler {

    public static String[] tokenize(String input){
        return input.split("\t");
    }

    public static String[][] parseData(String filename) throws FileNotFoundException, IOException {
        Scanner filein=new Scanner(new File(filename));
        Path path = Paths.get(filename);
        int lineCount = ((int) Files.lines(path).count());
        String[][] temp =new String[lineCount-1][5];
        filein.nextLine();
        for(int i=0;;i++){
            String[]tempStrings=tokenize(filein.nextLine());
            for(int j=0;j<tempStrings.length;j++){
                temp[i][j]=tempStrings[j];
            }
            if(!filein.hasNextLine())break;
        }

        return temp;
    }
}
