import java.util.*;
import java.util.stream.Collectors;

public class RIndex {

    private Character[] characters;
    private HashMap<Character, Integer> bwtC;
    private final ArrayList<Integer> preData = new ArrayList<>();
    private HashMap<Character, Integer> C;
    private final char[] sPrime;
    private final Integer[] R;
    private final Integer[] L;
    private final HashMap<Integer, Integer> distances = new HashMap<>();
    private final int lastSuffix;
    private final int sizeOfText;
    private int currentSuffix;

    // to be deleted later
    private final FMIndex fmIndex;

    public RIndex (char[] bwt, int[] suffixes) {

        sizeOfText = bwt.length;

        lastSuffix = suffixes[suffixes.length-1];

        ArrayList<Tuple<Character, Integer>> preRunLengthIndex = new ArrayList<>();

        ArrayList<Tuple<Character, Integer>> toCalculateL = new ArrayList<>();

        preRunLengthIndex.add(new Tuple<>(bwt[0], 0));
        toCalculateL.add(new Tuple<>(bwt[0], 0));
        preData.add(1);

        for (int i=1; i<sizeOfText; i++) {
            if(bwt[i]!=bwt[i-1]) {
                preRunLengthIndex.add(new Tuple<>(bwt[i], i));
                toCalculateL.add(new Tuple<>(bwt[i], i));
                distances.put(suffixes[i], (suffixes[i-1]-suffixes[i]));
                preData.add(i+1);
            }
        }

        ArrayList<Character> characters = new ArrayList<>();

        for (Tuple<Character, Integer> tuple : preRunLengthIndex) {
            characters.add(tuple.x);
        }

        int[] occArray = new int[characters.size()];
        for(int i=0; i<characters.size(); i++) {
            Character[] list = characters.subList(0, i).toArray(Character[]::new);
            occArray[i] = Collections.frequency(Arrays.asList(list), characters.get(i))+1;
        }

        Character[] charactersPrime = characters.toArray(Character[]::new);
        sPrime = new char[charactersPrime.length];
        for(int i=0; i<sPrime.length; i++) {
            sPrime[i] = (char) charactersPrime[i];
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

        for(int i=preRunLengthIndex.size()-1; i>0; i--) {
            int toUpdate = preRunLengthIndex.get(i).y;
            int k = i-1;
            while ( k>=0 && preRunLengthIndex.get(i).x==preRunLengthIndex.get(k).x) {
                toUpdate += preRunLengthIndex.get(k).y;
                k--;
            }
            preRunLengthIndex.get(i).y = toUpdate;
        }

        L = toBeL.toArray(Integer[]::new);

        ArrayList<Integer> indexes = new ArrayList<>();

        for (Tuple<Character, Integer> tuple : preRunLengthIndex) {
            indexes.add(tuple.y);
        }

        R = indexes.toArray(Integer[]::new);

        char[] sPrimeChar = new char[sPrime.length];
        for (int i=0; i< sPrimeChar.length; i++) {
            sPrimeChar[i] = (char) sPrime[i];
        }

        // create FM index to take the needed information from, than delete it! NOT AS FOLLOW
        fmIndex = new FMIndex(bwt, occArray, suffixes);

        this.C = fmIndex.computeC(sPrime, occArray);

        this.characters = fmIndex.getCharacters();

        bwtC =  fmIndex.getC();

        //fmIndex = null;

    }

    public int[] getRangeWithRIndex (char[] P, char[] bwt) {

        if (P.length==0) return new int[]{};

        for (char l : P) {
            if(!this.C.containsKey(l)) return new int[]{};
        }

        int i = P.length-1;
        char c = P[i], nc = FMIndex.nextGreatestAlphabet(this.characters, c);

        currentSuffix = lastSuffix;
        modifyLastSuffix(bwt, c, sizeOfText);

        int first = bwtC.get(c)+1;
        int last = bwtC.get(nc);

        if (nc==c) {
            last = sizeOfText;
        }

        while (first<=last && i>0) {

            c = P[i-1];
            modifyLastSuffix(bwt, c, last);

            first = bwtC.get(c) + rankOfBwtWithRIndex(c, first-1) + 1;
            last = bwtC.get(c) + rankOfBwtWithRIndex(c, last);
            i--;
        }

        if (last<first) {
            return new int[]{};
        } else {
            return new int[]{first, last};
        }

    }

    private void modifyLastSuffix(char[] bwt, char c, int last) {
        int k;
        k = getRunNumAndIndex(last).y;
        if(bwt[last-1]==c) {
            currentSuffix = currentSuffix - 1;
        } else {
            int p = fmIndex.rank(c, this.sPrime,k-1);
            int index = this.C.get(c) + p - 1;
            currentSuffix = this.L[index] - 1;
        }
    }

    public int rankOfBwtWithRIndex (char c, int q) {

        Tuple<Integer, Integer> tuple = getRunNumAndIndex(q);
        int j = tuple.x, k = tuple.y;
        char cPrime = sPrime[k-1];
        int p = fmIndex.rank(c, sPrime, k-1);

        // not in paper
        boolean hasPreRuns = true;
        if(p==0 && cPrime==c && q==j) {
            return 1;
        }
        if(p==0 && cPrime!=c) {
            return 0;
        }
        if(p==0) {
            p++;
            hasPreRuns = false;
        }

        if (c == cPrime && hasPreRuns) {
            return R[this.C.get(c)+p-1] + (q - j + 1);
        } else return R[this.C.get(c)+p-1];
    }

    private Tuple<Integer, Integer> getRunNumAndIndex(int i) {
        if(preData.contains(i)) return new Tuple<>(i, preData.indexOf(i)+1);
        else {
            int l = 0, r = preData.size()-1, runNum = 0;
            while (l<=r) {
                runNum = (l+r)/2;
                if (i<preData.get(runNum)) {
                    r = runNum-1;
                } else {
                    l = runNum+1;
                }
            }
            return new Tuple<>(preData.get(runNum), preData.indexOf(preData.get(runNum))+1 );
        }
    }

    public int[] locate (char[] P, char[] bwt) {

        int[] range = getRangeWithRIndex(P, bwt);

        if (range.length==0) return new int[]{};

        int[] result = new int[range[1] - range[0] + 1];

        result[result.length-1] = currentSuffix;

        for (int i= result.length-2; i>=0; i--) {

            int indexInDistances = currentSuffix;
            while (!distances.containsKey(indexInDistances)) indexInDistances--;

            currentSuffix = currentSuffix + distances.get(indexInDistances);
            result[i] = currentSuffix;

        }

        return result;

    }

    public ArrayList<Integer> getPreData() {
        return preData;
    }

    public char[] getSPrime() {
        return sPrime;
    }

    public Integer[] getR() {
        return R;
    }

    public HashMap<Character, Integer> getCOfRIndex() {
        return this.C;
    }

    public HashMap<Integer, Integer> getDistances() {
        return distances;
    }

    public Integer[] getL() {
        return L;
    }

}