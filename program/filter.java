import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class filter {

    public static void main(String[] args) {
        /* usage information */
        if(args.length != 2) {
            System.out.println("Usage: java filter traindir testfile");
            System.out.printf("  (You provided %d argument(s))\n\n", args.length);
            return;
        }

        train(args[0]);
        
        classify( readFile(args[1]) );
    }

    /**
    * Train our classifier
    * @param directory  The directory containing sample data
    * @return void
    */
    private static void train(String directory) {

    }

    /**
    *
    * @return "spam\n" if spam, "ham\n" otherwise
    */
    private static void classify(String message) {
        System.out.print("ham\n");
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

}