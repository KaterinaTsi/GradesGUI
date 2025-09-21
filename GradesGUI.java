/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.gradesgui;

/**
 *
 * @author katerina
 */

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;

import java.io.IOException;
import java.util.Scanner;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import java.io.FileWriter;
import java.io.BufferedWriter;


/**
 * Main class to start the GradesGUI application and display the GUI.
 */
public class GradesGUI {

    public static void main(String[] args) {
         // Launch the GUI in the Swing event dispatch thread
        SwingUtilities.invokeLater(() ->  new Grades().setVisible(true));
        
    }
}

/**
 * Class that creates the GUI and contains methods for processing student grades
 * and handling file input/output.
 */

class Grades extends JFrame {
    
     // GUI components
    private JTextField textEntry, textExit;
    private JLabel labelEntry, labelExit, labelResult, keno;
    private JButton btnCalc;
    private JTextArea areaResults;
    private JScrollPane scrollPane;
    
    private Map<String, List<Double>> gradesPerStudent = new HashMap<>(); 
    private String exitFile;
    
    private int validCount = 0;
    
    
     /**
     * Constructor of Grades class.
     * Initializes GUI components, sets layout, and adds action listeners.
     */
    public Grades() {
        
        textEntry = new JTextField();
        textExit = new JTextField();
        
        labelEntry = new JLabel("Όνομα αρχείου εισόδου: ");
        labelExit  = new JLabel("Όνομα αρχείου εξόδου: ");
        keno = new JLabel();  // Empty label for spacing in layout
        
        /** 
         * Button to calculate grades. ActionListener triggers process method
         * that executes all steps in correct order.
        */
        btnCalc = new JButton("Υπολογισμός");
        btnCalc.addActionListener(e -> Process());
        
        areaResults = new JTextArea(5, 40);
        areaResults.setEditable(false);
        scrollPane = new JScrollPane(areaResults);
       
        // Window settings
        setSize(500,400);
        setTitle("Βαθμολογίες φοιτητών");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setLayout(new GridLayout(4,2));
        
        // Add components to window
        add(labelEntry);
        add(textEntry);
        
        add(labelExit);
        add(textExit);
        
        add(keno);
        add(btnCalc);
        
        add(scrollPane);
        
        
    }
    
    
    /**
     * Executes the full workflow: read input file, create output file,
     * write output file, and display results in GUI.
     */
    private void Process() {
        
        ReadFile();
        CreateFile();
        WriteExitFile();
        WriteGUI();
    }
    
    
    /**
     * Reads the input file and stores valid grades.
     * Logs invalid records to err.log. Performs validation of student ID and lesson name.
     * Tracks the number of valid entries using validCount.
     */
    
    private void ReadFile() {
        
        String filename = textEntry.getText().trim();
        gradesPerStudent.clear(); 
        validCount = 0;

        try {
             File file = new File(filename);
             Scanner scanner = new Scanner(file);

             while(scanner.hasNextLine()) {
                 String line = scanner.nextLine();
                 
                 String[] parts = line.split(":");
                 
                 
                 // Validate correct format with 3 fields
                 if(parts.length == 3) {
                     
                     String AM = parts[0];
                     String lesson = parts[1];
                     
                    // Validate student ID: 6 alphanumeric characters
                     if (!AM.matches("[A-Za-z0-9]{6}")) {
                          System.err.println("Μη έγκυρο ΑΜ: " + AM + " στη γραμμή: " + line);
                           continue;
                       }
                   // Validate lesson: single word, no spaces
                     if (!lesson.matches("[A-Za-z0-9_]+")) {
                         System.err.println("Μη έγκυρο μάθημα: " + lesson + " στη γραμμή: " + line);
                         continue;
                        }
                     
                     try{
                         
                         double grade = Double.parseDouble(parts[2]);
                         
                         gradesPerStudent.putIfAbsent(AM, new ArrayList<>());
                         gradesPerStudent.get(AM).add(grade);
                         validCount++;
                         
                        } catch (NumberFormatException e) {
                            
                            logError("\nInvalid record: " +line);
                         
                        }
                        
                 }
                 
             
             }
             scanner.close();

        }catch (FileNotFoundException e) {

         JOptionPane.showMessageDialog(this, "Το αρχείο δεν βρέθηκε: " + filename, "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    }


     /**
     * Creates the output file if it does not exist.
     */
    private void CreateFile() {
        
        String filename = textExit.getText().trim();
        exitFile = filename;

        try {
             File file = new File(filename);

             if(file.createNewFile()) {
                 System.out.println("File created:" +file.getName());
              } else {
                 System.out.println("File already exists");
              }
    
         }catch (IOException e) {

         System.out.println("An error ");
         e.printStackTrace();
        }
    }
    
   /**
     * Writes the average grade of each student to the output file.
     * Calculates averages and formats the output.
     */
    private void WriteExitFile() {
        
        exitFile = textExit.getText().trim();
        
        
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(exitFile))) {
        
            for(Map.Entry<String, List<Double>> entry : gradesPerStudent.entrySet()){
            
                 String AM = entry.getKey();
                 List<Double> grades = entry.getValue();
            
                 double sum = 0;
                 for(double i : grades) {
                     
                      sum += i;
                  }
            
            double avg = sum / grades.size();
            
            writer.write(AM + ":" + String.format("%.2f", avg) + "\n");
            
            }
        } catch (IOException e) {
            
            System.err.println("Σφαλμα κατα την εγγραφη στο αρχειο: " + e.getMessage());
        }
   
    }
    
    
     
    /**
     * Finds minimum and maximum grade across all students.
     * @return String containing min and max values.
     */
    private String FindMinMax() {
        
        double min = 10;
        double max = 0;
        
        for(List<Double> grades : gradesPerStudent.values()) {
            for(double g : grades){
                if(g<min) min =g;
                if(g>max) max=g;
                 
            }
        }
        
        return String.format("\n Μικρότερη βαθμολογία: %.2f\n Μεγαλύτερη βαθμολογία: %.2f", min, max);
        
    }
    
    /**
     * Displays overall results in the GUI text area.
     */
    private void WriteGUI() {
        
        String minMax = FindMinMax();
        System.out.print(validCount);
        areaResults.setText("\nΣύνολο έγκυρων βαθμολογιών: " + validCount +minMax);
        
        
    }
    
    
     /**
     * Logs error messages to "err.log".
     * @param msg Message to log
     */
    private void logError(String msg) {
        
        try (BufferedWriter errorWriter = new BufferedWriter(new FileWriter("err.log", true))) {
            
             errorWriter.write(msg);
             errorWriter.newLine();
             
         } catch (IOException e) {
             
        System.err.println("Σφάλμα κατά την εγγραφή στο αρχείο err.log: " + e.getMessage());
        }
    }
   
}
