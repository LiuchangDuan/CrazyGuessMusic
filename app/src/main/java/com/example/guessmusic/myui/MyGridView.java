package com.example.guessmusic.myui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.guessmusic.R;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.util.Util;

import java.util.ArrayList;

public class MyGridView extends GridView {
	
	public final static int COUNTS_WORDS = 24;
	
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	
	private MyGridAdapter mAdapter;

	// ��ѡ���ֿ�ʼ����ʱ�Ķ���
	private Context mContext;
	
	private Animation mScaleAnimation;

	// WordButton���������
	private IWordButtonClickListener mWordButtonListener;

	public MyGridView(Context context, AttributeSet attributeSet) {
		
		super(context, attributeSet);
		
		mContext = context;
		
		mAdapter = new MyGridAdapter();

		// ��������������this����Ϊ��������౾���Ǽ̳�GridView�ģ���������ֱ����this
		this.setAdapter(mAdapter);
		
	}

	// �������ݣ��������ݡ����������Ǵ����洫�����ġ�
	public void updateData(ArrayList<WordButton> list) {
		
		mArrayList = list;
		
		//������������Դ
		setAdapter(mAdapter);
		
	}

	class MyGridAdapter extends BaseAdapter {
		
		public int getCount() {
			return mArrayList.size();
		}
		
		public Object getItem(int pos) {
			return mArrayList.get(pos);
		}
		
		public long getItemId(int pos) {
			return pos;
		}

		// ÿ�η��صĶ���һ��Button�ؼ���view
		public View getView(int pos, View v, ViewGroup p) {
			
			final WordButton holder;
			
			if (v == null) {
				// convertViewΪÿ��С����Button�Ĳ����ļ�
				v = Util.getView(mContext, R.layout.self_ui_gridview_item);
				
				holder = mArrayList.get(pos);

				// ����Ķ�������24��С����һ�γ���
				// ���ض���
				mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
				
				// ���ö������ӳ�ʱ��
				mScaleAnimation.setStartOffset(pos * 100);
				
				holder.mIndex = pos;

				/**
				 * �������һ����
				 * ����GridView��item��layout��android:layout_height����Ϊwrap_content
				 * ����item�߶�ʱϵͳ����֪��itemӦ�û��ƶ��
				 * ������ȡһ������̽��ȷ��item���Ƶľ���߶�
				 * �����͵��¶������һ��getView������
				 *
				 * �������ΪgetView()��position = 0�����˶��
				 * �����൱�ڵ�һ����ť�ϳ�ʼ���˶����ť���ص��ˣ�
				 * ���µ�һ����ť���֮���޷���ʧ
				 *
				 */
				if (holder.mViewButton == null) {
					holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
					holder.mViewButton.setOnClickListener(new View.OnClickListener() {
						// MyGridView�����¼� MainActivity�����߼�

						// �������߼�����Ҫ����������ʵ�֣���������GridView��ʵ�֡�
						// ��������������ʹ�á��۲���ģʽ�������ӿڡ�
						// private IWordButtonClickListener mWordButtonListener;
						// ����IWordButtonClickListener�����������ط�����ģ�����һ���ӿ�
						// �ӿ��ڲ���һ��onWordButtonClick�����������������MainActivity��ʵ�ֵ�
						// ������������������һ��������Ȼ��ͨ��������registOnWordButtonClick���������ʵ��������
						// Ȼ��Ϳ��Ե������ʵ���ڲ���onWordButtonClick������

						// �൱�����������MainActivity��ʵ�ֵ�onWordButtonClick()����
						@Override
						public void onClick(View v) {
							mWordButtonListener.onWordButtonClick(holder);
						}
					});
				}
				
				v.setTag(holder);
				
			} else {
				holder = (WordButton) v.getTag();
			}
			
			holder.mViewButton.setText(holder.mWordString);
			
			// ���Ŷ���
			v.startAnimation(mScaleAnimation);
			
			return v;
		}
		
	}

	/**
	 * ע������ӿڣ���������Ĺ�����ʵ��������������еı�����
	 * private IWordButtonClickListener mWordButtonListener;
	 *
	 * ��MainActivity�е���
	 *
	 * @param listener
	 */
	public void registOnWordButtonClick(IWordButtonClickListener listener) {
		mWordButtonListener = listener;
	}
	
}
