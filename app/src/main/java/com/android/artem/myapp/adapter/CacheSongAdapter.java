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

           /* Picasso.get().load(mData.get(position).getImage()).fit().centerInside()
                    .into(holder.previewImageView);
            */
        Glide.with(mContext)
                .load(mData.get(position).getImageUrl()) // image url
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_music_note_white_24dp) // any placeholder to load at start
                .error(R.drawable.ic_music_note_white_24dp)  // any image in case of error
                // resizing
                .into(holder.previewImageView);
            /*if(mContext instanceof SearchListActivity) {
                holder.addSongImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser user = auth.getCurrentUser();
                        songsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SongsFav").child(user.getUid());

                        Song song = new Song();
                        song.setTitle(mData.get(position).getTitle());
                        song.setId(mData.get(position).getId());
                        song.setGroup(mData.get(position).getGroup());
                        song.setImage(mData.get(position).getImage());
                        songsDatabaseReference.push().setValue(song);
                        holder.addSongImageView.setImageResource(R.drawable.ic_check_black_24dp);

                    }
                });
            }else{
                holder.addSongImageView.setVisibility(View.GONE);
            }*/
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SongActivity.class);

                //passing data to book activity
                intent.putExtra("title", mData.get(position).getTitle());
                intent.putExtra("group", mData.get(position).getGroup());
                intent.putExtra("id", mData.get(position).getNetUrl());
                intent.putExtra("image", mData.get(position).getImageUrl());
                intent.putExtra("context", String.valueOf(mContext));
                  /* if(mContext instanceof SearchListActivity) {
                    intent.putExtra("context", "1");
                   }else{
                       intent.putExtra("context", "2");
                   }*/
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
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            previewImageView = itemView.findViewById(R.id.previewImageView);


            //addSongImageView = itemView.findViewById(R.id.addSongImageView);



        }
    }



}
