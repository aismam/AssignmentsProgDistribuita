package View;

import Model.Model;

import javax.swing.*;
import java.io.IOException;

public interface View {

    void initialize();

    void launch(Model model);

    void start(Model model, int numberOfThreads) throws IOException;

    void suspend();

    void resume();

    void exit();

    void consoleWrite(String text);

}
