# LOG THẢO LUẬN VỚI AI TRONG QUÁ TRÌNH THỰC HIỆN THÍ NGHIỆM

> **Ghi chú:** Tài liệu này ghi lại lịch sử các lượt trao đổi giữa sinh viên và AI trong quá trình tìm hiểu lý thuyết từ bài giảng (*2.2 Mergesort* & *1.3 Algorithm Analysis*), tự dựng và rà soát cài đặt `MergeSort.java`, cũng như nhờ AI hỗ trợ viết script chạy thí nghiệm (`Experiment.java`) và vẽ đồ thị (`plot.py`).

---

## Phiên 1: Thảo luận lý thuyết & Điều kiện dừng `CUTOFF` từ slide bài giảng

**Sinh viên:**
> Đây là đề bài project giữa kỳ môn DSA của mình. Mình muốn làm từ từ từng bước để hiểu rõ bản chất vấn đề trong bài giảng trước khi code. Đầu tiên bạn giải thích giúp mình tại sao Mergesort là $O(N \log N)$ mà khi mảng nhỏ lại phải dùng Insertion Sort $O(N^2)$ với?

**AI:**
Chào bạn! Lý do nằm ở **chi phí ẩn (overhead)** và hằng số đứng trước độ phức tạp khi kích thước mảng con $n$ rất nhỏ:
1. **Chi phí gọi đệ quy (Recursion overhead):** Mergesort chia đôi mảng liên tục xuống tận khi mảng chỉ còn 1 phần tử (`hi <= lo`). Với mảng lớn, hơn 90% số lời gọi hàm đệ quy nằm ở các tầng lá cuối cùng (nơi mảng chỉ có 1, 2, 3, 4... phần tử). Mỗi lần gọi hàm tốn chi phí tạo Stack Frame.
2. **Chi phí mảng phụ (`aux`):** Mỗi lần gọi `merge(a, aux, lo, mid, hi)`, Mergesort phải copy dữ liệu từ `a` sang `aux` rồi lại so sánh để chép ngược về `a`.
3. **Ưu thế của Insertion Sort trên mảng nhỏ:** Insertion Sort chạy hoàn toàn tại chỗ (in-place) bằng 2 vòng lặp đơn giản, không gọi đệ quy, không dùng mảng phụ và tận dụng cực tốt bộ nhớ đệm **CPU L1 Cache**. Khi $n$ nhỏ (ví dụ $n \le 16$), số phép toán thực tế của Insertion Sort ($\approx n^2/4$) chạy nhanh hơn hẳn việc chia đệ quy và trộn mảng.

Trong slide trang 12 của bài giảng `2.2 Mergesort`, thầy cô có đưa đoạn code:
```java
if (hi <= lo + CUTOFF - 1) {
    Insertion.sort(a, lo, hi);
    return;
}
```
Đố bạn biết tại sao điều kiện dừng lại viết là `hi <= lo + CUTOFF - 1` mà không phải là `hi <= lo + CUTOFF`?

**Sinh viên:**
> Do `hi` là phần tử lớn nhất tối đa trong đoạn `cutoff` ta đang xét (khoảng từ `lo` đến `lo + cutoff - 1`).
> Tôi sẽ tự code phần `MergeSort.java` từ slide bài giảng, sau đó bạn hãy đánh giá và sửa lại cho hợp lý nhé, ưu tiên bám sát bài giảng và giữ code đơn giản, rõ ràng.

---

## Phiên 2: Đánh giá và sửa lỗi cài đặt `MergeSort.java`

**AI:**
Bạn giải thích chuẩn xác rồi! Vì cả `lo` và `hi` đều là chỉ số bao gồm (inclusive) nên đoạn từ `lo` đến `lo + CUTOFF - 1` có đúng `(lo + CUTOFF - 1) - lo + 1 = CUTOFF` phần tử. Đặc biệt, khi `CUTOFF = 1` thì điều kiện `hi <= lo + 1 - 1` trở thành `hi <= lo` — chính là Mergesort thuần túy không dùng Insertion Sort. Bạn cứ viết `MergeSort.java` theo slide rồi gửi mình xem nhé!

**Sinh viên:**
> Mình vừa tự code file `MergeSort.java` dựa theo slide rồi, chạy thử mảng nhỏ trong `main` thấy nó vẫn sắp xếp đúng thứ tự, nhưng không hiểu sao khi thử với mảng lớn hơn thì cảm giác nó chạy hơi chậm, với bạn xem giúp mình hàm `insertionSort` và hàm `sort` mình ghép từ slide vào như này có bị lỗi ngầm chỗ nào không:
> ```java
> public static void insertionSort(Comparable[] a, int lo, int hi) {
>     for (int i = lo; i <= hi; i++) {
>         for (int j = i; j > 0 && less(a[j], a[j - 1]); j--) {
>             exch(a, j, j - 1);
>         }
>     }
> }
>
> private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi, int cutoff) {
>     if (hi <= lo + cutoff - 1) {
>         insertionSort(a, lo, hi);
>         return;
>     }
>     int mid = (lo + hi) / 2;
>     sort(a, aux, lo, mid, cutoff);
>     sort(a, aux, mid + 1, hi, cutoff);
>     merge(a, aux, lo, mid, hi);
> }
> ```

**AI:**
Mình xem kỹ đoạn code của bạn rồi! Đúng là code này chạy vẫn ra mảng được sắp xếp đúng nên không báo lỗi ngay, nhưng có **1 lỗi logic khá kín** ở hàm `insertionSort` khiến mảng lớn bị chạy chậm, cùng 1 điểm nhỏ ở cách tính `mid`:

1. **Lỗi giới hạn biên trái trong `insertionSort(a, lo, hi)` (`j > 0` thay vì `j > lo`):**
   - Trong slide bài giảng khi cài đặt Insertion Sort cho toàn bộ mảng `a[0..N-1]`, điều kiện dừng của vòng lặp trong là `j > 0`.
   - Nhưng ở đây ta chỉ muốn sắp xếp **đoạn mảng con `a[lo..hi]`**. Khi bạn để `j > 0`, mỗi lần `insertionSort` được gọi ở các nhánh mảng con bên phải (nơi `lo > 0`), phần tử `a[j]` sẽ bị so sánh và đổi chỗ tràn sang cả vùng mảng bên trái của `lo` (`a[0..lo-1]`)! Điều này vừa phá vỡ ranh giới chia để trị của từng mảng con, vừa làm thuật toán chạy dư rất nhiều phép so sánh.
   - **Cách sửa:** Đổi `j > 0` thành **`j > lo`**:
     ```java
     for (int j = i; j > lo && less(a[j], a[j - 1]); j--)
     ```
2. **Cách tính `mid` trong hàm `sort`:**
   - Bạn đang viết `int mid = (lo + hi) / 2;`. Theo chuẩn slide bài giảng, nên đổi thành `int mid = lo + (hi - lo) / 2;` để tránh tràn số nguyên (Integer Overflow) khi `lo + hi` vượt quá giới hạn `Integer.MAX_VALUE`.
3. **Các phần còn lại bạn làm rất chuẩn:**
   - Cấp phát mảng `aux` đúng 1 lần duy nhất ở hàm `sort(Comparable[] a, int cutoff)` bên ngoài trước khi gọi đệ quy (đúng cảnh báo trang 9 của slide).
   - Hàm `merge` giữ nguyên thứ tự 4 nhánh điều kiện từ slide trang 7, đảm bảo tính ổn định (Stability).

**Sinh viên:**
> Tiếp theo là bước viết script để chạy test, phần này mình sẽ nhờ hoàn toàn vào bạn nhé, sau đó hãy đánh giá giúp mình theo ý của bạn.

---

## Phiên 3: Viết script chạy thí nghiệm & thống kê theo chủ đề 1.3 Algorithm Analysis

**AI:**
Ok bạn! Để đúng yêu cầu *"Xem lại chủ đề 1.3. Algorithm Analysis để ôn tập về thí nghiệm và thống kê kết quả thí nghiệm"*, mình đã viết file [`src/Experiment.java`](./src/Experiment.java) với thiết kế như sau:
- **Kích thước mảng:** $N = 500,000$ phần tử `Integer` sinh ngẫu nhiên với `seed` cố định (`2026L + t`) để đảm bảo tính tái lập.
- **Các mức `CUTOFF` khảo sát:** `{1, 3, 5, 7, 8, 10, 12, 15, 16, 20, 24, 32, 48, 64, 100}` (bao gồm mốc cơ sở `1`, mốc sách cũ `7`, mốc sách mới `10`, và các giá trị xung quanh).
- **Warm-up JVM:** Chạy nháp 5 vòng trước khi bấm giờ để trình biên dịch JIT của Java tối ưu hóa mã máy.
- **Đo lặp và thống kê (`StdStats`):** Với mỗi mức `CUTOFF`, chạy lặp `TRIALS = 15` lần trên bản copy (`clone()`) của cùng bộ mảng gốc, đo bằng `System.nanoTime()` rồi tính **Trung bình mẫu (`mean`)** và **Độ lệch chuẩn mẫu (`stddev`)**. Kết quả được lưu tự động vào file [`results/results.csv`](./results/results.csv).

**Sinh viên:**
> Sao phải có bước Warm-up JVM với phải `clone()` mảng ra mà không tạo mảng random mới ở mỗi vòng lặp luôn cho nhanh? Với cả trong bảng kết quả vừa chạy ra, sao ở `CUTOFF = 64` và `100` phần trăm cải thiện lại bị âm (`-11.58%`) thế kia?

**AI:**
Hai câu hỏi rất hay và rất dễ được hỏi khi vấn đáp:
1. **Tại sao phải Warm-up JVM và `clone()` mảng gốc?**
   - Khi Java mới khởi động, code chạy ở chế độ thông dịch (Interpreter) nên những lần chạy đầu tiên sẽ bị chậm bất thường. Chạy Warm-up 5 vòng giúp **JIT Compiler** biên dịch hàm `sort` sang mã máy thực sự trước khi ta bấm giờ, nhờ đó `CUTOFF = 1` (chạy đầu tiên) không bị thiệt thòi.
   - Phải `clone()` từ cùng một bộ mảng gốc `testDatasets[t]` vì sau khi `MergeSort.sort(a, cutoff)` chạy xong thì mảng `a` đã được sắp xếp rồi. Nếu không `clone()`, lần chạy sau sẽ nhận vào mảng đã sắp xếp sẵn; còn nếu sinh mảng ngẫu nhiên mới mỗi lần thì các mức `CUTOFF` lại không thi đấu trên cùng một bộ dữ liệu.
2. **Tại sao `CUTOFF = 64` và `100` lại bị âm (`-11.58%` và `-10.95%`)?**
   - Phần trăm cải thiện tính so với mốc cơ sở `CUTOFF = 1` (`246.57 ms`).
   - Khi `CUTOFF` nằm trong khoảng **`5` đến `12`**, thời gian chạy giảm mạnh xuống quanh **`196.61 ms` – `204.84 ms`** (nhanh hơn **17% – 20.26%**), trong đó **`CUTOFF = 12`** là tốt nhất trên máy bạn (`196.61 ms`, `StdDev = 3.96 ms`).
   - Nhưng khi đẩy `CUTOFF` lên quá lớn (`64` hoặc `100`), mỗi mảng con có tới 64–100 phần tử phải sắp xếp bằng Insertion Sort với chi phí bậc hai $O(K^2)$. Lúc này số phép so sánh và đổi chỗ của Insertion Sort phình quá to, làm tổng thời gian vọt lên `275.13 ms` — tức là **chậm hơn cả Mergesort gốc 11.58%** (nên mang dấu âm)!

**Sinh viên:**
> Còn phần vẽ đồ thị thì bạn hãy vẽ giúp mình nhé, hãy nói luôn bạn vẽ bằng gì?

---

## Phiên 4: Vẽ đồ thị kết quả và tổng hợp báo cáo

**AI:**
Mình dùng **Python** kết hợp với 2 thư viện chuẩn trong phân tích dữ liệu là **`pandas`** và **`matplotlib`** (code nằm trong file [`src/plot.py`](./src/plot.py)):
- Dùng `pandas.read_csv("results/results.csv")` để đọc bảng số liệu vừa đo được.
- Dùng `matplotlib.pyplot.errorbar()` để vẽ đường biểu diễn thời gian chạy trung bình (`Mean_ms`) theo từng mức `CUTOFF`, đồng thời gắn luôn thanh sai số thẳng đứng (`yerr = StdDev_ms`) tại mỗi điểm đo.
- Dùng `plt.axvline()` kẻ 2 đường gióng nét đứt tại `CUTOFF = 7` (giáo trình cũ) và `CUTOFF = 10` (giáo trình mới), cùng 1 chấm tròn đỏ đánh dấu điểm tối ưu nhất trên máy của bạn là `CUTOFF = 12 (196.6 ms)`.
- Xuất đồ thị ra file ảnh [`results/cutoff_chart.png`](./results/cutoff_chart.png).

**Sinh viên:**
> Đồ thị nhìn rõ hình chữ U luôn rồi đấy. Bây giờ bạn giúp mình tổng hợp lại toàn bộ phần mô tả thí nghiệm, cấu hình máy của mình, bảng số liệu CSV với cái ảnh đồ thị này vào một file báo cáo ngắn gọn để mình đọc ôn tập và nộp bài nhé.

**AI:**
Mình đã tổng hợp đầy đủ vào file [`REPORT.md`](./REPORT.md) gồm 5 mục: Mục tiêu thí nghiệm, Cấu hình máy (Intel Core i7-9750H, Java 25.0.2), Phương pháp thí nghiệm theo chủ đề 1.3, Bảng thống kê kèm đồ thị [`results/cutoff_chart.png`](./results/cutoff_chart.png), và Phần phân tích kết luận. Bạn có thể đọc lướt toàn bộ báo cáo chỉ trong vài phút để nắm chắc các ý chính khi đi vấn đáp!
