package com.floppyinfant.android.game.scenes.menus;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;

import com.floppyinfant.android.game.R;
import com.floppyinfant.android.game.manager.ResourceManager;
import com.floppyinfant.android.game.manager.SceneManager;
import com.floppyinfant.android.game.scenes.ManagedScene;

public class LevelSelector extends Entity {
	
	private final Scene mScene;
	private ResourceManager res = ResourceManager.getInstance();
	
	private final int mMaxLevel;	// current max level unlocked
	
	/**
	 * The LevelSelector object can be used to display a grid of level tiles for
	 * user selection.
	 * 
	 * @param pMaxLevel
	 *            Current max unlocked level.
	 * @param pScene
	 *            The Scene in which the LevelSelector will be displayed on.
	 */
	public LevelSelector(final int pMaxLevel, final Scene pScene) {
		
		this.mScene = pScene;
		this.mMaxLevel = pMaxLevel;
		
		createTiles();
		
	}

	public ManagedScene getLevelById(int levelNumber) {
		
		ManagedScene managedScene = null;
		
		if (levelNumber == 1) {
			
			managedScene = new com.floppyinfant.android.game.scenes.levels.schnecki.GameScene();
			//managedScene = new com.floppyinfant.android.game.scenes.levels.demo.GameScene();
			
		} else if (levelNumber == 2) {
			
			//managedScene = new com.floppyinfant.android.game.scenes.levels.giraffe.GameScene();
			
			String filenames[] = new String[] {"ae.png", "wood.png", "star.png", "bg.png"};
			managedScene = new com.floppyinfant.android.game.scenes.levels.rube.RubeScene(R.raw.ae_example, filenames);
			
		} else if (levelNumber == 3) {
			
			//managedScene = new com.floppyinfant.android.game.scenes.levels.esel.GameScene();
			managedScene = new com.floppyinfant.android.game.scenes.levels.physics.GameScene();
			
		}
		
		return managedScene;
	}
	
	public void createTiles() {
		
		float tempX = ResourceManager.getInstance().cameraWidth / 4f;
		float tempY = ResourceManager.getInstance().cameraHeight / 2f;
		boolean locked = false;
		int currentTileLevel = 1;
		ITextureRegion pTextureRegion = res.schneckiTextureRegion;
		Text pText = null;
		LevelTile schnecki = new LevelTile(tempX, tempY, locked, currentTileLevel, pTextureRegion, pText);
		//levelTile.attachText();
		this.attachChild(schnecki);
		mScene.registerTouchArea(schnecki);
		
		
		tempX = ResourceManager.getInstance().cameraWidth / 4f * 2f;
		locked = false;	// TODO locked
		currentTileLevel++;
		pTextureRegion = res.giraffeTextureRegion;
		pText = null;
		LevelTile giraffe = new LevelTile(tempX, tempY, locked, currentTileLevel, pTextureRegion, pText);
		//levelTile.attachText();
		this.attachChild(giraffe);
		mScene.registerTouchArea(giraffe);
		
		
		tempX = ResourceManager.getInstance().cameraWidth / 4f * 3f;
		locked = false;	// TODO locked
		currentTileLevel++;
		pTextureRegion = res.eselTextureRegion;
		pText = null;
		LevelTile esel = new LevelTile(tempX, tempY, locked, currentTileLevel, pTextureRegion, pText);
		//levelTile.attachText();
		this.attachChild(esel);
		mScene.registerTouchArea(esel);
		
	}
	
	/* *************************************************************************
	 * 
	 */

	public class LevelTile extends Sprite {
		
		private final boolean mIsLocked;
		private final int mLevelNumber;
		private Text mTileText;
		
		public LevelTile(float pX, float pY, boolean pIsLocked, int pLevelNumber, ITextureRegion pTextureRegion, Text pText) {
			super(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, ResourceManager.getInstance().engine.getVertexBufferObjectManager());
			
			/* Initialize the necessary variables for the LevelTile */
			this.mIsLocked = pIsLocked;
			this.mLevelNumber = pLevelNumber;
			this.mTileText = pText; // TODO attachText here
		}

		public boolean isLocked() {
			return this.mIsLocked;
		}
		
		public int getLevelNumber() {
			return this.mLevelNumber;
		}
		
		@Override
		public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			
			if (pSceneTouchEvent.isActionDown()) {
				
				if (!this.mIsLocked) {
					SceneManager.getInstance().showScene(LevelSelector.this.getLevelById(mLevelNumber));
				} else {
					// Tile locked event ...
					LevelSelector.this.mScene.getBackground().setColor(Color.RED);
				}
				return true;
			}
			return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
}
