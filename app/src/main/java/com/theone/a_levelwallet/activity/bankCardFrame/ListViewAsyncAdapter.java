package com.theone.a_levelwallet.activity.bankCardFrame;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.theone.a_levelwallet.R;

/**
 * 显示图片的适配器
 *@author changkai
 *@E-mail changkai@chinasofti.com
 *@version createTime:2013-5-22 上午9:19:26 
 */
public class ListViewAsyncAdapter extends BaseAdapter {
	private static final String TAG = "ListViewAsyncAdapter";
	private Context cext; //上下文对象
	private ArrayList<ImageUrl> urlList; //保存图片的url地址的集合
	private AsyncLoadImage asyncLoadImage; //异步加载图片的类
	private String cardNumber[];
	private int resource[];

	public ListViewAsyncAdapter(Context cext, ArrayList<ImageUrl> urlList, AsyncLoadImage asyncLoadImage, String[] textList, int[] resource) {
		this.cext = cext;
		this.asyncLoadImage = asyncLoadImage;
		this.urlList = urlList;
		this.cardNumber = textList;
		this.resource = resource;
	}

	@Override
	public int getCount() {
		return urlList.size();
	}

	@Override
	public Object getItem(int position) {
		return urlList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG, "getView()");
		if (convertView == null) {
			//获取布局
			convertView = LayoutInflater.from(cext).inflate(R.layout.bankcard_listview_style, null);
			//获取布局里面的ImageView组件
			final ImageView imageView = (ImageView) convertView.findViewById(R.id.item_imageView);
			//获取当前的图片的url地址
			final TextView textview = (TextView) convertView.findViewById(R.id.tv_imageAndcardnumber);
			//final String url = urlList.get(position).getImageUrl();
			//为图片设置一个tag 这个很重要这样的目的是为了控制每个ImageView组件都显示对应的图片
			//imageView.setTag(url);
			//Log.i(TAG, "初始化"+(position));
			textview.setText(cardNumber[position]);
			imageView.setImageResource(resource[position]);
			//开始异步加载图片
			/*Drawable drawable = asyncLoadImage.loadDrawable(url, new AsyncLoadImage.ImageCallback(){
				@Override
				public void imageLoad(Drawable image,String imageUrl) {
					//判断当前的url地址是否为当前组件的url地址 是则加载图片
					if(imageUrl.equals(imageView.getTag())){
					   imageView.setImageDrawable(image);
					   
					}
				}
			});
			 if(drawable==null){ //这里也很重要 如果没有则设置一个默认的图片 如果不设置表示后果很严重。我就是因为这个地方浪费了一上午的时间
					imageView.setImageResource(R.drawable.setblue);
				}else{//这里就是加载图片啦。。
					imageView.setImageDrawable(drawable);
			}*/
		}
		/*else{
			Log.i(TAG, "完成后的初始化"+(position));
			final ImageView imageView = (ImageView)convertView.findViewById(R.id.item_imageView);
			final String url = urlList.get(position).getImageUrl();
			imageView.setTag(url);
			 Drawable drawable = asyncLoadImage.loadDrawable(url, new AsyncLoadImage.ImageCallback(){
				@Override
				public void imageLoad(Drawable image,String imageUrl) {
					if(imageUrl.equals(imageView.getTag())){
						imageView.setImageDrawable(image);
					}
				}
			});
			if(drawable==null){
				imageView.setImageResource(R.drawable.bnt_back);
			}else{
					imageView.setImageDrawable(drawable);
			}
		}*/

			return convertView;
		}

		static class HadleView {
			ImageView imageView;
			TextView tt;
		}
	}
