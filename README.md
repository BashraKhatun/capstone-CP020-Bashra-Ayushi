
# Problem Title: SalesScope: Retail Sales Analytics Dashboard

**Problem ID:** CP020

## Team Members

1. **Name:** [BASHRA KHATUN] | **USN:** [1AT24CS041]
2. **Name:** [AYUSHI SINGH] | **USN:** [1AT24CS036]

## How to Run the Program

**Prerequisites:**
-Java JDK installed.
-Python 3 installed.
-Streamlit and Pandas installed.

**Steps:**
1. Clone or download this repository.
2. Open the project folder in your terminal.
3. Compile the program:

   For Linux/macOS:
   mkdir -p out
   javac -d out src/*.java
   
   For Windows PowerShell:
   New-Item -ItemType Directory -Force out
   javac -d out src/*.java

4. Execute the program in the terminal:

   For Linux/macOS:
   java -cp out Main
   
   For Windows PowerShell:
   java -cp out Main

5. Run the interactive dashboard:

   For Linux/macOS:
   python3 -m streamlit run dashboard/app.py

   For Windows PowerShell:
   .\dashboard\.venv\Scripts\python.exe -m streamlit run dashboard/app.py


## Features Implemented

-Add, view, and search retail sales records.
-Perform daily and weekly sales analysis.
-Identify the top 5 products by revenue.
-Identify the top 5 products by quantity sold.
-Analyze category-wise sales and revenue percentages.
-Compare sales performance across two weeks.
-Display sales insights through an interactive Streamlit dashboard.

## Sample Run

**Input and Output:**

=====================================
          WELCOME TO SALESSCOPE
       RETAIL SALES ANALYTICS SYSTEM
=====================================
1. Add Sales Record
2. View All Sales Records
3. Search Product
4. Daily Sales Analysis
5. Weekly Sales Analysis
6. Top 5 Products by Revenue
7. Top 5 Products by Quantity
8. Category-wise Sales Analysis
9. Two-Week Sales Comparison
10. Exit
-------------------------------------
Enter your choice: 1

Enter Product Name: Rice
Enter Category: Groceries
Enter Quantity: 2
Enter Unit Price: 50
Enter Sale Date: 2026-11-13

Sales record added successfully!


Enter your choice: 1

Enter Product Name: Milk
Enter Category: Dairy
Enter Quantity: 3
Enter Unit Price: 40
Enter Sale Date: 2026-11-13

Sales record added successfully!


Enter your choice: 1

Enter Product Name: Rice
Enter Category: Groceries
Enter Quantity: 1
Enter Unit Price: 50
Enter Sale Date: 2026-11-13

Sales record added successfully!


Enter your choice: 2

===== ALL SALES RECORDS =====

Record 1
Product: Rice
Category: Groceries
Quantity: 2
Unit Price: Rs. 50.0
Sale Date: 2026-11-13
Revenue: Rs. 100.0

Record 2
Product: Milk
Category: Dairy
Quantity: 3
Unit Price: Rs. 40.0
Sale Date: 2026-11-13
Revenue: Rs. 120.0

Record 3
Product: Rice
Category: Groceries
Quantity: 1
Unit Price: Rs. 50.0
Sale Date: 2026-11-13
Revenue: Rs. 50.0


Enter your choice: 3

Enter product name to search: Rice

Product Found!

Product: Rice
Category: Groceries
Quantity: 2
Unit Price: Rs. 50.0
Sale Date: 2026-11-13
Revenue: Rs. 100.0

Product: Rice
Category: Groceries
Quantity: 1
Unit Price: Rs. 50.0
Sale Date: 2026-11-13
Revenue: Rs. 50.0


Enter your choice: 4

Enter date for daily sales analysis: 2026-11-13

Daily Sales Revenue: Rs. 270.0


Enter your choice: 5

Enter any date to calculate weekly sales: 2026-11-13

Weekly Sales Revenue: Rs. 270.0


Enter your choice: 6

===== TOP 5 PRODUCTS BY REVENUE =====

1. Rice - Rs. 150.0
2. Milk - Rs. 120.0


Enter your choice: 7

===== TOP 5 PRODUCTS BY QUANTITY =====

1. Rice - 3 units
2. Milk - 3 units


Enter your choice: 8

===== CATEGORY-WISE SALES ANALYSIS =====

Groceries - Rs. 150.0 (55.56%)
Dairy - Rs. 120.0 (44.44%)

Total Revenue: Rs. 270.0


Enter your choice: 9

Enter any date for Week 1: 2026-11-13
Enter any date for Week 2: 2026-11-20

Week 1 Revenue: Rs. 270.0
Week 2 Revenue: Rs. 0.0

Revenue Difference: Rs. -270.0
Percentage Change: -100%
Sales Performance: Decreased


Enter your choice: 10

Thank you for using SalesScope!
