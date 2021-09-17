package Controller;

import Model.*;
import Utility.DocumentReadingObserver;
import View.GUIVersion.ViewGUIImpl;
import View.NoGUIVersion.ViewNoGUIVersionImpl;
import View.View;

public class ControllerImpl implements Controller, DocumentReadingObserver {

    final private Model model;
    final private View view;
    final private DocumentReadingObserver observer;

    public ControllerImpl(){
        this.model = new ModelImpl(this);
        final ViewGUIImpl view = new ViewGUIImpl(this.model);
        //final ViewNoGUIVersionImpl view = new ViewNoGUIVersionImpl(this.model);
        this.view = view;
        observer = view;
    }

    @Override
    public void initialize() {
        this.view.initialize();
        this.launch();
    }

    @Override
    public synchronized void notifyReadingCompleted(String fileName, String id) {
        observer.notifyReadingCompleted(fileName, id);
    }

    @Override
    public void notifyMessage(String word) {
        observer.notifyMessage(word);
    }

    private void launch(){
        view.launch(this.model);
    }

}
