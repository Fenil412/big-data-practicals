PRACTICAL 10 — PYSPARK + MLLIB
Aim from course outline: implement a machine-learning analytic algorithm for larger datasets using PySpark/MLlib. Options include regression or K-means clustering.

Files:
- linear_regression_pyspark.py: reads CSV columns area,price; builds features; trains LinearRegression; reports coefficients/intercept/predictions/RMSE.
- Sample input is below.

Create input.csv:
area,price
1000,200000
1500,280000
2000,360000
2500,440000
3000,520000
3500,600000

Run:
spark-submit linear_regression_pyspark.py input.csv

Output values depend on train/test split and data. For tiny sample datasets, metrics are only illustrative, not meaningful. If spark-submit is not on PATH, use the full path to spark-submit.cmd on Windows.
