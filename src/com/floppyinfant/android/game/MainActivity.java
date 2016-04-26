package com.floppyinfant.android.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.Engine;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.floppyinfant.android.game.manager.ResourceManager;
import com.floppyinfant.android.game.manager.SceneManager;
import com.floppyinfant.android.game.scenes.menus.MainMenu;

/**
 * AndEngine Game Engine
 * 
 * 
 * LINKS:
 * 
 * AndEngine:
 * http://www.andengine.org/
 * https://github.com/nicolasgramlich/AndEngine
 * https://github.com/RealFictionFactory/AndEnginePhysicsBox2DExtension/tree/GLES2-AnchorCenter
 * https://github.com/nazgee/AndEngineDebugDrawExtension
 * https://github.com/nazgee/ae-stub
 * 
 * Box2D:
 * http://box2d.org/
 * http://www.box2d.org/manual.html
 * http://www.jbox2d.org/
 * 
 * Physics Editors:
 * https://www.iforce2d.net/rube/
 * https://www.codeandweb.com/physicseditor
 * https://code.google.com/p/box2d-editor/
 * http://www.aurelienribon.com/blog/projects/physics-body-editor/
 * 
 * Loaders:
 * https://github.com/nazgee/AndEngineRubeLoaderExtension
 * https://github.com/LouisBHirst/AndEngineJb2dJson
 * http://www.iforce2d.net/b2djson/
 * https://github.com/iforce2d/b2dJson
 * https://github.com/ANDLABS-Git/AndEngine-PhysicsEditor-Extension
 * 
 * Sprites/ Graphics:
 * TexturePacker
 * Inkscape
 * Inkscape AndEngine-plugin
 * GIMP
 * GIMP Spritesheet-plugin
 * 
 * BOOKS:
 * <ul>
 * <li>AndEngine for Android - Game Development Cookbook, Jayme Schroeder, Brian Broyles, Packt Publishing<br />
 * http://www.packtpub.com/andengine-for-android-game-development-cookbook/book</li>
 * <li>Learning AndEngine, Martin Varga, 2014-09, Packt Publishing<br />
 * https://www.packtpub.com/game-development/learning-andengine</li>
 * </ul><br />
 * 
 * HOWTO (using Eclipse + ADT + SDK + NDK):<br />
 * Project > Properties > Android > add Libraries:<br /> 
 * AndEngine-GLES2-AnchorCenter, AndEnginePhysicsBox2DExtension<br />
 * <br />
 * class ... extends ...<br />
 * BaseGameActivity<br />
 * SimpleBaseGameActivity<br />
 * <br />
 * LayoutGameActivity<br />
 * SimpleLayoutGameActivity<br />
 * <br />
 * SimpleAsyncGameActivity<br />
 * <br />
 * 
 * HOWTO (using Android Studio based on IntelliJ-IDEA):<br />
 * <br />
 * 
 * PERFORMANCE TIPPS:<br />
 * Avoid Object Creation - use Object Pools, created in advance<br />
 * Avoid Getters and Setters - use public attributes<br />
 * Avoid Collections<br />
 * Avoid Interfaces - why?<br />
 * 
 * Ignoring entity updates<br />
 * diabling background window rendering<br />
 * creating sprite pools<br />
 * cutting down render time with sprite groups<br />
 * disabling rendering with entity culling<br />
 * <br />
 * 
 * 
 * (c) floppyinfant, 2013
 * 
 * @author Thorsten Mauthe
 * @since 2013-01-18
 * @version 0.1
 *
 */
public class MainActivity extends BaseGameActivity {
	
	private static int STEPS_PER_SECOND = ResourceManager.STEPS_PER_SECOND;
	
	// The resolution of the screen with which you are developing.
	private static float DESIGN_SCREEN_WIDTH_PIXELS = 960f;						// 960px
	private static float DESIGN_SCREEN_HEIGHT_PIXELS = 540f;					// 540px
	// The physical size of the screen with which you are developing: Diagonale 10,9mm (4,3") HTC Sensation
	private static float DESIGN_SCREEN_WIDTH_INCHES = 3.74f;					// 95mm
	private static float DESIGN_SCREEN_HEIGHT_INCHES = 2.126f;					// 54mm
	// Define a minimum and maximum screen resolution (to prevent cramped or overlapping screen elements).
	private static float SCREEN_MIN_WIDTH_PIXELS = 320f;
	private static float SCREEN_MIN_HEIGHT_PIXELS = 240f;		// 800x480
	private static float SCREEN_MAX_WIDTH_PIXELS = 1600f;
	private static float SCREEN_MAX_HEIGHT_PIXELS = 960f;
	
	// Smooth Camera
	private static final float CAMERA_VELOCITY_X = 10;
	private static final float CAMERA_VELOCITY_Y = 5;
	private static final float CAMERA_VELOCITY_ZOOM = 5;
	
	private SmoothCamera mCamera;
	
	private float mCameraWidth;
	private float mCameraHeight;
	private float mCameraScaleX;
	private float mCameraScaleY;
	
	private ResourceManager res = ResourceManager.getInstance();
	
	
	/* *************************************************************************
	 * AndEngine LayoutGameActivity
	 */
	/*
	@Override
	protected int getLayoutID() {
		return R.layout.main;
	}
	
	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.gameSurfaceView;
	}
	*/
	
	
	/* *************************************************************************
	 * AndEngine Lifecycle
	 */
	
	/**
	 * AndEngine: onCreateEngine(...)<br />
	 * <br />
	 * 
	 * <b>OBJECTS:</b> Engines<br />
	 * Engine<br />
	 * FixedStepEngine<br />
	 * LimitedFPSEngine<br />
	 * SingleSceneSplitScreenEngine<br />
	 * DoublesceneSplitscreenEngine<br />
	 * <br />
	 * 
	 */
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		
		/* 
		 * Logging
		 * 
		 * AndEngine Wrapper:
		 * Debug.v("This is AndEngine's Log Wrapper");
		 * 
		 * Android Logging:
		 * Log.v(TAG, "This is Android Logging");
		 * 
		 * Java Logging:
		 * java.util.logging
		 * Logger log = Logger.getLogger(MainActivity.class.getName());
		 * log.info("Java Util Logger")
		 * log4j, Logback
		 * slf4j (Simple Logging Facade)
		 * Logger log = LoggerFactory.getLogger(HelloMyLogger.class);
		 * 
		 * Debug levels: 
		 * error, warning, info, debug, verbose
		 * 
		 * Conditions: Release Code with 'DEBUG = 0'
		 * if (BuildConfig.DEBUG) {}
		 * if (ResourceManager.DEBUG) {}
		 * if (Debug.getDebugLevel().isSameOrLessThan(DebugLevel.DEBUG)) {}
		 * 
		 * @see http://android.kul.is/2013/08/tutorial-android-logging-and-logging-in.html
		 * @see http://openbook.rheinwerk-verlag.de/java7/1507_20_001.html
		 * 
		 */
		if (ResourceManager.DEBUG > 0) {
			Debug.i("Starting engine ...");
		}
		
		return new FixedStepEngine(pEngineOptions, STEPS_PER_SECOND);
	}
	
	/**
	 * AndEngine: onCreateEngineOptions(...)<br />
	 * <br />
	 * 
	 * <b>OBJECTS:</b> Cameras<br />
	 * Camera<br />
	 * BoundCamera<br />
	 * ZoomCamera<br />
	 * SmoothCamera<br />
	 * <br />
	 * 
	 * <b>METHODS:</b> Cameras<br />
	 * mCamera.setCenter(WIDTH/2, HEIGHT/2)<br />
	 * mCamera.offsetCenter(x, y)<br />
	 * <br />
	 * mCamera.isEntityVisible(entity)<br />
	 * mCamera.setChaseEntity(entity)<br />
	 * <br />
	 * mCamera.setBounds(0, 0, WIDTH * 4, HEIGHT)<br />
	 * mCamera.setBoundsEnabled(true)<br />
	 * <br />
	 * mCamera.setZoomFactor(1.5f)<br />
	 * <br />
	 * 
	 * <b>EngineOptions</b> (@see Learning AndEngine, Ch. 1, pp 30ff.):<br />
	 * FixedResolutionPolicy<br />
	 * FillResolutionPolicy<br />
	 * RelativeResolutionPolicy<br />
	 * RatioResolutionPolicy(ratio)<br />
	 * CropResolutionPolicy<br />
	 * 
	 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		// private
		calculateScaleFactors();
		createCamera();
		
		/* EngineOptions */
		EngineOptions engineOptions = new EngineOptions(
				true, 
				ScreenOrientation.LANDSCAPE_FIXED,
				/* ScreenResolutionPolicy */
				// TODO Scaling set ScreenResolutionPolicy: @see calculateScaleFactors() for Alternative
				new FillResolutionPolicy(),
				//new RatioResolutionPolicy(DESIGN_SCREEN_WIDTH_PIXELS, DESIGN_SCREEN_HEIGHT_PIXELS),
				mCamera);
		
		engineOptions.getAudioOptions().setNeedsMusic(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getRenderOptions().setDithering(true); // Turn on Dithering to smooth texture gradients.
		engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true); // Turn on MultiSampling to smooth the alias of hard-edge elements.
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON); // Set the Wake Lock options to prevent the engine from dumping textures when focus changes.
		
		return engineOptions;
	}
	
	/**
	 * Scaling
	 * used for Camera, Scene Scaling, Entity Scaling
	 * 
	 * @see AndEngine Cookbook/?
	 * @see {@link /AndroidGame/_sources/docs/cookbook/recipes/8987OS_05_Code/ApplyingSceneManager.java#onCreateEngineOptions()}
	 * @see {@link /MagneTankWithJars/src/ifl/games/runtime/MagneTankActivity.java#onCreateEngineOptions()}
	 */
	private void calculateScaleFactors() {
		
		// TODO Scaling
		
		/*
		// Alternative Way taken from an internet slideshow, @see ?
		DisplayMetrics om = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(om);
		int camWidth = 480;
		int camHeight = (int) (om.heightPixels * 480 / om.widthPixels);
		
		RatioResolutionPolicy policy = new RatioResolutionPolicy(camWidth, camHeight);
		Camera camera = new Camera(0, 0, camWidth, camHeight);
		*/
		
		// Determine the device's physical screen size.
		float actualScreenWidthInches = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().xdpi;
		float actualScreenHeightInches = getResources().getDisplayMetrics().heightPixels / getResources().getDisplayMetrics().ydpi;
		
		// TODO Scaling using FillResolutionPolicy vs. RatioResolutionPolicy
		// Set the Camera's Width & Height according to the device with which you design the game.
		mCameraWidth = 
				Math.round(
						Math.max(
								Math.min(
										DESIGN_SCREEN_WIDTH_PIXELS * (actualScreenWidthInches / DESIGN_SCREEN_WIDTH_INCHES), 
										SCREEN_MAX_WIDTH_PIXELS), 
										SCREEN_MIN_WIDTH_PIXELS));
		mCameraHeight = 
				Math.round(
						Math.max(
								Math.min(
										DESIGN_SCREEN_HEIGHT_PIXELS * (actualScreenHeightInches / DESIGN_SCREEN_HEIGHT_INCHES), 
										SCREEN_MAX_HEIGHT_PIXELS), 
										SCREEN_MIN_HEIGHT_PIXELS));
		
		mCameraScaleX = mCameraWidth / DESIGN_SCREEN_WIDTH_PIXELS;
		mCameraScaleY = mCameraHeight / DESIGN_SCREEN_HEIGHT_PIXELS;
	}
	
	/**
	 * Camera
	 *  
	 * created in onCreateEngineOptions(...)<br />
	 * <br />
	 * 
	 * <b>OBJECTS and METHODS:</b> Camera<br />
	 * SmoothCamera extends ZoomCamera extends BoundCamera extends Camera<br />
	 * <br />
	 * 
	 * <b>Camera</b>(0 ,0 ,WIDTH ,HEIGHT)<br />
	 * mCamera.setCenter(WIDTH/2, HEIGHT/2)<br />
	 * mCamera.offsetCenter(x, y)<br />
	 * <br />
	 * <b>mCamera.setChaseEntity(entity)</b><br />
	 * <br />
	 * 
	 * <b>BoundCamera</b>(0 ,0 ,WIDTH ,HEIGHT)<br />
	 * mCamera.setBounds(0, 0, WIDTH * 4, HEIGHT)<br />
	 * mCamera.setBoundsEnabled(true)<br />
	 * <br />
	 * 
	 * <b>ZoomCamera</b>(0 ,0 ,WIDTH ,HEIGHT)<br />
	 * mCamera.setZoomFactor(1.5f)  // cameraWidth=WIDTH/zoomFactor;<br />
	 * <br />
	 * 
	 * <b>SmoothCamera</b>(0 ,0 ,WIDTH ,HEIGHT ,maxVelocityX ,maxVelocityY ,maxZoomFactorChange)<br />
	 * mCamera.setCenter(WIDTH/2, HEIGHT/2)<br />
	 * mCamera.offsetCenter(x, y)<br />
	 * mCamera.setZoomFactor(z)<br />
	 * <br />
	 * mCamera.setCenterDirekt() // reset position without smoothing<br />
	 * mCamera.setZoomFactorDirect()<br />
	 * <br />
	 * 
	 * <i>Getter</i>-<b>METHODS:</b><br />
	 * mCamera.isEntityVisible(entity) // entity is outside viewport and can be recycled<br />
	 * mCamera.getXMin(), getXMax(), getYMin(), getYMax() // viewport boundaries; cameraWidth=getXMax()-getXMin()<br />
	 * mCamera.getWidth(), getHeight()<br />
	 * mCamera.getCenterX(),getCenterY()
	 * <br />
	 * 
	 */
	private void createCamera() {
		
		mCamera = new SmoothCamera(0, 0, mCameraWidth, mCameraHeight, CAMERA_VELOCITY_X, CAMERA_VELOCITY_Y, CAMERA_VELOCITY_ZOOM) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				/* move the camera position: side scrolling games */
				// @see /AndroidGame/docs/cookbook/recipes/8987OS_03_Code/UsingParallaxBackgrounds.java
				// @see /OpenGLdemos/src/ro/brite/android/nehe18/GlApp.java#onDrawFrame(GL10)
				
				super.onUpdate(pSecondsElapsed);
			}
		};
		// BoundCamera
		mCamera.setBounds(0, 0, mCameraWidth * 8, mCameraHeight * 2); // TODO: apply stitched background
		mCamera.setBoundsEnabled(true);
		
	}
	
	/**
	 * AndEngine: onCreateResources(...)<br />
	 * <br />
	 * 
	 * <b>OBJECTS:</b><br />
	 * ITextureRegion // must not exceed the maximum 1024x1024 texture size (hardware limit of some devices)<br />
	 * ITiledTextureRegion // for sprite sheets or tiled backgrounds; must follow the power-of-two dimension rule: 32x32, 64x6, 128x128, ...<br />
	 * <br />
	 * BitmapTextureAtlas<br />
	 * BuilableBitmapTextureAtlas<br />
	 * <br />
	 * AssetBitmapTexture // for RepeatingSpriteBackground<br />
	 * <br />
	 * Font, Text<br />
	 * Music, Sound<br />
	 * <br />
	 * 
	 * <b>METHODS:</b><br />
	 * BitmapTextureAtlasTextureRegionFactory.createFromAsset(...)<br />
	 * BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(...)<br />
	 * <br />
	 * TextureRegionFactory.extractFromTexture(...)<br />
	 * <br />
	 * textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));<br />
	 * textureAtlas.load()<br />
	 * <br />
	 * 
	 * @see org.andengine.ui.IGameInterface#onCreateResources(org.andengine.ui.IGameInterface.OnCreateResourcesCallback)
	 */
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		ResourceManager.getInstance().setup(this, mCameraWidth, mCameraHeight, mCameraScaleX, mCameraScaleY); 
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	/**
	 * AndEngine: onCreateScene(...)<br />
	 * <br />
	 * 
	 * <b>OBJECTS:</b><br />
	 * Scene<br />
	 * <br />
	 * 
	 * Background // just a color<br />
	 * EntityBackground<br />
	 * SpriteBackground<br />
	 * RepeatingSpriteBackground<br />
	 * PrallaxBackground, AutoParallaxBackground<br />
	 * <br />
	 * 
	 * AnalogOnScreenControl<br />
	 * DigitalOnScreenControl<br />
	 * <br />
	 * 
	 * <b>METHODS:</b> Touch Events<br />
	 * setOnSceneTouchListener(this) // implements IOnSceneTouchListener {onSceneTouchEvent(...)}<br />
	 * setTouchAreaBindingOnActionDownEnabled(true) // needed for ButtonSprite(){onAreaTouched(...)} // mScene.registerTouchArea(buttonSprite);<br />
	 * <br />
	 * 
	 * @see org.andengine.ui.IGameInterface#onCreateScene(org.andengine.ui.IGameInterface.OnCreateSceneCallback)
	 * 
	 * @see /AndroidGame/src/com/floppyinfant/android/game/scenes/levels/physics/AbstractGameScene.java
	 * @see /AndroidGame/src/com/floppyinfant/android/game/scenes/levels/physics/GameScene.java
	 */
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		
		/* 
		 * code moved to AbstractGameScene::Constructor
		 * and GameScene::Constructor
		 */
		
		mEngine.registerUpdateHandler(new FPSLogger());
		
		SceneManager.getInstance().showMainMenu();
		
		pOnCreateSceneCallback.onCreateSceneFinished(MainMenu.getInstance()); // mScene
	}
	
	/**
	 * AndEngine: onPopulateScene(...)<br />
	 * <br />
	 * 
	 * <b>OBJECTS:</b><br />
	 * Font, Text<br />
	 * Music, Sound<br />
	 * <br />
	 * OpenGL primitives: Line, Rectangle, Mesh, Gradient // DrawMode.*<br />
	 * Path<br />
	 * <br />
	 * Sprite<br />
	 * TiledSprite.setCurrentTileIndex() // e.g. toggle button<br />
	 * AnimatedSprite.animate()<br />
	 * ButtonSprite // TouchArea<br />
	 * <br />
	 * 
	 * METHODS:<br />
	 * new Entity() {onManagedUpdate(float pSecondsElapsed) {...}}<br />
	 * <br />
	 * 
	 * <b>EntityModifiers:</b><br />
	 * SequenceEntityModifier(modifier, ...)<br />
	 * ParallelEntityModifier(modifier, ...)<br />
	 * LoopEntityModifier(modifier [, ...])<br />
	 * <br />
	 * 
	 * <b>Modifiers:</b><br />
	 * MoveModifier, MoveByModifier, MoveXModifier, MoveYModifier<br />
	 * JumpModifier<br />
	 * PathModifier, CardinalSplineMoveModifier<br />
	 * <br />
	 * RotationModifier, RotationByModifier, RotationAtModifier<br />
	 * ScaleModifier, ScaleAtModifier<br />
	 * SkewModifier<br />
	 * <br />
	 * AlphaModifier, FadeInModifier, FadeOutModifier<br />
	 * ColorModifier<br />
	 * <br />
	 * DelayModifier<br />
	 * <br />
	 * 
	 * <b>Ease Functions:</b><br />
	 * new *Modifier(..., org.andengine.util.modifier.ease.*.getInstance())<br />
	 * <br />
	 * 
	 * <b>Modifiers Listeners:</b><br />
	 * new IEntityModifierListener() {onModifierStarted(){...}; onModifierFinished(){...}}<br />
	 * modifier.addModifierListener(listener);<br />
	 * <br />
	 * 
	 * @see org.andengine.ui.IGameInterface#onPopulateScene(org.andengine.entity.scene.Scene, org.andengine.ui.IGameInterface.OnPopulateSceneCallback)
	 * 
	 * @see /AndroidGame/src/com/floppyinfant/android/game/scenes/levels/physics/AbstractGameScene.java
	 * @see /AndroidGame/src/com/floppyinfant/android/game/scenes/levels/physics/GameScene.java
	 */
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
		
		/* 
		 * code moved to AbstractGameScene::onLoadScene()
		 * and GameScene::onLoadScene()
		 */
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	/* *************************************************************************
	 * Lifecycle
	 */
	
	@Override
	public synchronized void onPauseGame() {
		super.onPauseGame();
		
		if (res.music != null && res.music.isPlaying()){
			res.music.pause();
		}
		
		if (res.pd != null) {
			PdAudio.stopAudio();
		}
		
		disableAccelerationSensor();
	}

	@Override
	public synchronized void onResumeGame() {
		super.onResumeGame();
		System.gc();
		
		if(res.music != null && !res.music.isPlaying()){
			res.music.play();
		}
		
		if (res.pd != null) {
			PdAudio.startAudio(this);
		}
		
		// IAccelerationListener is implemented by the GameScenes
		if (res.sensor != null) {
			enableAccelerationSensor(res.sensor);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		PdAudio.release();
		PdBase.release();
		
		finish(); //System.exit(0);
	}
	
	/* *************************************************************************
	 * Interactivity
	 */
	
	/**
	 * Android API: Key Event Handling<br />
	 * <br />
	 */
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			/* handle back button behavior */
			
//			SceneManager.getInstance().getCurrentScene().onBackButtonPressed(); 	// implement Interface
			
			if (res.engine != null) {
				
				if (SceneManager.getInstance().isLayerShown) {
					SceneManager.getInstance().currentLayer.onHideLayer();
					
//				} else if (SceneManager.getInstance().mCurrentScene.getClass().getGenericSuperclass().equals(ManagedGameScene.class) ||	(SceneManager.getInstance().mCurrentScene.getClass().getGenericSuperclass().equals(ManagedMenuScene.class) &! SceneManager.getInstance().mCurrentScene.getClass().equals(MainMenu.class))) {
//					SceneManager.getInstance().showMainMenu();
				} else {
					SceneManager.getInstance().showMainMenu();
					//this.finish(); 	//System.exit(0);
				}
			}
			return true;
			
		} else if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
			/* handle menu button */
			
			// @see org.andengine.examples.MenuExample
			// this.mMainScene.setChildScene(this.mMenuScene, false, true, true);
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/* *************************************************************************
	 * Android API: OpenGL GLSurfaceView
	 */
	/*
	public class GLActivity extends Activity {
		
		private GLSurfaceView mGLSurfaceView;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			// Create our Preview view and set it as the content of our Activity
			mGLSurfaceView = new GLView(this);
			setContentView(mGLSurfaceView);
			
			mGLSurfaceView.requestFocus();
			mGLSurfaceView.setFocusableInTouchMode(true);
		}

		@Override
		protected void onResume() {
			super.onResume();
			mGLSurfaceView.onResume();
		}

		@Override
		protected void onPause() {
			super.onPause();
			mGLSurfaceView.onPause();
		}
	}
	
	public class GLView extends GLSurfaceView {

		private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
		private final float TRACKBALL_SCALE_FACTOR = 36.0f;

		private GLRenderer mRenderer;

		private float mPreviousX;
		private float mPreviousY;

		public GLView(Context context) {
			super(context);
			mRenderer = new GLRenderer();
			setRenderer(mRenderer);
			setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		}
		
		// ---------------------------------------------------------------------
		// Event Listener Callbacks
		// ---------------------------------------------------------------------
		
		@Override
		public boolean onTrackballEvent(MotionEvent e) {
			mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
			mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
			requestRender();
			return true;
		}

		@Override
		public boolean onTouchEvent(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			
			switch (e.getAction()) {
			case MotionEvent.ACTION_MOVE:
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				
				mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
				mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
				requestRender();
			}
			
			mPreviousX = x;
			mPreviousY = y;
			return true;
		}
	}
	
	// Renderer
	class GLRenderer implements GLSurfaceView.Renderer {
		
		private GLObject mObject;
		
		public float mAngleX;
		public float mAngleY;
		
		public GLRenderer() {
			mObject = new GLObject();
		}
		
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			
			gl.glDisable(GL10.GL_DITHER);
			
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			
			gl.glClearColor(0f, 0f, 0f, 1f);	// background color
			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			
			// Initialize the objects to be drawn here
			// initShapes();
		}
		
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			
			gl.glViewport(0, 0, width, height);
			
			float ratio = (float) width / height;
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		}
		
		public void onDrawFrame(GL10 gl) {
			// Clear screen
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			// When using GL_MODELVIEW, you must set the view point
			//GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0f);
			
			// Add Motion
			gl.glTranslatef(0, 0, -3.0f);
			gl.glRotatef(mAngleX, 0, 1, 0);
			gl.glRotatef(mAngleY, 1, 0, 0);
			
			// Draw
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			
			mObject.draw(gl);
		}
	}
	
	// A vertex shaded cube.
	class GLObject {

	    private IntBuffer   mVertexBuffer;
	    private IntBuffer   mColorBuffer;
	    private ByteBuffer  mIndexBuffer;
	    
	    public GLObject() {
	        
	    	int one = 0x10000;
	        
	        int vertices[] = {
	                -one, -one, -one,
	                 one, -one, -one,
	                 one,  one, -one,
	                -one,  one, -one,
	                -one, -one,  one,
	                 one, -one,  one,
	                 one,  one,  one,
	                -one,  one,  one,
	        };

	        int colors[] = {
	                  0,    0,    0,  one,
	                one,    0,    0,  one,
	                one,  one,    0,  one,
	                  0,  one,    0,  one,
	                  0,    0,  one,  one,
	                one,    0,  one,  one,
	                one,  one,  one,  one,
	                  0,  one,  one,  one,
	        };

	        byte indices[] = {
	                0, 4, 5,    0, 5, 1,
	                1, 5, 6,    1, 6, 2,
	                2, 6, 7,    2, 7, 3,
	                3, 7, 4,    3, 4, 0,
	                4, 7, 6,    4, 6, 5,
	                3, 0, 1,    3, 1, 2
	        };

	        // Buffers to be passed to gl*Pointer() functions
	        // must be direct, i.e., they must be placed on the
	        // native heap where the garbage collector cannot
	        // move them.
	        //
	        // Buffers with multi-byte datatypes (e.g., short, int, float)
	        // must have their byte order set to native order

	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());
	        mVertexBuffer = vbb.asIntBuffer();
	        mVertexBuffer.put(vertices);
	        mVertexBuffer.position(0);

	        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
	        cbb.order(ByteOrder.nativeOrder());
	        mColorBuffer = cbb.asIntBuffer();
	        mColorBuffer.put(colors);
	        mColorBuffer.position(0);

	        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
	        mIndexBuffer.put(indices);
	        mIndexBuffer.position(0);
	    }

	    public void draw(GL10 gl) {
	        
	    	gl.glFrontFace(gl.GL_CW);
	        
	        gl.glVertexPointer(3, gl.GL_FIXED, 0, mVertexBuffer);
	        gl.glColorPointer(4, gl.GL_FIXED, 0, mColorBuffer);
	        gl.glDrawElements(gl.GL_TRIANGLES, 36, gl.GL_UNSIGNED_BYTE, mIndexBuffer);
	    }
	}
	*/

}
