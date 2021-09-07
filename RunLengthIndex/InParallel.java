import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InParallel {

    public static class ToCalculateRThread extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;
        private int[] R;

        public ToCalculateRThread (ArrayList<Tuple<Character, Integer>> arrayList) {

            this.arrayList = arrayList;

        }

        @Override
        public void run() {

            this.arrayList.sort(Comparator.comparing(o -> o.x));

            System.out.println("11");

            for(int i=1; i<this.arrayList.size(); i++) {

                if (this.arrayList.get(i).x == this.arrayList.get(i-1).x) {
                    this.arrayList.get(i).y += this.arrayList.get(i-1).y;
                }

            }


            System.out.println("12");

            this.R = this.arrayList.parallelStream().map(o -> o.y).mapToInt(Integer::intValue).toArray();

            System.out.println("13");

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

            System.out.println("21");

            for (int i=0; i<this.arrayList.size()-1; i++) {
                this.arrayList.get(i).y = this.arrayList.get(i+1).y - 1;
            }
            this.arrayList.get(this.arrayList.size()-1).y = bwt.length - 1;

            System.out.println("22");

            this.arrayList.sort(Comparator.comparing(o -> o.x));

            System.out.println("23");

            this.L = this.arrayList.parallelStream().map(x -> this.suffixes[x.y]).mapToInt(Integer::intValue).toArray();

            System.out.println("24");

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

            System.out.println("31");

            this.sorted = this.arrayList.parallelStream().sorted(Comparator.comparing(o -> o.x)).collect(Collectors.toList());

            System.out.println("32");

            int[] distancesKeysArray = new int[this.sorted.size()];
            int[] distancesValuesArray = new int[this.sorted.size()];


            for(int i=0; i< this.sorted.size(); i++) {
                Tuple<Integer, Integer> temp = this.sorted.get(i);
                distancesKeysArray[i] = temp.x;
                distancesValuesArray[i] = temp.y;
            }

            this.keyDistance = distancesKeysArray;
            this.valueDistance = distancesValuesArray;

            System.out.println("33");

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

            System.out.println("41");

            this.preData = this.arrayList.parallelStream().mapToInt(Integer::intValue).toArray();

            System.out.println("42");

        }

        public int[] getPreData() {
            return preData;
        }
    }

}