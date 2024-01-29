package com.photoeditor.photoeffect3d.model;

public class Warna {
    float b = 0.0f;
    float g = 0.0f;
    float r = 0.0f;

    public Warna(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public float getR() {
        return this.r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public float getG() {
        return this.g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public float getB() {
        return this.b;
    }

    public void setB(float b) {
        this.b = b;
    }
}
