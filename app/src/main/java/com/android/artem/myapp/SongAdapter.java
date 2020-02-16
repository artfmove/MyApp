package com.android.artem.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



    public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

        private Context mContext;
        private List<Song> mData;

        public SongAdapter(Context mContext, List<Song> mData) {
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
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            holder.titleTextView.setText(mData.get(position).getTitle());
            holder.groupTextView.setText(mData.get(position).getGroup());
            //holder.titleTextView.setText(mData.get(position).getTitle());

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SongActivity.class);

                    //passing data to book activity
                    intent.putExtra("Title", mData.get(position).getTitle());
                    intent.putExtra("Group", mData.get(position).getGroup());
                    intent.putExtra("Id", mData.get(position).getId());

                    //start activity
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
            //private ImageView previewImageView;
            private CardView cardView;

            public MyViewHolder(View itemView){
                super(itemView);


                titleTextView = itemView.findViewById(R.id.titleTextView);
                groupTextView = itemView.findViewById(R.id.groupTextView);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                //previewImageView = itemView.findViewById(R.id.previewImageView);

            }
        }



    }
