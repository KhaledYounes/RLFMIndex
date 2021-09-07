import java.util.*;
import java.util.stream.Collectors;

public class InParallel {

    public static class PreRunsThread extends Thread {

        private int[] array;
        private ArrayList<Tuple<Character, Integer>> preRuns = new ArrayList<>();
        private char[] bwt;

        public PreRunsThread (int[] array, char[] bwt) {
            this.array = array.clone();
            this.bwt = bwt.clone();
        }

        @Override
        public void run() {

            this.preRuns.add(new Tuple<>(bwt[0], 1));

            for (int i=1; i<this.array.length-1; i++) {
                int current = this.array[i]-1; int post = this.array[i+1]-1;
                this.preRuns.add(new Tuple<>(bwt[current], post - current ));
            }

            this.preRuns.add(new Tuple<>(bwt[this.array[this.array.length-1]-1], bwt.length - (this.array[this.array.length-1] - 1) ));

        }

        public ArrayList<Tuple<Character, Integer>> getPreRuns() {
            return preRuns;
        }
    }

    public static class ToCalculateRThread extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;
        private int[] R;

        public ToCalculateRThread (ArrayList<Tuple<Character, Integer>> arrayList) {

            this.arrayList = arrayList;

        }

        @Override
        public void run() {

            this.arrayList.sort(Comparator.comparing(o -> o.x));

            for(int i=1; i<this.arrayList.size(); i++) {

                if (this.arrayList.get(i).x == this.arrayList.get(i-1).x) {
                    this.arrayList.get(i).y += this.arrayList.get(i-1).y;
                }

            }

            this.R = this.arrayList.parallelStream().map(o -> o.y).mapToInt(Integer::intValue).toArray();

        }

        public int[] getR() {
            return R;
        }

    }


    public static class ToCalculateLThread extends Thread {

        private int[] array;
        private char[] bwt;
        private int[] suffixes;
        private int[] L;

        public ToCalculateLThread (int[] array, char[] bwt, int[] suffixes) {
            this.array = array.clone();
            this.bwt = bwt.clone();
            this.suffixes = suffixes.clone();
        }

        @Override
        public void run() {

            ArrayList<Tuple<Character, Integer>> tupleArrayList = new ArrayList<>();

            tupleArrayList.add(new Tuple<>(bwt[0], 0));

            for (int i=1; i<this.array.length-1; i++) {
                int current = this.array[i]-1; int post = this.array[i+1] - 1;
                tupleArrayList.add(new Tuple<>(bwt[current], post-1));
            }

            tupleArrayList.add(new Tuple<>(bwt[this.array[this.array.length-1]-1], bwt.length-1));

            tupleArrayList.sort(Comparator.comparing(o -> o.x));

            this.L = tupleArrayList.parallelStream().map(x -> this.suffixes[x.y]).mapToInt(Integer::intValue).toArray();

        }

        public int[] getL() {
            return L;
        }
    }


    public static class DistancesThread extends Thread {

        private int[] array;
        private char[] bwt;
        private int[] suffixes;
        private List<Tuple<Integer, Integer>> sorted;
        private int [] keyDistance;
        private int [] valueDistance;

        public DistancesThread (int[] array, char[] bwt, int[] suffixes) {
            this.array = array.clone();
            this.bwt = bwt.clone();
            this.suffixes = suffixes.clone();
        }

        @Override
        public void run() {

            ArrayList<Tuple<Integer, Integer>> tupleArrayList = new ArrayList<>();

            for (int i=1; i<this.array.length; i++) {
                int temp = this.array[i]-1;
                tupleArrayList.add(new Tuple<>(suffixes[temp], suffixes[temp-1] - suffixes[temp]));
            }

            this.sorted = tupleArrayList.parallelStream().sorted(Comparator.comparing(o -> o.x)).collect(Collectors.toList());

            int[] distancesKeysArray = new int[this.sorted.size()];
            int[] distancesValuesArray = new int[this.sorted.size()];

            for(int i=0; i< this.sorted.size(); i++) {
                Tuple<Integer, Integer> tempTuple = this.sorted.get(i);
                distancesKeysArray[i] = tempTuple.x;
                distancesValuesArray[i] = tempTuple.y;
            }

            this.keyDistance = distancesKeysArray;
            this.valueDistance = distancesValuesArray;

        }

        public int[] getKeyDistance() {
            return keyDistance;
        }

        public int[] getValueDistance() {
            return valueDistance;
        }

    }

}
