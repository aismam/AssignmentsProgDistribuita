package Model;

import Utility.DocumentReadingObserver;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActor extends AbstractBehavior<MainActor.Start> {

    private final static int NUMBEROFTHREAD = (int) ( Runtime.getRuntime().availableProcessors() * 0.75 * (1 + 0.5));

    private final DocumentReadingObserver documentReadingObserver;
    private final MainMonitor monitor;
    final List<String> fileNames;
    private String[] excludedWords;
    private String dirPath;

    Random rand = new Random(); //instance of random class
    private int upperbound = 100;


    public MainActor(ActorContext<Start> context,DocumentReadingObserver ob,String dirPath, MainMonitor monitor, String[] excludedWords){
        super(context);
        this.monitor = monitor;
        this.documentReadingObserver = ob;
        fileNames = new ArrayList<>();
        this.excludedWords = excludedWords;
        this.dirPath = dirPath;
        worker = context.spawn(WorkerActor.create(this.monitor,Integer.toString(rand.nextInt(upperbound)),this.dirPath,this.documentReadingObserver,this.excludedWords), "worker");
    }

    public static class Start {
        public final String name;

        public Start(String name) {
            this.name = name;
        }
    }

    private final ActorRef<WorkerActor.Work> worker;

    public static Behavior<Start> create(DocumentReadingObserver ob,String dirPath,MainMonitor monitor,String[] excludedWords) {
        return Behaviors.setup(context -> new MainActor(context,ob,dirPath,monitor,excludedWords));
    }


    @Override
    public Receive<Start> createReceive() {
        return newReceiveBuilder().onMessage(Start.class, this::onStart).build();
    }

    private Behavior<Start> onStart(Start command) {
        //#create-actors
        ActorRef<WorkerActor.Result> replyTo =
                getContext().spawn(WorkerBot.create(NUMBEROFTHREAD), command.name);
        worker.tell(new WorkerActor.Work(command.name, replyTo));
        //#create-actors
        return this;
    }
}
