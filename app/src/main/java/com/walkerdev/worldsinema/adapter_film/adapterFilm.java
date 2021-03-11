package com.walkerdev.worldsinema.adapter_film;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.walkerdev.worldsinema.R;

import java.util.List;

public class adapterFilm {
    private LayoutInflater inflater;
    private List<dataFilm> data;
    private Context context;
    public adapterFilm(Context context, List<dataFilm> myData) {
        this.context = context;
        this.inflater = inflater;
        this.data = myData;
    }

    public adapterFilm(View v) {
    }

    public void onBindViewHolder(adapterFilm.filmViewHolder holder, int position) {
        dataFilm ndata = data.get(position);
        Picasso.with(context)
                .load("http://cinema.areas.su/up/images/" + ndata.moviePhoto)
                .placeholder(R.drawable.simple_icon)
                .error(R.drawable.simple_icon)
                .into(holder.Photo);
    }
    public adapterFilm onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_film, viewGroup, false);
        return new adapterFilm(v);
    }
    public static class filmViewHolder extends RecyclerView.ViewHolder {
        ImageView Photo;
        filmViewHolder(View itemView) {
            super(itemView);
            Photo = itemView.findViewById(R.id.img_to_adapter_film);
        }
    }

}
