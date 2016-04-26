package com.floppyinfant.android.game.scenes;

import com.floppyinfant.android.game.manager.ResourceManager;


public abstract class ManagedMenuScene extends ManagedScene {
	
	protected ResourceManager res = ResourceManager.getInstance();
	
	public ManagedMenuScene(float pLoadingScreenMinimumSecondsShown) {
		super(pLoadingScreenMinimumSecondsShown);
	}
	
	public ManagedMenuScene() {
		
	}
	
	@Override
	public void onUnloadManagedScene() {
		if(isLoaded) {
			// For menus, we are disabling the reloading of resources.
			// isLoaded = false;
			onUnloadScene();
		}
	}
}