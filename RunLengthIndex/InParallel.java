public class InParallel {

    public static class ToCalculateRLThread extends Thread {

        private final int[] suffixes;
        private final int[] preData;
        private final char[] sPrime;
        private final int[] R;
        private final int[] L;

        public ToCalculateRLThread (int[] suffixes, int[] preData, char[] sPrime) {

            this.suffixes = suffixes.clone();
            this.preData = preData.clone();
            this.sPrime = sPrime.clone();
            this.R = new int[preData.length];
            this.L = new int[preData.length];

        }

        @Override
        public void run() {

            this.R[0] = this.preData[1] - this.preData[0];
            this.L[0] = 0;
            for (int i=1; i<preData.length-1; i++) {
                this.R[i] = this.preData[i+1] - this.preData[i];
                this.L[i] = this.preData[i+1] - 2;
            }
            this.R[preData.length-1] = suffixes.length - (this.preData[this.preData.length-1] - 1);
            this.L[preData.length-1] = suffixes.length-1;

            mergeSortRuns(this.sPrime, this.R, this.L, this.sPrime.length);

            this.L[0] = suffixes[this.L[0]];

            for (int i=1; i<this.sPrime.length; i++) {

                int currentL = this.L[i];
                this.L[i] = suffixes[currentL];

                if(this.sPrime[i] == this.sPrime[i-1]) {
                    this.R[i] += this.R[i - 1];
                }
            }
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

        public int[] getR() {
            return R;
        }

        public int[] getL() {
            return L;
        }

    }


    public static class DistancesThread extends Thread {

        private final int[] suffixes;
        private final int[] preData;
        private final int[] keyDistance;
        private final int[] valueDistance;

        public DistancesThread (int[] suffixes, int[] preData) {
            this.suffixes = suffixes.clone();
            this.preData = preData.clone();
            this.keyDistance = new int[preData.length-1];
            this.valueDistance = new int[preData.length-1];
        }

        @Override
        public void run() {

            for (int i=1; i<this.preData.length; i++) {
                int currentRun = this.preData[i]-1;
                this.keyDistance[i-1] = this.suffixes[currentRun];
                this.valueDistance[i-1] = this.suffixes[currentRun-1] - this.suffixes[currentRun];
            }
            quickSortDistances(this.keyDistance, 0, this.keyDistance.length-1);
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

        public int[] getKeyDistance() {
            return keyDistance;
        }

        public int[] getValueDistance() {
            return valueDistance;
        }

    }

}
