package com.floppyinfant.android.game.scenes.menus;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;

public class ScoreScreen extends MenuScene implements IOnMenuItemClickListener {

	public ScoreScreen(Camera pCamera) {
		super(pCamera);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
