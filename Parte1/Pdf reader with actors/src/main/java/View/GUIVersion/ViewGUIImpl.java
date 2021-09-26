package View.GUIVersion;

import Model.Model;
import Utility.DocumentReadingObserver;
import View.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ViewGUIImpl implements View, DocumentReadingObserver {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    Model model;

    private final JFrame frame;
    private final JButton start;
    final JButton suspend;
    private final JButton exit;
    private final JButton resumption;

    JLabel pdfPath;
    JLabel exclusionFile;
    JLabel occurrence;

    JScrollPane scrollPane;

    JTextField txtPath;
    JTextField txtExclusionFile;
    JTextField txtOccurrence;
    private final JTextArea txtArea;
    final JPanel panel;

    public ViewGUIImpl(Model model){

        this.model = model;

        this.panel = new JPanel();
        frame = new JFrame("First Assignment");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,700);

        start = new JButton("Start");
        exit = new JButton("Exit");
        suspend = new JButton("Suspend");
        resumption = new JButton("Resume");

        txtArea = new JTextArea(30,50);
        txtExclusionFile = new JTextField(15);
        txtOccurrence = new JTextField(15);
        txtPath = new JTextField(15);
        txtArea.setEditable(false);

        scrollPane = new JScrollPane(txtArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        pdfPath = new JLabel("Insert directory that contain files");
        exclusionFile = new JLabel("Insert path of the exclusion file");
        occurrence = new JLabel("Insert number of occurrences");

        panel.setBorder(new EmptyBorder(new Insets(50, 50, 100, 50)));

        panel.add(exclusionFile);
        panel.add(txtExclusionFile);

        panel.add(occurrence);
        panel.add(txtOccurrence);

        panel.add(pdfPath);
        panel.add(txtPath);

        panel.add(scrollPane);

        panel.add(suspend);
        panel.add(exit);
        panel.add(start);
        panel.add(resumption);

        frame.getContentPane().add(panel);
    }

    @Override
    public void initialize(){
        frame.setVisible(true);
    }

    @Override
    public void launch(Model model) {
        this.start(model,model.getNumberOfThread());
    }

    @Override
    public void start(Model model, int numberOfThreads) {
        start.addActionListener(v->{
            String dirPath = this.txtPath.getText();
            //String dirPath = "D:" + File.separator + "PCD" + File.separator; // ismo
            //String dirPath = "D:" + File.separator + "PCD" + File.separator + "PDF" + File.separator; // tipo del gruppo
            //String dirPath = "D:" + File.separator + "PCD" + File.separator + "testFiles" + File.separator; //ismam robe
            //String dirPath = "D:" + File.separator + "PCD" + File.separator + "testFiles100mb" + File.separator; //ismam robe
            String exclusionFileName = this.txtExclusionFile.getText();
            //String exclusionFileName = "rule.txt";
            int occurrencesValue = this.txtOccurrence.getText().isEmpty() || !this.txtOccurrence.getText().chars().allMatch(Character::isDigit) ?  -1 : Integer.parseInt(this.txtOccurrence.getText());
            if(exclusionFileName.isEmpty() || !exclusionFileName.endsWith(".txt")){
                this.consoleWrite("Insert a right exclusion file path!");
            } else {
                if(occurrencesValue < 0){
                    this.consoleWrite("Insert a right number of occurrences!");
                } else {
                    try {
                        model.setFileNames(dirPath); // "D:" + File.separator + "PCD" + File.separator
                        model.setExcludedFiles(dirPath + exclusionFileName);
                        //Thread th = new Thread(()-> model.startThreads(dirPath, exclusionFileName, occurrencesValue));
                        Thread th = new Thread(()-> model.startActors(dirPath, exclusionFileName, occurrencesValue));
                        th.start();
                    } catch (NullPointerException | IOException e) {
                        this.consoleWrite("Insert a valid Directory!");
                    }
                }
            }
        });

        suspend.addActionListener(v -> this.suspend());

        resumption.addActionListener(v -> this.resume());

        exit.addActionListener(v-> this.exit());

    }

    @Override
    public void suspend() {
        model.pauseActors();
    }

    @Override
    public void resume() {
        model.resumeActors();
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public void consoleWrite(String text) {
        this.txtArea.append(text + "\n");
    }


    @Override
    public synchronized void notifyReadingCompleted(String fileName, String id) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SwingUtilities.invokeLater(()-> this.txtArea.append("[" + sdf.format(timestamp) + "]" + " Document: " + fileName + " is processed by thread " + id + "\n"));
        System.out.println("[" + sdf.format(timestamp) + "]" + " Document: " + fileName + " is processed by thread " + id + "\n");

    }

    @Override
    public synchronized void notifyMessage(String word) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SwingUtilities.invokeLater(() -> this.txtArea.append("[" + timestamp + "] " +word + "\n"));
        System.out.println( "[" + timestamp + "] " +word + "\n");

    }
}
