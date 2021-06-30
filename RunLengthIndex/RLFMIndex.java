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

        data = data.replace("\n", "");

        data = data.substring(0, 10000000);

        char[] result = new char[10000001];
        result[0] = '$';
        System.arraycopy(data.toCharArray(), 0, result, 1, result.length-1);

        data = null;

        System.out.println("________________");

        FMIndex fmIndex = new FMIndex(new String(result));

        //System.out.println("FM index needed: "+ /1024/1024 + " megabytes");

        char[] pattern = new char[]{'A', 'A', 'T', 'T', 'C', 'A', 'A', 'T', 'G', 'G', 'C'};
        //System.out.println(Arrays.toString(fmIndex.getRange(pattern)));
        System.out.println(Arrays.toString(fmIndex.locate(pattern)));

        System.out.println("_________________");

        RIndex rIndex = new RIndex(new String(result));

        //System.out.println("R index needed: "+ /1024/1024 + " megabytes");

        //System.out.println(Arrays.toString(rIndex.getRangeWithRIndex(pattern)));
        System.out.println(Arrays.toString(rIndex.locate(pattern)));

        Scanner scanner = new Scanner(System.in);

        while (true) {
            int current = scanner.nextInt();
            if(current==-1) break;
            else {
                char[] extract = new char[pattern.length];
                System.arraycopy(result, current, extract, 0, extract.length);
                System.out.println(Arrays.toString(extract));
            }
        }


        System.out.println();

    }

}
