package com.lzq.lianliankan2_3_3_v1_0.activity;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
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
import com.lzq.lianliankan2_3_3_v1_0.view.GameView;

public class LianLianKanActivity extends Activity {
	private GameConf config;
	private GameService gameService;
	private GameView gameView;
	private Button startButton;
	private TextView timeTextView;
	private AlertDialog.Builder lostDialog;
	private AlertDialog.Builder successDialog;
	private Timer timer = new Timer();
	private int gameTime;
	private boolean isPlaying;
	SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 8);
	int dis;
	private Piece selected = null;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x0123:
				timeTextView.setText("剩余时间：" + gameTime);
				gameTime--;
				if (gameTime < 0) {
					stopTimer();
					isPlaying = false;
					lostDialog.show();
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
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		Log.d("DisplayMetrics", "density=" + density + "; densityDPI="
				+ densityDPI);

		config = new GameConf(7, 8, 1, 9, 100000, densityDPI, this);
		gameView = (GameView) findViewById(R.id.gameview);
		timeTextView = (TextView) findViewById(R.id.timeText);
		startButton = (Button) findViewById(R.id.startButton);
		dis = soundPool.load(this, R.raw.dis, 1);
		gameService = new GameServiceImpl(this.config);
		gameView.setGameService(gameService);
		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startGame(GameConf.DEFAULT_TIME);
			}
		});
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
		lostDialog = createDialog("Lost", "游戏失败！重新开始", R.drawable.lost)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startGame(GameConf.DEFAULT_TIME);
					}
				});
		successDialog = createDialog("Success", "游戏胜利！重新开始", R.drawable.success)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startGame(GameConf.DEFAULT_TIME);
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
			Log.d("findPiece", "null");
			return;
		}
		Log.d("findPiece",
				String.valueOf(touchX) + " " + String.valueOf(touchY));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lian_lian_kan, menu);
		return true;
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
		pieces[prePiece.getIndexX()][prePiece.getIndexY()] = null;
		pieces[currentPiece.getIndexX()][currentPiece.getIndexY()] = null;

		List<Point> points = existImages.get(prePiece.getImage().getImageId());
		Point p1 = null;
		Point p2 = null;

		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			if (point.x == prePiece.getIndexX()
					&& point.y == prePiece.getIndexY()) {
				p1 = point;
			} else if (point.x == currentPiece.getIndexX()
					&& point.y == currentPiece.getIndexY()) {
				p2 = point;
			}
		}
		if (null != p1 && null != p2) {
			points.remove(p1);
			points.remove(p2);
			if (points.isEmpty()) {
				existImages.remove(prePiece.getImage().getImageId());
			} else {
				existImages.put(prePiece.getImage().getImageId(), points);
			}
		}

		this.selected = null;
		soundPool.play(dis, 1, 1, 0, 0, 1);
		if (!this.gameService.hasPieces()) {
			this.successDialog.show();
			stopTimer();
			isPlaying = false;
		}
	}

	private AlertDialog.Builder createDialog(String title, String message,
			int imageResource) {
		return new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setIcon(imageResource);
	}

	private void stopTimer() {
		this.timer.cancel();
		this.timer = null;
	}

}
