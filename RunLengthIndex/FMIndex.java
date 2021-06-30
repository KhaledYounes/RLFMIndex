import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FMIndex {

    private final int[] C;
    private final int[][] rankInitial;
    private final char[] bwtOfText;
    private final char[] characters;
    int[] suffixes;

    FMIndex(String Text) {

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

        ArrayList<HashMap<Character, Integer>> preRankInitial = new ArrayList<>();

        this.suffixes = suffixes;

        HashMap<Character, Integer> toBeC;

        toBeC = computeC(bwt, occArray);


        for (int i=0; i<bwt.length; i+=64) {
            HashMap<Character, Integer> hashMap = new HashMap<>();
            for (char c : toBeC.keySet()) {

                int k = i;
                int lastIndex = i - 64;
                while ( k>0 && k>lastIndex && bwt[k]!=c) k--;

                if(k==0){
                    if(bwt[0]==c) hashMap.put(c, occArray[k]);
                    else hashMap.put(c, 0);
                } else if (k==lastIndex){
                    hashMap.put(c, preRankInitial.get(k/64).get(c));
                } else {
                    hashMap.put(c, occArray[k]);
                }
            }
            preRankInitial.add(hashMap);
        }

        occArray = null;

        Character[] charactersCharacter = toBeC.keySet().toArray(Character[]::new);
        this.characters = new char[charactersCharacter.length];
        for (int i=0; i<this.characters.length; i++) {
            this.characters[i] = charactersCharacter[i];
        }

        charactersCharacter = null;

        Arrays.sort(this.characters);

        this.bwtOfText = bwt;

        Rank rank = new Rank(preRankInitial);

        preRankInitial = null;

        this.rankInitial = rank.getRankInitial();

        rank = null;

        this.C = new int[this.characters.length];

        for(int i=0; i<this.characters.length; i++) {
            this.C[i] = toBeC.get(this.characters[i]);
        }

        toBeC = null;

    }

    public static HashMap<Character, Integer> computeC (char[] bwt, int[] occ) {

        HashMap<Character, Integer> preC = new HashMap<>();

        for (char c : bwt) {
            if (!preC.containsKey(c)) {
                preC.put(c, 1);
            } else {
                preC.replace(c, preC.get(c), preC.get(c)+1);
            }
        }

        Character[] characters = preC.keySet().toArray(Character[]::new);
        Arrays.sort(characters);

        HashMap<Character, Integer> computedC = new HashMap<>();

        for (char c : characters) {
            computedC.put(c, preC.get(c));
        }

        for (int i=computedC.size()-1; i>0; i--) {

            int k = i;
            int newValue = 0;

            while (k>0) {
                newValue += computedC.get(characters[k-1]);
                k--;
            }

            computedC.replace(characters[i], computedC.get(characters[i]), newValue);

        }

        computedC.replace('$', computedC.get('$'), 0);

        return computedC;

    }

    public int rank(char c, char[] bwt, int q){

        List<Character> characterList = new ArrayList<>();
        for (char character : this.characters) characterList.add(character);

        if (!characterList.contains(c) || q<=0) return 0;

        q--;

        int index = q/64;

        if (q % 64 == 0) {
            return rankInitial[index][characterList.indexOf(c)];
        } else {

            int preValue = rankInitial[index][characterList.indexOf(c)];
            int toAdd = 0;

            for (int i = (64*index)+1; i<=q; i++) {
                if(bwt[i]==c) toAdd++;
            }
            return preValue+toAdd;
        }

    }

    public int[] getRange (char[] P) {

        if (P.length==0) return new int[]{};

        List<Integer> integerList = Arrays.stream(this.C).boxed().collect(Collectors.toList());
        List<Character> characterList = new ArrayList<>();
        for (char c : this.characters) {
            characterList.add(c);
        }

        for (char l : P) {
            if(!characterList.contains(l)) return new int[]{};
        }

        int i = P.length-1;
        char c = P[i], nc = nextGreatestAlphabet(this.characters, c);


        int first = integerList.get(characterList.indexOf(c))+1;
        int last = integerList.get(characterList.indexOf(nc));


        if(c==nc) {
            last = bwtOfText.length;
        }

        //System.out.println(Arrays.toString(new int[]{first, last}));

        while (first<=last && i>0) {
            c = P[i-1];

            first = integerList.get(characterList.indexOf(c)) + rank(c, this.bwtOfText, first-1) + 1;
            last = integerList.get(characterList.indexOf(c)) + rank(c, this.bwtOfText, last);

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

        return ' ';

    }

    public int[] locate (char[] P) {

        int [] range = getRange(P);

        if (range.length==0) return new int[]{};

        int first = range[0], last = range[1];
        int[] result = new int[last - first + 1];

        int firstIndex = first-1;
        for(int i=0; i<result.length; i++) {
            result[i] = this.suffixes[firstIndex];
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