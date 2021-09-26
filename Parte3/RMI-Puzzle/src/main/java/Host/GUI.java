package Host;

import Game.PuzzleBoard;
import Server.RemoteMethods;

import javax.swing.*;

public class GUI {

    private final RemoteMethods stub;
    private final int port;
    private final Host host;
    private int roomID;

    private final int n = 3;    // rows
    private final int m = 3;    // columns
    private final String imagePath = "src/main/java/Game/bletchley-park-mansion.jpg";

    private final JFrame jFrame;
    private final JButton create;
    //private final JButton start;
    private final JButton join;
    private final JButton printDebug;
    private final JTextArea textAreaRoomID;

    public GUI(RemoteMethods stub, int port, Host host){
        this.stub = stub;
        this.port = port;
        this.host = host;

        this.jFrame = new JFrame("Port: " + this.port);
        this.create = new JButton("Create Room");
        //this.start = new JButton("Start game");
        this.join = new JButton("Join Game - Insert roomID");
        this.printDebug = new JButton("PRINTDEBUG");
        this.textAreaRoomID = new JTextArea("Insert roomID");

    }

    public void start(){
        this.create.setBounds(130,100,100, 40);
        //this.start.setBounds(130,150,100,40);
        this.join.setBounds(130,200,100, 40);
        this.printDebug.setBounds(130,400,100, 40);

        this.textAreaRoomID.setBounds(130,300,100, 40);

        jFrame.add(this.create);
        //jFrame.add(this.start);
        jFrame.add(this.join);
        jFrame.add(this.printDebug);
        jFrame.add(this.textAreaRoomID);

        jFrame.setSize(400,500);
        jFrame.setLayout(null);
        jFrame.setVisible(true);

        this.listeners();
    }

    private void listeners(){

        this.create.addActionListener(e -> {
            this.roomID = this.host.createRoom(this.port, this.stub, 3, 3);
            final PuzzleBoard puzzle = new PuzzleBoard(this.n, this.m, this.imagePath, this.stub, this.host.getPositions(roomID), this.port, this.roomID);
            puzzle.setVisible(true);
            this.jFrame.setVisible(false);
        });

        this.join.addActionListener(w -> {
            try {
                this.host.joinRoom(this.port, this.stub, Integer.parseInt(this.textAreaRoomID.getText()));
                final PuzzleBoard puzzle = new PuzzleBoard(this.n, this.m, this.imagePath, this.stub, this.host.getPositions(roomID), this.port, this.roomID);
                puzzle.setVisible(true);
                this.jFrame.setVisible(false);
            } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                System.err.println("Insert a correct Room ID");
            }
        });

        this.printDebug.addActionListener(q -> {
            System.out.println("Port: " + this.port + " RoomID: " + this.host.getRoomID());
            System.out.println("Room status: " + this.host.getConnections(0).values());
        });
    }
}
