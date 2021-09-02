import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortInParallel extends Thread {

    private ArrayList<Tuple<Character, Integer>> arrayListChatInt;
    private ArrayList<Tuple<Integer, Integer>> arrayListIntInt;
    private List<Tuple<Integer, Integer>> sorted;

    public SortInParallel (ArrayList<Tuple<Character, Integer>> arrayListChatInt, ArrayList<Tuple<Integer, Integer>> arrayListIntInt) {
        this.arrayListChatInt = arrayListChatInt;
        this.arrayListIntInt = arrayListIntInt;
    }

    @Override
    public void run() {
        if ( this.arrayListChatInt != null ) {

            arrayListChatInt.sort(Comparator.comparing(o -> o.x));

        } else {

            sorted = arrayListIntInt.parallelStream().sorted(Comparator.comparing(o -> o.x)).collect(Collectors.toList());

        }
    }

    public List<Tuple<Integer, Integer>> getSorted() {
        return sorted;
    }

}
