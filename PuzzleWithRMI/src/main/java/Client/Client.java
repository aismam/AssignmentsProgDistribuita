package Client;

import Client.Game.GameView;
import Server.RemoteMethods;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class Client {

    JFrame jFrame;
    JButton jButtonCreate;
    JButton jButtonJoin;
    String clientID = "Unset";

    public Client(){

        jFrame = new JFrame();
        jButtonCreate = new JButton("Crea Stanza");
        jButtonJoin = new JButton("Unisciti alla stanza");

        jButtonCreate.setBounds(50,100,300,40);
        jButtonJoin.setBounds(50,200,300,40);

        jFrame.add(jButtonCreate);
        jFrame.add(jButtonJoin);

        jFrame.setSize(400,500);
        jFrame.setLayout(null);
        jFrame.setVisible(true);

        this.addListeners();


    }

    private void addListeners(){

        jButtonCreate.addActionListener(l -> {
            try {
                Registry registry = LocateRegistry.getRegistry(8080);
                RemoteMethods stub = (RemoteMethods) registry.lookup("Hello");

                String roomID = stub.createRoom();
                System.out.println("ID stanza: " + roomID + "\n Posizioni ricevute dal client: ");
                this.clientID = roomID;
                List<Integer> positions = stub.getRandomPositions(roomID);
                positions.forEach(e -> System.out.print(e + " - "));
                System.out.println("\n");

                GameView gv = new GameView(3,4, positions, stub, this.clientID);
                gv.setVisible(true);

            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        });
    }




}
