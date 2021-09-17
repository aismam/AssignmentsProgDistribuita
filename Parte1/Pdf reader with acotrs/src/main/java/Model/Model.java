package Model;

import java.io.IOException;

public interface Model {

    /**
     * Set the file names and call the method "update" for each file
     * @param dirName directory where is placed all files
     */
    void setFileNames(String dirName);

    /**
     * Start actors
     * @param dirPath location of directory where all files is contained
     * @param exclusionFileName name of the file that contain word to exclude
     * @param occurrences number of occurrences of result
     */
    void startActors(String dirPath, String exclusionFileName, int occurrences);

    /**
     * Pause all actors
     */
    void pauseActors();

    /**
     * Resume all actors
     */
    void resumeActors();

    /**
     * Set the excluded words
     * @param exclusionFilePath directory where exclusion file is placed
     */
    void setExcludedFiles(String exclusionFilePath) throws IOException;

    /**
     * get the number of thread
     * @return the number of thread
     */
    int getNumberOfThread();

}
