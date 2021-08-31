package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteMethods extends Remote {

    String createRoom() throws RemoteException;

    List<Integer> getRandomPositions(String id) throws RemoteException;

    void setPositions(List<Integer> positions, String id) throws RemoteException;
}
