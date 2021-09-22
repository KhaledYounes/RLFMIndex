import org.openjdk.jol.info.GraphLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RLFMIndex {

    private static String getReadFile (String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static void main(String[] args) {

        boolean test = false;

        String data = "";

        char[] pattern = "".toCharArray();

        int sample = 64;

        System.out.println();

        try {

            if (args.length==1) {
                data = getReadFile(args[0]);
            }
            else if (args.length==2) {
                data = getReadFile(args[0]);
                pattern = args[1].toCharArray();
            } else if (args.length==3) {
                data = getReadFile(args[0]);
                pattern = args[1].toCharArray();
                if (!args[2].equals("full")) {
                    data = data.substring(0, Integer.parseInt(args[2]));
                }
            } else if (args.length==4){
                data = getReadFile(args[0]);
                pattern = args[1].toCharArray();
                if (!args[2].equalsIgnoreCase("full")) {
                    data = data.substring(0, Integer.parseInt(args[2]));
                }
                if (!args[3].equalsIgnoreCase("no")) {
                    sample = Integer.parseInt(args[3]);
                }
            } else if (args.length==5) {
                data = getReadFile(args[0]);
                pattern = args[1].toCharArray();
                if (!args[2].equalsIgnoreCase("full")) {
                    data = data.substring(0, Integer.parseInt(args[2]));
                }
                if (!args[3].equalsIgnoreCase("no")) {
                    sample = Integer.parseInt(args[3]);
                }
                if (args[4].equalsIgnoreCase("test")) {
                    test = true;
                }
            } else {
                throw new IllegalArgumentException();
            }

        } catch (Exception exception) {
            System.out.println("args[0] -> Enter the path of the file to be indexed.");
            System.out.println("args[1] -> (Optional) Enter a pattern to search for.");
            System.out.println("args[2] -> (Optional) Enter up to which character you want index or full to index the whole text.");
            System.out.println("args[3] -> (Optional) Enter the grade of the sampling.");
            System.out.println("args[4] -> (Optional) Enter test, if you want to test the results");
            System.out.println();
            System.exit(0);
        }

        System.out.println();

        System.out.println("Input's length: " + data.length());

        System.out.println();

        data = Character.MIN_VALUE + data;

        System.out.println(".................................................");

        System.out.println();

        long startTime = System.currentTimeMillis();

        FMIndex fmIndex = new FMIndex(data, sample);

        long endTime = System.currentTimeMillis();

        System.out.println("Done with constructing the FM index. That took " + (endTime - startTime)/1000 + " seconds.");

        System.out.println("The size of the FM index is: " +  GraphLayout.parseInstance(fmIndex).totalSize()/1024/1024 + " megabytes.");

        System.out.println("Size (Bits per symbol) of the FM index is: " + (double) GraphLayout.parseInstance(fmIndex).totalSize()*8/data.length());

        startTime = System.nanoTime();

        int [] fmLocate = fmIndex.locate(pattern);

        endTime = System.nanoTime();

        System.out.println(Arrays.toString(fmLocate));

        if (fmLocate.length != 0) {
            System.out.println("Time taken by the query with FM index equals: " + ( (endTime - startTime)/1000.0) / fmLocate.length + " microseconds per occurrence.");
        }

        System.out.println();

        System.out.println(".................................................");

        System.out.println();

        startTime = System.currentTimeMillis();

        RIndex rIndex = new RIndex(data, sample, false);

        endTime = System.currentTimeMillis();

        System.out.println("Done with constructing the run length index sequentially. That took " + (endTime - startTime)/1000 + " seconds.");

        System.out.println();

        startTime = System.currentTimeMillis();

        RIndex rIndexParallel = new RIndex(data, sample, true);

        endTime = System.currentTimeMillis();

        System.out.println("Done with constructing the run length index in parallel. That took " + (endTime - startTime)/1000 + " seconds.");

        System.out.println();

        System.out.println("Runs to length (n/r): " + ((double) data.length()/rIndexParallel.getPreData().length) );

        System.out.println("The size of the r index is: " +  GraphLayout.parseInstance(rIndex).totalSize()/1024/1024 + " megabytes.");

        System.out.println("Size (Bits per symbol) of the r index is: " + (double) GraphLayout.parseInstance(rIndex).totalSize()*8/data.length());

        startTime = System.nanoTime();

        int[] rLocate = rIndex.locate(pattern);

        endTime = System.nanoTime();

        System.out.println(Arrays.toString(rLocate));

        if (rLocate.length != 0) {
            System.out.println("Time taken by the query with r index equals: " + ((endTime - startTime)/1000.0) / rLocate.length + " microseconds per occurrence.");
        }

        System.out.println();

        System.out.println(".................................................");

        System.out.println();

        if (test) {

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Enter -1 to exit or an index of the results array to make sure that the result at this index is correct.");
                int current = scanner.nextInt();
                //Enter -1 to end the program
                if(current==-1) break;
                else {
                    char[] extract = new char[pattern.length];
                    System.arraycopy(data.toCharArray(), current, extract, 0, extract.length);
                    System.out.println(Arrays.toString(extract));
                }
            }

        }

        System.out.println("End...");

        System.out.println();

    }

}
