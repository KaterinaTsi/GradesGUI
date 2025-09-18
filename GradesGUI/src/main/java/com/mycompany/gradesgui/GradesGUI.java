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
 * Η βασική κλάση εκκίνησης του προγράμματος που δημιουργει και εμφανιζει το GUI.
 */
public class GradesGUI {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() ->  new Grades().setVisible(true));
        
    }
}

/**
 * Κλάση που δημιουργεί το γραφικό περιβάλλον και περιλαμβάνει μεθόδους για την επεξεργασία των βαθμολογιών
 * και τον χειρισμό αρχείων.
 */

class Grades extends JFrame {
    
    //Δημιουργία components
    private JTextField textEntry, textExit;
    private JLabel labelEntry, labelExit, labelResult, keno;
    private JButton btnCalc;
    private JTextArea areaResults;
    private JScrollPane scrollPane;
    
    private Map<String, List<Double>> gradesPerStudent = new HashMap<>(); 
    private String exitFile;
    
    private int validCount = 0;
    
    
     /**
     * Κατασκευαστής της κλάσης Grades.
     * Αρχικοποιει και διατάσσει τα γραφικά στοιχεία και προσθετει τον listener.
     */
    public Grades() {
        
        textEntry = new JTextField();
        textExit = new JTextField();
        
        labelEntry = new JLabel("Όνομα αρχείου εισόδου: ");
        labelExit  = new JLabel("Όνομα αρχείου εξόδου: ");
        keno = new JLabel();  // Κενη ετικέτα για καλύτερη διάταξη στο παραθυρο
        
        /** 
         * Κουμπι υπολογισμοθ με action listener στη μέθοδο process που περιέχει όλες
         * τις μεθόδους του προγράμματος με την σωστή σειρά εκτέλεσης.
        */
        btnCalc = new JButton("Υπολογισμός");
        btnCalc.addActionListener(e -> Process());
        
        areaResults = new JTextArea(5, 40);
        areaResults.setEditable(false);
        scrollPane = new JScrollPane(areaResults);
       
        //Ρυθμισεις παραθύρου
        setSize(500,400);
        setTitle("Βαθμολογίες φοιτητών");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setLayout(new GridLayout(4,2));
        
        // Προσθηκη στοιχειων στο παράθυροο
        add(labelEntry);
        add(textEntry);
        
        add(labelExit);
        add(textExit);
        
        add(keno);
        add(btnCalc);
        
        add(scrollPane);
        
        
    }
    
    
    /**
     * Εκτελεί την πλήρη διαδικασία: ανάγνωση αρχειου, δημιουργία αρχειου εξόδου
     * εγγραφή αρχειου εξόδου και εμφάνιση αποτελεσμάτων σε παραθυρο GUI.
     */
    private void Process() {
        
        ReadFile();
        CreateFile();
        WriteExitFile();
        WriteGUI();
    }
    
    
    /**
     * Διαβάζει το αρχείο εισόδου και αποθηκεύει τις έγκυρες βαθμολογίες .
     * Καταγράφει τα σφάλματα σε αρχείο log, οι λανθασμενες εγγραφες δεν επεξεργάζονται.
     * Κάνει επισης έλεγχο για σωστά ΑΜ και  μαθηματα.
     * Κανει καταγραφή των εγκυρων βαθμολογιών μεσω του validCount.
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
                 
                 
                 //Ελεγχος για σωστά πεδία με :
                 if(parts.length == 3) {
                     
                     String AM = parts[0];
                     String lesson = parts[1];
                     
                     // Έλεγχος ΑΜ: 6 χαρακτήρες, μόνο γράμματα/ψηφία
                     if (!AM.matches("[A-Za-z0-9]{6}")) {
                          System.err.println("Μη έγκυρο ΑΜ: " + AM + " στη γραμμή: " + line);
                           continue;
                       }
                     // Έλεγχος μαθήματος: μία λέξη, χωρίς κενά
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
     * Δημιουργεί το αρχείο εξόδου .
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
     * Κανει εγγραφή στο αρχείο εξόδου τον μεσο όρο βαθμολογίας κάθε φοιτητή
     * με την ζητούμενη μορφή της εκφώνησης.
     * Υπολογίζει τονμεσο όρο.
     * 
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
     * Βρίσκει την μέγιστη και μικροτερη βαθμολογία.
     * Επιστρέφει κείμενο με την ελάχιστη και μέγιστη βαθμολογία.
     * 
     * @return String με μηνυμα που περιεχει min και max τιμές.
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
     * Εμφανίζει τα συνολικά αποτελέσματα στο παράθυρο της εφαρμογής.
     */
    private void WriteGUI() {
        
        String minMax = FindMinMax();
        System.out.print(validCount);
        areaResults.setText("\nΣύνολο έγκυρων βαθμολογιών: " + validCount +minMax);
        
        
    }
    
    
     /**
     * Καταγράφει μηνύματα σφάλματος στο αρχείο err.log.
     * @param msg Το μήνυμα που θα καταγραφεί.
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
