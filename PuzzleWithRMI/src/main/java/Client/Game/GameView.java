package Client.Game;

import Server.RemoteMethods;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameView extends JFrame {

    final int rows, columns;
    private List<Tile> tiles = new ArrayList<>();
    private List<Integer> randPositions;
    private final String clientID;

    RemoteMethods stub;

    private SelectionManager selectionManager = new SelectionManager();

    public GameView(int rows, int columns, List<Integer> randPositions, RemoteMethods stub, String clientID){
        this.randPositions = randPositions;
        this.rows = rows;
        this.columns = columns;
        this.stub = stub;
        this.clientID = clientID;

        setTitle("Puzzle");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel board = new JPanel();
        board.setBorder(BorderFactory.createLineBorder(Color.gray));
        board.setLayout(new GridLayout(rows, columns, 0, 0));
        getContentPane().add(board, BorderLayout.CENTER);

        createTiles("src/main/java/Client/Game/bletchley-park-mansion.jpg");
        paintPuzzle(board);
    }

    private void createTiles(final String imagePath) {
        final BufferedImage image;

        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load image", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        int position = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                final Image imagePortion = createImage(new FilteredImageSource(image.getSource(),
                        new CropImageFilter(j * imageWidth / columns,
                                i * imageHeight / rows,
                                (imageWidth / columns),
                                imageHeight / rows)));

                tiles.add(new Tile(imagePortion, position, randPositions.get(position)));

                position++;
            }
        }
        System.out.print("Create tiles situation, getOriginalPosition: ");
        this.tiles.forEach(e -> System.out.print(e.getOriginalPosition() + " - "));
        System.out. println("\n");
    }

    private void paintPuzzle(final JPanel board) {
        board.removeAll();

        /*System.out.print("PAINTPUZZLE PRE by getOriginalPosition: ");
        this.tiles.forEach(e -> System.out.print(e.getOriginalPosition() + " - "));
        System.out.println("\n");*/

        Collections.sort(tiles);

        try {
            this.stub.setPositions(tiles.stream()
                    .map(Tile::getOriginalPosition)
                    .collect(Collectors.toList()),this.clientID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        /*System.out.print("PAINTPUZZLE POST by getOriginalPosition: ");
        this.tiles.forEach(e -> System.out.print(e.getOriginalPosition() + " - "));
        System.out.println("\n");*/

        tiles.forEach(tile -> {
            final TileButton btn = new TileButton(tile);
            board.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(actionListener -> {
                try {
                    selectionManager.selectTile(tile, () -> {
                        paintPuzzle(board);
                        System.out.print("Post click by getOriginalPosition: ");
                        this.tiles.forEach(e -> System.out.print(e.getOriginalPosition() + " - "));
                        System.out.println("\n");
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        });

        pack();
        setLocationRelativeTo(null);
    }
}
