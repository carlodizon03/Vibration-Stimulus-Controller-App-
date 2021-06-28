package com.dfrobot.angelo.blunobasicdemo;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity<getBundle> extends BlunoLibrary {
	private Button ConnectButton;
	private TextView TvConnectionStatus;
	private TextView tvProgressLabel,tvProgressLabel2;
	private SeekBar seeker, seeker2;
	private ToggleButton tgl_vibration, tgl_sound;
	private boolean is_sound_on = false;
	private int bpm = 60;
	private int vib_duration = 0;
	final ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
	final Handler handler = new Handler();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onCreateProcess();														//onCreate Process by BlunoLibrary

		serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

		ConnectButton = (Button) findViewById(R.id.button_connect);
		TvConnectionStatus = (TextView) findViewById(R.id.textView_Status);
		tvProgressLabel = (TextView) findViewById(R.id.textView_seekValue);
		tvProgressLabel2 = (TextView) findViewById(R.id.textView_seekValue2);
		tgl_sound = (ToggleButton) findViewById(R.id.toggle_sound);
		tgl_vibration = (ToggleButton) findViewById(R.id.toggle_vibration);
		seeker = (SeekBar) findViewById(R.id.seekerBar);
		seeker2 = (SeekBar) findViewById(R.id.seekerBar2);
		seeker.setOnSeekBarChangeListener(seekBarChangeListener);
		seeker2.setOnSeekBarChangeListener(seekBarChangeListener2);

		ConnectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					buttonScanOnClickProcess();
			}
		});

		tgl_vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean is_checked) {
				if(is_checked){
					serialSend(String.valueOf(tvProgressLabel.getText()));
					bpm = Integer.valueOf(tvProgressLabel.getText().toString());

				}
				else{
					serialSend("s");
					toneGen1.stopTone();
				}
				serialSend("\n");
			}
		});

		tgl_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean is_checked) {
				if(is_checked){
					serialSend(String.valueOf(tvProgressLabel.getText()));
					serialSend("\n");
					bpm = Integer.valueOf(tvProgressLabel.getText().toString());
					is_sound_on = true;
				}
				else{
					is_sound_on = false;
					toneGen1.stopTone();

				}

			}
		});
		}


		SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// updated continuously as the user slides the thumb
			tvProgressLabel.setText(String.valueOf(progress));
			bpm = progress;
					//send the data to the BLUNO

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// called when the user first touches the SeekBar
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// called after the user finishes moving the SeekBar
			serialSend(String.valueOf(bpm));				//send the vibration per minute data to the BLUNO
			serialSend("\n");
//


		}
	};

	SeekBar.OnSeekBarChangeListener seekBarChangeListener2 = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// updated continuously as the user slides the thumb
			tvProgressLabel2.setText(String.valueOf(progress));
			vib_duration = progress;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// called when the user first touches the SeekBar
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// called after the user finishes moving the SeekBar
			serialSend(String.valueOf(vib_duration));				//send the vibration duration data to the BLUNO
			serialSend("v");
		}
	};
	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		onPauseProcess();														//onPause Process by BlunoLibrary
	}

	protected void onStop() {
		super.onStop();
//		onStopProcess();														//onStop Process by BlunoLibrary
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		onDestroyProcess();														//onDestroy Process by BlunoLibrary
	}

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
			case isConnected:
				TvConnectionStatus.setText("Connected");
				ConnectButton.setText("Disconnect");

//			buttonScan.setText("Connected");
				//send the data to the BLUNO
				break;
			case isConnecting:
//			buttonScan.setText("Connecting");
				TvConnectionStatus.setText("Connecting");

				break;
			case isToScan:
//			buttonScan.setText("Scan");

				break;
			case isScanning:
//			buttonScan.setText("Scanning");
				TvConnectionStatus.setText("Scanning");

				break;
			case isDisconnecting:
//			buttonScan.setText("isDisconnecting");
				ConnectButton.setText("Disconnect");

				break;
			default:
				break;
		}
	}



	@Override
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
			if (theString.charAt(0) == '1') {
				if(is_sound_on) {
					toneGen1.startTone(ToneGenerator.TONE_DTMF_0);
				}
			} else {
				toneGen1.stopTone();
			}

	}


}