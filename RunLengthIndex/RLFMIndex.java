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

        long beforeUsedMem, afterUsedMem;

        /*
        char[] options = {'A', 'C', 'G', 'T'};
        char[] result =  new char[100000];
        Random r = new Random();
        result[0] = '$';
        for(int i=1;i<result.length;i++){
            //result[i]= 'A';
            //if(i%100000==0) result[i]='C';
            result[i]=options[r.nextInt(options.length)];
        }

         */

        String data = getReadFile("C:\\Users\\Admin\\Downloads\\dna\\dna");

        data = data.replace("\n", "");

        char[] result = new char[1000001];
        result[0] = '$';
        System.arraycopy(data.toCharArray(), 0, result, 1, result.length - 1);


        System.out.println("________________");

        beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        FMIndex fmIndex = new FMIndex(new String(result));
        afterUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println("FM index needed: "+ (afterUsedMem-beforeUsedMem)/1024/1024 + " megabytes");


        char[] pattern = new char[]{'A', 'T', 'T', 'T', 'G', 'C', 'A', 'A', 'T'};
        //System.out.println(Arrays.toString(fmIndex.getRange(pattern)));
        System.out.println(Arrays.toString(fmIndex.locate(pattern)));

        System.out.println("_________________");

        beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        RIndex rIndex = new RIndex(new String(result));
        afterUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println("R index needed: "+ (afterUsedMem-beforeUsedMem)/1024/1024 + " megabytes");

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
