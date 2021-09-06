import java.util.*;

public class FMIndex {

    private final int[] C;
    private final int[][] rankInitial;
    private final char[] bwtOfText;
    private final char[] characters;
    private final int[] sampledIndex;
    private final int[] suffixes;
    private int sample;

    FMIndex(String Text, int sample) {

        this.sample = sample;

        char[] T = Text.toCharArray();
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


        V = null;
        T = null;

        ArrayList<Integer> suffixKeys = new ArrayList<>();
        ArrayList<Integer> suffixValues = new ArrayList<>();


        if (suffixes.length < this.sample) {
            for (int i=0; i< suffixes.length; i++) {
                suffixKeys.add(i);
                suffixValues.add(suffixes[i]);
            }
        } else {
            for (int i=0; i< suffixes.length; i++) {
                if (suffixes[i]%this.sample==0)  {
                    suffixKeys.add(i);
                    suffixValues.add(suffixes[i]);
                }
            }
        }

        int[] suffixKeysArray = new int[suffixKeys.size()];
        int[] suffixValuesArray = new int [suffixKeys.size()];

        for (int i=0; i<suffixKeys.size(); i++) {
            suffixKeysArray[i] = suffixKeys.get(i);
            suffixValuesArray[i] = suffixValues.get(i);
        }

        this.sampledIndex = suffixKeysArray;
        this.suffixes = suffixValuesArray;

        this.sample *= 4;

        HashMap<Character, Integer> toBeC;

        toBeC = computeC(bwt);

        ArrayList<HashMap<Character, Integer>> preRankInitial = new ArrayList<>();

        for (int i=0; i<bwt.length; i+= this.sample) {

            HashMap<Character, Integer> hashMap = new HashMap<>();

            for (char c : toBeC.keySet()) {

                int k = i;
                int lastIndex = i - this.sample;
                while ( k>0 && k>lastIndex && bwt[k]!=c) k--;

                if(k==0){
                    if(bwt[0]==c) hashMap.put(c, occArray[k]);
                    else hashMap.put(c, 0);
                } else if (k==lastIndex){
                    hashMap.put(c, preRankInitial.get(k/this.sample).get(c));
                } else {
                    hashMap.put(c, occArray[k]);
                }

            }

            preRankInitial.add(hashMap);

        }

        Character[] charactersCharacter = toBeC.keySet().toArray(new Character[0]);
        this.characters = new char[charactersCharacter.length];
        for (int i=0; i<this.characters.length; i++) {
            this.characters[i] = charactersCharacter[i];
        }

        Arrays.sort(this.characters);

        this.bwtOfText = bwt;

        this.rankInitial = new int[preRankInitial.size()][this.characters.length];
        for (int i=0; i < this.rankInitial.length; i++) {
            for(int j=0; j < this.characters.length; j++) {
                this.rankInitial[i][j] = preRankInitial.get(i).get(this.characters[j]);
            }
        }

        this.C = new int[this.characters.length];

        for(int i=0; i<this.characters.length; i++) {
            this.C[i] = toBeC.get(this.characters[i]);
        }

    }

    public static HashMap<Character, Integer> computeC (char[] bwt) {

        HashMap<Character, Integer> preC = new HashMap<>();

        for (char c : bwt) {
            if (!preC.containsKey(c)) {
                preC.put(c, 1);
            } else {
                preC.replace(c, preC.get(c)+1);
            }
        }

        Character[] characters = preC.keySet().toArray(new Character[0]);
        Arrays.sort(characters);

        HashMap<Character, Integer> computedC = new HashMap<>();

        computedC.put(characters[0], 0);
        for (int i = 1; i < preC.size(); i++) {

            computedC.put(characters[i], computedC.get(characters[i-1]) + preC.get(characters[i-1]));

        }

        return computedC;

    }

    public int rank(char c, char[] bwt, int q){

        int indexInCharacters = Arrays.binarySearch(this.characters, c);

        if (indexInCharacters < 0 || q<=0) return 0;

        q--;

        int index = q/this.sample;

        if (q % this.sample == 0) {
            return rankInitial[index][indexInCharacters];
        } else {

            int preValue = rankInitial[index][indexInCharacters];
            int toAdd = 0;

            for (int i = (this.sample*index)+1; i<=q; i++) {
                if(bwt[i]==c) toAdd++;
            }
            return preValue+toAdd;
        }

    }

    public int[] getRange (char[] P) {

        if (P.length==0) return new int[]{};

        for (char l : P) {
            if(Arrays.binarySearch(this.characters, l) < 0) return new int[]{};
        }

        int i = P.length-1;
        char c = P[i], nc = nextGreatestAlphabet(this.characters, c);


        int first = this.C[Arrays.binarySearch(this.characters, c)] + 1;
        int last = this.C[Arrays.binarySearch(this.characters, nc)];


        if(c==nc) {
            last = bwtOfText.length;
        }

        while (first<=last && i>0) {
            c = P[i-1];

            first = this.C[Arrays.binarySearch(this.characters, c)] + rank(c, this.bwtOfText, first-1) + 1;
            last = this.C[Arrays.binarySearch(this.characters, c)] + rank(c, this.bwtOfText, last);

            i--;
        }

        System.out.println(Arrays.toString(new int[]{first, last}));

        if (last<first) {
            return new int[]{};
        } else {
            return new int[]{first, last};
        }

    }

    public static char nextGreatestAlphabet(char[] alphabets, char c)
    {

        int l = 0;
        int r = alphabets.length-1;;

        while (l <= r)
        {
            int mid = (l + r) / 2;
            if (alphabets[mid] > c) {
                r = mid - 1;
            } else if (alphabets[mid] < c){
                l = mid + 1;
            } else {
                return (mid+1) < alphabets.length ? alphabets[mid+1] : alphabets[mid];
            }

        }

        return Character.MIN_VALUE;

    }

    public int[] locate (char[] P) {

        int [] range = getRange(P);

        if (range.length==0) return new int[]{};

        int first = range[0], last = range[1];
        int[] result = new int[last - first + 1];

        int firstIndex = first-1;
        for(int i=0; i<result.length; i++) {
            result[i] = getPosition(firstIndex);
            firstIndex++;
        }

        return result;

    }

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

    private int LF(int q) {

        return this.C[Arrays.binarySearch(this.characters, this.bwtOfText[q])] + rank(this.bwtOfText[q], this.bwtOfText, q);

    }

    private int getPosition(int i) {

        int j = i, t = 0;

        int indexInKeys = Arrays.binarySearch(this.sampledIndex, j);
        while (indexInKeys < 0) {
            j = LF(j);
            indexInKeys = Arrays.binarySearch(this.sampledIndex, j);
            t += 1;
        }

        return this.suffixes[indexInKeys] + t;

    }

    public char[] getBwtOfText() {
        return bwtOfText;
    }

    public int[][] getRankInitial() {
        return rankInitial;
    }

    public int[] getC() {
        return C;
    }

    public char[] getCharacters() {
        return characters;
    }

}