package com.lzq.lianliankan2_3_3_v1_0.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.lzq.lianliankan2_3_3_v1_0.R;
import com.lzq.lianliankan2_3_3_v1_0.conf.GameConf;

public class ListCropPicturesActivity extends Activity {
	private GridView gridView = null;
	private File[] files = null;
	public static final int CROP_MSG = 100;
	private boolean pictureRefresh = false;
	private CropPictureAdaptar cropPictureAdaptar = new CropPictureAdaptar(this);
	static {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		init();
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_list_crop_pictures);

		gridView = (GridView) findViewById(R.id.listcroppicture);
		gridView.setAdapter(cropPictureAdaptar);
		gridView.setOnItemClickListener(new OnItemClickListenerImpl());

	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) { // 选项单击事件
			ImageView showImg = new ImageView(ListCropPicturesActivity.this); // 定义图片组件
			showImg.setScaleType(ImageView.ScaleType.CENTER); // 居中显示
			showImg.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); // 布局参数
			final File f = (File) ListCropPicturesActivity.this.cropPictureAdaptar
					.getItem(position); // 取出Map
			Bitmap bm = BitmapFactory.decodeFile(getFilesDir().toString() + "/"
					+ f.getName());
			showImg.setImageBitmap(bm);
			Dialog dialog = new AlertDialog.Builder(
					ListCropPicturesActivity.this) // 创建Dialog
					// .setIcon(R.drawable.pic_m) // 设置显示图片
					.setTitle("查看图片") // 设置标题
					.setView(showImg) // 设置组件
					.setPositiveButton("删除",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									f.delete();
									pictureRefresh = true;
									refreshFiles();
									gridView.invalidateViews();
								}
							}).setNegativeButton("关闭", // 设置取消按钮
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create(); // 创建对话框
			dialog.show(); // 显示对话框

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_crop_pictures, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode && CROP_MSG == requestCode) {
			refreshFiles();
			gridView.invalidateViews();
			pictureRefresh = true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.createcroppicture:
			Intent intent = new Intent(ListCropPicturesActivity.this,
					MakePictureActivity.class);
			intent.putExtra("baseFileName", "ci");
			intent.putExtra("mOutputX", GameConf.PIECE_WIDTH);
			intent.putExtra("mOutputY", GameConf.PIECE_HEIGHT);
			startActivityForResult(intent, CROP_MSG);
			break;
		}
		return false;
	}

	private void init() {
		File parent = getFilesDir();
		files = parent.listFiles();
		cropPictureAdaptar.notifyDataSetChanged();
	}

	public File[] getFiles() {
		if (null == files) {
			init();
		}
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	private void refreshFiles() {
		File parent = getFilesDir();
		files = parent.listFiles();
	}

	class CropPictureAdaptar extends BaseAdapter {
		private Context context;

		public CropPictureAdaptar(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return getFiles().length;
		}

		@Override
		public Object getItem(int position) {
			return getFiles()[position];
		}

		@Override
		public long getItemId(int position) {
			int id = -1;
			File f = getFiles()[position];
			if (f.getName().startsWith("ci")) {
				id = Integer.valueOf(f.getName().split("[-|.]")[1]);
			}
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			ImageView imageView;
			imageView = new ImageView(context);
			// 设置显示图片的大小
			Bitmap bm = BitmapFactory.decodeFile(getFilesDir().toString() + "/"
					+ getFiles()[position].getName());
			imageView.setImageBitmap(bm);
			imageView.setLayoutParams(new GridView.LayoutParams(
					dm.widthPixels / 3, dm.widthPixels / 3));
			imageView.setScaleType(ImageView.ScaleType.CENTER);
			return imageView;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (pictureRefresh) {
				setResult(RESULT_OK);
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
