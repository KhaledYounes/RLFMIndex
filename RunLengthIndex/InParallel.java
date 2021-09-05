import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class InParallel {

    public static class PreRunsThread extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;
        private char[] bwt;
        private char[] sPrime;

        public PreRunsThread (ArrayList<Tuple<Character, Integer>> arrayList, char[] bwt) {
            this.arrayList = arrayList;
            this.bwt = bwt;
        }

        @Override
        public void run() {

            for (int i=1; i< this.bwt.length; i++) {
                if(this.bwt[i]!=this.bwt[i-1]) {
                    this.arrayList.add(new Tuple<>(bwt[i], i));
                }
            }

            System.out.println(1);

            for (int i=0; i<this.arrayList.size()-1; i++) {
                this.arrayList.get(i).y = this.arrayList.get(i + 1).y - this.arrayList.get(i).y;
            }

            this.arrayList.get(this.arrayList.size()-1).y =
                    (bwt.length) - this.arrayList.get(this.arrayList.size()-1).y;


            System.out.println(2);

            this.sPrime = this.arrayList.stream().map(o -> o.x)
                    .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))
                    .toCharArray();

            System.out.println(3);
        }

        public char[] getsPrime() {
            return sPrime;
        }

        public ArrayList<Tuple<Character, Integer>> getArrayList() {
            return arrayList;
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

            this.R = Arrays.stream(this.arrayList.stream().map(o -> o.y)
                    .toArray(Integer[]::new)).mapToInt(Integer::intValue).toArray();


        }

        public int[] getR() {
            return R;
        }

    }


    public static class ToCalculateLThread extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;
        private char[] bwt;
        private int[] suffixes;
        private int[] L;

        public ToCalculateLThread (ArrayList<Tuple<Character, Integer>> arrayList, char[] bwt, int[] suffixes) {
            this.arrayList = arrayList;
            this.bwt = bwt;
            this.suffixes = suffixes;
        }

        @Override
        public void run() {

            for (int i=1; i< this.bwt.length; i++) {
                if(this.bwt[i]!=this.bwt[i-1]) {
                    this.arrayList.add(new Tuple<>(bwt[i], i));
                }
            }

            for (int i=0; i<this.arrayList.size()-1; i++) {
                this.arrayList.get(i).y = this.arrayList.get(i+1).y - 1;
            }
            this.arrayList.get(this.arrayList.size()-1).y = bwt.length - 1;

            this.arrayList.sort(Comparator.comparing(o -> o.x));

            this.L = Arrays.stream(this.arrayList.stream().map(x -> this.suffixes[x.y])
                    .toArray(Integer[]::new)).mapToInt(Integer::intValue).toArray();

        }

        public int[] getL() {
            return L;
        }
    }


    public static class DistancesThread extends Thread {

        private ArrayList<Tuple<Integer, Integer>> arrayList;
        private char[] bwt;
        private int[] suffixes;
        private List<Tuple<Integer, Integer>> sorted;
        private int [] keyDistance;
        private int [] valueDistance;

        public DistancesThread (ArrayList<Tuple<Integer, Integer>> arrayList, char[] bwt, int[] suffixes) {
            this.arrayList = arrayList;
            this.bwt = bwt;
            this.suffixes = suffixes;
        }

        @Override
        public void run() {

            for (int i=1; i< this.bwt.length; i++) {
                if(this.bwt[i]!=this.bwt[i-1]) {
                    this.arrayList.add(new Tuple<>(suffixes[i], suffixes[i-1]-suffixes[i]));
                }
            }

            this.sorted = this.arrayList.parallelStream().sorted(Comparator.comparing(o -> o.x)).collect(Collectors.toList());


            int[] distancesKeysArray = new int[this.sorted.size()];
            int[] distancesValuesArray = new int[this.sorted.size()];


            for(int i=0; i< this.sorted.size(); i++) {
                distancesKeysArray[i] = this.sorted.get(i).x;
                distancesValuesArray[i] = this.sorted.get(i).y;
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


    public static class PreDataThread extends Thread {

        private ArrayList<Integer> arrayList;
        private char[] bwt;
        private int[] preData;

        public PreDataThread (ArrayList<Integer> arrayList, char[] bwt) {
            this.arrayList = arrayList;
            this.bwt = bwt;
        }

        @Override
        public void run() {

            for (int i=1; i< this.bwt.length; i++) {
                if(this.bwt[i]!=this.bwt[i-1]) {
                    this.arrayList.add(i+1);
                }
            }

            this.preData = this.arrayList.stream().mapToInt(Integer::intValue).toArray();

        }

        public int[] getPreData() {
            return preData;
        }
    }

}
