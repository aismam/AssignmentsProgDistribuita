package Host;

import Server.RemoteMethods;
import Server.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Host {

    private int roomID;
    private RemoteMethods stub;

    public Host(int portNumber){
        try{
            Server obj = new Server();
            this.stub = (RemoteMethods) UnicastRemoteObject.exportObject(obj,portNumber);
            GUI gui = new GUI(this.stub, portNumber, this);
            gui.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, RemoteMethods> getConnections(int roomID) {
        try {
            return this.stub.getConnections(roomID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public int getRoomID(){
        return this.roomID;
    }

    public int createRoom(int portNumber, RemoteMethods stub, int n, int m){
        try {
            int r = this.roomID = this.stub.singleUseGetRoom();
            this.stub.createRoom(portNumber,stub);
            this.stub.generateGridPositions(n,m);
            return r;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void joinRoom(int portNumber, RemoteMethods stub, int roomID){
        try {
            this.stub.joinRoom(roomID,portNumber,stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getPositions(int roomID){
        try {
            return this.stub.getPositions();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

}

