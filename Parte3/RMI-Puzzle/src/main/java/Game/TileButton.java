package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class TileButton extends JButton{

	private final Tile tile;

	public TileButton(final Tile tile) {
		super(new ImageIcon(tile.getImage()));
		this.tile = tile;
		
		addMouseListener(new MouseAdapter() {            
            @Override
            public void mouseClicked(MouseEvent e) {
            	setBorder(BorderFactory.createLineBorder(Color.red));
            }
        });
	}

	public Tile getTile() {
		return tile;
	}
}
