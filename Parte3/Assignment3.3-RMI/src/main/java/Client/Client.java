package Client;

import Game.PuzzleBoard;
import Server.RemoteMethods;
import Utility.UtilityFunctions;

import java.util.List;


public class Client {

    final String name;

    UtilityFunctions uFunctions = UtilityFunctions.getInstance();
    final PuzzleBoard puzzle;

    public Client(final int rows, final int columns, RemoteMethods stub, String name, List<Integer> randomPositions){

        this.name = name;
        final String imagePath = "src/main/java/Game/bletchley-park-mansion.jpg";
        puzzle = new PuzzleBoard(rows, columns, imagePath, stub, randomPositions);
        puzzle.setVisible(true);
        //System.out.println("Client: " + puzzle.getRandomList().toString());


        //System.out.println("Dopo Reverse: " + uFunctions.convert(puzzle.getRandomList()));
    }

}
