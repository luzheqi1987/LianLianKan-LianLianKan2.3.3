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
	String keyHead = "stage";
	GridView stagesView = null;
	int maxStage = -1;
	Map<String, Integer> resourceValues = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stage_select);
		Field[] drawableFields = R.drawable.class.getFields();
		sharedPreferences = getSharedPreferences(
				getString(R.string.preferences_key), MODE_PRIVATE);
		maxStage = sharedPreferences.getInt(getString(R.string.max_stage_key),
				1);
		int i = 1;

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

		while (sharedPreferences.getBoolean(getString(R.string.stage_head) + i,
				false)) {
			i++;
		}
		List<Integer> listValues = new ArrayList<Integer>();
		for (int j = 1; j <= maxStage; j++) {
			if (j <= i) {
				listValues.add(resourceValues
						.get(getString(R.string.stage_picture_head) + j
								+ getString(R.string.stage_picture_black)));
			} else {
				listValues.add(resourceValues
						.get(getString(R.string.stage_picture_head) + j
								+ getString(R.string.stage_picture_gray)));
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
				long id) { // ѡ����¼�
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
		int i = 1;
		while (sharedPreferences.getBoolean(getString(R.string.stage_head) + i,
				false)) {
			i++;
		}
		List<Integer> listValues = new ArrayList<Integer>();
		for (int j = 1; j <= maxStage; j++) {
			if (j <= i) {
				listValues.add(resourceValues
						.get(getString(R.string.stage_picture_head) + j
								+ getString(R.string.stage_picture_black)));
			} else {
				listValues.add(resourceValues
						.get(getString(R.string.stage_picture_head) + j
								+ getString(R.string.stage_picture_gray)));
			}
		}

		stagesView = (GridView) findViewById(R.id.liststages);
		stagesView.setAdapter(new ImageAdapter(this, listValues));
		stagesView.setOnItemClickListener(new OnItemClickListenerImpl(Math.min(
				i, maxStage)));
		super.onResume();
	}

	class ImageAdapter extends BaseAdapter {
		private Context context = null; // Context����
		private List<Integer> picIds = null; // ��������ͼƬ��Դ

		public ImageAdapter(Context context, List<Integer> picIds) {
			this.context = context; // ����Context
			this.picIds = picIds; // ����ͼƬ��Դ
		}

		@Override
		public int getCount() { // ȡ�ø���
			return this.picIds.size();
		}

		@Override
		public Object getItem(int position) { // ȡ��ÿһ�����Ϣ
			return this.picIds.get(position);
		}

		@Override
		public long getItemId(int position) { // ȡ��ָ�����ID
			return this.picIds.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView img = new ImageView(this.context); // ����ͼƬ��ͼ
			img.setImageResource(this.picIds.get(position)); // ��ImageView������Դ
			img.setScaleType(ImageView.ScaleType.CENTER); // ������ʾ
			return img;
		}
	}

}
