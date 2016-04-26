package com.floppyinfant.android.game.libs;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import com.floppyinfant.android.game.R;

import android.app.Activity;
import android.view.View.OnCreateContextMenuListener;

/**
 * Pure Data
 * use libpd for Android
 * 
 * @author TM
 * @see ResourceManager
 *
 */
public class PD extends Activity {
	
	PdUiDispatcher pd;
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		try {
			initPd("topaz.pd");
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
	
	@Override
	public synchronized void onPause() {
		if (pd != null) {
			PdAudio.stopAudio();
		}	
	}

	@Override
	public synchronized void onResume() {
		System.gc();
		
		if (pd != null) {
			PdAudio.startAudio(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		
		PdAudio.release();
		PdBase.release();
		
		finish(); //System.exit(0);
	}
	
	/**
	 * PureData (PD)
	 * 
	 * @see PureData:,
	 * 		{@link http://puredata.info/},
	 * 		{@link http://en.flossmanuals.net/PureData/},
	 * 		{@link http://www.youtube.com/watch?v=yKK1lwddfyM&list=PL12DC9A161D8DC5DC}
	 * 
	 * @see libPd:,
	 * 		{@link http://puredata.info/community/projects/software/libpd},
	 * 		{@link http://puredata.info/downloads/libpd},
	 * 		{@link http://libpd.cc/},
	 * 		{@link https://github.com/libpd/pd-for-android},
	 * 		Making Musical Apps - Real-time audio synthesis on Android and iOS, Peter Brinkmann, O'Reilly,
	 * 		{@link http://shop.oreilly.com/product/0636920022503.do}
	 * 
	 * -------------------------------------------------------------------------
	 * 
	 * IMPLEMENTATION:
	 * Project > Properties > Android > add Libraries: 
	 * PdCore
	 * initPd(){PdAudio.initAudio(...); PdBase.setReceiver(new PdUiDispatcher()); PdBase.openPatch(...)}
	 * onResumeGame(){PdAudio.startAudio(this);}
	 * onPauseGame(){PdAudio.stopAudio();}
	 * 
	 * triggerNote(int n) {PdBase.sendFloat("midinote", n); PdBase.sendBang("trigger");}
	 */
	public void initPd(String filename) throws IOException {
		// configure the audio glue
		int sampleRate = AudioParameters.suggestSampleRate();
		int inChannels = 0;	// needs permission android.permission.RECORD_AUDIO
		int outChannels = 2;
		PdAudio.initAudio(sampleRate, inChannels, outChannels, 8, true);
		
		pd = new PdUiDispatcher();
		PdBase.setReceiver(pd);
		
		// load the patch from res/raw/patch.zip
		File dir = getApplicationContext().getFilesDir();
		IoUtils.extractZipResource(getApplicationContext().getResources().openRawResource(R.raw.patch), dir, true);
		File patchFile = new File(dir, filename);
		PdBase.openPatch(patchFile.getAbsolutePath());
	}
	
	/**
	 * Events | Messages:
	 * PdBase.sendBang()
	 * PdBase.sendFloat()
	 * PdBase.sendList()
	 * PdBase.sendMessage()
	 * PdBase.sendSymbol()
	 * 
	 * MIDI-Events:
	 * PdBase.sendNoteOn()
	 * PdBase.sendControlChenge()
	 * PdBase.sendPitchBend()
	 * PdBase.sendProgramChange()
	 */
	public void triggerNote(int n) {
		// init synthesizer parameter
		PdBase.sendFloat("mod", 0);
		PdBase.sendFloat("cutoff", 100);
		PdBase.sendFloat("resonance", 5);
		
		PdBase.sendFloat("midinote", n);
		PdBase.sendBang("trigger");
	}
	
}
