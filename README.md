--create database

Create database bookreader;

--use database 

Use bookreader;

--users table

CREATE TABLE users (
id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(100) UNIQUE,
password VARCHAR(255),
role ENUM('user', 'admin') DEFAULT 'user' -- NEW COLUMN
);

-- Books table

CREATE TABLE books (
id INT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(255),
file_url VARCHAR(255),
cover_image_url VARCHAR(255)
);
