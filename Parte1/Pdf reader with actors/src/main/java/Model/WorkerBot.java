package Model;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class WorkerBot extends AbstractBehavior<WorkerActor.Result> {

    public static Behavior<WorkerActor.Result> create(int max) {
        return Behaviors.setup(context -> new WorkerBot(context, max));
    }

    private final int max;
    private int greetingCounter;

    private WorkerBot(ActorContext<WorkerActor.Result> context, int max) {
        super(context);
        this.max = max;
    }

    @Override
    public Receive<WorkerActor.Result> createReceive() {
        return newReceiveBuilder().onMessage(WorkerActor.Result.class, this::onResult).build();
    }

    private Behavior<WorkerActor.Result> onResult(WorkerActor.Result message) {
        greetingCounter++;
        getContext().getLog().info("Message received", greetingCounter, message.whom);
        if (greetingCounter == max) {
            return Behaviors.stopped();
        } else {
            message.from.tell(new WorkerActor.Work(message.whom, getContext().getSelf()));
            return this;
        }
    }
}