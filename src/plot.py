import pandas as pd
import matplotlib.pyplot as plt

# 1. Doc du lieu tu file results/results.csv
df = pd.read_csv("results/results.csv")

# 2. Tao khung bieu do
plt.figure(figsize=(10, 5.5))

# Ve duong thoi gian trung binh (Mean) kem thanh do lech chuan (StdDev)
plt.errorbar(df["Cutoff"], df["Mean_ms"], yerr=df["StdDev_ms"],
             fmt='-o', color='#1f77b4', ecolor='#a6c8e0',
             elinewidth=1.5, capsize=4, label="Thời gian chạy TB ± StdDev (ms)")

# 3. Danh dau cac moc quan trong: 7 (sach cu), 10 (sach moi), va diem tot nhat
plt.axvline(x=7, color='orange', linestyle='--', label="CUTOFF = 7 (Giáo trình cũ)")
plt.axvline(x=10, color='green', linestyle='--', label="CUTOFF = 10 (Giáo trình mới)")

best_row = df.loc[df["Mean_ms"].idxmin()]
plt.scatter(best_row["Cutoff"], best_row["Mean_ms"], color='red', s=100, zorder=5,
            label=f"Tối ưu trên máy: CUTOFF = {int(best_row['Cutoff'])} ({best_row['Mean_ms']:.1f} ms)")

# 4. Trang tri bieu do
plt.title("Ảnh hưởng của CUTOFF (Insertion Sort) đến thời gian chạy Mergesort (N = 500,000)", fontsize=13, fontweight='bold')
plt.xlabel("Giá trị CUTOFF (Ngưỡng chuyển sang Insertion Sort)", fontsize=11)
plt.ylabel("Thời gian chạy trung bình (ms)", fontsize=11)
plt.xticks(df["Cutoff"])
plt.grid(True, linestyle=':', alpha=0.6)
plt.legend()
plt.tight_layout()

# 5. Luu ra file anh
plt.savefig("results/cutoff_chart.png", dpi=150)
print("Da ve xong bieu do: results/cutoff_chart.png")
