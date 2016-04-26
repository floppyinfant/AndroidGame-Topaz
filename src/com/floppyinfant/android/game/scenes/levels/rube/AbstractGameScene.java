package com.floppyinfant.android.game.scenes.levels.rube;

import java.util.Locale;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.Particle;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.GravityParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.IParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.floppyinfant.android.game.R;
import android.hardware.SensorManager;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.floppyinfant.android.game.entities.ICollidableEntity;
import com.floppyinfant.android.game.manager.ResourceManager;
import com.floppyinfant.android.game.manager.SceneManager;
import com.floppyinfant.android.game.scenes.ManagedScene;

public abstract class AbstractGameScene extends ManagedScene implements IAccelerationListener, IOnSceneTouchListener, IPinchZoomDetectorListener, ContactListener {
	
	protected long startTime = System.currentTimeMillis();
	
	public HUD mHUD;
	public AbstractGameScene mScene = this;
	
	// TODO Player mPlayer
	protected Sprite mPlayer;
	
	// Loading Scene
	private Scene mLoadingScene;
	private Text mLoadingText;
	
	/* ZoomCamera */
	protected PinchZoomDetector mPinchZoomDetector;
	protected float mInitialPinchZoomFactor;
	protected static final float MIN_ZOOM_FACTOR = 0.125f;
	protected static final float MAX_ZOOM_FACTOR = 2.0f;
	
	/* ParticleSystem */
	protected PointParticleEmitter mParticleEmitter;
	protected BatchedSpriteParticleSystem mParticleSystem;

	/* AnalogOnScreenControl */
	protected AnalogOnScreenControl mController;
	
	
	/* *************************************************************************
	 * Constructors
	 */
	
	public AbstractGameScene() {
		// Let the Scene Manager know that we want to show a Loading Scene for at least 2 seconds.
		this(2f);
	}
	
	/**
	 * Initialization
	 * 
	 * Code from BaseGameActivity::onCreateScene(...)
	 * Code from BaseGameActivity::onPopulateScene(...)
	 * moved here and to GameScene::onLoadScene()
	 * 
	 * @param pLoadingScreenMinimumSecondsShown
	 */
	public AbstractGameScene(float pLoadingScreenMinimumSecondsShown) {
		super(pLoadingScreenMinimumSecondsShown);
		
		/* Scaling */
		// Scale the Game Scene according to the Camera's scale factor.
		// TODO Scaling
		this.setScale(res.cameraScaleX, res.cameraScaleY);
//		this.setPosition(0, res.cameraHeight / 2f);
		
	}
	
	/* *************************************************************************
	 * 1) LoadingScreen
	 *    a) load and show
	 *    b) hide and unload
	 *    
	 * 2) Scene
	 *    a) load
	 *    b) show
	 *    c) hide
	 *    d) unload
	 */
	
	/**
	 * Setup and return the loading screen/ scene.
	 */
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		
		mLoadingScene = new Scene();
		mLoadingScene.setBackgroundEnabled(true);
		
		// import com.floppyinfant.android.game.R;
		String text = res.activity.getResources().getString(R.string.loading);
		mLoadingText = new Text(0, 0, res.fontHarting96, text, res.engine.getVertexBufferObjectManager());
		//mLoadingText.setPosition(mLoadingText.getWidth() / 2f, res.cameraHeight - mLoadingText.getHeight() / 2f);
		mLoadingText.setPosition(res.cameraWidth / 2f, res.cameraHeight / 2f);
		mLoadingScene.attachChild(mLoadingText);
		
		// TODO: show image or video
		
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
	
	// -------------------------------------------------------------------------
	
	/**
	 * Load the resources to be used in the Game Scenes.
	 * (Populate Scene)
	 * 
	 * Code from BaseGameActivity::onCreateScene(...)
	 * Code from BaseGameActivity::onPopulateScene(...)
	 * moved here and to Scene::Constructor
	 * 
	 */
	@Override
	public void onLoadScene() {
		
		/* Resources */
		//res.loadWorld001();
		
		/* setup | initialize: HUD, Background, Entities, Physics */
		
	}
	
	@Override
	public void onShowScene() {
		if (mHUD != null) {
			res.engine.getCamera().setHUD(mHUD);
		}
	}
	
	@Override
	public void onHideScene() {
		if (mHUD != null) {
			res.engine.getCamera().setHUD(null);
		}		
	}
	
	/**
	 * MUST be done on Update Thread!
	 * 
	 * @see Learning AndEngine, Packt, Ch. 7, p. 163
	 */
	@Override
	public void onUnloadScene() {
		
		// detach and unload the scene.
		res.engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				
				setIgnoreUpdate(true);
			    
				// clean up acceleration sensor
				if (res.sensor != null) {
					res.sensor = null;
				}
				
				// clean up physics world
//				unregisterUpdateHandler(res.physicsWorld);
//			    res.physicsWorld.clearForces();
//			    res.physicsWorld.clearPhysicsConnectors();
//			    while (res.physicsWorld.getBodies().hasNext()) {
//			    	res.physicsWorld.destroyBody(res.physicsWorld.getBodies().next());
//			    }
			    
			    // clean up camera
			    res.camera.reset();
			    res.camera.setHUD(null);
			    res.camera.setChaseEntity(null);
			    
			    // clean up scene
				mScene.detachChildren();
				mScene.clearEntityModifiers();
				mScene.clearTouchAreas();
				mScene.clearUpdateHandlers();
				
				// clean resources
				//res.unloadWorld001Resources();
				
			}
		});
	}
	
}
