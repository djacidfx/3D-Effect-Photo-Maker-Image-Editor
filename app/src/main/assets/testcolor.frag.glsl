#extension GL_OES_EGL_image_external : require

//necessary
precision mediump float;
uniform samplerExternalOES camTexture;

varying vec2 v_CamTexCoordinate;
varying vec2 v_TexCoordinate;

uniform float offsetR;
uniform float offsetG;
uniform float offsetB;


uniform highp float xT;
uniform highp float yT;
//
uniform highp float r1;
uniform highp float g1;
uniform highp float b1;
//
uniform highp float r2;
uniform highp float g2;
uniform highp float b2;


void main ()
{

    vec4 cameraColor = texture2D(camTexture, v_CamTexCoordinate);

    vec2 textureCoordinate1 =  v_CamTexCoordinate;
    vec2 textureCoordinate2 =  v_CamTexCoordinate;
    //
    textureCoordinate1.y = textureCoordinate1.y - yT;
    textureCoordinate2.y = textureCoordinate2.y + yT;
    textureCoordinate1.x = textureCoordinate1.x - xT;
    textureCoordinate2.x = textureCoordinate2.x + xT;

    //
    vec4 textureColor1 = texture2D(camTexture, textureCoordinate1);
    if(textureCoordinate1.x>1.0){
        textureColor1.r=0.0;
        textureColor1.g=0.0;
        textureColor1.b=0.0;
    }
    if(textureCoordinate1.x<0.0){
        textureColor1.r=0.0;
        textureColor1.g=0.0;
        textureColor1.b=0.0;
    }
    if(textureCoordinate1.y>1.0){
        textureColor1.r=0.0;
        textureColor1.g=0.0;
        textureColor1.b=0.0;
    }
    if(textureCoordinate1.y<0.0){
        textureColor1.r=0.0;
        textureColor1.g=0.0;
        textureColor1.b=0.0;
    }
    vec4 textureColor2 = texture2D(camTexture, textureCoordinate2);
    if(textureCoordinate2.x>1.0){
        textureColor2.r=0.0;
        textureColor2.g=0.0;
        textureColor2.b=0.0;
    }
    if(textureCoordinate2.x<0.0){
        textureColor2.r=0.0;
        textureColor2.g=0.0;
        textureColor2.b=0.0;
    }
    if(textureCoordinate2.y>1.0){
        textureColor2.r=0.0;
        textureColor2.g=0.0;
        textureColor2.b=0.0;
    }
    if(textureCoordinate2.y<0.0){
        textureColor2.r=0.0;
        textureColor2.g=0.0;
        textureColor2.b=0.0;
    }
    //
    vec4 textureColor11 = vec4(textureColor1.r * r1, textureColor1.g * g1, textureColor1.b * b1, 1.0);
    vec4 textureColor22 = vec4(textureColor2.r * r2, textureColor2.g * g2, textureColor2.b * b2, 1.0);


    gl_FragColor = vec4(mix(textureColor11.rgb, textureColor22.rgb, textureColor22.a * 0.5),textureColor11.a);
}