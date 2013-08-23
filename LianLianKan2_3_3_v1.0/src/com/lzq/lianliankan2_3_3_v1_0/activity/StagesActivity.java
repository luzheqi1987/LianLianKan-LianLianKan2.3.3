package com.lzq.lianliankan2_3_3_v1_0.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lzq.lianliankan2_3_3_v1_0.R;

public class StagesActivity extends Activity {
	SharedPreferences sharedPreferences = null;
	GridView stagesView = null;
	int maxStage; // 最大的关卡数
	Map<String, Integer> resourceValues = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stage_select);

		Field[] drawableFields = R.drawable.class.getFields();
		sharedPreferences = getSharedPreferences(
				getString(R.string.preferences_key), MODE_PRIVATE);
		maxStage = sharedPreferences.getInt(getString(R.string.max_stage_key),
				getResources().getInteger(R.integer.default_stages));

		try {
			for (Field field : drawableFields) {
				if (field.getName().startsWith(
						getString(R.string.stage_picture_head))) {
					resourceValues.put(field.getName(),
							field.getInt(R.drawable.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showStages();
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		private int newerStage = -1;

		public OnItemClickListenerImpl(int stage) {
			newerStage = stage;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) { // 选项单击事件
			if (position >= newerStage) {
				return;
			}
			Intent intent = new Intent(StagesActivity.this,
					MainGameActivity.class);
			Intent it = getIntent();
			intent.putExtra(
					getString(R.string.volum_key),
					(float) sharedPreferences.getInt(
							getString(R.string.volum_key), -1) / (float) 100);
			intent.putExtra(getString(R.string.picture_refresh_key), it
					.getBooleanExtra(getString(R.string.picture_refresh_key),
							false));
			intent.putExtra(getString(R.string.stage_key), position + 1);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		showStages();
		super.onResume();
	}

	class ImageAdapter extends BaseAdapter {
		private Context context = null; // Context对象
		private List<Integer> picIds = null; // 保存所有图片资源

		public ImageAdapter(Context context, List<Integer> picIds) {
			this.context = context; // 接收Context
			this.picIds = picIds; // 保存图片资源
		}

		@Override
		public int getCount() { // 取得个数
			return this.picIds.size();
		}

		@Override
		public Object getItem(int position) { // 取得每一项的信息
			return this.picIds.get(position);
		}

		@Override
		public long getItemId(int position) { // 取得指定项的ID
			return this.picIds.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView img = new ImageView(this.context); // 定义图片视图
			img.setImageResource(this.picIds.get(position)); // 给ImageView设置资源
			img.setScaleType(ImageView.ScaleType.CENTER); // 居中显示
			return img;
		}
	}

	private void showStages() {
		int stage = 1;
		while (sharedPreferences.getBoolean(getString(R.string.stage_head)
				+ stage, false)) {
			stage++;
		}
		List<Integer> listValues = new ArrayList<Integer>();
		for (int i = 1; i <= maxStage; i++) {
			if (i <= stage) {
				listValues.add(resourceValues
						.get(getString(R.string.stage_picture_head) + i
								+ getString(R.string.stage_picture_black)));
			} else {
				listValues.add(resourceValues
						.get(getString(R.string.stage_picture_head) + i
								+ getString(R.string.stage_picture_gray)));
			}
		}

		stagesView = (GridView) findViewById(R.id.liststages);
		stagesView.setAdapter(new ImageAdapter(this, listValues));
		stagesView.setOnItemClickListener(new OnItemClickListenerImpl(Math.min(
				stage, maxStage)));
	}
}
