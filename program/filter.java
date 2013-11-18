import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

public class filter {

    public static void main(String[] args) {
        /* usage information */
        if(args.length != 2) {
            System.out.println("Usage: java filter traindir testfile");
            System.out.printf("  (You provided %d argument(s))\n\n", args.length);
            return;
        }

        /* initialise state */
        numSpam = 0;
        numHam = 0;
        wordCountSpam = new HashMap<String, Integer>();
        wordCountHam  = new HashMap<String, Integer>();

        train(args[0]);
        
        classify( textToWords( readFile(args[1]) ) );
    }

    private static int numSpam;
    private static int numHam;

    private static Map<String, Integer> wordCountSpam;
    private static Map<String, Integer> wordCountHam;

    /**
    * Train our classifier
    * @param directory  The directory containing sample data
    * @return void
    */
    private static void train(String directory) {

    }

    private static double calculateLikelihoodRatio(Set<String> words) {
        return 1.5;
    }

    /**
    *   Classifies a set of words representing a document
    * @return "spam\n" if spam, "ham\n" otherwise
    */
    private static void classify(Set<String> words) {
        if(calculateLikelihoodRatio(words) > 1 ) {
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
    * @return a set of words containted in text 
    */
    private static Set<String> textToWords(String text) {
        HashSet<String> toReturn = new HashSet<String>();
        Collections.addAll(toReturn, text.split(" "));
        return toReturn;
    }

}