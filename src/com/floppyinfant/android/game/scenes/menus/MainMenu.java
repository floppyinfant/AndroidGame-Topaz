package com.floppyinfant.android.game.scenes.menus;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
//import org.andengine.opengl.font.FontUtils;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.math.MathUtils;

import com.floppyinfant.android.game.manager.ResourceManager;
import com.floppyinfant.android.game.manager.SceneManager;
import com.floppyinfant.android.game.scenes.ManagedMenuScene;

public class MainMenu extends ManagedMenuScene {
	
	private static final MainMenu INSTANCE = new MainMenu();
		
	private Sprite mBackground;
	
	// Screens (as entities)
	/*
	private enum Screens {
		LevelSelector, MainMenu
	}
	private Screens mCurrentScreen = Screens.MainMenu;
	*/
	private Entity mMainMenuScreen;
	private Entity mLevelSelectScreen;
	
	
	/* *************************************************************************
	 * 
	 */
	
	public MainMenu() {
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
	}

	public static MainMenu getInstance() {
		return INSTANCE;
	}
	
	/* *************************************************************************
	 * 
	 */
	
	// No loading screen means no reason to use the following methods.
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		Scene scene = new Scene();
		
		return scene;
	}
	
	@Override
	public void onLoadingScreenUnloadAndHidden() {
	}
	
	/* *************************************************************************
	 * 
	 */
	
	/**
	 * 
	 */
	@Override
	public void onLoadScene() {
		
		// Resources
		ResourceManager.loadMenuResources();
		
		createBackground();
		createMovingClouds();
				
		// ---------------------------------------------------------------------
		
		// MainMenuScreen
		mMainMenuScreen = new Entity(0f, res.cameraHeight) {
			boolean hasLoaded = false;

			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if(!this.hasLoaded) {
					this.hasLoaded = true;
					this.registerEntityModifier(new MoveModifier(0.25f, 0f, res.cameraHeight, 0f, 0f));
				}
			}
		};
		
		// Title
		Font font = res.fontHarting96;
		String text = "Topaz";
		TextOptions textOptions = new TextOptions();
		textOptions.setHorizontalAlign(HorizontalAlign.CENTER);
//		float textWidth = FontUtils.measureText(font, text);
		float x = (res.cameraWidth / 2);// - (textWidth / 2);
		float y = (res.cameraHeight * 0.75f);// - (font.getLineHeight() / 2);
		Text title = new Text(x, y, font, text, text.length(), textOptions, res.vbom);
		title.setColor(1.0f, 0f, 0f);
		mMainMenuScreen.attachChild(title);
		
		
		// Play Button
		// Notice that the Game scenes, unlike menus, are not referred to in a static way.
		ButtonSprite playButton = new ButtonSprite(
				(res.cameraWidth - res.buttonTiledTextureRegion.getTextureRegion(0).getWidth()) / 2f,
				(res.cameraHeight - res.buttonTiledTextureRegion.getTextureRegion(0).getHeight()) * (1f / 3f), 
				res.buttonTiledTextureRegion.getTextureRegion(0), 
				res.buttonTiledTextureRegion.getTextureRegion(1), 
				res.engine.getVertexBufferObjectManager());
		Text playButtonText = new Text(0, 0, res.fontDefault32Bold, "PLAY", res.vbom);
		playButtonText.setPosition(playButton.getWidth() / 2, playButton.getHeight() / 2);
		playButton.attachChild(playButtonText);
		mMainMenuScreen.attachChild(playButton);
		playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// show the LevelSelectScreen
				MainMenu.this.mMainMenuScreen.registerEntityModifier(new MoveModifier(0.25f, MainMenu.this.mMainMenuScreen.getX(), MainMenu.this.mMainMenuScreen.getY(), - res.cameraWidth, 0f));
				MainMenu.this.mLevelSelectScreen.registerEntityModifier(new MoveModifier(0.25f, MainMenu.this.mLevelSelectScreen.getX(), MainMenu.this.mLevelSelectScreen.getY(), 0f, 0f));
				
				res.clickSound.play();
			}});
		this.registerTouchArea(playButton);
		
		// Option Button
		// Notice that the SceneManager is being told to not pause the scene while the OptionsLayer is open.
		ButtonSprite optionsButton = new ButtonSprite(
				playButton.getX() + playButton.getWidth(), 
				playButton.getY(),
				res.buttonTiledTextureRegion.getTextureRegion(0), 
				res.buttonTiledTextureRegion.getTextureRegion(1), 
				res.vbom);
		Text optionsButtonText = new Text(0,0,res.fontDefault32Bold,"OPTIONS", res.vbom);
		optionsButtonText.setPosition(optionsButton.getWidth()/2, optionsButton.getHeight()/2);
		optionsButton.attachChild(optionsButtonText);
		mMainMenuScreen.attachChild(optionsButton);
		optionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Show the OptionsLayer and play a click.
				SceneManager.getInstance().showOptionsLayer(false);
				res.clickSound.play();
			}
		});
		this.registerTouchArea(optionsButton);
		
		// TODO: Exit Button
		
		// TODO: Score Screen
		
		// TODO: Load and save, "Start a New Game"
		
		this.attachChild(mMainMenuScreen);
		
		// ---------------------------------------------------------------------
		
		// LevelSelectScreen
		int maxLevel = 999;	// TODO get from SharedPreferences
		mLevelSelectScreen = new Entity(res.cameraWidth, 0f);
		// LevelSelector
		mLevelSelectScreen.attachChild(new LevelSelector(maxLevel, MainMenu.this));
				
		// Create a backButton
		// TODO Scaling
		float padding = 20 * res.cameraScaleX;
		ButtonSprite backButton = new ButtonSprite(
				padding + (res.buttonBackTiledTextureRegion.getWidth() / 4),
				padding + (res.buttonBackTiledTextureRegion.getHeight() / 2),
				res.buttonBackTiledTextureRegion.getTextureRegion(0), 
				res.buttonBackTiledTextureRegion.getTextureRegion(1), 
				res.vbom);
//		Text backButtonText = new Text(0,0,res.fontDefault32Bold,"BACK", res.vbom);
//		backButtonText.setPosition(backButton.getWidth()/2, backButton.getHeight()/2);
//		optionsButton.attachChild(backButtonText);
		mLevelSelectScreen.attachChild(backButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Show the MainMenuScreen
				MainMenu.this.mMainMenuScreen.registerEntityModifier(new MoveModifier(0.25f, MainMenu.this.mMainMenuScreen.getX(), MainMenu.this.mMainMenuScreen.getY(), 0f, 0f));
				MainMenu.this.mLevelSelectScreen.registerEntityModifier(new MoveModifier(0.25f, MainMenu.this.mLevelSelectScreen.getX(), MainMenu.this.mLevelSelectScreen.getY(), res.cameraWidth, 0f));
				
				res.clickSound.play();
			}
		});
		this.registerTouchArea(backButton);
		
		this.attachChild(mLevelSelectScreen);
		
	}
	
	/* *************************************************************************
	 * Helper Functions
	 */
	
	/**
	 * 
	 */
	private void createBackground() {
		mBackground = new Sprite(res.cameraWidth / 2f, res.cameraHeight / 2f, res.backgroundMenuTextureRegion, res.vbom);
		// TODO Scaling
		mBackground.setScaleX(res.cameraWidth);
		mBackground.setScaleY(res.cameraHeight / res.backgroundMenuTextureRegion.getHeight());
		mBackground.setZIndex(-9999);
		this.attachChild(mBackground);		
	}

	/**
	 *  Create clouds that move from one side of the screen to the other, and repeat.
	 */
	private void createMovingClouds() {
		
		Sprite[] cloudSprites = new Sprite[20];
		for(Sprite curCloudSprite : cloudSprites) {
			ITextureRegion texture = res.cloudTextureRegion;	// TM: ersetzte 'this' durch 'texture', weil sich 'this' auf MainMenu bezieht, statt auf den Sprite
			// TODO Scaling
			curCloudSprite = new Sprite(
					MathUtils.random(-(texture.getWidth() * this.getScaleX()) / 2, res.cameraWidth + (texture.getWidth() * this.getScaleX()) / 2),
					MathUtils.random(-(texture.getHeight() * this.getScaleY()) / 2, res.cameraHeight + (texture.getHeight() * this.getScaleY()) / 2),
					res.cloudTextureRegion,
					res.vbom) {
				private float XSpeed = MathUtils.random(0.2f, 2f);
				private boolean initialized = false;
				
				@Override
				protected void onManagedUpdate(final float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					if (!initialized) {
						initialized = true;
						this.setScale(XSpeed / 2);
						this.setZIndex(-4000 + Math.round(XSpeed * 1000f));
						MainMenu.getInstance().sortChildren();
					}
					if (this.getX() < -(this.getWidth() * this.getScaleX()) / 2) {
						XSpeed = MathUtils.random(0.2f, 2f);
						this.setScale(XSpeed / 2);
						this.setPosition(res.cameraWidth + (this.getWidth() * this.getScaleX()) / 2, MathUtils.random(-(this.getHeight() * this.getScaleY()) / 2, res.cameraHeight + (this.getHeight() * this.getScaleY()) / 2));
						
						this.setZIndex(-4000 + Math.round(XSpeed * 1000f));
						MainMenu.getInstance().sortChildren();
					}
					this.setPosition(this.getX() - (XSpeed * (pSecondsElapsed / 0.016666f)), this.getY());
				}
			};
			this.attachChild(curCloudSprite);
		}
	}
	
	@Override
	public void onShowScene() {
	}
	
	@Override
	public void onHideScene() {
	}
	
	@Override
	public void onUnloadScene() {
	}
}
