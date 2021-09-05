import org.openjdk.jol.info.GraphLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RLFMIndex {

    // code to get the occurrences array.
    private static void unbwt(char[] T, char[] U, int[] LF, int n, int pidx) {
        int[] C = new int[256];
        int i, t;
        for(i = 0; i < 256; ++i) { C[i] = 0; }
        for(i = 0; i < n; ++i) { LF[i] = C[(int)(T[i] & 0xff)]++; }
        for(i = 0, t = 0; i < 256; ++i) { t += C[i]; C[i] = t - C[i]; }
        for(i = n - 1, t = 0; 0 <= i; --i) {
            t = LF[t] + C[(int)((U[i] = T[t]) & 0xff)];
            t += (t < pidx) ? 1 : 0;
        }
        C = null;
    }


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

        GraphLayout.parseInstance((Object) new int[]{1, 2, 3}).totalSize();

        String data = "";
        char[] pattern = "".toCharArray();

        System.out.println();

        if (args.length==1) {
            data = getReadFile(args[0]);
        }
        else if (args.length==2) {
            data = getReadFile(args[0]);
            pattern = args[1].toCharArray();
        } else if (args.length==3) {
            data = getReadFile(args[0]);
            pattern = args[1].toCharArray();
            data = data.substring(0, Integer.parseInt(args[2]));
        } else {
            System.out.println("args[0] -> Enter the path of the file to be indexed.");
            System.out.println("args[1] -> (Optional) Enter a pattern to search for.");
            System.out.println("args[2] -> (Optional) Enter up to which character you want index.");
            System.exit(0);
        }

        data = Character.MIN_VALUE + data;

        System.out.println();

        System.out.println(".................................................");

        System.out.println();

        long startTime = System.currentTimeMillis();

        FMIndex fmIndex = new FMIndex(data);

        long endTime = System.currentTimeMillis();

        System.out.println("Done with constructing the FM index. That took " + (endTime - startTime)/1000 + " seconds.");

        System.out.println("The size of the FM index is: " +  GraphLayout.parseInstance(fmIndex).totalSize()/1024/1024 + " megabytes.");

        System.out.println("Size (Bits per symbol) of the FM index is: " + (double) GraphLayout.parseInstance(fmIndex).totalSize()/data.length());

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

        RIndex rIndex = new RIndex(data);

        endTime = System.currentTimeMillis();

        System.out.println("Done with constructing the run length index. That took " + (endTime - startTime)/1000 + " seconds.");

        System.out.println("The size of the r index is: " +  GraphLayout.parseInstance(rIndex).totalSize()/1024/1024 + " megabytes.");

        System.out.println("Size (Bits per symbol) of the r index is: " + (double) GraphLayout.parseInstance(rIndex).totalSize()/data.length());

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

        System.out.println();

    }

}
