package Game;

public class SelectionManager {

	private boolean selectionActive = false;
	private Tile selectedTile;

	public void selectTile(final Tile tile, final Listener listener) {
		
		if(selectionActive) {
			selectionActive = false;
			
			swap(selectedTile, tile);
			
			listener.onSwapPerformed();
		} else {
			selectionActive = true;
			selectedTile = tile;
		}
	}

	public void swap(final Tile t1, final Tile t2) {
		int pos = t1.getCurrentPosition();
		t1.setCurrentPosition(t2.getCurrentPosition());
		t2.setCurrentPosition(pos);
		System.out.println("Swapped:" + t1.getOriginalPosition() + " - " + t2.getOriginalPosition() );
	}
	
	@FunctionalInterface
	interface Listener{
		void onSwapPerformed();
	}
}
