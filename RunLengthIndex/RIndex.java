import java.util.*;
import java.util.stream.Collectors;

public class RIndex {

    private final char[] characters;
    private final int[] bwtC;
    private final char[] sPrime;
    private final int[][] rankInitial;
    private final int[] preData;
    private final int[] C;
    private final int[] R;
    private final int[] L;
    private final int[] keyDistance;
    private final int[] valueDistance;
    private final int lastSuffix;
    private final int sizeOfText;
    private int currentSuffix;

    public RIndex (String Text) {

        long s, e;
        s = System.currentTimeMillis();

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


        sizeOfText = bwt.length;

        lastSuffix = suffixes[suffixes.length-1];

        V = null;
        T = null;

        e = System.currentTimeMillis();
        System.out.println("Step 1 took: " + (e-s)/1000 + " seconds");
        s = System.currentTimeMillis();

        HashMap<Character, Integer> toBeBwtC;

        toBeBwtC =  FMIndex.computeC(bwt);

        ArrayList<Tuple<Character, Integer>> preRunLengthIndex = new ArrayList<>();

        ArrayList<Tuple<Character, Integer>> toCalculateL = new ArrayList<>();

        ArrayList<Integer> prePreData = new ArrayList<>();

        TreeMap<Integer, Integer> distancesTree = new TreeMap<>();

        ArrayList<HashMap<Character, Integer>> preRankInitial = new ArrayList<>();

        preRunLengthIndex.add(new Tuple<>(bwt[0], 0));
        toCalculateL.add(new Tuple<>(bwt[0], 0));
        prePreData.add(1);

        e = System.currentTimeMillis();
        System.out.println("Step 2 took: " + (e-s)/1000 + " seconds");
        s = System.currentTimeMillis();

        for (int i=1; i<sizeOfText; i++) {
            if(bwt[i]!=bwt[i-1]) {
                preRunLengthIndex.add(new Tuple<>(bwt[i], i));
                toCalculateL.add(new Tuple<>(bwt[i], i));
                distancesTree.put(suffixes[i], suffixes[i-1]-suffixes[i]);
                prePreData.add(i+1);
            }
        }

        for (int i=0; i<preRunLengthIndex.size()-1; i++) {
            preRunLengthIndex.get(i).y = (preRunLengthIndex.get(i+1).y - preRunLengthIndex.get(i).y);
        }

        preRunLengthIndex.get(preRunLengthIndex.size()-1).y =
                (sizeOfText) - preRunLengthIndex.get(preRunLengthIndex.size()-1).y;

        this.preData = Arrays.stream(prePreData.toArray(new Integer[0])).mapToInt(Integer::intValue).toArray();

        this.sPrime = new char[preRunLengthIndex.size()];
        for (int i = 0; i < preRunLengthIndex.size(); i++) {
            this.sPrime[i] = (char) preRunLengthIndex.get(i).x;
        }

        e = System.currentTimeMillis();
        System.out.println("Step 3 took: " + (e-s)/1000 + " seconds");
        s = System.currentTimeMillis();

        preRunLengthIndex.sort(Comparator.comparing(o -> o.x));

        for(int i=1; i<preRunLengthIndex.size(); i++) {
            if (preRunLengthIndex.get(i).x==preRunLengthIndex.get(i-1).x) {
                preRunLengthIndex.get(i).y += preRunLengthIndex.get(i-1).y;
            }
        }

        ArrayList<Integer> indexes = new ArrayList<>();

        for (Tuple<Character, Integer> tuple : preRunLengthIndex) {
            indexes.add(tuple.y);
        }

        R = Arrays.stream(indexes.toArray(new Integer[0])).mapToInt(Integer::intValue).toArray();

        e = System.currentTimeMillis();
        System.out.println("Step 4 took: " + (e-s)/1000 + " seconds");
        s = System.currentTimeMillis();

        for(int i=0; i<toCalculateL.size()-1; i++) {
            toCalculateL.get(i).y = toCalculateL.get(i+1).y - 1;
        }
        toCalculateL.get(toCalculateL.size()-1).y = sizeOfText - 1;

        toCalculateL.sort(Comparator.comparing(o -> o.x));

        L = Arrays.stream(toCalculateL.stream().map(x -> suffixes[x.y]).toArray(Integer[]::new)).mapToInt(Integer::intValue).toArray();

        e = System.currentTimeMillis();
        System.out.println("Step 5 took: " + (e-s)/1000 + " seconds");
        s = System.currentTimeMillis();

        int[] occArrayOfSPrime = new int[this.sPrime.length];

        HashMap<Character, Integer> toCalculateOccOfSPrime = new HashMap<>();
        for (char c : toBeBwtC.keySet()) {
            toCalculateOccOfSPrime.put(c, 0);
        }
        for(int i=0; i<sPrime.length; i++) {
            char current = sPrime[i];
            toCalculateOccOfSPrime.put(current, toCalculateOccOfSPrime.get(current)+1);
            occArrayOfSPrime[i] = toCalculateOccOfSPrime.get(current);
        }

        HashMap<Character, Integer> toBeC;

        toBeC = FMIndex.computeC(sPrime);

        Character[] charactersCharacter = preRunLengthIndex.stream().map(x -> x.x).collect(Collectors.toSet()).toArray(new Character[0]);
        this.characters = new char[charactersCharacter.length];
        for (int i=0; i<this.characters.length; i++) {
            this.characters[i] = charactersCharacter[i];
        }

        Arrays.sort(this.characters);

        e = System.currentTimeMillis();
        System.out.println("Step 6 took: " + (e-s)/1000 + " seconds");
        s = System.currentTimeMillis();

        for (int i=0; i<this.sPrime.length; i+=64) {

            HashMap<Character, Integer> hashMap = new HashMap<>();
            for (char c : toBeC.keySet()) {

                int k = i;
                int lastIndex = i - 64;
                while ( k>0 && k>lastIndex && this.sPrime[k]!=c) k--;

                if(k==0){
                    if(this.sPrime[0]==c) hashMap.put(c, occArrayOfSPrime[k]);
                    else hashMap.put(c, 0);
                } else if (k==lastIndex){
                    hashMap.put(c, preRankInitial.get(k/64).get(c));
                } else {
                    hashMap.put(c, occArrayOfSPrime[k]);
                }

            }
            preRankInitial.add(hashMap);
        }

        int[] distancesKeysArray = new int[distancesTree.size()];
        int[] distancesValuesArray = new int[distancesTree.size()];


        int entryIndex = 0;
        for(Map.Entry<Integer, Integer> entries : distancesTree.entrySet()) {
            distancesKeysArray[entryIndex] = entries.getKey();
            distancesValuesArray[entryIndex] = entries.getValue();
            entryIndex++;
        }


        this.keyDistance = distancesKeysArray;
        this.valueDistance = distancesValuesArray;

        e = System.currentTimeMillis();
        System.out.println("Step 7 took: " + (e-s)/1000 + " seconds");
        s = System.currentTimeMillis();

        this.rankInitial = new int[preRankInitial.size()][this.characters.length];
        for (int i=0; i < this.rankInitial.length; i++) {
            for(int j=0; j < this.characters.length; j++) {
                this.rankInitial[i][j] = preRankInitial.get(i).get(this.characters[j]);
            }
        }

        this.bwtC = new int[toBeBwtC.size()];
        for (int i=0; i<toBeBwtC.size(); i++) {
            this.bwtC[i] = toBeBwtC.get(this.characters[i]);
        }

        this.C = new int[toBeC.size()];
        for(int i=0; i<toBeC.size(); i++) {
            this.C[i] = toBeC.get(this.characters[i]);
        }

        e = System.currentTimeMillis();
        System.out.println("Step 8 took: " + (e-s)/1000 + " seconds");

    }

    public int[] getRangeWithRIndex (char[] P) {

        if (P.length==0) return new int[]{};

        for (char l : P) {
            if(Arrays.binarySearch(this.characters, l) < 0) return new int[]{};
        }

        int i = P.length-1;
        char c = P[i], nc = FMIndex.nextGreatestAlphabet(this.characters, c);

        currentSuffix = lastSuffix;
        modifyLastSuffix(c, sizeOfText);

        int first = this.bwtC[Arrays.binarySearch(this.characters, c)] + 1;
        int last = this.bwtC[Arrays.binarySearch(this.characters, nc)];


        if (nc==c) {
            last = sizeOfText;
        }

        while (first<=last && i>0) {

            c = P[i-1];
            modifyLastSuffix(c, last);

            first = this.bwtC[Arrays.binarySearch(this.characters, c)] + rankOfBwtWithRIndex(c, first-1) + 1;
            last = this.bwtC[Arrays.binarySearch(this.characters, c)] + rankOfBwtWithRIndex(c, last);

            i--;
        }

        System.out.println(Arrays.toString(new int[]{first, last}));

        if (last<first) {
            return new int[]{};
        } else {
            return new int[]{first, last};
        }

    }

    private void modifyLastSuffix(char c, int last) {

        int k = getRunNumAndIndex(last).y;

        if(this.sPrime[k-1]==c) {
            currentSuffix = currentSuffix - 1;
        } else {
            int p = rankWithR(c, this.sPrime,k-1);
            int index = this.C[Arrays.binarySearch(this.characters, c)] + p - 1;
            currentSuffix = this.L[ index < this.L.length ? index : index-1] - 1;
        }
    }

    public int rankOfBwtWithRIndex (char c, int q) {

        Tuple<Integer, Integer> tuple = getRunNumAndIndex(q);
        int j = tuple.x, k = tuple.y;

        char cPrime = this.sPrime[k-1];

        int p = rankWithR(c, this.sPrime, k-1);

        if(p==0) {
            if(c==cPrime) return q - j + 1;
            else return 0;
        }

        int index =  this.C[Arrays.binarySearch(this.characters, c)] + p - 1;

        if (c == cPrime) {
            return R[index] + (q - j + 1);
        } else return R[index];

    }

    private Tuple<Integer, Integer> getRunNumAndIndex(int i) {

        int indexInPreData = Arrays.binarySearch(this.preData, i);

        if (indexInPreData < 0) {

            indexInPreData = Math.abs(indexInPreData + 1);

            return new Tuple<>(this.preData[indexInPreData-1], indexInPreData);

        } else {

            return new Tuple<>(i, indexInPreData+1);

        }

    }

    public int rankWithR(char c, char[] sPrime, int q){

        int indexInCharacters = Arrays.binarySearch(this.characters, c);

        if (indexInCharacters < 0 || q <= 0) return 0;

        q--;

        int index = q/64;

        if (q % 64 == 0) {
            return this.rankInitial[index][indexInCharacters];
        } else {

            int preValue = this.rankInitial[index][indexInCharacters];
            int toAdd = 0;

            for (int i = (64*index)+1; i<=q; i++) {
                if(this.sPrime[i]==c) toAdd++;
            }
            return preValue+toAdd;
        }

    }


    public int[] locate (char[] P) {

        int[] range = getRangeWithRIndex(P);

        if (range.length==0) return new int[]{};

        int[] result = new int[range[1] - range[0] + 1];

        result[result.length-1] = currentSuffix;

        for (int i= result.length-2; i>=0; i--) {

            int indexInDistancesKeys = Arrays.binarySearch(this.keyDistance, currentSuffix);

            int finalIndexInDistancesKeys =
                    indexInDistancesKeys > 0 ? indexInDistancesKeys : Math.abs(indexInDistancesKeys + 1) - 1;

            currentSuffix = currentSuffix + this.valueDistance[finalIndexInDistancesKeys];

            result[i] = currentSuffix;

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

    public int[] getPreData() {
        return preData;
    }

    public char[] getSPrime() {
        return sPrime;
    }

    public int[] getR() {
        return R;
    }

    public int[] getCOfRIndex() {
        return this.C;
    }

    public int[] getBwtC() {
        return bwtC;
    }

    public int[] getKeyDistance(){
        return this.keyDistance;
    }

    public int[] getValueDistance(){
        return this.valueDistance;
    }

    public int[] getL() {
        return L;
    }

}