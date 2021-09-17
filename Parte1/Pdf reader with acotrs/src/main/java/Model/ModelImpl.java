package Model;

import Utility.DocumentReadingObserver;
import akka.actor.typed.ActorSystem;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


public class ModelImpl implements Model{

    // https://www.youtube.com/watch?v=AIld6zEeBJ8&ab_channel=MindFusion
    // https://engineering.zalando.com/posts/2019/04/how-to-set-an-ideal-thread-pool-size.html
    private final static int NUMBEROFTHREAD = (int) ( Runtime.getRuntime().availableProcessors() * 0.75 * (1 + 0.5));

    private final DocumentReadingObserver documentReadingObserver;
    private final MainMonitor monitor;
    final List<String> fileNames;
    private String[] excludedWords;

    Long initTime;

    public ModelImpl(DocumentReadingObserver ob){
        this.monitor = new MainMonitor();
        this.documentReadingObserver = ob;
        fileNames = new ArrayList<>();
    }

    @Override
    public void setFileNames(String dirName) {
        File folder = new File(dirName); //"D:" + File.separator + "PCD" + File.separator
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        Arrays.stream(listOfFiles)
                .filter(File::isFile)
                .filter(v -> v.getName().endsWith(".pdf"))
                .forEach(v -> {
                    fileNames.add(v.getName());
                    monitor.updating(v.getName());
                    documentReadingObserver.notifyMessage("File " + v.getName() + " was added to the Monitor's List.");
                    System.out.println(v.getName()+" added to monitor list");
                });
    }

    @Override
    public void startActors(String dirPath, String exclusionFileName, int occurrences){

        this.initTime = System.currentTimeMillis();
        final ActorSystem<MainActor.Start> actorMain = ActorSystem.create(MainActor.create(documentReadingObserver,dirPath,this.monitor,excludedWords), "helloakka");
        actorMain.tell(new MainActor.Start("Start"));

        documentReadingObserver.notifyMessage( "STARTED WORKING");
        while(this.monitor.control){
            System.out.println("");
        }
        actorMain.terminate();

        List<String> mostUsedWord = monitor.wordCounter(occurrences);
        documentReadingObserver.notifyMessage("The number of processed word is: " + monitor.getTotalProcessedWords());
        mostUsedWord.forEach(this.documentReadingObserver::notifyMessage);

        this.calculateExecutionTime();
    }

    @Override
    public void pauseActors() {
        this.monitor.pause = true;
        documentReadingObserver.notifyMessage("ALL ACTORS ARE PAUSED");
    }

    @Override
    public void resumeActors() {
        this.monitor.pause = false;
        documentReadingObserver.notifyMessage("ALL ACTORS ARE RESUMED");
    }

    @Override
    public void setExcludedFiles(String exclusionFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(exclusionFilePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            String everything = sb.toString();
            everything = everything.toLowerCase();

            if(everything.isEmpty()){
                this.excludedWords = new String[]{"noFileFound"};
            }

            this.excludedWords = everything.split("\\r?\\n");
        } catch (FileNotFoundException e) {
            documentReadingObserver.notifyMessage("File not found! Words will not be excluded");
            this.excludedWords = new String[]{"noFileFound"};
        }
    }

    @Override
    public int getNumberOfThread() {
        return NUMBEROFTHREAD;
    }

    private void calculateExecutionTime(){
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - this.initTime;
        documentReadingObserver.notifyMessage("Total execution time is: " + totalTime + " Milliseconds");
    }

}
