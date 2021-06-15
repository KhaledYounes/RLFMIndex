import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FMIndex {

    private HashMap<Character, Integer> C;
    private final ArrayList<HashMap<Character, Integer>> rankInitial = new ArrayList<>();
    private final Character[] characters;
    int[] suffixes;

    FMIndex(char[] bwt, int[] occArray, int[] suffixes) {

        this.suffixes = suffixes;

        this.C = computeC(bwt, occArray);

        for (int i=0; i<bwt.length; i+=64) {
            HashMap<Character, Integer> hashMap = new HashMap<>();
            for (char c : C.keySet()) {

                int k = i;
                while ( k>0 && bwt[k]!=c) k--;

                if(k==0){
                    if(bwt[0]==c) hashMap.put(c, occArray[k]);
                    else hashMap.put(c, 0);
                } else {
                    hashMap.put(c, occArray[k]);
                }
            }
            rankInitial.add(hashMap);
        }

        this.characters = C.keySet().toArray(Character[]::new);
        Arrays.sort(this.characters);

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

        q--;

        int index = q/64;

        if (q % 64 == 0) {
            return rankInitial.get(index).get(c);
        } else {

            int preValue = rankInitial.get(index).get(c);
            int toAdd = 0;

            for (int i = (64*index)+1; i<=q; i++) {
                if(bwt[i]==c) toAdd++;
            }
            return preValue+toAdd;
        }

    }

    public int[] getRange (char[] P, char[] bwt) {

        for (char l : P) {
            if(!C.containsKey(l)) return new int[]{};
        }

        int i = P.length-1;
        char c = P[i], nc = nextGreatestAlphabet(this.characters, c);


        int first = C.get(c)+1;
        int last = C.get(nc);


        if(c==nc) {
            last = bwt.length;
        }

        //System.out.println(Arrays.toString(new int[]{first, last}));

        while (first<=last && i>0) {
            c = P[i-1];
            first = C.get(c) + rank(c, bwt, first-1) + 1;
            last = C.get(c) + rank(c, bwt, last);

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

    public static char nextGreatestAlphabet(Character[] alphabets, char c)
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

    public int[] locate (char[] P, char[] bwt) {

        int [] range = getRange(P, bwt);

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

    public ArrayList<HashMap<Character, Integer>> getRankInitial() {
        return rankInitial;
    }

    public HashMap<Character, Integer> getC() {
        return C;
    }

    public Character[] getCharacters() {
        return characters;
    }

}