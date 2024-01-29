package com.photoeditor.photoeffect3d.filter;

import android.opengl.GLES20;
import android.util.Log;

import jp.co.cyberagent.android.gpuimage.BuildConfig;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class GPUImageTestFilter extends GPUImageFilter {
    public static final String GRAYSCALE_FRAGMENT_SHADER = "precision highp float;\n\nvarying vec2 textureCoordinate;\n\nuniform sampler2D inputImageTexture;\nuniform highp float xT;\nuniform highp float yT;\nuniform highp float r1;\nuniform highp float g1;\nuniform highp float b1;\nuniform highp float r2;\nuniform highp float g2;\nuniform highp float b2;\n\nconst highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n\nvoid main()\n{\n  vec2 textureCoordinate1 =  textureCoordinate;\n  vec2 textureCoordinate2 =  textureCoordinate;\n  textureCoordinate1.y = textureCoordinate1.y - yT;\n  textureCoordinate2.y = textureCoordinate2.y + yT;\n  textureCoordinate1.x = textureCoordinate1.x - xT;\n  textureCoordinate2.x = textureCoordinate2.x + xT;\n  vec4 textureColor1 = texture2D(inputImageTexture, textureCoordinate1);\n  if(textureCoordinate1.x>1.0){\n       textureColor1.r=0.0;\n       textureColor1.g=0.0;\n       textureColor1.b=0.0;\n  } \n  if(textureCoordinate1.x<0.0){\n       textureColor1.r=0.0;\n       textureColor1.g=0.0;\n       textureColor1.b=0.0;\n  } \n  if(textureCoordinate1.y>1.0){\n       textureColor1.r=0.0;\n       textureColor1.g=0.0;\n       textureColor1.b=0.0;\n  } \n  if(textureCoordinate1.y<0.0){\n       textureColor1.r=0.0;\n       textureColor1.g=0.0;\n       textureColor1.b=0.0;\n  } \n  vec4 textureColor2 = texture2D(inputImageTexture, textureCoordinate2);\n  if(textureCoordinate2.x>1.0){\n       textureColor2.r=0.0;\n       textureColor2.g=0.0;\n       textureColor2.b=0.0;\n  } \n  if(textureCoordinate2.x<0.0){\n       textureColor2.r=0.0;\n       textureColor2.g=0.0;\n       textureColor2.b=0.0;\n  } \n  if(textureCoordinate2.y>1.0){\n       textureColor2.r=0.0;\n       textureColor2.g=0.0;\n       textureColor2.b=0.0;\n  } \n  if(textureCoordinate2.y<0.0){\n       textureColor2.r=0.0;\n       textureColor2.g=0.0;\n       textureColor2.b=0.0;\n  } \n  vec4 textureColor11 = vec4(textureColor1.r * r1, textureColor1.g * g1, textureColor1.b * b1, 1.0);\n  vec4 textureColor22 = vec4(textureColor2.r * r2, textureColor2.g * g2, textureColor2.b * b2, 1.0);\n  vec4 textureColor33 = vec4(mix(textureColor11.rgb, textureColor22.rgb, textureColor22.a * 0.5),textureColor11.a);\n  vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n  vec4 textureColor44 = vec4(mix(textureColor11.rgb, textureColor22.rgb, textureColor22.a * 0.5),textureColor11.a);\n  gl_FragColor = vec4(mix(textureColor11.rgb, textureColor22.rgb, textureColor22.a * 0.5),textureColor11.a);\n}";
    private float b1;
    private int b1L;
    private float b2;
    private int b2L;
    private float g1;
    private int g1L;
    private float g2;
    private int g2L;
    private boolean mIsInitialized;
    private float r1;
    private int r1L;
    private float r2;
    private int r2L;
    private float xT;
    private int xTL;
    private float yT;
    private int yTL;

    public GPUImageTestFilter() {
        this(0.1f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    public GPUImageTestFilter(float xT, float yT, float r1, float g1, float b1, float r2, float g2, float b2) {
        super(GPUImageFilter.NO_FILTER_VERTEX_SHADER, GRAYSCALE_FRAGMENT_SHADER);
        this.mIsInitialized = false;
        Log.d("pesan", BuildConfig.FLAVOR + xT + "," + yT + "," + r1 + "," + g1 + "," + b1 + "," + r2 + "," + g2 + "," + b2);
        this.xT = xT;
        this.yT = yT;
        this.r1 = r1;
        this.g1 = g1;
        this.b1 = b1;
        this.r2 = r2;
        this.g2 = g2;
        this.b2 = b2;
    }

    public void onInit() {
        super.onInit();
        this.xTL = GLES20.glGetUniformLocation(getProgram(), "xT");
        this.yTL = GLES20.glGetUniformLocation(getProgram(), "yT");
        this.r1L = GLES20.glGetUniformLocation(getProgram(), "r1");
        this.g1L = GLES20.glGetUniformLocation(getProgram(), "g1");
        this.b1L = GLES20.glGetUniformLocation(getProgram(), "b1");
        this.r2L = GLES20.glGetUniformLocation(getProgram(), "r2");
        this.g2L = GLES20.glGetUniformLocation(getProgram(), "g2");
        this.b2L = GLES20.glGetUniformLocation(getProgram(), "b2");
        setFloat(this.xTL, this.xT);
        setFloat(this.yTL, this.yT);
        setFloat(this.r1L, this.r1);
        setFloat(this.g1L, this.g1);
        setFloat(this.b1L, this.b1);
        setFloat(this.r2L, this.r2);
        setFloat(this.g2L, this.g2);
        setFloat(this.b2L, this.b2);
    }
}
