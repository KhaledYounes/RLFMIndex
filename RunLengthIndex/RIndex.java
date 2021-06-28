import java.util.*;
import java.util.stream.Collectors;

public class RIndex {

    private final char[] characters;
    private final char[] bwtOfText;
    private final int[] bwtC;
    private final int[][] rankInitial;
    private final char[] sPrime;
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


        this.bwtOfText = bwt;

        sizeOfText = bwt.length;

        lastSuffix = suffixes[suffixes.length-1];

        HashMap<Character, Integer> toBeBwtC;

        toBeBwtC =  FMIndex.computeC(bwt, occArray);

        ArrayList<Tuple<Character, Integer>> preRunLengthIndex = new ArrayList<>();

        ArrayList<Tuple<Character, Integer>> toCalculateL = new ArrayList<>();

        ArrayList<Integer> prePreData = new ArrayList<>();

        HashMap<Integer, Integer> distances = new HashMap<>();

        ArrayList<HashMap<Character, Integer>> preRankInitial = new ArrayList<>();

        preRunLengthIndex.add(new Tuple<>(bwt[0], 0));
        toCalculateL.add(new Tuple<>(bwt[0], 0));
        prePreData.add(1);

        for (int i=1; i<sizeOfText; i++) {
            if(bwt[i]!=bwt[i-1]) {
                preRunLengthIndex.add(new Tuple<>(bwt[i], i));
                toCalculateL.add(new Tuple<>(bwt[i], i));
                distances.put(suffixes[i], (suffixes[i-1]-suffixes[i]));
                prePreData.add(i+1);
            }
        }

        this.preData = Arrays.stream(prePreData.toArray(Integer[]::new)).mapToInt(Integer::intValue).toArray();

        ArrayList<Character> characters = new ArrayList<>();

        for (Tuple<Character, Integer> tuple : preRunLengthIndex) {
            characters.add(tuple.x);
        }

        Character[] charactersPrime = characters.toArray(Character[]::new);
        sPrime = new char[charactersPrime.length];
        for(int i=0; i<sPrime.length; i++) {
            sPrime[i] = (char) charactersPrime[i];
        }


        int[] occArrayOfSPrime = new int[characters.size()];

        HashMap<Character, Integer> toCalculateOccOfSPrime = new HashMap<>();
        for (char c : toBeBwtC.keySet()) {
                toCalculateOccOfSPrime.put(c, 0);
        }
        for(int i=0; i<sPrime.length; i++) {
            char current = sPrime[i];
            toCalculateOccOfSPrime.put(current, toCalculateOccOfSPrime.get(current)+1);
            occArrayOfSPrime[i] = toCalculateOccOfSPrime.get(current);
        }

        for (int i=0; i<preRunLengthIndex.size()-1; i++) {
            preRunLengthIndex.get(i).y = (preRunLengthIndex.get(i+1).y - preRunLengthIndex.get(i).y);
        }

        preRunLengthIndex.get(preRunLengthIndex.size()-1).y =
                (sizeOfText) - preRunLengthIndex.get(preRunLengthIndex.size()-1).y;


        for(int i=0; i<toCalculateL.size()-1; i++) {
            toCalculateL.get(i).y = toCalculateL.get(i+1).y - 1;
        }

        toCalculateL.get(toCalculateL.size()-1).y = sizeOfText - 1;

        Collections.sort(toCalculateL, Comparator.comparing(o -> o.x));

        List<Integer> toBeL = toCalculateL.stream().map(x -> suffixes[x.y]).collect(Collectors.toList());

        Collections.sort(preRunLengthIndex, Comparator.comparing(o -> o.x));

        for(int i=1; i<preRunLengthIndex.size(); i++) {
            if (preRunLengthIndex.get(i).x==preRunLengthIndex.get(i-1).x) {
                preRunLengthIndex.get(i).y += preRunLengthIndex.get(i-1).y;
            }
        }

        L = Arrays.stream(toBeL.toArray(Integer[]::new)).mapToInt(Integer::intValue).toArray();

        ArrayList<Integer> indexes = new ArrayList<>();

        for (Tuple<Character, Integer> tuple : preRunLengthIndex) {
            indexes.add(tuple.y);
        }

        R = Arrays.stream(indexes.toArray(Integer[]::new)).mapToInt(Integer::intValue).toArray();

        char[] sPrimeChar = new char[sPrime.length];
        for (int i=0; i< sPrimeChar.length; i++) {
            sPrimeChar[i] = (char) sPrime[i];
        }

        HashMap<Character, Integer> toBeC;

        toBeC = FMIndex.computeC(sPrime, occArrayOfSPrime);

        Character[] charactersCharacter = preRunLengthIndex.stream().map(x -> x.x).collect(Collectors.toSet()).toArray(Character[]::new);
        this.characters = new char[charactersCharacter.length];
        for (int i=0; i<this.characters.length; i++) {
            this.characters[i] = charactersCharacter[i];
        }
        Arrays.sort(this.characters);


        for (int i=0; i<sPrimeChar.length; i+=64) {

            HashMap<Character, Integer> hashMap = new HashMap<>();
            for (char c : toBeC.keySet()) {

                int k = i;
                while ( k>0 && sPrimeChar[k]!=c) k--;

                if(k==0){
                    if(sPrimeChar[0]==c) hashMap.put(c, occArrayOfSPrime[k]);
                    else hashMap.put(c, 0);
                } else {
                    hashMap.put(c, occArrayOfSPrime[k]);
                }

            }
            preRankInitial.add(hashMap);
        }

        Distance preDistances = new Distance(distances);
        this.keyDistance = preDistances.getKeyArray();
        this.valueDistance = preDistances.getValueArray();

        Rank rank = new Rank(preRankInitial);

        this.rankInitial = rank.getRankInitial();

        this.bwtC = new int[toBeBwtC.size()];
        for (int i=0; i<toBeBwtC.size(); i++) {
            this.bwtC[i] = toBeBwtC.get(this.characters[i]);
        }

        this.C = new int[toBeC.size()];
        for(int i=0; i<toBeC.size(); i++) {
            this.C[i] = toBeC.get(this.characters[i]);
        }

    }

    public int[] getRangeWithRIndex (char[] P) {

        if (P.length==0) return new int[]{};

        List<Integer> integerList = Arrays.stream(this.bwtC).boxed().collect(Collectors.toList());
        List<Character> characterList = new ArrayList<>();
        for (char c : this.characters) {
            characterList.add(c);
        }

        for (char l : P) {
            if(!characterList.contains(l)) return new int[]{};
        }

        int i = P.length-1;
        char c = P[i], nc = FMIndex.nextGreatestAlphabet(this.characters, c);

        currentSuffix = lastSuffix;
        modifyLastSuffix(this.bwtOfText, c, sizeOfText);

        int first = integerList.get(characterList.indexOf(c))+1;
        int last = integerList.get(characterList.indexOf(nc));


        if (nc==c) {
            last = sizeOfText;
        }

        //System.out.println(Arrays.toString(new int[]{first, last}));
        //System.out.println(this.currentSuffix);

        while (first<=last && i>0) {

            c = P[i-1];
            modifyLastSuffix(this.bwtOfText, c, last);

            first = integerList.get(characterList.indexOf(c)) + rankOfBwtWithRIndex(c, this.sPrime, first-1) + 1;
            last = integerList.get(characterList.indexOf(c)) + rankOfBwtWithRIndex(c, this.sPrime, last);

            //System.out.println(Arrays.toString(new int[]{first, last}));
            //System.out.println(this.currentSuffix);

            i--;
        }

        System.out.println(Arrays.toString(new int[]{first, last}));
        //System.out.println(this.currentSuffix);

        if (last<first) {
            return new int[]{};
        } else {
            return new int[]{first, last};
        }

    }

    private void modifyLastSuffix(char[] bwt, char c, int last) {

        List<Integer> integerList = Arrays.stream(this.C).boxed().collect(Collectors.toList());
        List<Character> characterList = new ArrayList<>();
        for (char t : this.characters) {
            characterList.add(t);
        }

        int k;
        k = getRunNumAndIndex(last).y;
        int indexInBWT = last-1;
        if(bwt[indexInBWT]==c) {
            currentSuffix = currentSuffix - 1;
        } else {
            int p = rankWithR(c, this.sPrime,k-1);
            int index = integerList.get(characterList.indexOf(c)) + p - 1;
            currentSuffix = this.L[ index < this.L.length ? index : index-1] - 1;
        }
    }

    public int rankOfBwtWithRIndex (char c, char[] bwt, int q) {

        List<Integer> integerList = Arrays.stream(this.C).boxed().collect(Collectors.toList());
        List<Character> characterList = new ArrayList<>();
        for (char t : this.characters) {
            characterList.add(t);
        }

        Tuple<Integer, Integer> tuple = getRunNumAndIndex(q);
        int j = tuple.x, k = tuple.y;

        char cPrime = this.sPrime[k-1];

        int p = rankWithR(c, this.sPrime, k-1);

        if(p==0) {
            if(c==cPrime) return q - j + 1;
            else return 0;
        }

        int index =  integerList.get(characterList.indexOf(c))+p-1;

        if (c == cPrime) {
            return R[index] + (q - j + 1);
        } else return R[index];

    }

    private Tuple<Integer, Integer> getRunNumAndIndex(int i) {

        List<Integer> integerList = Arrays.stream(preData).boxed().collect(Collectors.toList());

        if(integerList.contains(i)) return new Tuple<>(i, integerList.indexOf(i)+1);
        else {

            int runNum = i-1;

            while (runNum>=0) {
                if(integerList.contains(runNum)) {
                    return new Tuple<>(runNum, integerList.indexOf(runNum) + 1 );
                } else {
                    runNum--;
                }
            }
            return new Tuple<>(0, 0);
        }
    }

    public int rankWithR(char c, char[] sPrime, int q){


        List<Character> characterList = new ArrayList<>();
        for (char character : this.characters) characterList.add(character);

        if (!characterList.contains(c) || q<=0) return 0;

        q--;

        int index = q/64;

        if (q % 64 == 0) {
            return this.rankInitial[index][characterList.indexOf(c)];
        } else {

            int preValue = this.rankInitial[index][characterList.indexOf(c)];
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

        List<Integer> keyList = Arrays.stream(this.keyDistance).boxed().collect(Collectors.toList());

        HashSet<Integer> integerHashSet = new HashSet<>(keyList);

        for (int i= result.length-2; i>=0; i--) {

            int key = currentSuffix;

            while (!integerHashSet.contains(key)) {
                key--;
            }

            currentSuffix = currentSuffix + this.valueDistance[keyList.indexOf(key)];
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