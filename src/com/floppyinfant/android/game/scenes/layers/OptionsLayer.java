package com.floppyinfant.android.game.scenes.layers;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import com.floppyinfant.android.game.manager.ResourceManager;
import com.floppyinfant.android.game.manager.SceneManager;
import com.floppyinfant.android.game.scenes.ManagedLayer;

public class OptionsLayer extends ManagedLayer {
	
	private static final OptionsLayer INSTANCE = new OptionsLayer();
	
	protected ResourceManager res = ResourceManager.getInstance();
	
	// Animates the layer to slide in from the top.
	IUpdateHandler SlideIn = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (OptionsLayer.getInstance().getY() > res.cameraHeight / 2f) {
				OptionsLayer.getInstance().setPosition(OptionsLayer.getInstance().getX(), Math.max(OptionsLayer.getInstance().getY() - (3600 * (pSecondsElapsed)), res.cameraHeight / 2f));
			} else {
				OptionsLayer.getInstance().unregisterUpdateHandler(this);
			}
		}
		@Override public void reset() {}
	};
	
	// Animates the layer to slide out through the top and tell the SceneManager to hide it when it is off-screen;
	IUpdateHandler SlideOut = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (OptionsLayer.getInstance().getY() < res.cameraHeight / 2f + 480f) {
				OptionsLayer.getInstance().setPosition(OptionsLayer.getInstance().getX(), Math.min(OptionsLayer.getInstance().getY() + (3600 * (pSecondsElapsed)), res.cameraHeight / 2f + 480f));
			} else {
				OptionsLayer.getInstance().unregisterUpdateHandler(this);
				SceneManager.getInstance().hideLayer();
			}
		}
		@Override public void reset() {}
	};
	
	/* *************************************************************************
	 * 
	 */
	
	public static OptionsLayer getInstance() {
		return INSTANCE;
	}

	/* *************************************************************************
	 * 
	 */
	
	@Override
	public void onLoadLayer() {
		// Create and attach a background that hides the Layer when touched.
		final float BackgroundX = 0f;
		final float BackgroundY = 0f;
		final float BackgroundWidth = 760f;
		final float BackgroundHeight = 440f;
		Rectangle smth = new Rectangle(BackgroundX, BackgroundY, BackgroundWidth, BackgroundHeight, res.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp() && pTouchAreaLocalX < this.getWidth() && pTouchAreaLocalX > 0 && pTouchAreaLocalY < this.getHeight() && pTouchAreaLocalY > 0) {
					res.clickSound.play();
					onHideLayer();
				}
				return true;
			}
		};
		smth.setColor(0f, 0f, 0f, 0.85f);
		this.attachChild(smth);
		this.registerTouchArea(smth);
		
		// Create the OptionsLayerTitle text for the Layer.
		Text OptionsLayerTitle = new Text(0, 0, res.fontDefault32Bold, "OPTIONS", res.vbom);
		OptionsLayerTitle.setPosition(0f, BackgroundHeight / 2f - OptionsLayerTitle.getHeight());
		this.attachChild(OptionsLayerTitle);
		
		// Let the player know how to get out of the blank Options Layer
		Text OptionsLayerSubTitle = new Text(0, 0, res.fontDefault32Bold, "Tap to return", res.vbom);
		OptionsLayerSubTitle.setScale(0.75f);
		OptionsLayerSubTitle.setPosition(0f, -BackgroundHeight / 2f + OptionsLayerSubTitle.getHeight());
		this.attachChild(OptionsLayerSubTitle);
		
		this.setPosition(res.cameraWidth / 2f, res.cameraHeight / 2f + 480f);
	}

	@Override
	public void onShowLayer() {
		this.registerUpdateHandler(SlideIn);
	}

	@Override
	public void onHideLayer() {
		this.registerUpdateHandler(SlideOut);
	}
	
	@Override
	public void onUnloadLayer() {
		
	}
}
