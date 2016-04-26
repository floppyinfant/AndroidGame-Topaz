package com.floppyinfant.android.game.scenes.levels.physics;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.floppyinfant.android.game.entities.ICollidableEntity;
import com.floppyinfant.android.game.entities.Player;
import com.floppyinfant.android.game.entities.EntityFactory;

public class GameScene extends AbstractGameScene {
	
	
	/**
	 * "Encapsulate what varies..."
	 * 
	 * @see com.floppyinfant.android.game.scenes.levels.demo.AbstractGameScene#AbstractGameScene()
	 * @see com.floppyinfant.android.game.scenes.levels.demo.AbstractGameScene#onLoadScene()
	 */
	@Override
	public void onLoadScene() {
		super.onLoadScene();
		
		/* 
		 * AndEngineDebugDrawExtension
		 * Learning AndEngine, PacktPub, Ch. 8 "Advanced Physics"
		 * https://github.com/nazgee/AndEngineDebugDrawExtension
		 */
		if (res.DEBUG > 0) {
			DebugRenderer dr = new DebugRenderer(res.physicsWorld, res.vbom);
		    dr.setZIndex(999);
		    attachChild(dr);
		}
		
		
	    /* Sensor */
	    res.sensor = this;
	    
	    
	    /* Actions */
		
	    // Touch
		// @see com.floppyinfant.android.game.scenes.levels.physics.AbstractGameScene.createTouchControlls()
	    //setOnSceneTouchListener(this);
		
	    // ContactListener
	    // @see com.floppyinfant.android.game.scenes.levels.physics.AbstractGameScene.createPhysicsWorld()
	    //res.physicsWorld.setContactListener(this);
	    
	    /*
	    ICollisionCallback collisionCallback = new ICollisionCallback(){
			@Override 
			onCollision() {}
		};
		CollisionHandler collisionHandler = new CollisionHandler(collisionCallback, player, enemy);
		mScene.registerUpdateHandler(collisionHandler);
	     */
	    
		// ---------------------------------------------------------------------
	    
		/* Entities */
		
		// TODO Player
		//Player player = PlayerFactory.getInstance().createPlayer();
		//attachChild(player);
		//setContactListener
		
		
		// TODO Enemies: Aliens
		
		
		// TODO Endgegner: Candyman
		
		
	    //demoSarah();
		demoSchnecki();
		
	}
	
	/* *************************************************************************
	 * Helper Methods
	 */
	
	/**
	 * ParticleSystem.setCenter(x,y)
	 * 
	 * The ParticleSystem is initialized in super class AbstractGameScene.
	 * 
	 * @param x position
	 * @param y position
	 */
	private void setParticleSystem(float x, float y) {
		mParticleEmitter.setCenter(x, y);
		mParticleSystem.setParticlesSpawnEnabled(true);
	}
	
	/**
	 * DEMO 
	 * "Esel fallen vom Himmel; Feuer flammt an dieser Stelle auf."
	 * 
	 * @param pSceneTouchEvent
	 */
	private void demoEselAndFire(TouchEvent pSceneTouchEvent) {
		
		/* Physics
		 * DynamicBodies 
		 * moved by forces*/
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		Sprite sprite = new Sprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY(), res.eselTextureRegion, res.vbom);
		sprite.setScale(0.3f);
		Body body = PhysicsFactory.createBoxBody(res.physicsWorld, sprite, BodyType.DynamicBody, FIXTURE_DEF);
		this.attachChild(sprite);
		res.physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true));
		
		
		/* ParticleSystem */
		setParticleSystem(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
	}

	/**
	 * DEMO
	 * "Schnecki dreht sich"
	 */
	private void demoSchnecki() {
		
		/* Physics
		 * KinematicBodies
		 * are not moved by forces,
		 * but by their set velocities */
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		Sprite sprite = new Sprite(res.cameraWidth, res.cameraHeight / 5, res.schneckiTextureRegion, res.vbom);
		sprite.setScale(0.4f);
		Body body = PhysicsFactory.createBoxBody(res.physicsWorld, sprite, BodyType.KinematicBody, FIXTURE_DEF);
		this.attachChild(sprite);
		res.physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true));
		// move the kinematic body
		body.setLinearVelocity(-1.5f, 0f);
		body.setAngularVelocity((float) (-Math.PI));
		
	}
	
	/**
	 * DEMO
	 * "Sarah drag and drop"
	 */
	private void demoSarah() {
		
		mPlayer = new Sprite(res.cameraWidth / 2, res.cameraHeight / 2, res.sarahTextureRegion, res.vbom);
		this.attachChild(mPlayer);
		
	}
	
	/* *************************************************************************
	 * Interactivity
	 */
	
	/**
	 * AndEngine: Sensor Event Handling<br />
	 * <br />
	 * 
	 * HOWTO:<br />
	 * class ... implements <b>IAccelerationListener</b><br />
	 * onResumeGame() {<b>enableAccelerationSensor(this)</b>;}<br />
	 * onPauseGame() {<b>disableAccelerationSensor()</b>;}<br />
	 * <br />
	 * Add Library <b>/AndEnginePhysicsBox2DExtension</b> to Project<br />
	 * <br />
	 * 
	 * @see /AndEngineExamples/src/org/andengine/examples/PhysicsExample.java
	 * 
	 * @param pAccelerationData
	 */
	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		// set gravity in physicsWorld
		if (res.physicsWorld != null) {
			final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
			res.physicsWorld.setGravity(gravity);
			Vector2Pool.recycle(gravity);
		}
		
		// update values
		res.yaw = pAccelerationData.getX();
		res.pitch = pAccelerationData.getY();
	}
	
	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// not used
	}
	
	/* *************************************************************************
	 * Android API: Sensor Event Handling<br />
	 * <br />
	 * 
	 * HOWTO:<br />
	 * class ... implements SensorEventListener<br />
	 * <b>onCreate()</b> {mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);}<br />
	 * <b>onResume()</b> {mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);}<br />
	 * <b>onPause()</b> {mSensorManager.unregisterListener(this);}<br />
	 * <br />
	 * 
	 * @see /AndroidApp/src/com/floppyinfant/android/SensorActivity.java
	 * @see /AccelerometerPlay/src/com/example/android/accelerometerplay/AccelerometerPlayActivity.java
	 * @see /RajawaliExamples/src/com/monyetmabuk/rajawali/tutorials/RajawaliAccelerometerActivity.java
	 * @see VR_Fernrohr/Orientation.java from ct OpenGL Project: http://www.heise.de/ct/inhalt/2013/24/212/
	 */
	
	/*
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// not used
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
	
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mGravity[0] = ALPHA * mGravity[0] + (1 - ALPHA) * event.values[0];
			mGravity[1] = ALPHA * mGravity[1] + (1 - ALPHA) * event.values[1];
			mGravity[2] = ALPHA * mGravity[2] + (1 - ALPHA) * event.values[2];
		}
		
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
			mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
			mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
		}
		
		// @see /RajawaliExamples/src/com/monyetmabuk/rajawali/tutorials/RajawaliAccelerometerActivity.java
		// mRenderer.setAccelerometerValues(event.values[1] - mGravity[1] * SENSITIVITY, event.values[0] - mGravity[0] * SENSITIVITY, 0);
		
		// @see VR_Fernrohr/Orientation.java
		
		float R[] = new float[9];
		float I[] = new float[9];
		boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
		if (success) {
			float orientation[] = new float[3];
			SensorManager.getOrientation(R, orientation);
			
			yaw = (float) Math.toDegrees(orientation[0]); // magnetic orientation
			yaw = (yaw + 360) % 360;
			pitch = (float) Math.toDegrees(orientation[2]);
			GLRenderer.setOrientation(yaw, pitch);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		//setContentView(R.layout.sensor);
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mGSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mGSensor, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mMSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	*/
	
	
	/**
	 * AndEngine: Touch Event Handling<br />
	 * <br />
	 * 
	 * HOWTO:<br />
	 * 1) a) class ... implements <b>IOnSceneTouchListener</b><br />
	 * Override <code>Scene.onSceneTouchEvent()</code><br />
	 * onCreateScene() {mScene.<b>setOnSceneTouchListener(this)</b>;}<br />
	 * <br />
	 * 
	 * b) as anonymous class:<br />
	 * <code>mScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
	 *		@Override
	 *		public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
	 *			// do something
	 *			return true;
	 *		}
	 * });</code><br />
	 * <br />
	 * 
	 * 2) <b>TouchArea:</b><br />
	 * Override <code>Entity.onAreaTouched(event, x, y)</code>.<br />
	 * This method must be registered: <code>Scene.registerTouchArea(this)</code><br />
	 * <br />
	 * Example:<br />
	 * ButtonSprite(){onAreaTouched(...)}<br />
	 * mScene.registerTouchArea(buttonSprite);<br />
	 * <br />
	 * 
	 * <code>setTouchAreaBindingOnActionDownEnabled(true);</code><br />
	 * <br />
	 * 
	 * @see this.createTouchControlls()
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// if this method is also implemented in AbstractGameScene, super must be explicitly called!
		//super.onSceneTouchEvent(pScene, pSceneTouchEvent);
		
		/* Pinch-Zoom */
		mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);	// pass scene touch events to the pinch zoom detector
		
		/* onTouchEvent */
		if (pSceneTouchEvent.isActionMove()) {
			
			if (mPlayer != null) {
				mPlayer.setPosition(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			}
			
			return true;
		} else if (pSceneTouchEvent.isActionDown()) {
			
			// clearEntityModifiers();
			// registerEntityModifier(new ...Modifier(...));
			
			/* Move Player */
			// player.move(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			
			// -----------------------------------------------------------------
			
			demoEselAndFire(pSceneTouchEvent);
			
			return true;
		} else if (pSceneTouchEvent.isActionUp()) {
			
			return true;
		}
		return false;
	}
	
	/* *************************************************************************
	 * Android API: Touch Event Handling<br />
	 * <br />
	 */
	/*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		}
		
		return super.onTouchEvent(event);
	}
	 */
	
	/**
	 * AndEngine: PinchZoom Detection<br />
	 * <br />
	 * 
	 * HOWTO:<br />
	 * class ... implements <b>IPinchZoomDetectorListener, IOnSceneTouchListener</b><br />
	 * onCreateEngineOptions(){... new ZoomCamera(); mCamera.setBounds...}<br />
	 * onCreateScene(){mScene.setOnSceneTouchListener(this); mPinchZoomDetector = new PinchZoomDetector(this); mPinchZoomDetector.setEnabled(true);}<br />
	 * // use a StitchedBackground<br />
	 * <br />
	 * onSceneTouchEvent(){mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);}<br />
	 * onPinchZoomStarted(){mInitialPinchZoomFactor = mCamera.getZoomFactor();}<br />
	 * onPinchZoom(){newZoomFactor = mInitialPinchZoomFactor * pZoomFactor; mCamera.setZoomFactor(newZoomFactor);}<br />
	 * onPinchZoomFinished(){//same as onPinchZoom()}<br />
	 * <br />
	 * 
	 * @param pPinchZoomDetector
	 * @param pSceneTouchEvent
	 */
	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector, TouchEvent pSceneTouchEvent) {
		mInitialPinchZoomFactor = ((SmoothCamera)res.engine.getCamera()).getZoomFactor();	// On first detection of pinch zooming, obtain the initial zoom factor
	}
	
	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {
		float newZoomFactor = mInitialPinchZoomFactor * pZoomFactor;
		if(newZoomFactor < MAX_ZOOM_FACTOR && newZoomFactor > MIN_ZOOM_FACTOR) {
			((SmoothCamera)res.engine.getCamera()).setZoomFactor(newZoomFactor);
		}
	}
	
	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {
		float newZoomFactor = mInitialPinchZoomFactor * pZoomFactor;
		if(newZoomFactor < MAX_ZOOM_FACTOR && newZoomFactor > MIN_ZOOM_FACTOR) {
			((SmoothCamera)res.engine.getCamera()).setZoomFactor(newZoomFactor);
		}
	}
	
		
	@Override
	public void beginContact(Contact contact) {
		// @see Learning AndEngine
//		if (checkContact(contact, Player.TYPE, Enemy.TYPE)) {
//			// set a flag for handling in the
//			// overriden mScene.onManagedUpdate() method
//		}
		
		// @see AndEngine Cookbook
		
	}

	@Override
	public void endContact(Contact contact) {
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
	
}
