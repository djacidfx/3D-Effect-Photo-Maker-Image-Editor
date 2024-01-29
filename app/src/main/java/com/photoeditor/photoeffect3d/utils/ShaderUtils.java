package com.photoeditor.photoeffect3d.utils;

import android.content.Context;
import android.view.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.co.cyberagent.android.gpuimage.BuildConfig;

public class ShaderUtils {
    public static String getStringFromFileInAssets(Context ctx, String filename) throws IOException {
        return getStringFromFileInAssets(ctx, filename, true);
    }

    public static String getStringFromFileInAssets(Context ctx, String filename, boolean useNewline) throws IOException {
        InputStream is = ctx.getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                builder.append(line + (useNewline ? "\n" : BuildConfig.FLAVOR));
            } else {
                is.close();
                return builder.toString();
            }
        }
    }

    public static void goFullscreen(Window window) {
        window.getDecorView().setSystemUiVisibility(5894);
    }
}
