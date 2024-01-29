package com.photoeditor.photoeffect3d;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.photoeditor.photoeffect3d.adapter.FilterAdapter;
import com.photoeditor.photoeffect3d.application.LogoApplication;
import com.photoeditor.photoeffect3d.custom.RecyclerItemClickListener;
import com.photoeditor.photoeffect3d.custom.RecyclerItemClickListener.OnItemClickListener;
import com.photoeditor.photoeffect3d.fragments.CameraFragment;
import com.photoeditor.photoeffect3d.fragments.PermissionsHelper;
import com.photoeditor.photoeffect3d.fragments.PermissionsHelper.PermissionsListener;
import com.photoeditor.photoeffect3d.gl.CameraRenderer;
import com.photoeditor.photoeffect3d.gl.CameraRenderer.OnRendererReadyListener;
import com.photoeditor.photoeffect3d.gl.TestRenderer;
import com.photoeditor.photoeffect3d.model.DataItem;
import com.photoeditor.photoeffect3d.model.Warna;
import com.photoeditor.photoeffect3d.utils.ShaderUtils;

import java.util.ArrayList;
import java.util.Arrays;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageScreenBlendFilter;

public class NormalActivity extends AppCompatActivity implements PermissionsListener, OnRendererReadyListener {
    private static final String TAG = NormalActivity.class.getSimpleName();
    private static final String TAG_CAMERA_FRAGMENT = "tag_camera_frag";
    final int SELECT_IMAGE = 11;
    FrameLayout cfDraw;
    private FilterAdapter filterAdapter;
    private ArrayList<DataItem> filterList = new ArrayList();
    private RecyclerView filterRecyclerView;
    Bitmap hasil;
    int height;

    Warna lC1 = new Warna(1.0f, 0.0f, 0.0f);
    Warna lC2 = new Warna(0.0f, 1.0f, 1.0f);
    int lastType = 0;


    LinearLayout llTransDiagonal;
    LinearLayout llTransLeft;
    LinearLayout llTransUp;
    private CameraFragment mCameraFragment;

    private LinearLayoutManager mLayoutManager2;
    private PermissionsHelper mPermissionsHelper;
    private boolean mPermissionsSatisfied = false;
    private CameraRenderer mRenderer;
    private boolean mRestartCamera = false;
    private SurfaceTextureListener mTextureListener = new SurfaceTextureListener() {
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            NormalActivity.this.setReady(surface, width, height);
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            NormalActivity.this.mCameraFragment.configureTransform(width, height);
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };
    TextureView mTextureView;
    String newString = null;
    SharedPreferences prefs;
    boolean process = false;
    //    SeekBar sbColor = null;
    SeekBar sbDistance = null;
    private int show = 1;
    Bitmap sumber;
    ImageView swap;
    TestRenderer testRenderer;
    LinearLayout tvImport = null;

    int width;

    public void generateBitmap() {
        this.process = true;
        float xX = 0.0f;
        float yY = 0.0f;
        if (this.lastType == 0) {
            yY = (((float) this.sbDistance.getProgress()) * 0.2f) / 100.0f;
        } else if (this.lastType == 1) {
            xX = (((float) this.sbDistance.getProgress()) * 0.16f) / 100.0f;
            yY = (((float) this.sbDistance.getProgress()) * 0.16f) / 100.0f;
        } else if (this.lastType == 2) {
            xX = (((float) this.sbDistance.getProgress()) * 0.2f) / 100.0f;
        }
        if (this.testRenderer != null && this.lC1 != null && this.lC2 != null) {
            this.testRenderer.setDistance(xX, yY, this.lC1.getR(), this.lC1.getG(), this.lC1.getB(), this.lC2.getR(), this.lC2.getG(), this.lC2.getB());
            this.process = false;
        }
    }

    private Warna colorToWarna(String col) {
        int color = Color.parseColor(col);
        return new Warna(((float) Color.red(color)) / 255.0f, ((float) Color.green(color)) / 255.0f, ((float) Color.blue(color)) / 255.0f);
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        setContentView(R.layout.activity_normal);
        this.mTextureView = (TextureView) findViewById(R.id.texture_view);
        this.cfDraw = (FrameLayout) findViewById(R.id.cf_draw);
        this.swap = (ImageView) findViewById(R.id.img_switch_camera);
        this.swap.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NormalActivity.this.mCameraFragment.swapCamera();
            }
        });

        this.tvImport = (LinearLayout) findViewById(R.id.tv_import);
        this.sbDistance = (SeekBar) findViewById(R.id.sb_distance);
//        this.sbColor = (SeekBar) findViewById(R.id.sb_color);
        this.llTransLeft = (LinearLayout) findViewById(R.id.ll_transleft);
        this.llTransDiagonal = (LinearLayout) findViewById(R.id.ll_transdiagonal);
        this.llTransUp = (LinearLayout) findViewById(R.id.ll_transup);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/BebasNeue-Bold.otf");

        addFilter();
        this.filterRecyclerView = (RecyclerView) findViewById(R.id.recycler_view2);
        this.filterAdapter = new FilterAdapter(this, this.filterList);
        this.mLayoutManager2 = new LinearLayoutManager(getApplicationContext(), 0, false);
        this.filterRecyclerView.setLayoutManager(this.mLayoutManager2);
        this.filterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.filterRecyclerView.setAdapter(this.filterAdapter);
        this.filterRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this.filterRecyclerView, new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position >= 0 && position < NormalActivity.this.filterList.size()) {
                    NormalActivity.this.lC1 = ((DataItem) NormalActivity.this.filterList.get(position)).getWarna1();
                    NormalActivity.this.lC2 = ((DataItem) NormalActivity.this.filterList.get(position)).getWarna2();
                    if (((LogoApplication) NormalActivity.this.getApplicationContext()).getShowingPromt() == 1.0d && position >= 6 && position < 10 && !iapvar.sticker_v1) {
                        Log.d("pesan", "Promt");
                        final AlertDialog alertDialog = new AlertDialog.Builder(NormalActivity.this).create();
                        alertDialog.setTitle("Leave us a feedback");
                        alertDialog.setMessage("Leave us a 5 stars review to unlock more filters. We promised to make the app even better with each update");
                        alertDialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setButton(-1, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String appPackageName = NormalActivity.this.getPackageName();
                                Log.d("pesan", "test=" + appPackageName);
                                try {
                                    NormalActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName)));
                                } catch (ActivityNotFoundException e) {
                                    NormalActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                                NormalActivity.this.prefs.edit().putInt("com.psd2filter.a3deffect.prompt", 1).apply();
                                ((LogoApplication) NormalActivity.this.getApplication()).setShowingPromt(0.0d);
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
                    else if (!NormalActivity.this.process) {
                        NormalActivity.this.generateBitmap();
                    }
                }
                Log.d("pesan", "pos=" + position);
            }

            public void onLongItemClick(View view, int position) {
            }
        }));
        this.tvImport.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Bitmap bit = NormalActivity.loadBitmapFromView(NormalActivity.this.cfDraw);
                Bitmap bt = NormalActivity.this.mTextureView.getBitmap();
                GPUImage gp = new GPUImage(NormalActivity.this);
                GPUImageScreenBlendFilter fil = new GPUImageScreenBlendFilter();
                fil.setBitmap(bit);
                gp.setFilter(fil);
                bt = gp.getBitmapWithFilterApplied(bt);
                ((LogoApplication) NormalActivity.this.getApplication()).setBitmapResult(bt);

                NormalActivity.this.startActivity(new Intent(NormalActivity.this, SaveActivity.class));
                HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
            }
        });
        this.llTransLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NormalActivity.this.lastType = 0;
                NormalActivity.this.generateBitmap();
                HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
            }
        });
        this.llTransDiagonal.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NormalActivity.this.lastType = 1;
                NormalActivity.this.generateBitmap();
                HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
            }
        });
        this.llTransUp.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NormalActivity.this.lastType = 2;
                NormalActivity.this.generateBitmap();
                HomeActivity.p_adssss.Inter_Counted(Badha_Mate.Adss);
            }
        });
        this.sbDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!NormalActivity.this.process) {
                    NormalActivity.this.generateBitmap();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                NormalActivity.this.generateBitmap();
            }
        });

        setupCameraFragment();


    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    void updateUi() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10001 && resultCode == -1) {
            updateUi();
        }
    }


    private void setupCameraFragment() {
        if (this.mCameraFragment == null || !this.mCameraFragment.isAdded()) {
            this.mCameraFragment = CameraFragment.getInstance();
            this.mCameraFragment.setCameraToUse(0);
            this.mCameraFragment.setTextureView(this.mTextureView);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(this.mCameraFragment, TAG_CAMERA_FRAGMENT);
            transaction.commit();
        }
    }

    public void onPermissionsSatisfied() {
        Log.d(TAG, "onPermissionsSatisfied()");
        this.mPermissionsSatisfied = true;
    }

    public void onPermissionsFailed(String[] failedPermissions) {
        Log.e(TAG, "onPermissionsFailed()" + Arrays.toString(failedPermissions));
        this.mPermissionsSatisfied = false;
        Toast.makeText(this, "shadercam needs all permissions to function, please try again.", Toast.LENGTH_SHORT).show();
        finish();
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        ShaderUtils.goFullscreen(getWindow());

        if (this.mTextureView.isAvailable()) {
            setReady(this.mTextureView.getSurfaceTexture(), this.mTextureView.getWidth(), this.mTextureView.getHeight());
        } else {
            this.mTextureView.setSurfaceTextureListener(this.mTextureListener);
        }
    }

    protected void onPause() {
        super.onPause();
        shutdownCamera(false);
        this.mTextureView.setSurfaceTextureListener(null);
    }

    protected void setReady(SurfaceTexture surface, int width, int height) {
        this.mRenderer = getRenderer(surface, width, height);
        this.mRenderer.setCameraFragment(this.mCameraFragment);
        this.mRenderer.setOnRendererReadyListener(this);
        this.mRenderer.start();
        this.mCameraFragment.configureTransform(width, height);
    }

    protected CameraRenderer getRenderer(SurfaceTexture surface, int width, int height) {
        this.testRenderer = new TestRenderer(this, surface, width, height);
        return this.testRenderer;
    }

    private void shutdownCamera(boolean restart) {
        if ((!PermissionsHelper.isMorHigher() || this.mPermissionsSatisfied) && this.mCameraFragment != null && this.mRenderer != null && this.mRenderer.getRenderHandler() != null) {
            this.mCameraFragment.closeCamera();
            this.mRestartCamera = restart;
            this.mRenderer.getRenderHandler().sendShutdown();
            this.mRenderer = null;
        }
    }

    public void onRendererReady() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (NormalActivity.this.mRenderer != null && NormalActivity.this.mRenderer.getPreviewTexture() != null) {
                    NormalActivity.this.mCameraFragment.setPreviewTexture(NormalActivity.this.mRenderer.getPreviewTexture());
                    NormalActivity.this.mCameraFragment.openCamera();
                }
            }
        });
    }

    public void onRendererFinished() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (NormalActivity.this.mRestartCamera) {
                    NormalActivity.this.setReady(NormalActivity.this.mTextureView.getSurfaceTexture(), NormalActivity.this.mTextureView.getWidth(), NormalActivity.this.mTextureView.getHeight());
                    NormalActivity.this.mRestartCamera = false;
                }
            }
        });
    }
}
