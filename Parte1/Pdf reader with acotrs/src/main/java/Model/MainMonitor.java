package Model;

import java.util.*;
import java.util.stream.IntStream;

public class MainMonitor {

    private final List<String> fileNameList = new ArrayList<>();
    private final Map<String, Integer> map = new HashMap<>();
    private int wordCounter =0;
    public boolean control = true;
    public boolean pause = false;

    public synchronized String getWork(){
        String element = fileNameList.get(0);
        fileNameList.remove(0);
        return element;
    }

    public synchronized void addMap(String key){
        map.put(key, map.containsKey(key) ? map.get(key)+1 : 1);
    }

    public synchronized int getTotalProcessedWords(){
        return this.wordCounter;
    }

    public synchronized void updating(String filename){
        fileNameList.add(filename);
    }

    public synchronized void incrementWordCounter(int numberOfWords){
        this.wordCounter = this.wordCounter + numberOfWords;
    }

    public synchronized List<String> wordCounter(int counts){
        List<String> words = new ArrayList<>();
        IntStream.range(0, counts).boxed().forEach(v->{
            String key = "";
            int value = 0;
            for(Map.Entry<String, Integer> entry : map.entrySet()){
                if(entry.getValue()> value){
                    key = entry.getKey();
                    value = entry.getValue();
                }
            }
            words.add("The word '" + key + "' is used " + value + " times.");
            map.remove(key);
        });
        return words;
    }

    public synchronized int getSize(){
        return this.fileNameList.size();
    }

}
