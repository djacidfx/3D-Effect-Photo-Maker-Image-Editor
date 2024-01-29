package com.gms.ads;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.photoeditor.photoeffect3d.R;


public class ExitListAdapter extends BaseAdapter {

	static class viewHolder {
		TextView tvAppName, ivAppDesc;
		ImageView ivIcon, ivAppAds;
	}

	ArrayList<HashMap<String, String>> exitTopTrendingAdsPackage;
	private Context context;

	public ExitListAdapter(Context context,
			ArrayList<HashMap<String, String>> exitTopTrendingAdsPackage) {
		this.context = context;
		this.exitTopTrendingAdsPackage = exitTopTrendingAdsPackage;
	}

	public int getCount() {
		return exitTopTrendingAdsPackage.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final viewHolder holder;

		holder = new viewHolder();
		convertView = LayoutInflater.from(context).inflate(
				R.layout.exit_toptrending_ads_list_item, null);

		holder.tvAppName = (TextView) convertView.findViewById(R.id.tvAppName);
		// holder.tvAppName.setSelected(true);

		holder.ivAppDesc = (TextView) convertView.findViewById(R.id.ivAppDesc);
		holder.ivAppDesc.setSelected(true);

		holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);

		holder.ivAppAds = (ImageView) convertView.findViewById(R.id.ivAppAds);

		holder.tvAppName.setText(exitTopTrendingAdsPackage.get(position).get(
				"cat_name"));
		holder.ivAppDesc.setText(exitTopTrendingAdsPackage.get(position).get(
				"cat_des"));

		Glide.with(context)
				.load(exitTopTrendingAdsPackage.get(position).get("cat_icon"))
				.listener(new RequestListener<String, GlideDrawable>() {

					@Override
					public boolean onException(Exception e, String model,
							Target<GlideDrawable> target,
							boolean isFirstResource) {
						return false;
					}

					@Override
					public boolean onResourceReady(GlideDrawable resource,
							String model, Target<GlideDrawable> target,
							boolean isFromMemoryCache, boolean isFirstResource) {
						// progressBar.setVisibility(View.GONE);

						// didTapButton(holder.ivIcon);

						return false;
					}
				}).placeholder(R.drawable.temp_ad_icon)
				.error(R.drawable.temp_ad_icon).fitCenter().into(holder.ivIcon);

		Glide.with(context)
				.load(exitTopTrendingAdsPackage.get(position).get("ad_icn"))
				.placeholder(R.drawable.temp_ad_icon)
				.error(R.drawable.temp_ad_icon).into(holder.ivAppAds);
		convertView.setTag(holder);

		return convertView;
	}

	public void didTapButton(View view) {
		final Animation myAnim = AnimationUtils.loadAnimation(context,
				R.anim.bounce);

		MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
		myAnim.setInterpolator(interpolator);

		view.startAnimation(myAnim);
	}

	class MyBounceInterpolator implements android.view.animation.Interpolator {
		private double mAmplitude = 1;
		private double mFrequency = 10;

		MyBounceInterpolator(double amplitude, double frequency) {
			mAmplitude = amplitude;
			mFrequency = frequency;
		}

		public float getInterpolation(float time) {
			return (float) (-1 * Math.pow(Math.E, -time / mAmplitude)
					* Math.cos(mFrequency * time) + 1);
		}
	}

}
