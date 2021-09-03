import java.util.ArrayList;

public class ForInParallel {

    public static class PreRuns extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;
        private char[] bwt;

        public PreRuns (ArrayList<Tuple<Character, Integer>> arrayList, char[] bwt) {
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

        }

    }


    public static class ToBeL extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;
        private char[] bwt;

        public ToBeL (ArrayList<Tuple<Character, Integer>> arrayList, char[] bwt) {
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

        }

    }


    public static class Distances extends Thread {

        private ArrayList<Tuple<Integer, Integer>> arrayList;
        private char[] bwt;
        private int[] suffixes;

        public Distances (ArrayList<Tuple<Integer, Integer>> arrayList, char[] bwt, int[] suffixes) {
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

        }

    }


    public static class PreData extends Thread {

        private ArrayList<Integer> arrayList;
        private char[] bwt;

        public PreData (ArrayList<Integer> arrayList, char[] bwt) {
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

        }

    }


    public static class ModifyPreRuns extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;

        public ModifyPreRuns (ArrayList<Tuple<Character, Integer>> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public void run() {

            for (int i=0; i<arrayList.size()-1; i++) {
                arrayList.get(i).y = (arrayList.get(i + 1).y - arrayList.get(i).y);
            }

        }

    }


    public static class ModifyL extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;

        public ModifyL (ArrayList<Tuple<Character, Integer>> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public void run() {

            for (int i=0; i<arrayList.size()-1; i++) {
                arrayList.get(i).y = arrayList.get(i+1).y - 1;
            }

        }

    }


    public static class SPrime extends Thread {

        private ArrayList<Tuple<Character, Integer>> arrayList;
        private char [] array;

        public SPrime (char[] array, ArrayList<Tuple<Character, Integer>> arrayList) {
            this.array = array;
            this.arrayList = arrayList;
        }

        @Override
        public void run() {

            for (int i=0; i<arrayList.size()-1; i++) {
                this.array[i] = (char) arrayList.get(i).x;
            }

        }

    }


    public static class PreDataArray extends Thread {

        private ArrayList<Integer> arrayList;
        private int [] array;

        public PreDataArray (int[] array, ArrayList<Integer> arrayList) {
            this.array = array;
            this.arrayList = arrayList;
        }

        @Override
        public void run() {

            for (int i=0; i<arrayList.size()-1; i++) {
                this.array[i] = (int) arrayList.get(i);
            }

        }

    }


}
