package com.photoeditor.photoeffect3d;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.gms.ads.Adssss;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    LinearLayout llCamera = null;

    LinearLayout llImport = null;

    String[] permissions = new String[] { android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.CAMERA };


    Context mContext = this;

    public static Adssss p_adssss;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(1024, 1024);
		StartAppSDK.init(this, "" + Badha_Mate.app, true);
        setContentView(R.layout.activity_home);


        p_adssss = new Adssss(mContext, mContext.getPackageName(),
                Badha_Mate.Acccccc, Badha_Mate.e1, Badha_Mate.e2,
                Badha_Mate.b1, Badha_Mate.b2);

        p_adssss.Splash_Screen();

        p_adssss.Banner((RelativeLayout) findViewById(R.id.adview),1);



        checkPermissions();


        this.llCamera = (LinearLayout) findViewById(R.id.ll_camera);
        this.llImport = (LinearLayout) findViewById(R.id.ll_import);
        this.llCamera.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Log.d("pesan", "Camera");
                HomeActivity.this.startActivity(new Intent(HomeActivity.this, NormalActivity.class));
                return;

            }
        });
        this.llImport.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                Log.d("pesan", "import");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction("android.intent.action.GET_CONTENT");
                HomeActivity.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 11);
                return;
            }
        });


    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == -1) {
            if (data != null) {

                Intent i = new Intent(this, EditActivity.class);
                i.putExtra("STRING_I_NEED", "" + data.getData());
                startActivity(i);

            } else {
                Toast.makeText(this, "Load Image Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<String>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded
                    .toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }


}
