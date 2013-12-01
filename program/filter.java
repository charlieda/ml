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

    public static void main(String[] args) {
        /* usage information */
        if(args.length != 2) {
            System.out.println("Usage: java filter traindir testfile");
            System.out.printf("  (You provided %d argument(s))\n\n", args.length);
            return;
        }

        train(args[0]);

        System.err.println(wordCounts);
        
        classify( getWordCounts( readFile(args[1]) ) );
    }

    // the number of spam and ham documents
    private static int numSpam = 0;
    private static int numHam = 0;

    // total number of words we've seen
    private static int totalSpamWords = 0;
    private static int totalHamWords = 0;

    // table of word counts for each word we've seen
    // the size of this is the size of our vocabulary
    private static Map<String, Counts> wordCounts = new HashMap<String, Counts>();

    /**
    * Train our classifier
    * @param directory  The directory containing sample data
    * @return void
    */
    private static void train(String directory) {
        File dir = new File( directory );
        if( !dir.isDirectory() ) {
            System.err.println("Error: Supplied directory is not a directory.");
            return;
        }

        for( File f : dir.listFiles() ) {
            for( Map.Entry<String, Integer> entry : getWordCounts( readFile(f.toString()) ).entrySet() ) {
                String w = entry.getKey();

                if(wordCounts.get(w) == null) {
                    Counts c = new filter.Counts();
                    wordCounts.put(w, c);
                }

                Counts count = wordCounts.get(w);

                if(f.toPath().getName( f.toPath().getNameCount() - 1).toString().contains("spam") ) {
                    // this sample is a spam message
                    numSpam++;
                    count.spamCount += entry.getValue();
                } else {
                    numHam++;
                    count.hamCount += entry.getValue();
                }

                wordCounts.put(w, count);
            }
        }
    }

    private static double calculateLikelihoodRatio(Map<String, Integer> words) {

        // initialise our ratios to the prior distribution
        double hamLogRatio = Math.log((double)numHam / (numHam + numSpam));
        double spamLogRatio = Math.log((double)numSpam / (numHam + numSpam));

        // add likelihood ration for each word in our vocab
        for( Map.Entry<String, Counts> entry : wordCounts.entrySet() ) {
            String w = entry.getKey();
            int countInHam = entry.getValue().hamCount;
            int countInSpam = entry.getValue().spamCount;
            int vocabSize = wordCounts.size();

            if(words.containsKey(w)) {
                System.err.println(w);
                hamLogRatio += Math.log( Math.pow( (countInHam + 1.0) / (totalHamWords + vocabSize), words.get(w) ) );
                hamLogRatio += Math.log( Math.pow( (countInSpam + 1.0) / (totalSpamWords + vocabSize), words.get(w) ) );
            }
        }

        System.err.println(hamLogRatio - spamLogRatio);
        return hamLogRatio - spamLogRatio;
    }

    /**
    *   Classifies a set of words representing a document
    * @return "spam\n" if spam, "ham\n" otherwise
    */
    private static void classify(Map<String, Integer> words) {
        if(calculateLikelihoodRatio(words) > 0 ) {
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

    public static class Counts {
        public int spamCount;
        public int hamCount;
        public Counts() {
            this.spamCount = 0;
            this.hamCount = 0;
        }

        public String toString() {
            return "Spam: " + this.spamCount + " Ham: " + this.hamCount;
        }
    }
}