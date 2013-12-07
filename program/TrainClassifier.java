import java.io.*;
import java.util.*;

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

    // the number of spam and ham documents
    private static int numSpam = 0;
    private static int numHam = 0;

    // total number of words we've seen
    private static int totalSpamWords = 0;
    private static int totalHamWords = 0;

    private static int vocabSize = 0;

    /**
    * Train our classifier
    * @param directory  The directory containing sample data
    * @return void
    */
    private static NaiveBayesClassifier train(String directory) {

        // table of word counts for each word we've seen
        // the size of this is the size of our vocabulary
        Map<String, NaiveBayesClassifier.Counts> wordCounts = new HashMap<String, NaiveBayesClassifier.Counts>();

        File dir = new File( directory );
        if( !dir.isDirectory() ) {
            System.err.println("Error: Supplied directory is not a directory.");
            return null;
        }

        for( File f : dir.listFiles() ) {
            boolean isSpam = f.toPath().getName( f.toPath().getNameCount() - 1).toString().contains("spam");
            // add to number of documents seen
            if( isSpam ) {
                numSpam++;
            } else {
                numHam++;
            }

            // add up word counts
            for( Map.Entry<String, Integer> entry : getWordCounts( readFile(f.toString()) ).entrySet() ) {
                String w = entry.getKey();
                if(!stopWords.contains(w)) {

                    if(wordCounts.get(w) == null) {
                        // we haven't seen this word yet.
                        NaiveBayesClassifier.Counts c = new NaiveBayesClassifier.Counts();
                        wordCounts.put(w, c);
                    }

                    NaiveBayesClassifier.Counts count = wordCounts.get(w);

                    if( isSpam ) {
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

        vocabSize = wordCounts.size();
        if(vocabSize < 20) {
            printTopWords( wordCounts );
        }

        // cull words that aren't a good idicator of spam or ham
        ArrayList<String> toRemove = new ArrayList<String>();
        double threshold = 0.00000001;
        for( Map.Entry<String, NaiveBayesClassifier.Counts> entry : wordCounts.entrySet() ) {

            if(getUsefulness(entry.getValue()) < threshold) {
                totalSpamWords -= entry.getValue().spamCount;
                totalHamWords -= entry.getValue().hamCount;
                toRemove.add(entry.getKey());
            }
        }
        wordCounts.keySet().removeAll(toRemove);
        System.out.println("Removed " + toRemove.size() + " words of < " + threshold + " usefulness. (" + wordCounts.size() + " left)");

        return new NaiveBayesClassifier(numHam, numSpam, totalHamWords, totalSpamWords, wordCounts);
    }

    /// (count(w) in ham + 1) / (totalHamWords + |V|)

    private static void printTopWords( Map<String, NaiveBayesClassifier.Counts> c) {
        System.out.println("totalSpamWords = " + totalSpamWords );
        System.out.println("totalHamWords  = " + totalHamWords  );
        System.out.println("vocabSize = " + vocabSize);
        List<Map.Entry> a = new ArrayList<Map.Entry>(c.entrySet());
        Collections.sort(a,
                 new Comparator() {
                     public int compare(Object o1, Object o2) {
                         Map.Entry<String, NaiveBayesClassifier.Counts>  e1 = (Map.Entry) o1;
                         Map.Entry<String, NaiveBayesClassifier.Counts>  e2 = (Map.Entry) o2;

                         double e1Usefulness = getUsefulness(e1.getValue());
                         double e2Usefulness = getUsefulness(e2.getValue());

                         return ((Comparable) new Double(e1Usefulness)).compareTo(e2Usefulness);
                     }
                 });

        for (Map.Entry<String, NaiveBayesClassifier.Counts> e : a) {
                double prob = (double)(e.getValue().spamCount + 1.0) / (double)(totalSpamWords + vocabSize);
                System.out.println(String.format("%30s", "'" + e.getKey() + "'") + "\t" + getUsefulness(e.getValue()) + "\t" + prob);
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

    // returns how "useful" a particular set of counts it
    // higher values = more useful
    private static double getUsefulness(NaiveBayesClassifier.Counts c) {
        double spamProb = (double)(c.spamCount + 1.0) / (double)(totalSpamWords + vocabSize);
        double hamProb = (double)(c.hamCount + 1.0) / (double)(totalHamWords + vocabSize);

        return (spamProb - hamProb) * (spamProb - hamProb);
    }

    /**
    * @return a map of word -> counts, based on how many times each word appears in the text
    */
    public static Map<String, Integer> getWordCounts(String text) {
        Map<String, Integer> toReturn = new HashMap<String, Integer>();
        for(String w : filter.getWords(text)) {
            if(w.length() > 1) {
                if( !toReturn.containsKey( w ) ) {
                    toReturn.put(w, 0);
                }
                toReturn.put(w, toReturn.get(w) + 1);
            }
        }
        return toReturn;
    }
}