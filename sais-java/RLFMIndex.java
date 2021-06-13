import java.util.*;

public class RLFMIndex {

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

        char[] T = "$alabaralalabarda".toCharArray();
        int n = T.length;
        char[] U = new char[n];
        char[] V = new char[n];
        int[] A = new int[n];


        int pidx = sais.bwtransform(T, U, A, n);

        char[] text = "$alabaralalabarda".toCharArray();
        int m = text.length;
        int[] SA = new int[m];

        sais.suffixsort(text, SA, m);
        SA[0] = m;

        unbwt(U, V, A, n, pidx);

        for (int i=0; i<A.length; i++)
            A[i] += 1;

        System.out.println(Arrays.toString(V));
        System.out.println(Arrays.toString(U));
        System.out.println(Arrays.toString(SA));

        FMIndex fmIndex = new FMIndex(U, A, SA);

        System.out.println("________________");

        System.out.println(Arrays.toString(fmIndex.getRange(new char[]{'l', 'a'}, U)));
        System.out.println(Arrays.toString(fmIndex.locate(new char[]{'l', 'a'}, U)));

        System.out.println("_________________");

        RIndex rIndex = new RIndex(U, SA);

        System.out.println(Arrays.toString(rIndex.getSPrime()));
        System.out.println(Arrays.toString(rIndex.getR()));
        System.out.println(new TreeMap<>(rIndex.getCOfRIndex()).toString());
        System.out.println(Arrays.toString(rIndex.getL()));
        System.out.println(new TreeMap<>(rIndex.getDistances()).toString());

        System.out.println("__________________");

        System.out.println(Arrays.toString(rIndex.getRangeWithRIndex(new char[]{'l', 'a'}, U)));
        System.out.println(Arrays.toString(rIndex.locate(new char[]{'l', 'a'}, U)));

    }

}
