package com.lzq.lianliankan2_3_3_v1_0.activity;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.lzq.lianliankan2_3_3_v1_0.R;
import com.lzq.lianliankan2_3_3_v1_0.conf.GameConf;
import com.lzq.lianliankan2_3_3_v1_0.model.LinkInfo;
import com.lzq.lianliankan2_3_3_v1_0.model.Piece;
import com.lzq.lianliankan2_3_3_v1_0.serivce.GameService;
import com.lzq.lianliankan2_3_3_v1_0.serivce.GameServiceImpl;
import com.lzq.lianliankan2_3_3_v1_0.utils.ImageUtil;
import com.lzq.lianliankan2_3_3_v1_0.view.GameView;

public class MainGameActivity extends Activity {
	private GameConf config;
	private GameService gameService;
	private GameView gameView;
	// private Button startButton;
	private TextView timeTextView;
	private AlertDialog.Builder lostDialog;
	private AlertDialog.Builder successDialog;
	private Timer timer = new Timer();
	private int gameTime;
	private boolean isPlaying;
	SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 8);
	int dis;
	int plam;
	int plamPlay;
	int xu;
	int xuPlay;
	private Piece selected = null;
	private float volum = 0.1f;
	private boolean pictureRefresh = false;
	private int stage = -1;
	SharedPreferences sharedPreferences = null;
	SharedPreferences.Editor editor = null;
	String keyHead = "stage";
	private Button helpBtn = null;
	private int helpNum = 3;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x0123:
				timeTextView.setText(getString(R.string.last_time_label) + " "
						+ gameTime);
				gameTime--;
				if (gameTime < 0) {
					stopTimer();
					isPlaying = false;
					lostDialog.show();
					xuPlay = soundPool.play(xu, volum, volum, 0, 0, 1);
					return;
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lian_lian_kan);
		init();
	}

	private void init() {
		config = new GameConf(9, 11, 0, 0, 100000, this);
		gameView = (GameView) findViewById(R.id.gameview);
		timeTextView = (TextView) findViewById(R.id.timeText);
		helpBtn = (Button) findViewById(R.id.helpbtn);
		helpBtn.setText(getString(R.string.help_label) + " " + helpNum);
		// startButton = (Button) findViewById(R.id.startButton);
		sharedPreferences = getSharedPreferences(
				getString(R.string.preferences_key), MODE_PRIVATE);
		editor = sharedPreferences.edit();
		dis = soundPool.load(this, R.raw.dis, 1);
		plam = soundPool.load(this, R.raw.plam, 1);
		xu = soundPool.load(this, R.raw.xu, 1);
		gameService = new GameServiceImpl(this.config);
		gameView.setGameService(gameService);
		Intent it = getIntent();
		volum = it.getFloatExtra(getString(R.string.volum_key), 0.1f);
		pictureRefresh = it.getBooleanExtra(
				getString(R.string.picture_refresh_key), false);
		stage = it.getIntExtra(getString(R.string.stage_key), -1);
		gameView.setStage(stage);

		if (pictureRefresh) {
			ImageUtil.refreshImageValues();
		}

		if (ImageUtil.isEmpty()) {
			createDialog(getString(R.string.no_picture_dialog_label),
					getString(R.string.no_picture_dialog_message), -1)
					.setPositiveButton(getString(R.string.dialog_yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).show();
		} else {
			startGame(GameConf.DEFAULT_TIME);
		}
		// startButton.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// startGame(GameConf.DEFAULT_TIME);
		// }
		// });
		this.gameView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!isPlaying) {
					return false;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					gameViewTouchDown(event);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					gameViewTouchUp(event);
				}
				return true;
			}
		});
		lostDialog = createDialog(getString(R.string.lost_dialog_label),
				getString(R.string.lost_dialog_message), R.drawable.lost)
				.setPositiveButton(getString(R.string.dialog_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								soundPool.stop(xuPlay);
								startGame(GameConf.DEFAULT_TIME);
							}
						}).setCancelable(false);

		successDialog = createDialog(getString(R.string.success_dialog_label),
				getString(R.string.success_dialog_message), R.drawable.success)
				.setPositiveButton(getString(R.string.dialog_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								soundPool.stop(plamPlay);
								finish();
							}
						}).setCancelable(false);
		helpBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (helpNum >= 1) {
					selected = null;
					gameView.helpCouples();
					helpNum--;
					helpBtn.setText(getString(R.string.help_label) + " "
							+ helpNum);
				}
				if (helpNum < 1) {
					helpBtn.setEnabled(false);
				}
			}
		});
	}

	@Override
	protected void onPause() {
		stopTimer();
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (isPlaying) {
			startGame(gameTime);
		}
		super.onResume();
	}

	private void gameViewTouchDown(MotionEvent event) {
		Piece[][] pieces = gameService.getPieces();
		Map<Integer, List<Point>> existImages = gameService.getExistImages();
		float touchX = event.getX();
		float touchY = event.getY();
		Piece currentPiece = gameService.findPiece(touchX, touchY);
		if (null == currentPiece) {
			Log.d(getString(R.string.find_piece_label),
					getString(R.string.null_message));
			return;
		}
		// Log.d("findPiece",
		// String.valueOf(touchX) + " " + String.valueOf(touchY));
		this.gameView.setSelectedPiece(currentPiece);
		if (null == this.selected) {
			this.selected = currentPiece;
			this.gameView.postInvalidate();
			return;
		} else if (null != this.selected) {
			LinkInfo linkInfo = this.gameService.link(this.selected,
					currentPiece);
			if (null == linkInfo) {
				this.selected = currentPiece;
				this.gameView.postInvalidate();
			} else {
				handleSuccessLink(linkInfo, this.selected, currentPiece,
						pieces, existImages);
			}
		}
	}

	private void gameViewTouchUp(MotionEvent event) {
		this.gameView.postInvalidate();
	}

	private void startGame(int gameTime) {
		if (null != this.timer) {
			stopTimer();
		}
		this.gameTime = gameTime;
		if (gameTime == GameConf.DEFAULT_TIME) {
			gameView.startGame();
		}
		isPlaying = true;
		this.timer = new Timer();

		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0x0123);
			}
		}, 0, 1000);
		this.selected = null;
	}

	private void handleSuccessLink(LinkInfo linkInfo, Piece prePiece,
			Piece currentPiece, Piece[][] pieces,
			Map<Integer, List<Point>> existImages) {
		this.gameView.setLinkInfo(linkInfo);
		this.gameView.setSelectedPiece(null);
		this.gameView.postInvalidate();
		this.gameView.setFirstPiece(prePiece);
		this.gameView.setSecondPiece(currentPiece);
		this.gameView.move();
		this.gameView.invalidate();

		// pieces[prePiece.getIndexX()][prePiece.getIndexY()] = null;
		// pieces[currentPiece.getIndexX()][currentPiece.getIndexY()] = null;
		// List<Point> points =
		// existImages.get(prePiece.getImage().getImageId());
		// Point p1 = null;
		// Point p2 = null;
		//
		// for (int i = 0; i < points.size(); i++) {
		// Point point = points.get(i);
		// if (point.x == prePiece.getIndexX()
		// && point.y == prePiece.getIndexY()) {
		// p1 = point;
		// } else if (point.x == currentPiece.getIndexX()
		// && point.y == currentPiece.getIndexY()) {
		// p2 = point;
		// }
		// }
		// if (null != p1 && null != p2) {
		// points.remove(p1);
		// points.remove(p2);
		// if (points.isEmpty()) {
		// existImages.remove(prePiece.getImage().getImageId());
		// } else {
		// existImages.put(prePiece.getImage().getImageId(), points);
		// }
		// }
		this.selected = null;
		soundPool.play(dis, volum, volum, 0, 0, 1);
		if (!this.gameService.hasPieces()) {
			this.successDialog.show();
			plamPlay = soundPool.play(plam, volum, volum, 0, 0, 1);
			stopTimer();
			isPlaying = false;
			if (!sharedPreferences.getBoolean(keyHead + stage, false)) {
				editor.putBoolean(keyHead + stage, true);
				editor.commit();
			}
		}
	}

	private AlertDialog.Builder createDialog(String title, String message,
			int imageResource) {
		return new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setIcon(imageResource);
	}

	private void stopTimer() {
		if (null != timer) {
			this.timer.cancel();
			this.timer = null;
		}
	}

}
