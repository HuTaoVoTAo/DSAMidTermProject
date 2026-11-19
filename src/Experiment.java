import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

public class Experiment {

    // 1. Ham tao mang ngau nhien kich thuoc N (dung seed co dinh de ket qua lap lai duoc)
    public static Integer[] generateRandomArray(int n, long seed) {
        Random rand = new Random(seed);
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = rand.nextInt(1_000_000);
        }
        return a;
    }

    // 1b. Ham luu bo du lieu test ra file .txt de nop kem theo yeu cau de bai
    public static void saveDatasetToFile(Integer[] a, String filename) throws Exception {
        PrintWriter out = new PrintWriter(new FileWriter(filename));
        out.println(a.length); // Dong dau tien ghi so luong phan tu N
        for (int i = 0; i < a.length; i++) {
            out.println(a[i]);
        }
        out.close();
    }

    // 2. Tinh Trung binh (Mean) - theo chu de 1.3 Algorithm Analysis (StdStats.mean)
    public static double mean(double[] times) {
        double sum = 0.0;
        for (double t : times) {
            sum += t;
        }
        return sum / times.length;
    }

    // 3. Tinh Do lech chuan (Sample StdDev) - theo chu de 1.3 (StdStats.stddev)
    public static double stddev(double[] times) {
        double avg = mean(times);
        double sumSq = 0.0;
        for (double t : times) {
            sumSq += (t - avg) * (t - avg);
        }
        return Math.sqrt(sumSq / (times.length - 1));
    }

    public static void main(String[] args) throws Exception {
        int N = 500_000;       // Kich thuoc mang thu nghiem: 500,000 phan tu
        int TRIALS = 15;       // So lan do lap lai cho moi CUTOFF
        int[] cutoffs = {1, 3, 5, 7, 8, 10, 12, 15, 16, 20, 24, 32, 48, 64, 100};

        System.out.println("=== BAT DAU THI NGHIEM TIM CUTOFF TOI UU ===");
        System.out.println("Kich thuoc mang N = " + N + " | So lan lap TRIALS = " + TRIALS);

        // Buoc A: JVM Warm-up (Chay nhap 5 lan de JIT Compiler toi uu ma may)
        System.out.println("Dang Warm-up JVM...");
        for (int w = 0; w < 5; w++) {
            Integer[] warmArr = generateRandomArray(100_000, w);
            MergeSort.sort(warmArr, 7);
        }

        // Buoc B: Tao bo du lieu cho T lan thu (dam bao moi CUTOFF deu chay tren CUNG bo du lieu)
        Integer[][] testDatasets = new Integer[TRIALS][];
        for (int t = 0; t < TRIALS; t++) {
            testDatasets[t] = generateRandomArray(N, 2026L + t);
        }
        // Luu bo du lieu test dau tien (seed = 2026) ra file data/test_data.txt
        new java.io.File("data").mkdirs();
        saveDatasetToFile(testDatasets[0], "data/test_data.txt");
        System.out.println("Da luu bo du lieu thi nghiem ra file: data/test_data.txt");

        // Mo file CSV de luu ket qua thong ke
        new java.io.File("results").mkdirs();
        PrintWriter csv = new PrintWriter(new FileWriter("results/results.csv"));
        csv.println("Cutoff,Mean_ms,StdDev_ms,Improvement_Percent");

        System.out.println("\n--------------------------------------------------------------");
        System.out.printf("%-10s | %-15s | %-15s | %-15s%n", "CUTOFF", "Mean (ms)", "StdDev (ms)", "Cai thien (%)");
        System.out.println("--------------------------------------------------------------");

        double baselineMean = 0.0; // Thoi gian cua CUTOFF = 1 (Mergesort chuan khong cutoff)
        int bestCutoff = 1;
        double bestMean = Double.MAX_VALUE;

        // Buoc C: Do thoi gian cho tung gia tri CUTOFF
        for (int cutoff : cutoffs) {
            double[] times = new double[TRIALS];

            for (int t = 0; t < TRIALS; t++) {
                // Clone mang goc de khong lam thay doi du lieu cua cac CUTOFF sau
                Integer[] a = testDatasets[t].clone();

                // Bam gio bang System.nanoTime()
                long start = System.nanoTime();
                MergeSort.sort(a, cutoff);
                long end = System.nanoTime();

                // Doi tu nano-giay sang mili-giay (1 ms = 1,000,000 ns)
                times[t] = (end - start) / 1_000_000.0;
            }

            double avgTime = mean(times);
            double stdTime = stddev(times);

            if (cutoff == 1) {
                baselineMean = avgTime;
            }

            // Tinh % nhanh hon so voi Mergesort goc (CUTOFF = 1)
            double improvement = ((baselineMean - avgTime) / baselineMean) * 100.0;

            if (avgTime < bestMean) {
                bestMean = avgTime;
                bestCutoff = cutoff;
            }

            System.out.printf("%-10d | %-15.2f | %-15.2f | %+14.2f%%%n",
                    cutoff, avgTime, stdTime, improvement);
            csv.printf("%d,%.4f,%.4f,%.2f%n", cutoff, avgTime, stdTime, improvement);
        }

        csv.close();
        System.out.println("--------------------------------------------------------------");
        System.out.printf("=> KET LUAN: Tren may cua ban, CUTOFF tot nhat la %d (Trung binh: %.2f ms)%n",
                bestCutoff, bestMean);
        System.out.println("=> Da luu bang so lieu vao file: results/results.csv");
    }
}
