package com.android.artem.myapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.artem.myapp.R;
import com.android.artem.myapp.model.Cache;
import com.android.artem.myapp.model.Song;
import com.android.artem.myapp.activities.SearchListActivity;
import com.android.artem.myapp.activities.SongActivity;
import com.android.artem.myapp.util.mColor;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



public class CacheSongAdapter extends RecyclerView.Adapter<CacheSongAdapter.MyViewHolder> {


    private Context mContext;
    private List<Cache> mData;

    public CacheSongAdapter(Context mContext, List<Cache> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.song_item,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.titleTextView.setText(mData.get(position).getTitle());
        holder.groupTextView.setText(mData.get(position).getGroup());



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SongActivity.class);

                intent.putExtra("title", mData.get(position).getTitle());
                intent.putExtra("group", mData.get(position).getGroup());
                intent.putExtra("id", mData.get(position).getNetUrl());


                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView titleTextView, groupTextView;
        private ImageView previewImageView;
        private CardView cardView;


        public MyViewHolder(View itemView){
            super(itemView);


            titleTextView = itemView.findViewById(R.id.titleTextView);
            groupTextView = itemView.findViewById(R.id.groupTextView);
            cardView = itemView.findViewById(R.id.cardView);
            previewImageView = itemView.findViewById(R.id.previewImageView);

            previewImageView.setBackgroundColor(mColor.getGcolors()[1]);



        }
    }



}
