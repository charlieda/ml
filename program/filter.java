import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;

public class filter {

    public static NaiveBayesClassifier classifier;

    public static void main(String[] args) {
        /* usage information */
        if(args.length != 2) {
            System.out.println("Usage: java filter traindir testfile");
            System.out.printf("  (You provided %d argument(s))\n\n", args.length);
            return;
        }

        train(args[0]);
        
        classify( getWordCounts( readFile(args[1]) ) );
    }

    /**
    * Train our classifier
    * @param directory  The directory containing sample data
    * @return void
    */
    private static void train(String directory) {

        // the number of spam and ham documents
        int numSpam = 0;
        int numHam = 0;

        // total number of words we've seen
        int totalSpamWords = 0;
        int totalHamWords = 0;

        // table of word counts for each word we've seen
        // the size of this is the size of our vocabulary
        Map<String, NaiveBayesClassifier.Counts> wordCounts = new HashMap<String, NaiveBayesClassifier.Counts>();

        File dir = new File( directory );
        if( !dir.isDirectory() ) {
            System.err.println("Error: Supplied directory is not a directory.");
            return;
        }

        for( File f : dir.listFiles() ) {
            // add to number of documents seen
            if(f.toPath().getName( f.toPath().getNameCount() - 1).toString().contains("spam") ) {
                numSpam++;
            } else {
                numHam++;
            }

            // add up word counts
            for( Map.Entry<String, Integer> entry : getWordCounts( readFile(f.toString()) ).entrySet() ) {
                String w = entry.getKey();

                if(wordCounts.get(w) == null) {
                    NaiveBayesClassifier.Counts c = new NaiveBayesClassifier.Counts();
                    wordCounts.put(w, c);
                }

                NaiveBayesClassifier.Counts count = wordCounts.get(w);

                if(f.toPath().getName( f.toPath().getNameCount() - 1).toString().contains("spam") ) {
                    // this sample is a spam message
                    count.spamCount += entry.getValue();
                    totalSpamWords += entry.getValue();
                } else {
                    count.hamCount += entry.getValue();
                    totalHamWords += entry.getValue();
                }

                wordCounts.put(w, count);
            }
        }

        classifier = new NaiveBayesClassifier(numHam, numSpam, totalHamWords, totalSpamWords, wordCounts);
    }

    /**
    *   Classifies a set of words representing a document
    * @return "spam\n" if spam, "ham\n" otherwise
    */
    private static void classify(Map<String, Integer> words) {
        if(classifier.getLikelihoodRatio(words) > 0 ) {
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