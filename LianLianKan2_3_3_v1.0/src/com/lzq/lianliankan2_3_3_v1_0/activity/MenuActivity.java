package com.lzq.lianliankan2_3_3_v1_0.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.lzq.lianliankan2_3_3_v1_0.R;
import com.lzq.lianliankan2_3_3_v1_0.conf.GameConf;

public class MenuActivity extends Activity {
	public static final int LIST_MSG = 10;
	public static final int MAX_STAGE = 3;
	// public static final String PREFERENCES_KEY = "linkproperty";
	// public static final String MAX_STAGE_KEY = "maxStage";
	// public static final String VOLUM_KEY = "volum";
	// public static final String PICTURE_REFRESH_KEY = "pictureRefresh";
	// public static final String OK = "ok";
	// public static final String CANCEL = "Cancel";

	Button makePictureBtn = null;
	Button startBtn = null;
	Button setBtn = null;
	AudioManager aManager = null;
	SharedPreferences sharedPreferences = null;
	SharedPreferences.Editor editor = null;
	SeekBar volumBar = null;
	int currentVolum = -1;
	boolean pictureRefresh = false;
	ImageView title = null;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initGameConf();
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_start);
		startBtn = (Button) findViewById(R.id.startBtn);
		setBtn = (Button) findViewById(R.id.setBtn);
		makePictureBtn = (Button) findViewById(R.id.makePicBtn);
		aManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		title = (ImageView) findViewById(R.id.title);

		final Animation anmi = AnimationUtils.loadAnimation(this, R.anim.anmi);
		anmi.setFillAfter(true);
		final Animation reverse = AnimationUtils.loadAnimation(this,
				R.anim.reverse);
		reverse.setFillAfter(true);

		final LinearLayout setLayout = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.activity_set, null);

		volumBar = (SeekBar) setLayout.findViewById(R.id.volum);

		int systemVolum = aManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		initSharedPreferences(systemVolum);

		final AlertDialog settingDialog = new AlertDialog.Builder(
				MenuActivity.this)
				.setTitle(getString(R.string.setting))
				.setView(setLayout)
				.setPositiveButton(getString(R.string.dialog_ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								currentVolum = volumBar.getProgress();
								editor.putInt(getString(R.string.volum_key),
										currentVolum);
								editor.commit();
							}
						})
				.setNegativeButton(getString(R.string.dialog_cancel), null)
				.create();

		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this,
						StagesActivity.class);
				intent.putExtra(getString(R.string.volum_key),
						(float) currentVolum / (float) 100);
				intent.putExtra(getString(R.string.picture_refresh_key),
						pictureRefresh);
				pictureRefresh = false;
				startActivity(intent);
			}
		});

		setBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				settingDialog.show();
			}
		});

		makePictureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this,
						CropPicturesActivity.class);
				startActivityForResult(intent, LIST_MSG);
			}
		});

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					title.startAnimation(anmi);
				}
				if (msg.what == 0x124) {
					title.startAnimation(reverse);
				}
			}
		};

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(0x123);
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(0x124);
					}
				}, 500);
			}
		}, 0, 1000);
	}

	/**
	 * @param systemVolum
	 */
	private void initSharedPreferences(int systemVolum) {
		sharedPreferences = getSharedPreferences(
				getString(R.string.preferences_key), MODE_PRIVATE);
		editor = sharedPreferences.edit();

		currentVolum = sharedPreferences.getInt(getString(R.string.volum_key),
				-1);
		if (-1 == currentVolum) {
			currentVolum = systemVolum;
			editor.putInt(getString(R.string.volum_key), currentVolum);
			editor.commit();
			volumBar.setProgress(currentVolum);
		} else {
			volumBar.setProgress(currentVolum);
		}

		if (-1 == sharedPreferences.getInt(getString(R.string.max_stage_key),
				-1)) {
			editor.putInt(getString(R.string.max_stage_key), MAX_STAGE);
			editor.commit();
		}
	}

	private void initGameConf() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		@SuppressWarnings("unused")
		float density = dm.density; // ÆÁÄ»ÃÜ¶È£¨ÏñËØ±ÈÀý£º0.75/1.0/1.5/2.0£©
		int densityDPI = dm.densityDpi; // ÆÁÄ»ÃÜ¶È£¨Ã¿´çÏñËØ£º120/160/240/320£©
		density = dm.density; // ÆÁÄ»ÃÜ¶È£¨ÏñËØ±ÈÀý£º0.75/1.0/1.5/2.0£©
		densityDPI = dm.densityDpi; // ÆÁÄ»ÃÜ¶È£¨Ã¿´çÏñËØ£º120/160/240/320£©
		GameConf.init(densityDPI, getFilesDir().toString());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode && LIST_MSG == requestCode) {
			pictureRefresh = true;
		}
	}
}
