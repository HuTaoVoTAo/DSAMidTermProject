public class MergeSort {

    // 1. Ham ho tro: So sanh v < w (dung chuan slide)
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    // 2. Ham ho tro: Doi cho 2 phan tu a[i] va a[j]
    private static void exch(Comparable[] a, int i, int j) {
        Comparable swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    // 3. Insertion Sort cho doan mang con a[lo..hi]
    public static void insertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(a[j], a[j - 1]); j--) {
                exch(a, j, j - 1);
            }
        }
    }

    // 4. Ham Merge: Gop 2 nua da sap xep a[lo..mid] va a[mid+1..hi] (Slide trang 7)
    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
        // Buoc 4.1: Copy du lieu tu a sang mang phu aux
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        // Buoc 4.2: Tron tu aux nguoc ve lai a
        int i = lo;       // con tro nua trai
        int j = mid + 1;  // con tro nua phai
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)              a[k] = aux[j++]; // Nua trai da het
            else if (j > hi)               a[k] = aux[i++]; // Nua phai da het
            else if (less(aux[j], aux[i])) a[k] = aux[j++]; // Phan tu ben phai nho hon
            else                           a[k] = aux[i++]; // Phan tu ben trai nho hon hoac bang
        }
    }

    // 5. Ham Sort de quy co su dung CUTOFF (Slide trang 9 + trang 12)
    private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi, int cutoff) {
        // CAI TIEN CUTOFF: Neu doan mang con <= cutoff phan tu -> dung Insertion Sort
        if (hi <= lo + cutoff - 1) {
            insertionSort(a, lo, hi);
            return;
        }

        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid, cutoff);      // Sap xep nua trai
        sort(a, aux, mid + 1, hi, cutoff);  // Sap xep nua phai
        merge(a, aux, lo, mid, hi);         // Gop 2 nua lai
    }

    // 6. Ham Sort chinh (diem bat dau goi)
    public static void sort(Comparable[] a, int cutoff) {
        Comparable[] aux = new Comparable[a.length]; // Cap phat mang phu DUNG 1 LAN
        sort(a, aux, 0, a.length - 1, cutoff);
    }

    // Ham main nho de test thu xem code chay dung chua
    public static void main(String[] args) {
        Integer[] arr = {38, 27, 43, 3, 9, 82, 10, 15, 1, 99, 54, 2};
        System.out.println("Truoc khi sort: " + java.util.Arrays.toString(arr));

        // Test thu voi CUTOFF = 4
        sort(arr, 4);

        System.out.println("Sau khi sort:   " + java.util.Arrays.toString(arr));
    }
}
