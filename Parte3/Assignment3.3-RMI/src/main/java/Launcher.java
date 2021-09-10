import Client.Client;
import Server.Server;
import Server.RemoteMethods;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Launcher {

    public static void main(String[] args) {

        try {
            Server obj = new Server(3,3);

            RemoteMethods stub = (RemoteMethods) UnicastRemoteObject.exportObject(obj, 8080);

            Registry registry = LocateRegistry.createRegistry(8080);
            registry.bind("ServerA", stub);
            System.err.println("Server ready");

            final List<Integer> randomPositions = new ArrayList<>();
            IntStream.range(0, 3*3).forEach(randomPositions::add);
            Collections.shuffle(randomPositions);

            Client clientA = new Client(3,3, stub, "Client A", randomPositions);

            /*Client clientB = new Client(3,3, stub, "Client B", randomPositions);

            Client clientC = new Client(3,3, stub, "Client C", randomPositions);*/




        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }




    }
}
