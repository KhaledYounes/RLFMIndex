import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Rank {

    private final int[][] rankInitial;

    public Rank(ArrayList<HashMap<Character, Integer>> ranks) {
        Character[] chars = ranks.get(0).keySet().toArray(Character[]::new);
        Arrays.sort(chars);
        this.rankInitial = new int[ranks.size()][chars.length];
        for (int i=0; i < this.rankInitial.length; i++) {
            for(int j=0; j < chars.length; j++) {
                this.rankInitial[i][j] = ranks.get(i).get(chars[j]);
            }
        }
    }

    public int[][] getRankInitial() {
        return rankInitial;
    }

}
