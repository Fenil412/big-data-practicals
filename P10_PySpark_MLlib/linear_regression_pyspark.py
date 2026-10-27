# Practical 10: PySpark MLlib linear regression
# Input CSV format: area,price
# Example:
# area,price
# 1000,200000
# 1500,280000
# 2000,360000
#
# Run:
#   spark-submit linear_regression_pyspark.py input.csv
# Requires PySpark installed and a working Spark environment.

import sys
from pyspark.sql import SparkSession
from pyspark.sql.functions import col
from pyspark.ml.feature import VectorAssembler
from pyspark.ml.regression import LinearRegression
from pyspark.ml.evaluation import RegressionEvaluator

def main():
    if len(sys.argv) != 2:
        print("Usage: spark-submit linear_regression_pyspark.py <input.csv>")
        raise SystemExit(1)

    spark = SparkSession.builder.appName("Practical10LinearRegression").getOrCreate()
    df = (spark.read.option("header", True).option("inferSchema", True).csv(sys.argv[1])
          .select(col("area").cast("double"), col("price").cast("double"))
          .na.drop())

    assembler = VectorAssembler(inputCols=["area"], outputCol="features")
    data = assembler.transform(df).select("features", col("price").alias("label"))
    train, test = data.randomSplit([0.8, 0.2], seed=42)

    lr = LinearRegression(featuresCol="features", labelCol="label", maxIter=20)
    model = lr.fit(train)
    predictions = model.transform(test)

    print("Coefficients:", model.coefficients)
    print("Intercept:", model.intercept)
    predictions.select("features", "label", "prediction").show(truncate=False)

    evaluator = RegressionEvaluator(labelCol="label", predictionCol="prediction", metricName="rmse")
    print("RMSE:", evaluator.evaluate(predictions))
    spark.stop()

if __name__ == "__main__":
    main()
