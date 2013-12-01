import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;

public class TrainClassifier {

    private static ArrayList<String> stopWords;

    public static void main(String[] args) {

        stopWords = new ArrayList<String>();
        stopWords.add("_");
        stopWords.add(":");
        stopWords.add(",");
        stopWords.add(".");
        stopWords.add("/");
        stopWords.add("the");
        stopWords.add("in");
        stopWords.add("of");
        stopWords.add(")");
        stopWords.add("(");

        if(args.length != 1) {
            System.err.println("Usage: java TrainClassifier directory");
            return;
        }

        System.out.println("Training classifier based on " + args[0]);

        NaiveBayesClassifier c = train(args[0]);

        try {
            FileOutputStream fileOut = new FileOutputStream("classifier.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(c);
            out.close();
            fileOut.close();
        } catch(IOException i) {
            i.printStackTrace();
        }

    }

    /**
    * Train our classifier
    * @param directory  The directory containing sample data
    * @return void
    */
    private static NaiveBayesClassifier train(String directory) {

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
            return null;
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
                if(!stopWords.contains(w)) {
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
        }

        return new NaiveBayesClassifier(numHam, numSpam, totalHamWords, totalSpamWords, wordCounts);
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