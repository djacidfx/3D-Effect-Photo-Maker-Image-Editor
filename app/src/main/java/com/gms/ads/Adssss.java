package com.gms.ads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.photoeditor.photoeffect3d.R;
import com.startapp.android.publish.ads.banner.BannerListener;
import com.startapp.android.publish.ads.banner.bannerstandard.BannerStandard;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;


public class Adssss {

	Context mContext;
	InterstitialAd interstitialAd;
	RelativeLayout relative;
	String Inter, Inter1, Inter2;
	String Banner, Banner1, Banner2;
	StartAppAd startAppAd;

	ConnectivityManager connec;

	Context aContext;
	String Packages;
	String Acc_Name;

	boolean ads_exit_Preferense, ads_enter_Preferense;

	String Enter_Ads_Popup_Or_Not = "", Exit_Top_Ads_Popup_Or_Not = "";

	private LayoutInflater mInflater;
	LinearLayout LL;
	HorizontalScrollView HH;

	@SuppressWarnings("static-access")
	public Adssss(Context mcontext, String Package, String acc_Name,
			final String Inter_ID, final String Inter_ID1,
			final String Banner_ID, final String Banner_ID1) {

		aContext = mcontext;
		Packages = Package;
		Acc_Name = acc_Name;

		Glob.Exit_Ads_List = new ArrayList<HashMap<String, String>>();

		Glob.Enter_Ads_List = new ArrayList<HashMap<String, String>>();

		Glob.Top_Ads_List = new ArrayList<HashMap<String, String>>();

		new GetExit_Ads().execute();

		// Google.................
		mContext = mcontext;
		Inter1 = Inter_ID;
		Inter2 = Inter_ID1;

		Banner1 = Banner_ID;
		Banner2 = Banner_ID1;

		startAppAd = new StartAppAd(mContext);

		connec = (ConnectivityManager) mContext
				.getSystemService(mContext.CONNECTIVITY_SERVICE);

	}

	@SuppressLint("NewApi")
	public void Enter_Ads(Context aContext2, RelativeLayout RRRR) {

		LL = new LinearLayout(aContext2);
		HH = new HorizontalScrollView(aContext2);

		HH.setVerticalScrollBarEnabled(false);
		HH.setHorizontalScrollBarEnabled(false);
		HH.setVisibility(View.GONE);

		mInflater = LayoutInflater.from(aContext2);

		mInflater = LayoutInflater.from(aContext2);

		new GetEnterAds(aContext2, Packages, HH, LL, RRRR).execute();

	}

	public void Exit_Ads(Context aContext) {

		ExitdialogMessage("Are you sure to Exit! - (Ads)", aContext);

	}

	public void Top_Ads(Context aContext) {

		TopdialogMessage("Top Tranding Apps - (Ads)", aContext);

	}

	@SuppressLint("NewApi")
	public class GetEnterAds extends AsyncTask<String, Integer, String> {

		private Context aacContext;
		private HorizontalScrollView hhorizontalScrollView;
		private LinearLayout llayout;
		private RelativeLayout rlayout;

		public GetEnterAds(Context context, String packagename,
				HorizontalScrollView horizontalScrollView, LinearLayout layout,
				RelativeLayout relativeLayout) {
			this.aacContext = context;
			this.hhorizontalScrollView = horizontalScrollView;
			this.llayout = layout;
			this.rlayout = relativeLayout;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {
			HttpHandler sh = new HttpHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(HttpHandler.Adsss + Acc_Name
					+ ".json");

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					JSONArray contacts = jsonObj.getJSONArray("" + Acc_Name);

					Enter_Ads_Popup_Or_Not = ((jsonObj
							.getJSONArray("Enter_Popup_Or_Not"))
							.getJSONObject(0)).getString("Enter_Popup_Or_Not");

					// looping through All Contacts
					for (int i = 0; i < contacts.length(); i++) {
						JSONObject c = contacts.getJSONObject(i);

						String ID = c.getString("cat_id");
						String NAME = c.getString("cat_name");
						String ICON = c.getString("cat_icon");
						String DES = c.getString("cat_des");
						String AD_ICON = c.getString("ad_icn");

						if (ID.equals(Packages)) {

						} else {

							// tmp hash map for single contact
							HashMap<String, String> contact = new HashMap<String, String>();

							// adding each child node to HashMap key => value

							contact.put("cat_id", ID);
							contact.put("cat_name", NAME);
							contact.put("cat_icon", ICON);
							contact.put("cat_des", DES);
							contact.put("ad_icn", AD_ICON);

							// adding contact to contact list
							Glob.Enter_Ads_List.add(contact);

							Collections.shuffle(Glob.Enter_Ads_List);

						}

					}

				} catch (final JSONException e) {

				}
			} else {

			}
			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (Glob.Exit_Ads_List.size() == 0) {

				ads_enter_Preferense = false;

				rlayout.setVisibility(View.GONE);
				hhorizontalScrollView.setVisibility(View.GONE);

			} else {

				ads_enter_Preferense = true;

				rlayout.setVisibility(View.VISIBLE);
				hhorizontalScrollView.setVisibility(View.VISIBLE);

				for (int i = 0; i < Glob.Exit_Ads_List.size(); i++) {
					View view = mInflater.inflate(R.layout.enter_ads_list_item,
							llayout, false);
					final ImageView ivIcon = (ImageView) view
							.findViewById(R.id.ivIcon);
					ImageView ivAppAds = (ImageView) view
							.findViewById(R.id.ivAppAds);

					final TextView ivAppDesc = (TextView) view
							.findViewById(R.id.ivAppDesc);
					ivAppDesc.setSelected(true);

					ivAppDesc
							.setText(Glob.Enter_Ads_List.get(i).get("cat_des"));

					TextView tvAppName = (TextView) view
							.findViewById(R.id.tvAppName);
					tvAppName
							.setText(Glob.Exit_Ads_List.get(i).get("cat_name"));
					// tvAppName.setSelected(true);

					Glide.with(aacContext)
							.load(Glob.Enter_Ads_List.get(i).get("cat_icon"))
							.listener(
									new RequestListener<String, GlideDrawable>() {

										@Override
										public boolean onException(
												Exception arg0, String arg1,
												Target<GlideDrawable> arg2,
												boolean arg3) {
											// TODO Auto-generated method stub
											return false;
										}

										@Override
										public boolean onResourceReady(
												GlideDrawable arg0,
												String arg1,
												Target<GlideDrawable> arg2,
												boolean arg3, boolean arg4) {
											// TODO Auto-generated method stub

											DidTapButton(aacContext, ivIcon);

											return false;
										}
									}).placeholder(R.drawable.temp_ad_icon)
							.error(R.drawable.temp_ad_icon).fitCenter()
							.into(ivIcon);
					Glide.with(aacContext)
							.load(Glob.Enter_Ads_List.get(i).get("ad_icn"))
							.placeholder(R.drawable.temp_ad_icon)
							.error(R.drawable.temp_ad_icon).fitCenter()
							.into(ivAppAds);

					llayout.addView(view);

					final int ii = i;
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							if (Enter_Ads_Popup_Or_Not.equals("0")) {

								Uri uri = Uri.parse("market://details?id="
										+ Glob.Enter_Ads_List.get(ii).get(
												"cat_id"));

								Intent myAppLinkToMarket = new Intent(
										Intent.ACTION_VIEW, uri);
								try {
									aacContext.startActivity(myAppLinkToMarket);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(
											aacContext,
											"you don't have Google play installed",
											Toast.LENGTH_LONG).show();
								}

							} else {

								Go_With_Popup(
										aacContext,
										Glob.Enter_Ads_List.get(ii).get(
												"cat_id"),
										Glob.Enter_Ads_List.get(ii).get(
												"cat_name"),
										Glob.Enter_Ads_List.get(ii).get(
												"cat_icon"),
										Glob.Enter_Ads_List.get(ii).get(
												"cat_des"), Glob.Enter_Ads_List
												.get(ii).get("ad_icn"));

							}

						}

					});

				}

				hhorizontalScrollView.addView(llayout);

				rlayout.addView(hhorizontalScrollView);

			}

		}
	}

	@SuppressLint({ "InflateParams", "InlinedApi" })
	public void ExitdialogMessage(String string, final Context context) {

		Collections.shuffle(Glob.Exit_Ads_List);

		final Dialog exitDialog = new Dialog(context);
		exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View convertView = (View) inflater.inflate(R.layout.exit_ads_layout,
				null);
		exitDialog.setContentView(convertView);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(exitDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		exitDialog.getWindow().setAttributes(lp);

		exitDialog.setCancelable(true);
		exitDialog.setCanceledOnTouchOutside(false);
		ListView lvExitAds = (ListView) convertView
				.findViewById(R.id.lvExitAds);
		TextView tvHeading = (TextView) convertView
				.findViewById(R.id.tvHeading);
		TextView tvNoLater = (TextView) convertView
				.findViewById(R.id.tvNoLater);
		TextView tvRateUs = (TextView) convertView.findViewById(R.id.tvRateUs);
		TextView tvYes = (TextView) convertView.findViewById(R.id.tvYes);
		tvHeading.setText(string);
		tvNoLater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitDialog.dismiss();
			}
		});

		tvRateUs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Uri uri = Uri.parse("market://details?id=" + Packages);
				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);

				try {

					context.startActivity(myAppLinkToMarket);

				} catch (ActivityNotFoundException e) {

					// the device hasn't installed Google Play
					Toast.makeText(context,
							"you don't have Google play installed",
							Toast.LENGTH_LONG).show();

				}
			}

		});
		tvYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitDialog.dismiss();
				((Activity) context).moveTaskToBack(true);
				((Activity) context).finish();
			}
		});
		if (ads_exit_Preferense == false) {
			lvExitAds.setVisibility(View.GONE);

		} else {
			lvExitAds.setVisibility(View.VISIBLE);
			final ExitListAdapter exitAdsListAdapter = new ExitListAdapter(
					context, Glob.Exit_Ads_List);
			lvExitAds.setAdapter(exitAdsListAdapter);

			lvExitAds
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							if (Exit_Top_Ads_Popup_Or_Not.equals("0")) {

								Uri uri = Uri.parse("market://details?id="
										+ Glob.Exit_Ads_List.get(position).get(
												"cat_id"));

								Intent myAppLinkToMarket = new Intent(
										Intent.ACTION_VIEW, uri);
								try {
									context.startActivity(myAppLinkToMarket);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(
											context,
											"you don't have Google play installed",
											Toast.LENGTH_LONG).show();
								}

							} else {

								Go_With_Popup(
										context,
										Glob.Exit_Ads_List.get(position).get(
												"cat_id"),
										Glob.Exit_Ads_List.get(position).get(
												"cat_name"),
										Glob.Exit_Ads_List.get(position).get(
												"cat_icon"),
										Glob.Exit_Ads_List.get(position).get(
												"cat_des"),
										Glob.Exit_Ads_List.get(position).get(
												"ad_icn"));

							}

						}
					});
		}
		exitDialog.show();

	}

	@SuppressLint("NewApi")
	private class GetExit_Ads extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			HttpHandler sh = new HttpHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(HttpHandler.Adsss + Acc_Name
					+ ".json");

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					JSONArray contacts = jsonObj.getJSONArray("" + Acc_Name);

					Exit_Top_Ads_Popup_Or_Not = ((jsonObj
							.getJSONArray("Exit_Top_Popup_Or_Not"))
							.getJSONObject(0))
							.getString("Exit_Top_Popup_Or_Not");

					// looping through All Contacts
					for (int i = 0; i < contacts.length(); i++) {
						JSONObject c = contacts.getJSONObject(i);

						String ID = c.getString("cat_id");
						String NAME = c.getString("cat_name");
						String ICON = c.getString("cat_icon");
						String DES = c.getString("cat_des");
						String AD_ICON = c.getString("ad_icn");

						if (ID.equals(Packages)) {

						} else {

							// tmp hash map for single contact
							HashMap<String, String> contact = new HashMap<String, String>();

							// adding each child node to HashMap key => value

							contact.put("cat_id", ID);
							contact.put("cat_name", NAME);
							contact.put("cat_icon", ICON);
							contact.put("cat_des", DES);
							contact.put("ad_icn", AD_ICON);

							// adding contact to contact list
							Glob.Exit_Ads_List.add(contact);

							Glob.Top_Ads_List.add(contact);

						}

					}
				} catch (final JSONException e) {

				}
			} else {

			}

			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog

			if (Glob.Exit_Ads_List.size() == 0) {

				ads_exit_Preferense = false;

			} else {

				ads_exit_Preferense = true;

			}

		}

	}

	@SuppressLint({ "InflateParams", "InlinedApi" })
	private void TopdialogMessage(String string, final Context aContext2) {
		// TODO Auto-generated method stub

		Collections.shuffle(Glob.Top_Ads_List);

		final Dialog exitDialog = new Dialog(aContext2);
		exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) aContext2
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View convertView = (View) inflater.inflate(R.layout.exit_ads_layout,
				null);
		exitDialog.setContentView(convertView);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(exitDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		exitDialog.getWindow().setAttributes(lp);

		exitDialog.setCancelable(true);
		exitDialog.setCanceledOnTouchOutside(false);
		ListView lvExitAds = (ListView) convertView
				.findViewById(R.id.lvExitAds);
		TextView tvHeading = (TextView) convertView
				.findViewById(R.id.tvHeading);
		TextView tvNoLater = (TextView) convertView
				.findViewById(R.id.tvNoLater);
		TextView tvRateUs = (TextView) convertView.findViewById(R.id.tvRateUs);
		TextView tvYes = (TextView) convertView.findViewById(R.id.tvYes);
		tvHeading.setText(string);
		tvNoLater.setVisibility(View.GONE);

		tvRateUs.setVisibility(View.GONE);
		tvYes.setVisibility(View.GONE);
		if (ads_exit_Preferense == false) {
			lvExitAds.setVisibility(View.GONE);

		} else {
			lvExitAds.setVisibility(View.VISIBLE);
			final ExitListAdapter exitAdsListAdapter = new ExitListAdapter(
					aContext2, Glob.Top_Ads_List);
			lvExitAds.setAdapter(exitAdsListAdapter);

			lvExitAds
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							if (Exit_Top_Ads_Popup_Or_Not.equals("0")) {

								Uri uri = Uri.parse("market://details?id="
										+ Glob.Top_Ads_List.get(position).get(
												"cat_id"));

								Intent myAppLinkToMarket = new Intent(
										Intent.ACTION_VIEW, uri);
								try {
									aContext2.startActivity(myAppLinkToMarket);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(
											aContext2,
											"you don't have Google play installed",
											Toast.LENGTH_LONG).show();
								}

							} else {

								Go_With_Popup(
										aContext2,
										Glob.Top_Ads_List.get(position).get(
												"cat_id"),
										Glob.Top_Ads_List.get(position).get(
												"cat_name"),
										Glob.Top_Ads_List.get(position).get(
												"cat_icon"),
										Glob.Top_Ads_List.get(position).get(
												"cat_des"),
										Glob.Top_Ads_List.get(position).get(
												"ad_icn"));

							}

						}
					});
		}
		exitDialog.show();

	}

	@SuppressLint({ "InlinedApi", "InflateParams" })
	private void Go_With_Popup(final Context mContext,
			final String App_Package, String App_Name, String App_Icon,
			String App_Des, String App_Ad_Icon) {
		// TODO Auto-generated method stub

		final Dialog exitDialog = new Dialog(mContext);
		exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View convertView = (View) inflater.inflate(R.layout.yes_no_popup, null);
		exitDialog.setContentView(convertView);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(exitDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		exitDialog.getWindow().setAttributes(lp);

		exitDialog.setCancelable(true);
		exitDialog.setCanceledOnTouchOutside(true);

		ImageView Img_App_Icon = (ImageView) convertView
				.findViewById(R.id.Img_App_Icon);

		Glide.with(mContext).load(App_Icon)
				.placeholder(R.drawable.temp_ad_icon)
				.error(R.drawable.temp_ad_icon).fitCenter().into(Img_App_Icon);

		TextView Txt_App_Name = (TextView) convertView
				.findViewById(R.id.Txt_App_Name);

		Txt_App_Name.setText(App_Name);

		ImageView Img_Ad_Icon = (ImageView) convertView
				.findViewById(R.id.Img_Ad_Icon);

		Glide.with(mContext).load(App_Ad_Icon)
				.placeholder(R.drawable.temp_ad_icon)
				.error(R.drawable.temp_ad_icon).fitCenter().into(Img_Ad_Icon);

		TextView Txt_Yes = (TextView) convertView.findViewById(R.id.Txt_Yes);

		TextView Txt_No = (TextView) convertView.findViewById(R.id.Txt_No);
		Txt_Yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Uri uri = Uri.parse("market://details?id=" + App_Package);

				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					mContext.startActivity(myAppLinkToMarket);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(mContext,
							"you don't have Google play installed",
							Toast.LENGTH_LONG).show();
				}

				exitDialog.dismiss();

			}
		});

		Txt_No.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				exitDialog.dismiss();

			}
		});

		exitDialog.show();

	}

	public void DidTapButton(Context context, View view) {
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

	// Google.................
	// Splasd Declaration. . . . .
	public void Splash_Screen() {

		if (Gmsprefads.getsplashcount(mContext) >= 10) {

			Interstial();

		} else {

			StartAppAd.disableSplash();

		}

	}

	// Load Google Banner Ads
	public void Banner(final RelativeLayout Ad_Layout, int Banner_Type) {

		AdSize Banner_Type_Size;

		if (Banner_Type == 1) {

			Banner_Type_Size = AdSize.SMART_BANNER;

		} else if (Banner_Type == 2) {

			Banner_Type_Size = AdSize.LARGE_BANNER;

		} else {

			Banner_Type_Size = AdSize.SMART_BANNER;

		}

		if (Gmsprefads.getbanner(mContext) == 0) {

			Banner = Banner1;

			Gmsprefads.setbanner(mContext, 1);

		} else {

			Banner = Banner2;

			Gmsprefads.setbanner(mContext, 0);

		}

		AdView mAdView = new AdView(mContext);
		mAdView.setAdSize(Banner_Type_Size);
		mAdView.setAdUnitId(Banner);
		AdRequest adre = new AdRequest.Builder().build();
		mAdView.loadAd(adre);
		Ad_Layout.addView(mAdView);

		mAdView.setAdListener(new AdListener() {

			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub

				Ad_Layout.setVisibility(View.VISIBLE);
				Gmsprefads.setsplashcount(mContext, (0));

				super.onAdLoaded();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				// TODO Auto-generated method stub
				DisplayMetrics metrics = mContext.getResources()
						.getDisplayMetrics();

			/*	if ((metrics.heightPixels) > (metrics.widthPixels)) {

					Ad_Layout.getLayoutParams().height = (int) (metrics.heightPixels / 10);

				} else if ((metrics.widthPixels) > (metrics.heightPixels)) {

					Ad_Layout.getLayoutParams().height = (int) (metrics.heightPixels / (6.5));

				} else {

					Ad_Layout.getLayoutParams().height = (int) (metrics.heightPixels / 10);

				}

				*/

				@SuppressWarnings("deprecation")
				final BannerStandard startAppBanner = new BannerStandard(
						mContext);
				RelativeLayout.LayoutParams bannerParameters = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
				bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				// Add to main Layout
				Ad_Layout.setVisibility(View.VISIBLE);
				Ad_Layout.setBackgroundColor(Color.TRANSPARENT);
				Ad_Layout.addView(startAppBanner, bannerParameters);

				startAppBanner.setBannerListener(new BannerListener() {

					@Override
					public void onReceiveAd(View arg0) {
						// TODO Auto-generated method stub
						// Ad_Layout.getLayoutParams().height = 100;

					}

					@Override
					public void onFailedToReceiveAd(View arg0) {
						// TODO Auto-generated method stub

						Ad_Layout.destroyDrawingCache();

						Enter_Ads(mContext, Ad_Layout);

					}

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						startAppBanner.hideBanner();

						Enter_Ads(mContext, Ad_Layout);

					}
				});

				Gmsprefads.setsplashcount(mContext,
						(Gmsprefads.getsplashcount(mContext) + 1));

			}
		});

	}

	// Load & Show Google Intrestial Ads
	public void Interstial() {

		if (Gmsprefads.getinter(mContext) == 0) {

			Inter = Inter1;

			Gmsprefads.setinter(mContext, 1);

		} else {

			Inter = Inter2;

			Gmsprefads.setinter(mContext, 0);

		}

		try {

			AdRequest adRequest = new AdRequest.Builder().build();
			interstitialAd = new InterstitialAd(mContext);
			interstitialAd.setAdUnitId(Inter);

			interstitialAd.loadAd(adRequest);

			interstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {

					interstitialAd.show();
					Gmsprefads.setsplashcount(mContext, (0));

				}

				@Override
				public void onAdFailedToLoad(int errorCode) {

					startAppAd.loadAd(new AdEventListener() {

						@Override
						public void onReceiveAd(Ad arg0) {
							// TODO Auto-generated method stub

							startAppAd.showAd();

							Gmsprefads.setcount(mContext,
									(Gmsprefads.getcount(mContext) + 1));

						}

						@Override
						public void onFailedToReceiveAd(Ad arg0) {
							// TODO Auto-generated method stub

						}
					});

				}

			});
		} catch (Exception e) {

		}

	}

	// Inter Counted. . . . .
	public void Inter_Counted(int Count) {

		if (Count == Gmsprefads.getcount(mContext)) {

			Interstial();

			Gmsprefads.setcount(mContext, 1);

		} else {

			Gmsprefads.setcount(mContext, (Gmsprefads.getcount(mContext) + 1));
		}

	}

}