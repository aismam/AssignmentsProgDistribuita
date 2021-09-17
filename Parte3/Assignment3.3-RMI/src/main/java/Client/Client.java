package Client;

import Game.PuzzleBoard;
import Server.RemoteMethods;

import javax.swing.*;
import java.util.List;


public class Client {

    final String name;
    final int rows;
    final int columns;
    final RemoteMethods stub;
    final List<Integer> randomPositions;

    PuzzleBoard puzzle;

    public Client(final int rows, final int columns, RemoteMethods stub, String name, List<Integer> randomPositions){

        this.rows = rows;
        this.columns = columns;
        this.name = name;
        this.stub = stub;
        this.randomPositions = randomPositions;

        this.startMainMenu();

    }

    private void startMainMenu(){

        JFrame frame = new JFrame();

        JButton createButton = new JButton("Start new Game: ");
        createButton.setBounds(50,50,200,50);

        JButton participateButton = new JButton("Participate game: ");
        participateButton.setBounds(50,150,200,50);

        frame.add(createButton);
        frame.add(participateButton);

        frame.setSize(600,300);

        frame.setLayout(null);
        frame.setVisible(true);

        createButton.addActionListener(e -> {
            frame.setVisible(false);
            this.newGame();
        });
    }

    private void newGame(){
        final String imagePath = "src/main/java/Game/bletchley-park-mansion.jpg";
        puzzle = new PuzzleBoard(rows, columns, imagePath, stub, randomPositions);
        puzzle.setVisible(true);
    }

}
