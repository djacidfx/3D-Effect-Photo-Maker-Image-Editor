package com.photoeditor.photoeffect3d;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.photoeditor.photoeffect3d.adapter.FilterAdapter;
import com.photoeditor.photoeffect3d.application.LogoApplication;
import com.photoeditor.photoeffect3d.custom.RecyclerItemClickListener;
import com.photoeditor.photoeffect3d.custom.RecyclerItemClickListener.OnItemClickListener;
import com.photoeditor.photoeffect3d.filter.GPUImageTestFilter;
import com.photoeditor.photoeffect3d.model.DataItem;
import com.photoeditor.photoeffect3d.model.Warna;

import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;

public class EditActivity extends AppCompatActivity {
    static boolean active = false;
    final int SELECT_IMAGE = 11;
    FrameLayout cfDraw;
    private FilterAdapter filterAdapter;
    private ArrayList<DataItem> filterList = new ArrayList();
    private RecyclerView filterRecyclerView;
    Bitmap hasil;
    ImageView ivExport;
    ImageView ivImageView = null;
    Warna lC1 = new Warna(1.0f, 0.0f, 0.0f);
    Warna lC2 = new Warna(0.0f, 1.0f, 1.0f);
    int lastType = 0;

    LinearLayout llTransDiagonal;
    LinearLayout llTransLeft;
    LinearLayout llTransUp;

    private LinearLayoutManager mLayoutManager2;
    String newString = null;
    SharedPreferences prefs;
    boolean process = false;
    SeekBar sbDistance = null;
    private int show = 1;
    Bitmap sumber;
    Bitmap sumber2;
    LinearLayout tvImport = null;

    int width;

    public void generateBitmap() {
        if (this.sumber2 != null) {
            this.process = true;
            float xX = 0.0f;
            float yY = 0.0f;
            if (this.lastType == 0) {
                xX = (((float) this.sbDistance.getProgress()) * 0.2f) / 100.0f;
            } else if (this.lastType == 1) {
                xX = (((float) this.sbDistance.getProgress()) * 0.16f) / 100.0f;
                yY = (((float) this.sbDistance.getProgress()) * 0.16f) / 100.0f;
            } else if (this.lastType == 2) {
                yY = (((float) this.sbDistance.getProgress()) * 0.2f) / 100.0f;
            }
            GPUImage mGPUImage = new GPUImage(this);
            mGPUImage.setFilter(new GPUImageTestFilter(xX, yY, this.lC1.getR(), this.lC1.getG(), this.lC1.getB(), this.lC2.getR(), this.lC2.getG(), this.lC2.getB()));
            if (this.sumber2 != null) {
                this.ivImageView.setImageBitmap(mGPUImage.getBitmapWithFilterApplied(this.sumber2));
            }
            this.process = false;
        }
    }

    public void generateBitmap2() {
        if (this.sumber != null) {
            this.process = true;
            float xX = 0.0f;
            float yY = 0.0f;
            if (this.lastType == 0) {
                xX = (((float) this.sbDistance.getProgress()) * 0.2f) / 100.0f;
            } else if (this.lastType == 1) {
                xX = (((float) this.sbDistance.getProgress()) * 0.16f) / 100.0f;
                yY = (((float) this.sbDistance.getProgress()) * 0.16f) / 100.0f;
            } else if (this.lastType == 2) {
                yY = (((float) this.sbDistance.getProgress()) * 0.2f) / 100.0f;
            }
            GPUImage mGPUImage = new GPUImage(this);
            mGPUImage.setFilter(new GPUImageTestFilter(xX, yY, this.lC1.getR(), this.lC1.getG(), this.lC1.getB(), this.lC2.getR(), this.lC2.getG(), this.lC2.getB()));
            if (this.sumber != null) {
                this.hasil = mGPUImage.getBitmapWithFilterApplied(this.sumber);
            }
            this.process = false;
        }
    }

    private void updatWatermarkPosition(int finalX, int finalY) {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_edit);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.cfDraw = (FrameLayout) findViewById(R.id.cf_draw);
        this.tvImport = (LinearLayout) findViewById(R.id.tv_import);
        this.ivImageView = (ImageView) findViewById(R.id.iv_image);
        this.sbDistance = (SeekBar) findViewById(R.id.sb_distance);
        this.llTransLeft = (LinearLayout) findViewById(R.id.ll_transleft);
        this.llTransDiagonal = (LinearLayout) findViewById(R.id.ll_transdiagonal);
        this.llTransUp = (LinearLayout) findViewById(R.id.ll_transup);

        this.ivExport = (ImageView) findViewById(R.id.iv_export);

        addFilter();
        this.filterRecyclerView = (RecyclerView) findViewById(R.id.recycler_view2);
        this.filterAdapter = new FilterAdapter(this, this.filterList);
        this.mLayoutManager2 = new LinearLayoutManager(getApplicationContext(), 0, false);
        this.filterRecyclerView.setLayoutManager(this.mLayoutManager2);
        this.filterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.filterRecyclerView.setAdapter(this.filterAdapter);
        this.filterRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this.filterRecyclerView, new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position >= 0 && position < EditActivity.this.filterList.size()) {
                    EditActivity.this.lC1 = ((DataItem) EditActivity.this.filterList.get(position)).getWarna1();
                    EditActivity.this.lC2 = ((DataItem) EditActivity.this.filterList.get(position)).getWarna2();
                    if (((LogoApplication) EditActivity.this.getApplicationContext()).getShowingPromt() == 1.0d && position >= 6 && position < 10 && !iapvar.sticker_v1) {
                        Log.d("pesan", "Promt");
                        final AlertDialog alertDialog = new AlertDialog.Builder(EditActivity.this).create();
                        alertDialog.setTitle("Leave us a feedback");
                        alertDialog.setMessage("Leave us a 5 stars review to unlock more filters. We promised to make the app even better with each update");
                        alertDialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setButton(-1, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String appPackageName = EditActivity.this.getPackageName();
                                Log.d("pesan", "test=" + appPackageName);
                                try {
                                    EditActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName)));
                                } catch (ActivityNotFoundException e) {
                                    EditActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                                EditActivity.this.prefs.edit().putInt("com.psd2filter.a3deffect.prompt", 1).apply();
                                ((LogoApplication) EditActivity.this.getApplication()).setShowingPromt(0.0d);
                            }
                        });
                        alertDialog.setOnShowListener(new OnShowListener() {
                            public void onShow(DialogInterface arg0) {
                                alertDialog.getButton(-2).setTextColor(-16777216);
                                alertDialog.getButton(-1).setTextColor(-16777216);
                            }
                        });
                        alertDialog.show();
                    }

//                    else if (position >= 10 && !iapvar.filter_v1) {
//                        Intent i = new Intent(EditActivity.this, SalesActivity.class);
//                        i.putExtra("INT_I_NEED", 0);
//                        EditActivity.this.startActivityForResult(i, 10001);
//                    }

                    else if (!EditActivity.this.process) {
                        EditActivity.this.generateBitmap();
                    }
                    Log.d("pesan", "pos=" + position);
                }
            }

            public void onLongItemClick(View view, int position) {
            }
        }));
        this.ivExport.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EditActivity.this.generateBitmap2();
                Bitmap bit = EditActivity.loadBitmapFromView(EditActivity.this.cfDraw);
                if (EditActivity.this.hasil != null && bit != null) {
                    ((LogoApplication) EditActivity.this.getApplication()).setBitmapResult(Bitmap.createBitmap(bit, (bit.getWidth() - EditActivity.this.hasil.getWidth()) / 2, (bit.getHeight() - EditActivity.this.hasil.getHeight()) / 2, EditActivity.this.hasil.getWidth(), EditActivity.this.hasil.getHeight()));
                    EditActivity.this.startActivity(new Intent(EditActivity.this, SaveActivity.class));
                    HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
                }
            }
        });

        this.llTransLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EditActivity.this.lastType = 0;
                EditActivity.this.generateBitmap();
                HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
            }
        });
        this.llTransDiagonal.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EditActivity.this.lastType = 1;
                EditActivity.this.generateBitmap();
                HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
            }
        });
        this.llTransUp.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EditActivity.this.lastType = 2;
                EditActivity.this.generateBitmap();
                HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
            }
        });
        this.sbDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!EditActivity.this.process) {
                    EditActivity.this.generateBitmap();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                EditActivity.this.generateBitmap();
            }
        });
        this.tvImport.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Log.d("pesan", "Okess");
                if (EditActivity.this.isStoragePermissionGranted()) {
                    EditActivity.this.pickGallery();
                    HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
                }
            }
        });
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                this.newString = null;
            } else {
                this.newString = extras.getString("STRING_I_NEED");
            }
        } else {
            this.newString = (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (EditActivity.this.newString != null) {
                    if (EditActivity.active) {
                        Glide.with(EditActivity.this).load(Uri.parse(EditActivity.this.newString)).asBitmap().fitCenter().override(EditActivity.this.ivImageView.getWidth(), EditActivity.this.ivImageView.getHeight()).into(new SimpleTarget<Bitmap>() {
                            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                EditActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        EditActivity.this.sumber = resource;
                                        EditActivity.this.sumber2 = Bitmap.createScaledBitmap(EditActivity.this.sumber, EditActivity.this.sumber.getWidth() / 2, EditActivity.this.sumber.getHeight() / 2, false);
                                        EditActivity.this.generateBitmap();
                                        EditActivity.this.updatWatermarkPosition(EditActivity.this.sumber.getWidth(), EditActivity.this.sumber.getHeight());
                                    }
                                });
                            }
                        });
                    }
                } else if (EditActivity.active) {
                    Glide.with(EditActivity.this).load(Integer.valueOf(R.drawable.backhome)).asBitmap().fitCenter().override(EditActivity.this.ivImageView.getWidth(), EditActivity.this.ivImageView.getHeight()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            EditActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    EditActivity.this.sumber = resource;
                                    EditActivity.this.sumber2 = Bitmap.createScaledBitmap(EditActivity.this.sumber, EditActivity.this.sumber.getWidth() / 2, EditActivity.this.sumber.getHeight() / 2, false);
                                    EditActivity.this.generateBitmap();
                                    EditActivity.this.updatWatermarkPosition(EditActivity.this.sumber.getWidth(), EditActivity.this.sumber.getHeight());
                                }
                            });
                        }
                    });
                }
            }
        }, 1000);

    }

    public void onStart() {
        super.onStart();
        active = true;
    }

    public void onStop() {
        super.onStop();
        active = false;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }


    private void addFilter() {
        DataItem isi0 = new DataItem();
        isi0.setWarna1(new Warna(0.0f, 1.0f, 1.0f));
        isi0.setWarna2(new Warna(1.0f, 0.0f, 0.0f));
        this.filterList.add(isi0);
        DataItem isi1 = new DataItem();
        isi1.setWarna1(new Warna(1.0f, 0.0f, 0.0f));
        isi1.setWarna2(new Warna(0.0f, 1.0f, 1.0f));
        this.filterList.add(isi1);
        DataItem isi2 = new DataItem();
        isi2.setWarna1(new Warna(1.0f, 1.0f, 0.0f));
        isi2.setWarna2(new Warna(0.0f, 0.0f, 1.0f));
        this.filterList.add(isi2);
        DataItem isi3 = new DataItem();
        isi3.setWarna1(new Warna(0.0f, 0.0f, 1.0f));
        isi3.setWarna2(new Warna(1.0f, 1.0f, 0.0f));
        this.filterList.add(isi3);
        DataItem isi4 = new DataItem();
        isi4.setWarna1(new Warna(1.0f, 0.0f, 1.0f));
        isi4.setWarna2(new Warna(0.0f, 1.0f, 0.0f));
        this.filterList.add(isi4);
        DataItem isi5 = new DataItem();
        isi5.setWarna1(new Warna(0.0f, 1.0f, 0.0f));
        isi5.setWarna2(new Warna(1.0f, 0.0f, 1.0f));
        this.filterList.add(isi5);
        DataItem isi6 = new DataItem();
        isi6.setWarna1(colorToWarna("#000fff"));
        isi6.setWarna2(colorToWarna("#fff000"));
        this.filterList.add(isi6);
        DataItem isi7 = new DataItem();
        isi7.setWarna1(colorToWarna("#fff000"));
        isi7.setWarna2(colorToWarna("#000fff"));
        this.filterList.add(isi7);
        DataItem isi8 = new DataItem();
        isi8.setWarna1(colorToWarna("#ff000f"));
        isi8.setWarna2(colorToWarna("#00fff0"));
        this.filterList.add(isi8);
        DataItem isi9 = new DataItem();
        isi9.setWarna1(colorToWarna("#00fff0"));
        isi9.setWarna2(colorToWarna("#ff000f"));
        this.filterList.add(isi9);
        DataItem isi10 = new DataItem();
        isi10.setWarna1(colorToWarna("#f000ff"));
        isi10.setWarna2(colorToWarna("#0fff00"));
        this.filterList.add(isi10);
        DataItem isi11 = new DataItem();
        isi11.setWarna1(colorToWarna("#0fff00"));
        isi11.setWarna2(colorToWarna("#f000ff"));
        this.filterList.add(isi11);
        DataItem isi12 = new DataItem();
        isi12.setWarna1(colorToWarna("#f0f000"));
        isi12.setWarna2(colorToWarna("#0f0fff"));
        this.filterList.add(isi12);
        DataItem isi13 = new DataItem();
        isi13.setWarna1(colorToWarna("#0f0fff"));
        isi13.setWarna2(colorToWarna("#f0f000"));
        this.filterList.add(isi13);
        DataItem isi14 = new DataItem();
        isi14.setWarna1(colorToWarna("#00f0f0"));
        isi14.setWarna2(colorToWarna("#ff0f0f"));
        this.filterList.add(isi14);
        DataItem isi15 = new DataItem();
        isi15.setWarna1(colorToWarna("#ff0f0f"));
        isi15.setWarna2(colorToWarna("#00f0f0"));
        this.filterList.add(isi15);
        DataItem isi16 = new DataItem();
        isi16.setWarna1(colorToWarna("#000ff0"));
        isi16.setWarna2(colorToWarna("#fff00f"));
        this.filterList.add(isi16);
        DataItem isi17 = new DataItem();
        isi17.setWarna1(colorToWarna("#fff00f"));
        isi17.setWarna2(colorToWarna("#000ff0"));
        this.filterList.add(isi17);
        DataItem isi18 = new DataItem();
        isi18.setWarna1(colorToWarna("#0ff000"));
        isi18.setWarna2(colorToWarna("#f00fff"));
        this.filterList.add(isi18);
        DataItem isi19 = new DataItem();
        isi19.setWarna1(colorToWarna("#f00fff"));
        isi19.setWarna2(colorToWarna("#0ff000"));
        this.filterList.add(isi19);
        DataItem isi20 = new DataItem();
        isi20.setWarna1(colorToWarna("#f0000f"));
        isi20.setWarna2(colorToWarna("#0ffff0"));
        this.filterList.add(isi20);
        DataItem isi21 = new DataItem();
        isi21.setWarna1(colorToWarna("#0ffff0"));
        isi21.setWarna2(colorToWarna("#f0000f"));
        this.filterList.add(isi21);
    }

    private Warna colorToWarna(String col) {
        int color = Color.parseColor(col);
        return new Warna(((float) Color.red(color)) / 255.0f, ((float) Color.green(color)) / 255.0f, ((float) Color.blue(color)) / 255.0f);
    }

    private void pickGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 11);
    }

    public boolean isStoragePermissionGranted() {
        if (VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 2);
        return false;
    }

    void updateUi() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10001 && resultCode == -1) {
            updateUi();
        }
        if (requestCode == 11 && resultCode == -1 && data != null) {
            Glide.with(this).load(data.getData()).asBitmap().fitCenter().override(this.ivImageView.getWidth(), this.ivImageView.getHeight()).into(new SimpleTarget<Bitmap>() {
                public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    EditActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            EditActivity.this.sumber = resource;
                            EditActivity.this.sumber2 = Bitmap.createScaledBitmap(EditActivity.this.sumber, EditActivity.this.sumber.getWidth() / 2, EditActivity.this.sumber.getHeight() / 2, false);
                            Log.d("pesan", "x=" + resource.getWidth() + ",y=" + resource.getHeight());
                            EditActivity.this.generateBitmap();
                            EditActivity.this.updatWatermarkPosition(EditActivity.this.sumber.getWidth(), EditActivity.this.sumber.getHeight());
                        }
                    });
                }
            });
        }
    }
}
