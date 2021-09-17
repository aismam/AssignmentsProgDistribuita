package View.NoGUIVersion;

import Model.Model;
import Utility.DocumentReadingObserver;
import View.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ViewNoGUIVersionImpl implements View, DocumentReadingObserver {

    Model model;
    String directory, exPath, occurrences;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");


    public ViewNoGUIVersionImpl(Model model){
        this.model = model;
    }

    @Override
    public void initialize() {
        System.out.println("No GUI Version");
    }

    @Override
    public void launch(Model model) {
        this.start(model, model.getNumberOfThread());
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
        System.out.println(text);
    }

    @Override
    public void start(Model model, int numberOfThreads) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Insert the exclusion file path: ");
            exPath = br.readLine();
            if(exPath.isEmpty() || !exPath.endsWith(".txt")){
                System.out.println("Insert a right exclusion file path!");
            } else {
                System.out.println("Insert number of occurrences: ");
                occurrences = br.readLine();
                if(Integer.parseInt(occurrences) <= 0){
                    System.out.println("Wrong number of occurrences!");
                } else {
                    try{
                        System.out.println("Insert directory: ");
                        directory = br.readLine();
                        model.setFileNames(directory); // "D:" + File.separator + "PCD" + File.separator
                        model.setExcludedFiles(directory + exPath);
                        Thread th = new Thread(()-> model.startActors(directory, exPath, Integer.parseInt(occurrences)));
                        th.start();
                    } catch (NullPointerException | IOException e) {
                        this.consoleWrite("Insert a valid Directory!");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyReadingCompleted(String fileName, String id) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("[" + sdf.format(timestamp) + "]" + " Document: " + fileName + " is processed by thread " + id + "\n");
    }

    @Override
    public void notifyMessage(String word) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println( "[" + timestamp + "] " +word + "\n");
    }
}
