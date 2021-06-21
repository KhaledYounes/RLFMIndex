import java.util.Arrays;
import java.util.HashMap;

public class Distance {

    private final int[] keyArray;
    private final int[] valueArray;

    public Distance(HashMap<Integer, Integer> hashMap) {

        Integer[] key = hashMap.keySet().toArray(Integer[]::new);
        Integer[] value = hashMap.values().toArray(Integer[]::new);

        this.keyArray = Arrays.stream(key).mapToInt(Integer::intValue).toArray();
        this.valueArray = Arrays.stream(value).mapToInt(Integer::intValue).toArray();

    }

    public int getValueOfKey(int key) {
        return this.valueArray[indexOf(key)];
    }

    private int indexOf(int key) {
        int l=0, r= keyArray.length-1, m = 0;
        while (l<=r) {
            m = (l+r)/2;
            if (this.keyArray[m]==key) break;
            else if (this.keyArray[m]<key) l = m+1;
            else r = m-1;
        }
        return this.keyArray[m]==key ? m : -1;
    }

    public int[] getKeyArray() {
        return keyArray;
    }

    public int[] getValueArray() {
        return valueArray;
    }

}
