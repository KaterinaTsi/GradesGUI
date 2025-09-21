# GradesGUI

**GradesGUI** is a Java Swing application that processes student exam data from a CSV file.  
It calculates per-student average grades, displays statistics (min, max, valid count), and exports sorted results. Invalid entries are logged automatically.  

---

## 🚀 Features

- ✅ GUI-based file input/output for easy use  
- ✅ Reads student grades from CSV (`StudentID:Lesson:Grade`)  
- ✅ Validates student IDs and lesson names  
- ✅ Calculates average grades per student  
- ✅ Displays summary statistics (total valid grades, min, max)  
- ✅ Logs invalid records in `err.log`  
- ✅ Exports results to an output CSV file  

---

📝 How to Use

1. Launch the application:  
   ```bash
   java GradesGUI
2. Enter the input CSV file name.

3. Enter the output CSV file name.

4. Click Calculate.

5. View results in the GUI and find invalid records in err.log.

---

📂 Input File Format

     StudentID:Lesson:Grade
     
- StudentID –> 6 alphanumeric characters

- Lesson –> single word (letters, numbers, underscores)

- Grade –> numeric value (decimal allowed)

Example:

    A12345:Math:8.5 
    B67890:Physics:9.0

---

📊 Output

Output file contains the average grade per student:
       
       A12345:8.50
       B67890:9.00
- GUI shows:

  >Total valid grades

  >Minimum grade
  
  >Maximum grade

---

⚙️ Requirements

- Java 8 or higher
- Swing library (included in standard Java)
