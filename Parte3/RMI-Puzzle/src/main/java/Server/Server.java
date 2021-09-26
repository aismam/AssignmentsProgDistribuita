package Server;

import Game.PuzzleBoard;
import Utility.ConnectionHandlerSingleton;
import Utility.UtilityFunctions;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Server implements RemoteMethods {

    private Integer myPort;
    private RemoteMethods myStub;
    private int roomID;
    private final ConnectionHandlerSingleton connectionHandlerSingleton = ConnectionHandlerSingleton.getInstance();

    private List<Integer> randomPositions = new ArrayList<>();
    private List<Boolean> lockList = new ArrayList<>(); // TRUE = unlocked

    public Server(){
    }

    @Override
    public Map<Integer, RemoteMethods> getConnections(int roomID) throws RemoteException {
        return this.connectionHandlerSingleton.getConnections(roomID);
    }

    @Override
    public int singleUseGetRoom() throws RemoteException {
        return this.connectionHandlerSingleton.roomSize();
    }

    @Override
    public int createRoom(int portNumber, RemoteMethods stub) throws RemoteException {
        //TODO dubbia utilita
        this.myPort = portNumber;
        this.myStub = stub;
        //TODO questo serve
        this.roomID = this.connectionHandlerSingleton.subscribe(portNumber, stub);
        return this.roomID;
    }

    @Override
    public void joinRoom(int roomID, int hostPortNumber, RemoteMethods hostStub) throws RemoteException {
        this.myPort = hostPortNumber;
        this.myStub = hostStub;
        // Add myself (host) and my data to the room
        this.connectionHandlerSingleton.join(roomID,hostPortNumber,hostStub);
        // notify all other hosts in the room
        this.connectionHandlerSingleton.getConnections(roomID).values().forEach(e ->
                this.receiveJoin(roomID, this.connectionHandlerSingleton.getKeyByValue(e, roomID).get(0), e));
        // set own positions and lockList
        this.connectionHandlerSingleton.getConnections(roomID).values().forEach(e -> {
            if(!e.equals(this.myStub)){
                try {
                    this.randomPositions = e.getPositions();
                    this.lockList = e.getLocks();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void generateGridPositions(int n, int m) throws RemoteException {
        // creation of random positions list
        this.randomPositions = IntStream.range(0,n*m).boxed().collect(Collectors.toList());
        Collections.shuffle(this.randomPositions);
        // creation of the list that contain locks
        this.lockList = IntStream.range(0,n*m).mapToObj(e -> true).collect(Collectors.toList());
        //TODO debug
        System.out.println("SERVER, positions: " + this.randomPositions);
        System.out.println("SERVER, locklist: " + this.lockList);
    }

    @Override
    public List<Integer> getPositions() throws RemoteException {
        return this.randomPositions;
    }

    @Override
    public void setPositions(List<Integer> newPositions) throws RemoteException {
        this.randomPositions = newPositions;
    }

    @Override
    public List<Boolean> getLocks() throws RemoteException {
        return this.lockList;
    }

    @Override
    public boolean request(int position) throws RemoteException {
        return this.lockList.get(position); // True if unlocked
    }

    @Override
    public void lockPosition(int index) throws RemoteException {
        this.lockList.remove(index);
        this.lockList.add(index, false);
        System.out.println("LOCKED position: " + index);
        System.out.println("ALL LOCKS: " + this.lockList);
    }

    @Override
    public void unlockPosition(int firstIndex, int secondIndex) throws RemoteException {
        this.lockList.remove(firstIndex);
        this.lockList.add(firstIndex, true);
        this.lockList.remove(secondIndex);
        this.lockList.add(secondIndex, true);
        System.out.println("UNLOCKED, first index: " + firstIndex + " and second index: " + secondIndex);
    }

    @Override
    public void swapPerformed() throws RemoteException {
        this.connectionHandlerSingleton.getConnections(this.roomID).values().forEach(stub -> {
            if(!stub.equals(this.myStub)){
                try {
                    stub.setPositions(this.randomPositions);
                    System.out.println("SWAP PERFORMED by port:" + this.myPort);
                    System.out.println("Send message to port: " + ConnectionHandlerSingleton.getInstance().getKeyByValue(stub,0));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void receiveJoin(int roomID, int portNumber, RemoteMethods stub) {
        this.connectionHandlerSingleton.join(roomID,portNumber,stub);
    }

}
