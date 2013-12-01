import java.util.Map;
import java.lang.Math;

public class NaiveBayesClassifier implements java.io.Serializable {

    // the number of spam and ham documents
    private int numSpam = 0;
    private int numHam = 0;

    // total number of words we've seen 
    private int totalSpamWords = 0;
    private int totalHamWords = 0;

    // table of word counts for each word we've seen
    // the size of this is the size of our vocabulary
    private Map<String, Counts> wordCounts;

    public NaiveBayesClassifier(int numHam, int numSpam, int totalHamWords, int totalSpamWords, Map<String, Counts> wordCounts) {
        this.numHam = numHam;
        this.numSpam = numSpam;
        this.totalSpamWords = totalSpamWords;
        this.totalHamWords = totalHamWords;
        this.wordCounts = wordCounts;
    }

    public double getLikelihoodRatio( Map<String, Integer> words) {
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
                // System.err.println(w);
                //System.err.println("ln( (" + (countInHam + 1) + " / " + (totalHamWords + vocabSize) + ") ^ " + words.get(w) + ")");
                // hamLogRatio += Math.log( Math.pow( (countInHam + 1.0) / (totalHamWords + vocabSize), words.get(w) ) );
                // spamLogRatio += Math.log( Math.pow( (countInSpam + 1.0) / (totalSpamWords + vocabSize), words.get(w) ) );
                hamLogRatio += logPow( (countInHam + 1.0) / (totalHamWords + vocabSize) , words.get(w));
                spamLogRatio += logPow((countInSpam + 1.0) / (totalSpamWords + vocabSize), words.get(w));

                //System.err.println("Likelihood ratio: " + (hamLogRatio - spamLogRatio) + " after " + w);
            }
        }
        System.err.println("Final Likelihood ratio: " + (hamLogRatio - spamLogRatio) );
        return hamLogRatio - spamLogRatio;
    }

    /**
    * @return ln( value ^ exp )
    */
    private static double logPow( double value, double exp) {
        double ret = 0;
        for(int i = 0; i < exp; i++) {
            ret += Math.log(value);
        }
        return ret;
    }

    public static class Counts implements java.io.Serializable {
        public int spamCount;
        public int hamCount;
        public Counts() {
            this.spamCount = 0;
            this.hamCount = 0;
        }

        public String toString() {
            return "Spam: " + this.spamCount + " Ham: " + this.hamCount + "\n";
        }
    }
}