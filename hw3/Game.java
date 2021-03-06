package hw3;

import java.util.ArrayList;
import java.util.Random;

import api.Direction;
import api.Move;
import api.TilePosition;


//Author: Sam Shifflett
//Date: 4/3/2018
//2048 Game class that is called in GameMain to run the application

/**
 * The Game class contains the state and logic for an implementation of a video
 * game such as "Threes". The basic underlying state is an n by n grid of tiles,
 * represented by integer values. A zero in a cell is considered to be *
 * "empty". To play the game, a client calls the method
 * <code>shiftGrid()</code>, selecting one of the four directions (LEFT, RIGHT,
 * UP, DOWN). Each row or column is then shifted according to the rules
 * encapsulated in the associated <code>GameUtil</code> object. The move is
 * completed by calling <code>newTile</code>, which makes a new tile appear in
 * the grid in preparation for the next move.
 * <p>
 * In between the call to <code>shiftGrid()</code> and the call to
 * <code>newTile</code>, the client may also call <code>undo()</code>, which
 * reverts the grid to its state before the shift.
 * <p>
 * The game uses an instance of java.util.Random to generate new tile values and
 * to select the location for a new tile to appear. The new values are generated
 * by the associated <code>GameUtil</code>'s
 * <code>generateRandomTileValue</code> method, and the new positions are
 * generated by the <code>GameUtil</code>'s
 * <code>generateRandomTilePosition</code> method. The values are always
 * generated one move ahead and stored, in order to support the ability for a UI
 * to provide a preview of the next tile value.
 * <p>
 * The score is the sum over all cells of the individual scores returned by the
 * <code>GameUtil</code>'s <code>getScoreForValue()</code> method.
 */
public class Game {
	//to keep track of the grid's size
	
	private int sizeOfGrid = 0;
	
	//GameUtil object to use inside methods of Game
	
	private GameUtil game = new GameUtil();
	
	//grid to return as final grid after shifts and undo
	
	private int[][] grid;
	
	//grid to track the last move and keep the previous grid's tile placements in memory
	
	private int[][] forUndoGrid;
	
	//overall score for the game
	
	private int score = 0;
	
	//random object to use in parameters of methods
	
	private Random rand = new Random();
	
	//universal last move to use in parameters of methods
	
	private Direction lastDir;
	
	//boolean value to validate shiftGrid was called previously
	
	private boolean isShifted = false;

	/**
	 * Constructs a game with a grid of the given size, using a default random
	 * number generator. The initial grid is produced by the
	 * <code>initializeNewGrid</code> method of the given <code>GameUtil</code>
	 * object.
	 * 
	 * @param givenSize
	 *            size of the grid for this game
	 * @param givenConfig
	 *            given instance of GameUtil
	 */
	public Game(int givenSize, GameUtil givenConfig) {
		// just call the other constructor
		this(givenSize, givenConfig, new Random());
	}

	/**
	 * Constructs a game with a grid of the given size, using the given instance of
	 * <code>Random</code> for the random number generator. The initial grid is
	 * produced by the <code>initializeNewGrid</code> method of the given
	 * <code>GameUtil</code> object.
	 * 
	 * @param givenSize
	 *            size of the grid for this game
	 * @param givenConfig
	 *            given instance of GameUtil
	 * @param givenRandom
	 *            given instance of Random
	 */
	//once the size for the game is dictated, the forUndoGrid can be initialized
	public Game(int givenSize, GameUtil givenConfig, Random givenRandom) {
		grid = game.initializeNewGrid(givenSize, givenRandom);
		sizeOfGrid = givenSize;
		forUndoGrid = new int[sizeOfGrid][sizeOfGrid];
	}

	/**
	 * Returns the value in the cell at the given row and column.
	 * 
	 * @param row
	 *            given row
	 * @param col
	 *            given column
	 * @return value in the cell at the given row and column
	 */
	public int getCell(int row, int col) {

		return grid[row][col];

	}

	/**
	 * Sets the value of the cell at the given row and column. <em>NOTE: This method
	 * should not be used by clients outside of a testing environment.</em>
	 * 
	 * @param row
	 *            given row
	 * @param col
	 *            given col
	 * @param value
	 *            value to be set
	 */
	public void setCell(int row, int col, int value) {
		grid[row][col] = value;
	}

	/**
	 * Returns the size of this game's grid.
	 * 
	 * @return size of the grid
	 */
	public int getSize() {
		return sizeOfGrid;
	}

	/**
	 * Returns the current score.
	 * 
	 * @return score for this game
	 */
	public int getScore() {
		score = game.calculateTotalScore(grid);
		return score;
	}

	/**
	 * Copy a row or column from the grid into a new one-dimensional array. There
	 * are four possible actions depending on the given direction:
	 * <ul>
	 * <li>LEFT - the row indicated by the index <code>rowOrColumn</code> is copied
	 * into the new array from left to right
	 * <li>RIGHT - the row indicated by the index <code>rowOrColumn</code> is copied
	 * into the new array in reverse (from right to left)
	 * <li>UP - the column indicated by the index <code>rowOrColumn</code> is copied
	 * into the new array from top to bottom
	 * <li>DOWN - the row indicated by the index <code>rowOrColumn</code> is copied
	 * into the new array in reverse (from bottom to top)
	 * </ul>
	 * 
	 * @param rowOrColumn
	 *            index of the row or column
	 * @param dir
	 *            direction from which to begin copying
	 * @return array containing the row or column
	 */
	// based on the direction, copy the array in a certain fashion that will allow the shiftArray in GameUtil to work correctly
	public int[] copyRowOrColumn(int rowOrColumn, Direction dir) {
		int[] rowOrCol = new int[sizeOfGrid];
		int count = 0;

		if (dir == Direction.LEFT) {
			for (int j = 0; j < sizeOfGrid; j++) {
				rowOrCol[j] = grid[rowOrColumn][j];
			}

		} else if (dir == Direction.UP) {
			for (int j = 0; j < sizeOfGrid; j++) {
				rowOrCol[j] = grid[j][rowOrColumn];
			}
		} else if (dir == Direction.RIGHT) {
			for (int j = sizeOfGrid - 1; j >= 0; j--) {
				rowOrCol[count] = grid[rowOrColumn][j];
				count++;
			}
		} else if (dir == Direction.DOWN) {
			for (int j = sizeOfGrid - 1; j >= 0; j--) {
				rowOrCol[count] = grid[j][rowOrColumn];
				count++;
			}

		}
		return rowOrCol;
	}

	/**
	 * Updates the grid by copying the given one-dimensional array into a row or
	 * column of the grid. There are four possible actions depending on the given
	 * direction:
	 * <ul>
	 * <li>LEFT - the given array is copied into the the row indicated by the index
	 * <code>rowOrColumn</code> from left to right
	 * <li>RIGHT - the given array is copied into the the row indicated by the index
	 * <code>rowOrColumn</code> in reverse (from right to left)
	 * <li>UP - the given array is copied into the column indicated by the index
	 * <code>rowOrColumn</code> from top to bottom
	 * <li>DOWN - the given array is copied into the column indicated by the index
	 * <code>rowOrColumn</code> in reverse (from bottom to top)
	 * </ul>
	 * 
	 * @param arr
	 *            the array from which to copy
	 * @param rowOrColumn
	 *            index of the row or column
	 * @param dir
	 *            direction from which to begin copying
	 */
	//updates the universal grid to match the changes made by shifting the array values based off of the move objects
	public void updateRowOrColumn(int[] arr, int rowOrColumn, Direction dir) {
		int count = 0;

		if (dir == Direction.LEFT) 
		{
			for (int i = 0; i < sizeOfGrid; i++) 
			{
				grid[rowOrColumn][i] = arr[i];
			}
		} 
		else if (dir == Direction.UP) 
		{
			for (int i = 0; i < sizeOfGrid; i++) 
			{
				grid[i][rowOrColumn] = arr[i];
			}
		} 
		else if (dir == Direction.RIGHT) 
		{
			for (int i = sizeOfGrid - 1; i >= 0; i--) 
			{
				grid[rowOrColumn][i] = arr[count];
				count++;
			}
		} 
		else if (dir == Direction.DOWN) 
		{
			for (int i = sizeOfGrid - 1; i >= 0; i--) 
			{
				grid[i][rowOrColumn] = arr[count];
				count++;
			}
		}
	}

	/**
	 * Plays one step of the game by shifting the grid in the given direction.
	 * Returns a list of Move objects describing all moves performed. All Move
	 * objects must include a valid value for <code>getRowOrColumn()</code> and
	 * <code>getDirection()</code>. If no cells are moved, the method returns an
	 * empty list.
	 * <p>
	 * The shift of an individual row or column is performed by the method
	 * <code>shiftArray</code> of <code>GameUtil</code>.
	 * <p>
	 * The score is not updated.
	 * 
	 * @param dir
	 *            direction in which to shift the grid
	 * @return list of moved or merged tiles
	 */
	//shifts the grid based off of the Move objects that are returned by shiftArray and updates the universal grid 
	//based off of those Move objects
	public ArrayList<Move> shiftGrid(Direction dir) {
		
		//populate the forUndoGrid with the universal grid to log the previous coordinates of values for utilization of undo
		
		for (int row = 0; row < sizeOfGrid; row += 1)
	    {
	      for (int col = 0; col < sizeOfGrid; col += 1)
	      {
	    	  forUndoGrid[row][col] = grid[row][col];
	      }
	     
	    }
		
		//set isShifted to true so that the undo method can execute if necessary
		
		isShifted = true;
		
		//initialize the size of the array we are shifting
		
		int[] arrToBeShifted = new int[sizeOfGrid];
		
		//create a temporary placeholder ArrayList of Move objects that pass Move objects of a single array
		//into the finalMove ArrayList that holds all Move objects for the universal grid
		
		ArrayList<Move> tempMove = new ArrayList<Move>();
		ArrayList<Move> finalMove = new ArrayList<Move>();
		
		//initialize/update instance variable of lastDir
		
		lastDir = dir;
		
		//in this for loop 

		for (int i = 0; i < sizeOfGrid; i++) 
		{
			
			//populate the tempMove ArrayList with Move objects returned from shiftArray of the array it is evaluating
			//in copyRowOrColumn
			
			tempMove = game.shiftArray(copyRowOrColumn(i, dir));
			
			//set the row and direction of the Move objects returned to tempMove and 
			//add the Move object to the Move ArrayList of the universal grid
			
			for (int j = 0; j < tempMove.size(); j++) 
			{
				tempMove.get(j).setDirection(i, dir);
				finalMove.add(tempMove.get(j));
			}
			
			//populates the initialized arrToBeShifted with the array returned from copyRowOrColumn
			
			arrToBeShifted = copyRowOrColumn(i, dir);
			
			//based off of the Move objects in tempMove this updates the arrToBeShifted with the
			//values in their corresponding indexes
			
			for (int h = 0; h < tempMove.size(); h++) 
			{
				if (tempMove.get(h).isMerged() == true) 
				{
					arrToBeShifted[tempMove.get(h).getNewIndex()] = tempMove.get(h).getNewValue();
					arrToBeShifted[tempMove.get(h).getOldIndex()] = 0;
				} 
				else 
				{
					arrToBeShifted[tempMove.get(h).getNewIndex()] = tempMove.get(h).getValue();
					arrToBeShifted[tempMove.get(h).getOldIndex()] = 0;
				}
			}
			
			//remove all of the Move objects in tempMove to get the next row/column Move objects
			
			tempMove.removeAll(tempMove);
			updateRowOrColumn(arrToBeShifted, i, dir);
		}
		
		//return the Move objects for the entire universal grid
		
		return finalMove;

	}

	/**
	 * Reverts the shift performed in a previous call to <code>shiftGrid()</code>,
	 * provided that neither <code>newTile()</code> nor <code>undo()</code> has been
	 * called. If there was no previous call to <code>shiftGrid()</code> without a
	 * <code>newTile()</code> or <code>undo()</code>, this method does nothing and
	 * returns false; otherwise returns true.
	 * 
	 * @return true if the previous shift was undone, false otherwise
	 */
	//updates the universal grid with the original grid before the shiftGrid call
	public boolean undo() {
		
		if(isShifted)
		{
			for (int row = 0; row < sizeOfGrid; row += 1)
		    {
		      for (int col = 0; col < sizeOfGrid; col += 1)
		      {
		    	  grid[row][col] = forUndoGrid[row][col];
		      }
		    }
			isShifted = false;
			return true;
		}
		
		return false;
	}

	/**
	 * Generates a new tile and places its value in the grid, provided that there
	 * was a previous call to <code>shiftGrid</code> without a corresponding call to
	 * <code>undo</code> or <code>newTile</code>. The tile's position is determined
	 * according to the <code>generateRandomTilePosition</code> of this game's
	 * associated <code>GameUtil</code> object. If there was no previous call to
	 * <code>shiftGrid</code> without an <code>undo</code> or <code>newTile</code>,
	 * this method does nothing and returns null; otherwise returns a
	 * <code>TilePosition</code> object with the new tiles's position and value.
	 * Note that the returned tile's value should match the <em>current</em> value
	 * returned by <code>getNextTileValue</code>, and if this method returns a
	 * non-null value the upcoming tile value should be updated according to
	 * <code>generateRandomTileValue()</code>. This method should update the total
	 * score and the score should include the newly generated tile.
	 * 
	 * @return TilePosition containing the new tile's position and value, or null if
	 *         no new tile is created
	 */
	//updates the grid with a new tile that the coordinates are dictated by the generateRandomTilePosition
	//it returns Null if the Tile value is already populated by a value in the universal grid
	public TilePosition newTile() 
	{
		
		//generates a new random tile position on grid
		
		TilePosition tile = game.generateRandomTilePosition(grid, rand, lastDir);
		
		//checks to see if the space in the grid is already populated with a tile
		
		if(grid[tile.getRow()][tile.getCol()] != 0)
		{
			return null;
		}
		
		//if the tile is open for placement then the tile is placed within the universal grid
		
		grid[tile.getRow()][tile.getCol()] = tile.getValue();
		
		return tile;
	}

	/**
	 * Returns the value that will appear on the next tile generated in a call to
	 * <code>newTile</code>. This is an accessor method that does not modify the
	 * game state.
	 * 
	 * @return value to appear on the next generated tile
	 */
	// this is for the next tile value to be displayed so the user can see which tile may be placed next
	public int getNextTileValue() {

		return game.generateRandomTileValue(rand);
	}

}
