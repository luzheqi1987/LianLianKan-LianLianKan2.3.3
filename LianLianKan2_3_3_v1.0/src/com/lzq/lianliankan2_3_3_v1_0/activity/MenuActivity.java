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
	private Button cropPictureBtn = null; // 创建图片按钮
	private Button startBtn = null;// 开始游戏按钮
	private Button setBtn = null;// 设置按钮
	private AudioManager aManager = null;// audio管理实例
	private SharedPreferences sharedPreferences = null; // 配置文件
	private SharedPreferences.Editor sharedPreferencesEditor = null;// 配置文件editor
	private SeekBar volumBar = null;// 音量调节条
	private int currentVolum;// 当前音量
	private boolean pictureRefresh = false; // 图片是否有根性
	private ImageView gameName = null; // 游戏名称
	private Animation anmi = null;// 游戏名称动画
	private Animation reverse = null;// 游戏名称逆动画
	private AlertDialog settingDialog = null; // 设置界面

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_start);

		final LinearLayout setLayout = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.activity_set, null); // 设置界面的布局
		startBtn = (Button) findViewById(R.id.startBtn);
		setBtn = (Button) findViewById(R.id.setBtn);
		cropPictureBtn = (Button) findViewById(R.id.makePicBtn);
		aManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		gameName = (ImageView) findViewById(R.id.gamename);
		volumBar = (SeekBar) setLayout.findViewById(R.id.volum);

		initGameConf(); // 初始化游戏配置

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
	 * 初始化游戏配置
	 */
	private void initGameConf() {
		initBaseInfo();
		initGameNameAnmi();
		initSharedPreferences();
	}

	/**
	 * 初始化基础信息
	 */
	private void initBaseInfo() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		// int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		// density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		GameConf.init(densityDPI, getFilesDir().toString());

		anmi = AnimationUtils.loadAnimation(this, R.anim.anmi);
		anmi.setFillAfter(true);
		reverse = AnimationUtils.loadAnimation(this, R.anim.reverse);
		reverse.setFillAfter(true);
	}

	/**
	 * 初始化游戏名称动画
	 */
	private void initGameNameAnmi() {
		anmi = AnimationUtils.loadAnimation(this, R.anim.anmi);
		anmi.setFillAfter(true);
		reverse = AnimationUtils.loadAnimation(this, R.anim.reverse);
		reverse.setFillAfter(true);
	}


	/**
	 * 初始化配置文件
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
			pictureRefresh = true; // 图片库已经更新
		}
	}
}
