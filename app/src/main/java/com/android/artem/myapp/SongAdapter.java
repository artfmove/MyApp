package com.android.artem.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

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
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.titleTextView.setText(mData.get(position).getTitle());
            holder.groupTextView.setText(mData.get(position).getGroup());

            Picasso.get().load(mData.get(position).getImage()).fit().centerInside()
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
                    intent.putExtra("id", mData.get(position).getId());
                    intent.putExtra("image", mData.get(position).getImage());
                    intent.putExtra("context", String.valueOf(mContext));
                   if(mContext instanceof SearchListActivity) {
                    intent.putExtra("context", "1");
                   }else{
                       intent.putExtra("context", "2");
                   }
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

            //private ImageView addSongImageView;

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
