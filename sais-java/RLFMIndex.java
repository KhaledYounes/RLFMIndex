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

    public static void main(String[] args) {

        long beforeUsedMem, afterUsedMem;

        char[] options = {'A', 'C', 'G', 'T'};
        char[] result =  new char[100000];
        Random r = new Random();
        result[0] = '$';
        for(int i=1;i<result.length;i++){
            result[i]=options[r.nextInt(options.length)];
        }

        //result = "$alabaralalabarda".toCharArray();

        char[] T = result;
        int n = T.length;
        char[] U = new char[n];
        char[] V = new char[n];
        int[] A = new int[n];


        int pidx = sais.bwtransform(T, U, A, n);

        char[] text = result;
        int m = text.length;
        int[] SA = new int[m];

        sais.suffixsort(text, SA, m);
        SA[0] = m;

        unbwt(U, V, A, n, pidx);

        for (int i=0; i<A.length; i++)
            A[i] += 1;

        //System.out.println(Arrays.toString(V));
        //System.out.println(Arrays.toString(U));
        //System.out.println(Arrays.toString(SA));

        System.out.println("________________");

        beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        FMIndex fmIndex = new FMIndex(U, A, SA);
        afterUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println("FM index needed: "+ (afterUsedMem-beforeUsedMem)/1024/1024 + " megabytes");


        char[] pattern = new char[]{'C', 'G', 'A', 'A', 'T', 'T', 'G'};
        //System.out.println(Arrays.toString(fmIndex.getRange(pattern, U)));
        System.out.println(Arrays.toString(fmIndex.locate(pattern, U)));

        //System.out.println(Arrays.toString(fmIndex.getRange(new char[]{'l', 'a'}, U)));
        //System.out.println(Arrays.toString(fmIndex.locate(new char[]{'l', 'a'}, U)));


        System.out.println("_________________");


        beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        RIndex rIndex = new RIndex(U, A, SA);
        afterUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println("R index needed: "+ (afterUsedMem-beforeUsedMem)/1024/1024 + " megabytes");

        //System.out.println(Arrays.toString(rIndex.getSPrime()));
        //System.out.println(Arrays.toString(rIndex.getR()));
        //System.out.println(new TreeMap<>(rIndex.getCOfRIndex()).toString());
        //System.out.println(Arrays.toString(rIndex.getL()));
        //System.out.println(new TreeMap<>(rIndex.getDistances()).toString());


        //System.out.println(Arrays.toString(rIndex.getRangeWithRIndex(pattern, U)));
        System.out.println(Arrays.toString(rIndex.locate(pattern, U)));

        //System.out.println(Arrays.toString(rIndex.getRangeWithRIndex(new char[]{'l', 'a'}, U)));
        //System.out.println(Arrays.toString(rIndex.locate(new char[]{'l', 'a'}, U)));

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
