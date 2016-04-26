package com.floppyinfant.android.game.manager;

public class GameManager {
	
	/* Since this class is a singleton, we must declare an instance
	 * of this class within itself. The singleton will be instantiated
	 * a single time during the course of an application's full life-cycle
	 */
	private static GameManager INSTANCE;
	
	private static final int INITIAL_SCORE = 0;
	private static final int INITIAL_LIFE_COUNT = 3;
	
	/* The game manager should keep track of certain data involved in
	 * our game. This particular game manager holds data for score, bird
	 * counts and enemy counts.
	 */
	private int mCurrentScore;
	private int mLifeCount;
	
	// The constructor does not do anything for this singleton
	GameManager(){
	}
	
	/* For a singleton class, we must have some method which provides
	 * access to the class instance. getInstance is a static method,
	 * which means we can access it globally (within other classes).
	 * If the GameManager has not yet been instantiated, we create a 
	 * new one.
	 */
	public static GameManager getInstance(){
		if(INSTANCE == null){
			INSTANCE = new GameManager();
		}
		return INSTANCE;
	}
	
	// get the current score
	public int getCurrentScore(){
		return this.mCurrentScore;
	}
	
	// get the life count
	public int getLifeCount(){
		return this.mLifeCount;
	}

	// increase the current score, most likely when an enemy is destroyed
	public void incrementScore(int pIncrementBy){
		mCurrentScore += pIncrementBy;
	}
	
	public void decrementLifeCount(){
		mLifeCount -= 1;
	}
	
	// Any time a bird is launched, we decrement our bird count
	
	// Any time an enemy is hit/destroyed, we decrement the enemy count
	
	// Resetting the game simply means we must revert back to initial values.
	public void resetGame(){
		this.mCurrentScore = GameManager.INITIAL_SCORE;
		this.mLifeCount = GameManager.INITIAL_LIFE_COUNT;
	}
}