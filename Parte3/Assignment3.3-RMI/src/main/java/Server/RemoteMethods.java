package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteMethods extends Remote {

    void setPositions(List<Integer> position) throws RemoteException;

    List<Integer> getRandomPositions() throws RemoteException;
}
