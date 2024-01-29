package com.photoeditor.photoeffect3d.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoeditor.photoeffect3d.R;
import com.photoeditor.photoeffect3d.model.DataItem;
import com.photoeditor.photoeffect3d.model.Warna;

import java.util.List;

public class FilterAdapter extends Adapter<FilterAdapter.MyViewHolder> {
    private Context context;
    private List<DataItem> moviesList;
    public ViewGroup parents;

    public class MyViewHolder extends ViewHolder {
        public TextView genre;
        public ImageView image;
        public TextView title;
        public TextView year;

        public MyViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.image_preview);

            this.title = (TextView) view.findViewById(R.id.title);
        }
    }

    public FilterAdapter(Context mcontext, List<DataItem> moviesList) {
        this.context = mcontext;
        this.moviesList = moviesList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_list, parent, false);
        this.parents = parent;
        Log.d("pesan", "panjang x=" + this.parents.getWidth() + ",y=" + this.parents.getHeight());
        return new MyViewHolder(itemView);
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataItem movie = (DataItem) this.moviesList.get(position);
        Log.d("pesan", "panjang x=" + this.parents.getWidth() + ",y=" + this.parents.getHeight());
        holder.image.setImageBitmap(createCheckerBoard(200.0f, movie.getWarna1(), movie.getWarna2()));
    }

    private Bitmap createCheckerBoard(float size, Warna warna1, Warna warna2) {
        Bitmap tempBitmap = Bitmap.createBitmap((int) size, (int) size, Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        new Paint().setColor(Color.rgb((int) (warna1.getR() * 255.0f), (int) (warna1.getG() * 255.0f), (int) (warna1.getB() * 255.0f)));
        new Paint().setColor(Color.rgb((int) (warna2.getR() * 255.0f), (int) (warna2.getG() * 255.0f), (int) (warna2.getB() * 255.0f)));
        Paint paint3 = new Paint();
        paint3.setShader(new LinearGradient(0.0f, 0.0f, size, 0.0f, Color.rgb((int) (warna1.getR() * 255.0f), (int) (warna1.getG() * 255.0f), (int) (warna1.getB() * 255.0f)), Color.rgb((int) (warna2.getR() * 255.0f), (int) (warna2.getG() * 255.0f), (int) (warna2.getB() * 255.0f)), TileMode.MIRROR));
        tempCanvas.drawRect(0.0f, 0.0f, size, size, paint3);
        return tempBitmap;
    }

    public int getItemCount() {
        return this.moviesList.size();
    }
}
