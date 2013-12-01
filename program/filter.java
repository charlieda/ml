import java.io.*;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;

public class filter {

    public static void main(String[] args) {
        /* usage information */
        if(args.length != 1) {
            System.out.println("Usage: java filter testfile");
            System.out.printf("  (You provided %d argument(s))\n\n", args.length);
            return;
        }

        // load classifier

        NaiveBayesClassifier c;
        try {
            FileInputStream fileIn = new FileInputStream("classifier.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            c = (NaiveBayesClassifier) in.readObject();
            in.close();
            fileIn.close();
        } catch(IOException i) {
            i.printStackTrace();
            return;
        } catch(ClassNotFoundException cnf) {
            System.out.println("NaiveBayesClassifier class not found");
            cnf.printStackTrace();
            return;
        }

        // classify given file

        if(c.getLikelihoodRatio( getWordCounts( readFile(args[0]) ) ) > 0 ) {
            System.out.print("ham\n");
        } else {
            System.out.print("spam\n");
        }
    }

    private static String readFile(String filename) {
        File messageFile = new File( filename );
        StringBuilder message = new StringBuilder((int)messageFile.length());
        Scanner scanner;
        try {
            scanner = new Scanner(messageFile);
        } catch (FileNotFoundException e) {
            System.err.printf("Error: Couldn't read message file '%s'\nExiting...\n", filename);
            return "";
        }
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {        
                message.append(scanner.nextLine() + lineSeparator);
            }
        } finally {
            scanner.close();
        }
        return message.toString();
    }

    /**
    * @return a map of word -> counts, based on how many times each word appears in the text
    */
    private static Map<String, Integer> getWordCounts(String text) {
        Map<String, Integer> toReturn = new HashMap<String, Integer>();
        for(String w : text.split(" ")) {
            w = w.trim();
            if( !toReturn.containsKey( w ) ) {
                toReturn.put(w, 0);
            }
            toReturn.put(w, toReturn.get(w) + 1);
        }
        return toReturn;
    }

}