package com.floppyinfant.android.game.scenes.levels.schnecki;

import java.util.Locale;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.opengl.font.Font;

import com.floppyinfant.android.game.manager.SceneManager;
import com.floppyinfant.android.game.scenes.ManagedScene;

public abstract class AbstractGameScene extends ManagedScene implements IOnSceneTouchListener {
	
	public HUD mHUD = new HUD();
	public AbstractGameScene mScene = this;

	// Loading Scene
	private Scene mLoadingScene;
	private Text mLoadingText;
	
	/* Entities */
	protected Sprite mSarahSprite;
	
	protected long startTime = System.currentTimeMillis();
	
	
	/* *************************************************************************
	 * 
	 */
	
	public AbstractGameScene() {
		this(2f);	// Let the Scene Manager know that we want to show a Loading Scene for at least 2 seconds.
	};
	
	/**
	 * 
	 * @param pLoadingScreenMinimumSecondsShown
	 */
	public AbstractGameScene(float pLoadingScreenMinimumSecondsShown) {
		super(pLoadingScreenMinimumSecondsShown);
		
		/* Touch */
		this.setOnSceneTouchListener(this);			// implements IOnSceneTouchListener {onSceneTouchEvent(...)}
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);	// needed for ButtonSprite(){onAreaTouched(...)} // mScene.registerTouchArea(buttonSprite);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		
		// Scale the Game Scene according to the Camera's scale factor.
		// TODO Scaling
//		this.setScale(res.cameraScaleX, res.cameraScaleY);
//		this.setPosition(0, res.cameraHeight / 2f);
		
		/* HUD (heads up display) */
//		mHUD = new HUD();
//		mCamera.setHUD(mHUD);
//		mHUD.setScaleCenter(0f, 0f);
		// TODO Scaling
//		mHUD.setScale(res.cameraScaleX, res.cameraScaleY);
		
		/* Controller */
		/* @see chapter 4 */
		/* @see /AndEngineExamples/src/org/andengine/examples/AnalogOnScreenControlExample.java */
		
	}
	
	/* *************************************************************************
	 * 
	 */
	
	/**
	 * Setup and return the loading screen.
	 */
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		
		mLoadingScene = new Scene();
		mLoadingScene.setBackgroundEnabled(true);
		
		mLoadingText = new Text(0, 0, res.fontHarting96, "Loading...", res.engine.getVertexBufferObjectManager());
		//mLoadingText.setPosition(mLoadingText.getWidth() / 2f, res.cameraHeight - mLoadingText.getHeight() / 2f);
		mLoadingText.setPosition(res.cameraWidth / 2f, res.cameraHeight / 2f);
		mLoadingScene.attachChild(mLoadingText);
		
		return mLoadingScene;
	}
	
	/**
	 * Detach the loading screen resources.
	 */
	@Override
	public void onLoadingScreenUnloadAndHidden() {
		
		mLoadingText.detachSelf();
		mLoadingText = null;
		
		mLoadingScene = null;
	}

	/* *************************************************************************
	 * 
	 */
	
	/**
	 * Load the resources to be used in the Game Scenes.
	 */
	@Override
	public void onLoadScene() {
		
		// Resources
		res.loadWorld001();
		
		createBackground();
		createHUD();
			
	}
	
	private void createBackground() {
		/* Background */
		Sprite background = new Sprite(
				res.cameraWidth / 2f, 
				res.cameraHeight / 2f, 
				res.backgroundMarTextureRegion, 
				res.engine.getVertexBufferObjectManager());
		// TODO Scaling
//		background.setScale(res.cameraScaleX, res.cameraScaleY);
		this.attachChild(background);
		
		/*
		// (Auto)ParallaxBackground
		// side-scrolling game (or just for clouds constantly passing by)
		float autoParallaxSpeed = 3; // change per second
		AutoParallaxBackground background = new AutoParallaxBackground(0f, 0f, 1.0f, autoParallaxSpeed);
		// ...or bound to camera position
		ParallaxBackground background = new ParallaxBackground(0f, 0f, 1.0f) {
			float cameraPreviousX = 0;
			//float cameraPreviousY = 0;
			float parallaxValueOffset = 0;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				float cameraCurrentX = res.engine.getCamera().getCenterX();
				// if the cameras position has changed since last update...
				if (cameraPreviousX != cameraCurrentX) {
					parallaxValueOffset += cameraCurrentX - cameraPreviousX;
					this.setParallaxValue(parallaxValueOffset);
				}
				cameraPreviousX = cameraCurrentX;
				super.onUpdate(pSecondsElapsed);
			}
		};
		background.attachParallaxEntity(new ParallaxEntity(5, far));
		background.attachParallaxEntity(new ParallaxEntity(10, mid));
		background.attachParallaxEntity(new ParallaxEntity(15, close));
		this.setBackground(background);
		this.setBackgroundEnabled(true);
		*/
		
	}

	private void createHUD() {
		/* HUD */
		// TODO get the button from the MainMenu
		// Setup the HUD Buttons and Button Texts.
		ButtonSprite MainMenuButton = new ButtonSprite(
				0f, 
				0f, 
				res.buttonOptionsTiledTextureRegion.getTextureRegion(0), 
				res.buttonOptionsTiledTextureRegion.getTextureRegion(1), 
				res.engine.getVertexBufferObjectManager());
		// TODO Scaling
//		MainMenuButton.setScale(1 / res.cameraScaleX, 1 / res.cameraScaleY);
		MainMenuButton.setPosition((MainMenuButton.getWidth() * MainMenuButton.getScaleX()) / 2f, (MainMenuButton.getHeight() * MainMenuButton.getScaleY()) / 2f);
		MainMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				res.clickSound.play();
				SceneManager.getInstance().showMainMenu();
			}
		});
//		Text MainMenuButtonText = new Text(MainMenuButton.getWidth() / 2, MainMenuButton.getHeight() / 2,res.fontDefault32Bold, "MENU", res.engine.getVertexBufferObjectManager());
//		MainMenuButton.attachChild(MainMenuButtonText);
		mHUD.attachChild(MainMenuButton);
		mHUD.registerTouchArea(MainMenuButton);
		
		
		ButtonSprite OptionsButton = new ButtonSprite(0f,0f, 
				res.buttonTiledTextureRegion.getTextureRegion(0), 
				res.buttonTiledTextureRegion.getTextureRegion(1), 
				res.engine.getVertexBufferObjectManager());
		// TODO Scaling
//		OptionsButton.setScale(1 / res.cameraScaleX, 1 / res.cameraScaleY);
		OptionsButton.setPosition(res.cameraWidth - ((OptionsButton.getWidth() * OptionsButton.getScaleX()) / 2f), (OptionsButton.getHeight() * OptionsButton.getScaleY()) / 2f);
		OptionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				res.clickSound.play();
				SceneManager.getInstance().showOptionsLayer(true);
			}
		});
		Text OptionsButtonText = new Text(0, 0, res.fontDefault32Bold, "OPTIONS", res.engine.getVertexBufferObjectManager());
		OptionsButtonText.setPosition((OptionsButton.getWidth()) / 2, (OptionsButton.getHeight()) / 2);
		OptionsButton.attachChild(OptionsButtonText);
		mHUD.attachChild(OptionsButton);
		mHUD.registerTouchArea(OptionsButton);
		
		
		/* Text Time */
		Font font = res.fontDefault24Normal;
		font.prepareLetters("Time: 1234567890".toCharArray());
		
		final String TIME_PREFIX = "Time: ";
		final String TIME_FORMAT = "00:00:00";
		final int TIME_MAX_CHAR_COUNT = TIME_PREFIX.length() + TIME_FORMAT.length();
		
		Text timeText = new Text(0, 0, font, TIME_PREFIX + TIME_FORMAT, TIME_MAX_CHAR_COUNT, res.engine.getVertexBufferObjectManager()) {

			//long startTime = System.currentTimeMillis();
			long lastSecond = 0;
			
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				long second = System.currentTimeMillis();
				if (lastSecond != second) {
					lastSecond = second;
					
					long now = System.currentTimeMillis() - startTime;
					long hour = now / (1000*60*60);
					long min = (now % (1000*60*60)) / (1000*60);
					long sec = ((now % (1000*60*60)) % (1000*60)) / 1000;
					
					// build a new String with the current time
					String timeSuffix = String.format(Locale.US, "%02d:%02d:%02d", hour, min, sec);
					this.setText(TIME_PREFIX + timeSuffix);
					this.setX(res.cameraWidth - this.getWidth() / 2f);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		
		timeText.setColor(0, 1.0f, 1.0f);
		timeText.setY(res.cameraHeight - timeText.getHeight() / 2);
		this.attachChild(timeText);

	}
	
	@Override
	public void onShowScene() {
		res.engine.getCamera().setHUD(mHUD);
	}
	
	@Override
	public void onHideScene() {
		res.engine.getCamera().setHUD(null);
	}
	
	@Override
	public void onUnloadScene() {
		// detach and unload the scene.
		res.engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				mScene.detachChildren();
				mScene.clearEntityModifiers();
				mScene.clearTouchAreas();
				mScene.clearUpdateHandlers();
			}
		});
	}
	
	/* *************************************************************************
	 * 
	 */
	
	/**
	 * AndEngine: Touch Event Handling<br />
	 * <br />
	 * 
	 * IMPLEMENTATION:<br />
	 * class ... implements <b>IOnSceneTouchListener</b><br />
	 * onCreateScene() {mScene.<b>setOnSceneTouchListener(this)</b>;}<br />
	 * <br />
	 * 
	 * CODE SNIPPET:<br />
	 * rotateModel(gl);<br />
	 * // update rotations<br />
	 * delta = currentMillis - lastMillis;<br />
	 * dx += dxSpeed * delta;<br />
	 * dampenSpeed(delta){dxSpeed *= (1.0f - 0.001f * delta);}<br />
	 * <br />
	 * 
	 * @see /OpenGLdemos/src/ro/brite/android/nehe18/GlApp.java
	 * @see /OpenGLdemos/src/ro/brite/android/nehe18/GlRenderer.java#onDraw(GL10 gl){// update rotations}
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		/* Pinch-zoom camera */
		//mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);	// pass scene touch events to the pinch zoom detector
		
		/* onTouchEvent */
		if (pSceneTouchEvent.isActionMove()) {
			mSarahSprite.setPosition(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			return true;
		} else if (pSceneTouchEvent.isActionDown()) {
			
			//return true;
		}
		return false;
	}
	
}
