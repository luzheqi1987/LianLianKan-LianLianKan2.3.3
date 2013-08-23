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
	private Button cropPictureBtn = null; // ����ͼƬ��ť
	private Button startBtn = null;// ��ʼ��Ϸ��ť
	private Button setBtn = null;// ���ð�ť
	private AudioManager aManager = null;// audio����ʵ��
	private SharedPreferences sharedPreferences = null; // �����ļ�
	private SharedPreferences.Editor sharedPreferencesEditor = null;// �����ļ�editor
	private SeekBar volumBar = null;// ����������
	private int currentVolum;// ��ǰ����
	private boolean pictureRefresh = false; // ͼƬ�Ƿ��и���
	private ImageView gameName = null; // ��Ϸ����
	private Animation anmi = null;// ��Ϸ���ƶ���
	private Animation reverse = null;// ��Ϸ�����涯��
	private AlertDialog settingDialog = null; // ���ý���

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_start);

		final LinearLayout setLayout = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.activity_set, null); // ���ý���Ĳ���
		startBtn = (Button) findViewById(R.id.startBtn);
		setBtn = (Button) findViewById(R.id.setBtn);
		cropPictureBtn = (Button) findViewById(R.id.makePicBtn);
		aManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		gameName = (ImageView) findViewById(R.id.gamename);
		volumBar = (SeekBar) setLayout.findViewById(R.id.volum);

		initGameConf(); // ��ʼ����Ϸ����

		settingDialog = new AlertDialog.Builder(MenuActivity.this)
				.setTitle(getString(R.string.setting))
				.setView(setLayout)
				.setPositiveButton(getString(R.string.dialog_ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								currentVolum = volumBar.getProgress();
								sharedPreferencesEditor.putInt(
										getString(R.string.volum_key),
										currentVolum);
								sharedPreferencesEditor.commit();
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

		cropPictureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this,
						CropPicturesActivity.class);
				startActivityForResult(intent,
						getResources().getInteger(R.integer.list_msg));
			}
		});

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == getResources().getInteger(
						R.integer.game_name_anmi)) {
					gameName.startAnimation(anmi);
				}
				if (msg.what == getResources().getInteger(
						R.integer.game_name_reverse)) {
					gameName.startAnimation(reverse);
				}
			}
		};

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(getResources().getInteger(
						R.integer.game_name_anmi));
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(getResources().getInteger(
								R.integer.game_name_reverse));
					}
				}, getResources().getInteger(R.integer.game_name_act_time) / 2);
			}
		}, 0, getResources().getInteger(R.integer.game_name_act_time));
	}

	/**
	 * ��ʼ����Ϸ����
	 */
	private void initGameConf() {
		initBaseInfo();
		initGameNameAnmi();
		initSharedPreferences();
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	private void initBaseInfo() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// float density = dm.density; // ��Ļ�ܶȣ����ر�����0.75/1.0/1.5/2.0��
		// int densityDPI = dm.densityDpi; // ��Ļ�ܶȣ�ÿ�����أ�120/160/240/320��
		// density = dm.density; // ��Ļ�ܶȣ����ر�����0.75/1.0/1.5/2.0��
		int densityDPI = dm.densityDpi; // ��Ļ�ܶȣ�ÿ�����أ�120/160/240/320��
		GameConf.init(densityDPI, getFilesDir().toString());

		anmi = AnimationUtils.loadAnimation(this, R.anim.anmi);
		anmi.setFillAfter(true);
		reverse = AnimationUtils.loadAnimation(this, R.anim.reverse);
		reverse.setFillAfter(true);
	}

	/**
	 * ��ʼ����Ϸ���ƶ���
	 */
	private void initGameNameAnmi() {
		anmi = AnimationUtils.loadAnimation(this, R.anim.anmi);
		anmi.setFillAfter(true);
		reverse = AnimationUtils.loadAnimation(this, R.anim.reverse);
		reverse.setFillAfter(true);
	}


	/**
	 * ��ʼ�������ļ�
	 */
	private void initSharedPreferences() {
		sharedPreferences = getSharedPreferences(
				getString(R.string.preferences_key), MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();

		currentVolum = sharedPreferences.getInt(getString(R.string.volum_key),
				getResources().getInteger(R.integer.error_num));
		if (getResources().getInteger(R.integer.error_num) == currentVolum) {
			currentVolum = aManager.getStreamVolume(AudioManager.STREAM_MUSIC);;
			sharedPreferencesEditor.putInt(getString(R.string.volum_key),
					currentVolum);
			sharedPreferencesEditor.commit();
		} 
		volumBar.setProgress(currentVolum);

		if (getResources().getInteger(R.integer.error_num) == sharedPreferences
				.getInt(getString(R.string.max_stage_key), getResources()
						.getInteger(R.integer.error_num))) {
			sharedPreferencesEditor.putInt(getString(R.string.max_stage_key),
					getResources().getInteger(R.integer.max_stage));
			sharedPreferencesEditor.commit();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode
				&& getResources().getInteger(R.integer.list_msg) == requestCode) {
			pictureRefresh = true; // ͼƬ���Ѿ�����
		}
	}
}
