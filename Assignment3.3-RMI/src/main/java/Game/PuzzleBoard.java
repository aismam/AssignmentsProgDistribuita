package Game;

import Server.RemoteMethods;
import Utility.UtilityFunctions;

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

public class PuzzleBoard extends JFrame {
	
	final int rows, columns;
	private List<Tile> tiles = new ArrayList<>();
	private final RemoteMethods stub;

    private BufferedImage image;
    final JPanel board = new JPanel();

    private List<Integer> randomPositions;

	private final SelectionManager selectionManager = new SelectionManager();
    private final UtilityFunctions uFunctions = UtilityFunctions.getInstance();
	
    public PuzzleBoard(final int rows, final int columns, final String imagePath, RemoteMethods stub, List<Integer> randomPositions) {
    	this.rows = rows;
		this.columns = columns;
		this.stub = stub;
		this.randomPositions=randomPositions;
    	
    	setTitle("Puzzle");
        setResizable(false);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        board.setBorder(BorderFactory.createLineBorder(Color.gray));
        board.setLayout(new GridLayout(rows, columns, 0, 0));
        getContentPane().add(board, BorderLayout.CENTER);
        
        createTiles(imagePath);
        paintPuzzle(board);

        Thread t = new Thread(() -> {
            while(true){
                try {
                    try {
                        System.out.println("RandomPos: " + this.randomPositions);
                        System.out.println("ServerPos: " + stub.getRandomPositions());
                        if(!this.randomPositions.equals(stub.getRandomPositions())){
                            this.randomPositions = stub.getRandomPositions();
                            this.refreshPuzzle();
                            this.paintPuzzle(this.board);
                            System.err.println("Tiles refreshati");
                        }
                    } catch (RemoteException r){
                        r.printStackTrace();
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("Passati 5 secondi!");
            }
        });
        t.start();
    }

    
    private void createTiles(final String imagePath) {
        try {
            this.image = ImageIO.read(new File(imagePath));
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

                tiles.add(new Tile(imagePortion, position, randomPositions.get(position)));
                position++;
            }
        }

        try {
            this.stub.setPositions(this.randomPositions);
        } catch (RemoteException e){
            e.printStackTrace();
        }
        System.out.println("Dopo tiles.add: " + randomPositions);
        System.out.println("Dopo tiles.add, originalPosition: " + this.tiles.stream().map(Tile::getOriginalPosition).collect(Collectors.toList()));
	}
    
    private void paintPuzzle(final JPanel board) {
    	board.removeAll();
    	
    	Collections.sort(tiles);
    	
    	tiles.forEach(tile -> {
    		final TileButton btn = new TileButton(tile);
            board.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(actionListener -> selectionManager.selectTile(tile, () -> {
                paintPuzzle(board);
                /*System.out.print("\n Posizioni dopo click: ");
                this.tiles.forEach(e -> System.out.print(e.getOriginalPosition() + " - "));*/
                //System.out.println("Dentro Listener, originalPosition: " + this.tiles.stream().map(Tile::getOriginalPosition).collect(Collectors.toList()));
                try {
                    stub.setPositions(this.getPositions());
                    this.randomPositions = this.getPositions();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                checkSolution();
            }));
    	});
    	
    	pack();
        //setLocationRelativeTo(null);
    }

    private List<Integer> getPositions(){
        return this.tiles.stream().map(Tile::getOriginalPosition).collect(Collectors.toList());
    }

    private void refreshPuzzle(){
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        int position = 0;

        List<Tile> newTiles = new ArrayList<>();

        List<Integer> newPositions = uFunctions.convert(randomPositions);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                final Image imagePortion = createImage(new FilteredImageSource(image.getSource(),
                        new CropImageFilter(j * imageWidth / columns,
                                i * imageHeight / rows,
                                (imageWidth / columns),
                                imageHeight / rows)));

                newTiles.add(new Tile(imagePortion, position, newPositions.get(position)));
                position++;
            }
        }
        this.tiles = newTiles;
        System.out.println("Posizioni refresh: " + this.randomPositions);
    }

    private void checkSolution() {
    	if(tiles.stream().allMatch(Tile::isInRightPlace)) {
    		JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE);
    	}
    }

    public List<Integer> getRandomList(){
        return this.tiles.stream()
                .map(Tile::getOriginalPosition)
                .collect(Collectors.toList());
    }

}
