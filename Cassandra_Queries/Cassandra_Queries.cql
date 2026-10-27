-- Cassandra CRUD and restaurant dataset examples
-- Run in cqlsh. Replace values as needed for your lab environment.

CREATE KEYSPACE IF NOT EXISTS college
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE college;

DROP TABLE IF EXISTS students;
CREATE TABLE students (
    student_id int PRIMARY KEY,
    name text,
    department text,
    age int
);

-- CREATE
INSERT INTO students (student_id, name, department, age)
VALUES (1, 'Fenil', 'CSE', 20);
INSERT INTO students (student_id, name, department, age)
VALUES (2, 'Aarav', 'IT', 21);

-- READ
SELECT * FROM students;
SELECT name, department FROM students WHERE student_id = 1;

-- UPDATE
UPDATE students SET age = 21 WHERE student_id = 1;
SELECT * FROM students WHERE student_id = 1;

-- DELETE one row
DELETE FROM students WHERE student_id = 2;
SELECT * FROM students;

-- DELETE a column value (not the whole row)
UPDATE students SET department = null WHERE student_id = 1;

-- RESTAURANT DATASET: a query-friendly table example
CREATE TABLE IF NOT EXISTS restaurants_by_city (
    city text,
    restaurant_id text,
    restaurant_name text,
    cuisine text,
    rating decimal,
    PRIMARY KEY ((city), restaurant_id)
);

INSERT INTO restaurants_by_city (city, restaurant_id, restaurant_name, cuisine, rating)
VALUES ('Ahmedabad', 'R001', 'Sample Dhaba', 'Gujarati', 4.2);
INSERT INTO restaurants_by_city (city, restaurant_id, restaurant_name, cuisine, rating)
VALUES ('Ahmedabad', 'R002', 'Sample Cafe', 'Cafe', 4.0);
INSERT INTO restaurants_by_city (city, restaurant_id, restaurant_name, cuisine, rating)
VALUES ('Surat', 'R003', 'Sample Thali House', 'Gujarati', 4.4);

-- Query must include partition key city
SELECT * FROM restaurants_by_city WHERE city = 'Ahmedabad';

-- Update and delete
UPDATE restaurants_by_city SET rating = 4.5
WHERE city = 'Ahmedabad' AND restaurant_id = 'R001';
DELETE FROM restaurants_by_city
WHERE city = 'Ahmedabad' AND restaurant_id = 'R002';

-- DROP TABLE restaurants_by_city; -- destructive; run only when needed
