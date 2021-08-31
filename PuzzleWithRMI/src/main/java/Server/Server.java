package Server;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements RemoteMethods {

    private final Map<Integer, GameCore> rooms;
    int counter = 1;

    public Server() {
        this.rooms = new HashMap<>();
    }

    @Override
    public String createRoom() throws RemoteException {
        GameCore gc = new GameCore(3,4, counter);
        this.rooms.put(counter, gc);
        System.out.println("Room ID: " + counter);
        counter ++;
        return Integer.toString(counter -1);
    }

    @Override
    public List<Integer> getRandomPositions(String id) throws RemoteException {
        return this.rooms.get(Integer.parseInt(id)).getRandPosition();
    }

    @Override
    public void setPositions(List<Integer> positions, String id) throws RemoteException {
        this.rooms.get(Integer.parseInt(id)).setRandPosition(positions);

        System.out.print("\nPosition Changed: ");
        this.rooms.get(Integer.parseInt(id)).getRandPosition().forEach(e -> System.out.print(e + " - "));
    }


}