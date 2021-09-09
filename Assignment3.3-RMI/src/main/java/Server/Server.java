package Server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Server implements RemoteMethods{

    private List<Integer> randomPositions;
    List<String> clients;

    public Server(final int rows, final int columns){
        randomPositions = new ArrayList<>();
        IntStream.range(0, rows*columns).forEach(e -> randomPositions.add(e));
        Collections.shuffle(randomPositions);
    }

    @Override
    public void setPositions(List<Integer> position) throws RemoteException {
        this.randomPositions = position;
    }

    @Override
    public List<Integer> getRandomPositions() throws RemoteException {
        return this.randomPositions;
    }

    @Override
    public void register( String name) throws RemoteException {
        clients.add(name);
        System.err.println(name);
    }
}
