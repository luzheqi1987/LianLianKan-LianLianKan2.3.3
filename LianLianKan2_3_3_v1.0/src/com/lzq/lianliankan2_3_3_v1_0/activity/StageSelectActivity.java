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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lzq.lianliankan2_3_3_v1_0.R;

public class StageSelectActivity extends Activity {
	SharedPreferences sharedPreferences = null;
	String keyHead = "stage";
	GridView stagesView = null;
	int maxStage = -1;
	Map<String, Integer> resourceValues = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stage_select);
		Field[] drawableFields = R.drawable.class.getFields();
		sharedPreferences = getSharedPreferences("linkproperty", MODE_PRIVATE);
		maxStage = sharedPreferences.getInt("maxStage", 1);
		int i = 1;

		try {
			for (Field field : drawableFields) {
				if (field.getName().startsWith("stage_")) {
					resourceValues.put(field.getName(),
							field.getInt(R.drawable.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (sharedPreferences.getBoolean(keyHead + i, false)) {
			i++;
		}
		List<Integer> listValues = new ArrayList<Integer>();
		for (int j = 1; j <= maxStage; j++) {
			if (j <= i) {
				listValues.add(resourceValues.get("stage_" + j + "_black"));
			} else {
				listValues.add(resourceValues.get("stage_" + j + "_gray"));
			}
		}

		stagesView = (GridView) findViewById(R.id.liststages);
		stagesView.setAdapter(new ImageAdapter(this, listValues));
		stagesView.setOnItemClickListener(new OnItemClickListenerImpl(Math.min(
				i, maxStage)));
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
			Intent intent = new Intent(StageSelectActivity.this,
					LianLianKanActivity.class);
			Intent it = getIntent();
			intent.putExtra("volum",
					(float) sharedPreferences.getInt("volum", -1) / (float) 100);
			intent.putExtra("pictureRefresh",
					it.getBooleanExtra("pictureRefresh", false));
			intent.putExtra("stage", position + 1);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		int i = 1;
		while (sharedPreferences.getBoolean(keyHead + i, false)) {
			i++;
		}
		List<Integer> listValues = new ArrayList<Integer>();
		for (int j = 1; j <= maxStage; j++) {
			if (j <= i) {
				listValues.add(resourceValues.get("stage_" + j + "_black"));
			} else {
				listValues.add(resourceValues.get("stage_" + j + "_gray"));
			}
		}

		stagesView = (GridView) findViewById(R.id.liststages);
		stagesView.setAdapter(new ImageAdapter(this, listValues));
		stagesView.setOnItemClickListener(new OnItemClickListenerImpl(Math.min(
				i, maxStage)));
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

}
