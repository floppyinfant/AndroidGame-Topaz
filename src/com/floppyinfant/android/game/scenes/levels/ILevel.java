package com.floppyinfant.android.game.scenes.levels;

/**
 * Interface to structure the GameScenes.
 * 
 * Not implemented - nor used by any Manager yet.
 * 
 * @author TM
 * @since 2015-12-24
 *
 */
public interface ILevel {
	
	/**
	 * Load resources first - then populate the scene
	 */
	public void populate();
	
	/**
	 * Release objects - then unload resources
	 */
	public void destroy();
	
	/**
	 * App Lifecycle
	 */
	public void onPause();
	
	/**
	 * App Lifecycle
	 */
	public void onResume();

	/**
	 * handle BackButton behavior
	 */
	public void onBackButtonPressed();
	
}
