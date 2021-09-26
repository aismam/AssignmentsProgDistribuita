package Server;

import Game.PuzzleBoard;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface RemoteMethods extends Remote {

    Map<Integer, RemoteMethods> getConnections(int roomID) throws RemoteException;

    int singleUseGetRoom() throws RemoteException;

    int createRoom(int portNumber, RemoteMethods stub) throws RemoteException;

    void joinRoom(int roomID, int hostPortNumber, RemoteMethods hostStub) throws RemoteException;

    void generateGridPositions(int n, int m) throws RemoteException;

    List<Integer> getPositions() throws RemoteException;

    void setPositions(List<Integer> newPositions) throws RemoteException;

    List<Boolean> getLocks() throws RemoteException;

    boolean request(int position) throws RemoteException;

    void lockPosition(int index) throws RemoteException;

    void unlockPosition(int firstIndex, int secondIndex) throws RemoteException;

    void swapPerformed() throws RemoteException;

}
