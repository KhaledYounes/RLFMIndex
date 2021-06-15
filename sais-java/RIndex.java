import java.util.*;
import java.util.stream.Collectors;

public class RIndex {

    private final Character[] characters;
    private final HashMap<Character, Integer> bwtC;
    private final ArrayList<Integer> preData = new ArrayList<>();
    private final HashMap<Character, Integer> C;
    private final ArrayList<HashMap<Character, Integer>> rankInitial = new ArrayList<>();
    private final char[] sPrime;
    private final Integer[] R;
    private final Integer[] L;
    private final HashMap<Integer, Integer> distances = new HashMap<>();
    private final int lastSuffix;
    private final int sizeOfText;
    private int currentSuffix;

    public RIndex (char[] bwt, int[] occArray, int[] suffixes) {

        sizeOfText = bwt.length;

        lastSuffix = suffixes[suffixes.length-1];

        bwtC =  FMIndex.computeC(bwt, occArray);


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


        Character[] charactersPrime = characters.toArray(Character[]::new);
        sPrime = new char[charactersPrime.length];
        for(int i=0; i<sPrime.length; i++) {
            sPrime[i] = (char) charactersPrime[i];
        }


        int[] occArrayOfSPrime = new int[characters.size()];

        HashMap<Character, Integer> toCalculateOccOfSPrime = new HashMap<>();
        for (char c : bwtC.keySet()) {
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

        this.C = FMIndex.computeC(sPrime, occArrayOfSPrime);

        this.characters = preRunLengthIndex.stream().map(x -> x.x).collect(Collectors.toSet()).toArray(Character[]::new);
        Arrays.sort(this.characters);


        for (int i=0; i<sPrimeChar.length; i+=64) {

            HashMap<Character, Integer> hashMap = new HashMap<>();
            for (char c : this.C.keySet()) {

                int k = i;
                while ( k>0 && sPrimeChar[k]!=c) k--;

                if(k==0){
                    if(sPrimeChar[0]==c) hashMap.put(c, occArrayOfSPrime[k]);
                    else hashMap.put(c, 0);
                } else {
                    hashMap.put(c, occArrayOfSPrime[k]);
                }

            }
            this.rankInitial.add(hashMap);
        }


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

        //System.out.println(Arrays.toString(new int[]{first, last}));

        while (first<=last && i>0) {

            c = P[i-1];
            modifyLastSuffix(bwt, c, last);

            first = bwtC.get(c) + rankOfBwtWithRIndex(c, this.sPrime, first-1) + 1;
            last = bwtC.get(c) + rankOfBwtWithRIndex(c, this.sPrime, last);

            //System.out.println(Arrays.toString(new int[]{first, last}));

            i--;
        }

        System.out.println(Arrays.toString(new int[]{first, last}));

        if (last<first) {
            return new int[]{};
        } else {
            return new int[]{first, last};
        }

    }

    private void modifyLastSuffix(char[] bwt, char c, int last) {
        int k;
        k = getRunNumAndIndex(last).y;
        int indexInBWT = last-1;
        if(bwt[indexInBWT]==c) {
            currentSuffix = currentSuffix - 1;
        } else {
            int p = rankWithR(c, this.sPrime,k-1);
            int index = this.C.get(c) + p - 1;
            currentSuffix = this.L[ index < this.L.length ? index : index-1] - 1;
        }
    }

    public int rankOfBwtWithRIndex (char c, char[] bwt, int q) {

        Tuple<Integer, Integer> tuple = getRunNumAndIndex(q);
        int j = tuple.x, k = tuple.y;

        char cPrime = this.sPrime[k-1];

        int p = rankWithR(c, this.sPrime, k-1);

        if(p==0) {
            if(c==cPrime) return q - j + 1;
            else return 0;
        }

        int index = this.C.get(c)+p-1;

        if (c == cPrime) {
            return R[index] + (q - j + 1);
        } else return R[index];

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

    public int rankWithR(char c, char[] sPrime, int q){

        q--;

        int index = q/64;

        if (q % 64 == 0) {
            return this.rankInitial.get(index).get(c);
        } else {

            int preValue = this.rankInitial.get(index).get(c);
            int toAdd = 0;

            for (int i = (64*index)+1; i<=q; i++) {
                if(this.sPrime[i]==c) toAdd++;
            }
            return preValue+toAdd;
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