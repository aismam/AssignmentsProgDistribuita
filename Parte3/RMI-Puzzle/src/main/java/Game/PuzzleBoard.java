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

@SuppressWarnings("serial")
public class PuzzleBoard extends JFrame {

    final int port;
	final int rows, columns;
	private List<Tile> tiles = new ArrayList<>();

	private final RemoteMethods myStub;
	private final int roomID;
    private List<Integer> randomPositions;

    private final UtilityFunctions uFunctions = UtilityFunctions.getInstance();

    private List<Integer> tempIndex = new ArrayList<>();


    final JPanel board = new JPanel();
    private BufferedImage image;
	
	private SelectionManager selectionManager = new SelectionManager();

    public PuzzleBoard(final int rows, final int columns, final String imagePath, RemoteMethods stub, List<Integer> randomPositions, int port, int roomID) {
        this.port = port;
    	this.rows = rows;
		this.columns = columns;
		this.randomPositions = randomPositions;
		this.myStub = stub;
		this.roomID = roomID;
    	
    	setTitle("Puzzle in port: " + this.port);
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
                        System.out.println("ServerPos: " + stub.getPositions());


                        if(!this.randomPositions.equals(stub.getPositions())){

                        }
                        this.randomPositions = stub.getPositions();
                        this.refreshPuzzle();
                        this.paintPuzzle(this.board);
                        System.err.println("Tiles refreshati");
                    } catch (RemoteException r){
                        r.printStackTrace();
                    }
                    Thread.sleep(5000);
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

                tiles.add(new Tile(imagePortion, position, this.randomPositions.get(position)));
                position++;
            }
        }

        System.out.print("CONSTRUCTOR ORIGINAL order: ");
        this.tiles.forEach(e -> System.out.print(e.getCurrentPosition()+ " - "));
        System.out.println(" ");
	}
    
    private void paintPuzzle(final JPanel board) {
    	board.removeAll();

    	Collections.sort(tiles);
    	
    	tiles.forEach(tile -> {
    		final TileButton btn = new TileButton(tile);
            board.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(actionListener -> {

                if(!this.request(btn)){
                    // case if the clicked button is already locked
                    this.paintPuzzle(board);
                } else {
                    // case if the clicked button is unlocked
                    try {
                        // locking the button
                        this.myStub.lockPosition(btn.getTile().getCurrentPosition());
                        this.tempIndex.add(btn.getTile().getCurrentPosition());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    selectionManager.selectTile(tile, () -> {
                        try{
                            // unlocking locked buttons
                            this.myStub.unlockPosition(this.tempIndex.get(0), this.tempIndex.get(1));
                            this.tempIndex.clear();
                            // set host new positions
                            this.myStub.setPositions(this.getPositions());
                            this.randomPositions = this.getPositions();
                            // update other hosts with new positions
                            this.myStub.swapPerformed();
                            System.out.println("TEMPINDEX: " + this.tempIndex);
                        } catch (RemoteException re) {
                            re.printStackTrace();
                        }
                        paintPuzzle(board);
                        System.out.print("tiles order: ");
                        this.tiles.forEach(e -> System.out.print(e.getOriginalPosition()+ " - "));
                        System.out.println(" ");
                        checkSolution();
                    });
                }
            });
    	});
    	
    	pack();
        //setLocationRelativeTo(null);
    }

    public void refreshPuzzle(){
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        int position = 0;

        List<Tile> newTiles = new ArrayList<>();

        List<Integer> newPositions = new ArrayList<>();
        try {
            newPositions = uFunctions.convert(this.myStub.getPositions());
        } catch (RemoteException rem){
            rem.printStackTrace();
        }
        //List<Integer> newPositions = uFunctions.convert(randomPositions);

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
        System.out.println("REFRESH - Posizioni: " + this.randomPositions);
    }

    private void checkSolution() {
    	if(tiles.stream().allMatch(Tile::isInRightPlace)) {
    		JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE);
    	}
    }

    private Boolean request(TileButton btn){
        System.err.println("Action Listener: " + btn.getTile().getCurrentPosition());
        // there are all results of sent requests
        List<Boolean> ret = new ArrayList<>();
        try {
            // get all others connection
            this.myStub.getConnections(this.roomID).values().forEach(val -> {
                if(!val.equals(this.myStub)){
                    try {
                        // sending request, result is true if the tile is unlocked, false otherwise
                        ret.add(val.request(btn.getTile().getCurrentPosition()));
                    } catch (RemoteException re) {
                        re.printStackTrace();
                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Stato bottone: " + !ret.contains(false));
        // return false if a single (or more) index of array is false, true otherwise
        return !ret.contains(false);
    }

    private List<Integer> getPositions(){
        return this.tiles.stream()
                .map(Tile::getOriginalPosition)
                .collect(Collectors.toList());
    }
}
