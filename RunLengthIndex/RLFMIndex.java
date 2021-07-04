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

        /*

        char[] options = {'A', 'C', 'G', 'T'};
        char[] result =  new char[100000];
        Random r = new Random();
        //result[0] = '$';
        for(int i=0;i<result.length;i++){
            //result[i]= 'A';
            //if(i%100000==0) result[i]='C';
            result[i]=options[r.nextInt(options.length)];
        }

        StringBuilder string = new StringBuilder(new String(result));

        String toBeAdded = string.toString();

        string.append(toBeAdded.repeat(9));

        String input = '$' + string.toString();

         */

        String data = getReadFile("C:\\Users\\Admin\\Downloads\\dna\\dna");

        //data = data.replace("\n", "");

        data = data.substring(0, 10000000);

        data = Character.MIN_VALUE + data;

        /*
        System.out.println("Length of the file is: " + data.length());

        char[] T = data.toCharArray();
        int n = T.length;
        char[] bwt = new char[n];
        char[] V = new char[n];
        int[] occArray = new int[n];

        int pidx = sais.bwtransform(T, bwt, occArray, n);

        int m = T.length;
        int[] suffixes = new int[m];

        sais.suffixsort(T, suffixes, m);
        suffixes[0] = m;

        unbwt(bwt, V, occArray, n, pidx);

        for (int i=0; i<occArray.length; i++)
            occArray[i] += 1;


        int runLength = 1;
        for (int i=1; i< bwt.length; i++) {
            if(bwt[i]!=bwt[i-1]) {
                runLength++;
            }
        }

        System.out.println("Run length of the bwt of the file is: " + runLength);

         */

        //char[] result = new char[10000001];
        //result[0] = '$';
        //System.arraycopy(data.toCharArray(), 0, result, 1, result.length-1);

        //data = null;

        //System.out.println("________________");

        FMIndex fmIndex = new FMIndex(data);
        System.out.println("Done with constructing the FM index");

        //System.out.println("FM index needed: "+ /1024/1024 + " megabytes");

        char[] pattern = new char[]{'A', 'A', 'G', 'T', 'T', 'A', 'A', 'C', 'C', 'A'};
        //char[] pattern = "<title>Java Language Home Page</title>".toCharArray();
        //System.out.println(Arrays.toString(fmIndex.getRange(pattern)));
        System.out.println(Arrays.toString(fmIndex.locate(pattern)));

        //System.out.println("_________________");

        RIndex rIndex = new RIndex(data);
        System.out.println("Done with constructing the run length index");

        //System.out.println("R index needed: "+ /1024/1024 + " megabytes");

        //System.out.println(Arrays.toString(rIndex.getRangeWithRIndex(pattern)));
        System.out.println(Arrays.toString(rIndex.locate(pattern)));

        Scanner scanner = new Scanner(System.in);

        while (true) {
            int current = scanner.nextInt();
            if(current==-1) break;
            else {
                char[] extract = new char[pattern.length];
                System.arraycopy(data.toCharArray(), current, extract, 0, extract.length);
                System.out.println(Arrays.toString(extract));
                //System.out.println("Enter -1 to end the program");
            }
        }

        System.out.println();

    }

}
