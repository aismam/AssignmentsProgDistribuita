package Utility;

public interface DocumentReadingObserver {

    void notifyReadingCompleted(String fileName, String id);

    void notifyMessage(String word);

}
