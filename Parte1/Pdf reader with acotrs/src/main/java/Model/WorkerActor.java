package Model;

import Utility.DocumentReadingObserver;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WorkerActor extends AbstractBehavior<WorkerActor.Work> {

  private final MainMonitor monitor;
  private final String id;
  private final String path;
  private final DocumentReadingObserver observer;
  private final String[] excludedWords;

  public WorkerActor(ActorContext<Work> context,MainMonitor monitor, String id, String path, DocumentReadingObserver observer, String[] excludedWords) {
    super(context);
    this.monitor = monitor;
    this.id = id;
    this.path = path;
    this.observer = observer;
    this.excludedWords = excludedWords;
  }

  public static final class Work {
    public final String whom;
    public final ActorRef<Result> replyTo;

    public Work(String whom, ActorRef<Result> replyTo) {
      this.whom = whom;
      this.replyTo = replyTo;
    }

  }

  public static final class Result {
    public final String whom;
    public final ActorRef<Work> from;

    public Result(String whom, ActorRef<Work> from) {
      this.whom = whom;
      this.from = from;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Result result = (Result) o;
      return Objects.equals(whom, result.whom) &&
              Objects.equals(from, result.from);
    }

    @Override
    public int hashCode() {
      return Objects.hash(whom, from);
    }

    @Override
    public String toString() {
      return "Message sent{" +
              "whom='" + whom + '\'' +
              ", from=" + from +
              '}';
    }
  }

  public static Behavior<Work> create(MainMonitor monitor, String id, String path, DocumentReadingObserver observer, String[] excludedWords) {
    return Behaviors.setup(context -> new WorkerActor(context,monitor,id,path,observer,excludedWords));
  }

  @Override
  public Receive<Work> createReceive() {
    return newReceiveBuilder().onMessage(Work.class, this::onWork).build();
  }

  private Behavior<Work> onWork(Work command) throws IOException {
    while(monitor.getSize()>0 && !monitor.pause) {
      getContext().getLog().info("Hello {}!", command.whom);
      this.compute(monitor.getWork());
      command.replyTo.tell(new Result(command.whom, getContext().getSelf()));
      if(monitor.getSize() == 0){
        monitor.control = false;
      }
    }
      return this;
  }

  private void compute(String fileName) throws IOException {

    System.out.println("computing started");
    PDDocument document = PDDocument.load(new File(path + fileName));
    AccessPermission ap = document.getCurrentAccessPermission();
    this.checkAccessPermission(ap);
    this.count(document, fileName);
    document.close();
  }

  private void count(PDDocument document, String fileName) throws IOException {

    PDFTextStripper stripper = new PDFTextStripper();
    stripper.setSortByPosition(true);
    for (int p = 1; p <= document.getNumberOfPages(); ++p) {
      stripper.setStartPage(p);
      stripper.setEndPage(p);
      String text = stripper.getText(document);

      text = text.toLowerCase();
      String[] words = text.trim().split("\\W+");
      this.addToMap(this.compareWords(this.excludedWords, words));
    }
    observer.notifyReadingCompleted(fileName, this.id);
    observer.notifyMessage("In the map there are " + monitor.getTotalProcessedWords() + " words");
  }

  private String[] compareWords(String[] filter, String[] words){

    List<String> listWords = Arrays.stream(words).collect(Collectors.toList());
    List<String>  listFilters = Arrays.stream(filter).collect(Collectors.toList());
    listFilters.forEach(v -> listWords.removeIf(s -> s.contains(v)));
    return listWords.toArray(new String[0]);
  }

  private void addToMap(String[] words){
    int totalWords =0;
    for (String s : words){
      monitor.addMap(s);
      totalWords++;
    }
    monitor.incrementWordCounter(totalWords);
  }

  private void checkAccessPermission(AccessPermission ap){
    if (!ap.canExtractContent()){
      try {
        throw new IOException("You do not have permission to extract text");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}

