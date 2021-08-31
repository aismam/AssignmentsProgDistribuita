package Server;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class GameCore extends JFrame{

    private final int id;
    private final int rows, columns;
    private List<Integer> randPosition = new ArrayList<>();

    public GameCore(int rows, int columns, int id) {
        this.id = id;
        this.rows = rows;
        this.columns = columns;

        this.createRandomPositions();
    }

    public List<Integer> getRandPosition() {
        return this.randPosition;
    }

    public void setRandPosition(List<Integer> positions){
        this.randPosition = positions;
    }

    public int getId() {
        return this.id;
    }

    public void createRandomPositions(){
        IntStream.range(0, rows*columns).forEach(randPosition::add);
        Collections.shuffle(randPosition);
        /*randPosition.forEach(e -> System.out.print(e + " - "));
        System.out.println(" ");*/
    }
}
