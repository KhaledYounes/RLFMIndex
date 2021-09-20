import java.util.*;
import java.util.stream.Collector;

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
    private final int sample;

    public RIndex (String Text, int sample, boolean parallel) {

        this.sample = 4*sample;

        long s, e;
        s = System.currentTimeMillis();

        char[] T = Text.toCharArray();
        int n = T.length;
        char[] bwt = new char[n];

        int m = T.length;
        int[] suffixes = new int[m];

        sais.suffixsort(T, suffixes, m);
        suffixes[0] = m;

        int runsLength = 1;
        for (int i=0; i<m; i++) {
            int currentSuffix = suffixes[i];
            if (currentSuffix > 0 ) bwt[i] = T[currentSuffix-1];
            else bwt[i] = Character.MIN_VALUE;
            if (i>0 && bwt[i]!=bwt[i-1]) runsLength++;
        }

        sizeOfText = bwt.length;

        lastSuffix = suffixes[suffixes.length-1];

        e = System.currentTimeMillis();
        System.out.println("Step 1 (constructing the suffix array): " + (e-s)/1000 + " seconds");
        s = System.currentTimeMillis();

        this.preData = new int[runsLength];
        this.sPrime = new char[runsLength];
        this.keyDistance = new int[runsLength-1];
        this.valueDistance = new int[runsLength-1];
        this.R = new int[runsLength];
        this.L = new int[runsLength];


        /*
        if (parallel) {

        prePreData.add(1);
        for (int i=1; i< bwt.length; i++) {
            if(bwt[i]!=bwt[i-1]) {
                prePreData.add(i+1);
            }
        }

        this.preData = prePreData.parallelStream().mapToInt(Integer::intValue).toArray();

        InParallel.PreRunsThread preRunsThread = new InParallel.PreRunsThread(this.preData, bwt); preRunsThread.start();
        InParallel.DistancesThread distancesThread = new InParallel.DistancesThread(this.preData, bwt, suffixes); distancesThread.start();
        InParallel.ToCalculateLThread toCalculateLThread = new InParallel.ToCalculateLThread(this.preData, bwt, suffixes); toCalculateLThread.start();

        try {

            preRunsThread.join();

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ArrayList<Tuple<Character, Integer>> preRunLengthIndex = preRunsThread.getPreRuns();

        this.sPrime = preRunLengthIndex.parallelStream().map(o -> o.x)
                .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))
                .toCharArray();

        this.characters = preRunLengthIndex.parallelStream().map(x -> x.x).distinct()
                .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))
                .toCharArray();

        Arrays.sort(this.characters);

        InParallel.ToCalculateRThread toCalculateRThread = new InParallel.ToCalculateRThread(preRunLengthIndex); toCalculateRThread.start();

        int[] occArrayOfSPrime = new int[this.sPrime.length];

        HashMap<Character, Integer> toCalculateOccOfSPrime = new HashMap<>();
        for (char c : toBeBwtC.keySet()) {
            toCalculateOccOfSPrime.put(c, 0);
        }
        for(int i=0; i<this.sPrime.length; i++) {
            char current = this.sPrime[i];
            toCalculateOccOfSPrime.put(current, toCalculateOccOfSPrime.get(current)+1);
            occArrayOfSPrime[i] = toCalculateOccOfSPrime.get(current);
        }

        HashMap<Character, Integer> toBeC;

        toBeC = FMIndex.computeC(this.sPrime);

        for (int i=0; i<this.sPrime.length; i+=this.sample) {

            HashMap<Character, Integer> hashMap = new HashMap<>();
            for (char c : toBeC.keySet()) {

                int k = i;
                int lastIndex = i - this.sample;
                while ( k>0 && k>lastIndex && this.sPrime[k]!=c) k--;

                if(k==0){
                    if(this.sPrime[0]==c) hashMap.put(c, occArrayOfSPrime[k]);
                    else hashMap.put(c, 0);
                } else if (k==lastIndex){
                    hashMap.put(c, preRankInitial.get(k/this.sample).get(c));
                } else {
                    hashMap.put(c, occArrayOfSPrime[k]);
                }

            }
            preRankInitial.add(hashMap);
        }

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

        try {

            toCalculateLThread.join();
            distancesThread.join();
            toCalculateRThread.join();

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.L = toCalculateLThread.getL();
        this.R = toCalculateRThread.getR();
        this.keyDistance = distancesThread.getKeyDistance();
        this.valueDistance = distancesThread.getValueDistance();


    } else {


        }
         */

        int runIndex = 0;
        this.preData[runIndex] = 1;
        this.sPrime[runIndex] = bwt[0];
        for (int i=1; i<bwt.length; i++) {
            if (bwt[i]!=bwt[i-1]) {
                runIndex++;
                this.preData[runIndex] = i+1;
                this.sPrime[runIndex] = bwt[i];
                this.keyDistance[runIndex-1] = suffixes[i];
                this.valueDistance[runIndex-1] = suffixes[i-1] - suffixes[i];
            }
        }

        this.R[0] = this.preData[1] - this.preData[0];
        this.L[0] = 0;
        for (int i=1; i<runsLength-1; i++) {
            this.R[i] = this.preData[i+1] - this.preData[i];
            this.L[i] = this.preData[i+1] - 2;
        }
        this.R[runsLength-1] = bwt.length - (this.preData[this.preData.length-1] - 1);
        this.L[runsLength-1] = bwt.length-1;

        char[] clonedSPrime = this.sPrime.clone();

        mergeSortRuns(clonedSPrime, this.R, this.L, runsLength);

        ArrayList<Character> characterArrayList = new ArrayList<>();

        this.L[0] = suffixes[this.L[0]];
        characterArrayList.add(clonedSPrime[0]);
        for (int i=1; i<clonedSPrime.length; i++) {

            int currentL = this.L[i];
            this.L[i] = suffixes[currentL];

            if(clonedSPrime[i] == clonedSPrime[i-1]) {
                this.R[i] += this.R[i-1];
            } else {
                characterArrayList.add(clonedSPrime[i]);
            }

        }

        this.characters = characterArrayList.parallelStream()
                .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))
                .toCharArray();


        quickSortDistances(this.keyDistance, 0, runsLength-2);

        HashMap<Character, Integer> toBeBwtC =  FMIndex.computeC(bwt);

        ArrayList<HashMap<Character, Integer>> preRankInitial = new ArrayList<>();

        int[] occArrayOfSPrime = new int[runsLength];

        HashMap<Character, Integer> toCalculateOccOfSPrime = new HashMap<>();
        for (char c : toBeBwtC.keySet()) {
            toCalculateOccOfSPrime.put(c, 0);
        }
        for(int i=0; i<this.sPrime.length; i++) {
            char current = this.sPrime[i];
            toCalculateOccOfSPrime.put(current, toCalculateOccOfSPrime.get(current)+1);
            occArrayOfSPrime[i] = toCalculateOccOfSPrime.get(current);
        }

        HashMap<Character, Integer> toBeC = FMIndex.computeC(this.sPrime);

        for (int i=0; i<this.sPrime.length; i+=this.sample) {

            HashMap<Character, Integer> hashMap = new HashMap<>();
            for (char c : toBeC.keySet()) {

                int k = i;
                int lastIndex = i - this.sample;
                while ( k>0 && k>lastIndex && this.sPrime[k]!=c) k--;

                if(k==0){
                    if(this.sPrime[0]==c) hashMap.put(c, occArrayOfSPrime[k]);
                    else hashMap.put(c, 0);
                } else if (k==lastIndex){
                    hashMap.put(c, preRankInitial.get(k/this.sample).get(c));
                } else {
                    hashMap.put(c, occArrayOfSPrime[k]);
                }

            }
            preRankInitial.add(hashMap);
        }

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
        System.out.println("Step 2 (constructing the r index): " + (e-s)/1000 + " seconds");
        System.out.println();

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
            int p = rankWithR(c,k-1);
            int index = this.C[Arrays.binarySearch(this.characters, c)] + p - 1;
            currentSuffix = this.L[ index < this.L.length ? index : index-1] - 1;
        }
    }

    public int rankOfBwtWithRIndex (char c, int q) {

        Tuple<Integer, Integer> tuple = getRunNumAndIndex(q);
        int j = tuple.x, k = tuple.y;

        char cPrime = this.sPrime[k-1];

        int p = rankWithR(c, k-1);

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

    public int rankWithR(char c, int q){

        int indexInCharacters = Arrays.binarySearch(this.characters, c);

        if (indexInCharacters < 0 || q <= 0) return 0;

        q--;

        int index = q/this.sample;

        if (q % this.sample == 0) {
            return this.rankInitial[index][indexInCharacters];
        } else {

            int preValue = this.rankInitial[index][indexInCharacters];
            int toAdd = 0;

            for (int i = (this.sample*index)+1; i<=q; i++) {
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

    private void mergeSortRuns(char[] sPrime, int[] R, int[] L, int n) {

        if (n < 2) {
            return;
        }

        int mid = n / 2;

        char[] l = new char[mid];
        char[] r = new char[n - mid];

        int [] lR = new int[mid];
        int [] rR = new int[n - mid];

        int [] lL = new int[mid];
        int [] rL = new int[n - mid];

        System.arraycopy(sPrime, 0, l, 0, mid);
        if (n - mid >= 0) System.arraycopy(sPrime, mid, r, 0, n - mid);

        System.arraycopy(R, 0, lR, 0, mid);
        if (n - mid >= 0) System.arraycopy(R, mid, rR, 0, n - mid);

        System.arraycopy(L, 0, lL, 0, mid);
        if (n - mid >= 0) System.arraycopy(L, mid, rL, 0, n - mid);

        mergeSortRuns(l, lR, lL,  mid);
        mergeSortRuns(r, rR, rL, n - mid);

        mergeRuns(sPrime, l, r, R, lR, rR, L, lL, rL, mid, n - mid);
    }
    private void mergeRuns(char[] sPrime, char[] l, char[] r, int[] R, int[] lR, int[] rR, int[] L, int[] lL, int[] rL, int left, int right) {

        int i = 0, j = 0, k = 0;

        while (i < left && j < right) {
            if (l[i] <= r[j]) {
                sPrime[k] = l[i];
                R[k] = lR[i];
                L[k] = lL[i];
                k++;
                i++;
            }
            else {
                sPrime[k] = r[j];
                R[k] = rR[j];
                L[k] = rL[j];
                k++;
                j++;
            }
        }
        while (i < left) {
            sPrime[k] = l[i];
            R[k] = lR[i];
            L[k] = lL[i];
            k++;
            i++;
        }
        while (j < right) {
            sPrime[k] = r[j];
            R[k] = rR[j];
            L[k] = rL[j];
            k++;
            j++;
        }
    }


    private void swapDistances(int[] arr, int i, int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;

        int tempValue = this.valueDistance[i];
        this.valueDistance[i] = this.valueDistance[j];
        this.valueDistance[j] = tempValue;
    }
    private int partitionDistances(int[] arr, int low, int high)
    {

        int pivot = arr[high];

        int i = (low - 1);

        for(int j = low; j <= high - 1; j++)
        {

            if (arr[j] < pivot)
            {
                i++;
                swapDistances(arr, i, j);
            }
        }
        swapDistances(arr, i + 1, high);
        return (i + 1);
    }
    private void quickSortDistances(int[] arr, int low, int high)
    {
        if (low < high)
        {

            int pi = partitionDistances(arr, low, high);

            quickSortDistances(arr, low, pi - 1);
            quickSortDistances(arr, pi + 1, high);
        }
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