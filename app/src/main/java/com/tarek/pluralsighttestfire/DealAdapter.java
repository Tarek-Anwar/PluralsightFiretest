package com.tarek.pluralsighttestfire;

import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DealAdapter extends RecyclerView.Adapter<DealAdapter.TravelViewHolder> {

    ArrayList<TravelDeal> models;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public DealAdapter() {
      //  FirebaseUtil.openFbReference("traveldeals");
        firebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        databaseReference = FirebaseUtil.mDatabaseReference;
        models = FirebaseUtil.trevalModels;

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                TravelDeal tm = snapshot.getValue(TravelDeal.class);
                tm.setId(snapshot.getKey());
                models.add(tm);
                notifyItemChanged(models.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_travel,parent,false);
        return new TravelViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        holder.bulidUI(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class TravelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title , desc , price ;
        ImageView imageView ;

        public TravelViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_travel);
            desc = itemView.findViewById(R.id.tv_desc);
            price = itemView.findViewById(R.id.tv_price);
            imageView = itemView.findViewById(R.id.img_tv);
            itemView.setOnClickListener(this);
        }

        public  void bulidUI(TravelDeal model){
            title.setText(model.getTitle());
            desc.setText(model.getDescrption());
            price.setText(model.getPrice());
            showImage(model.getImageUrl());

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Intent intent = new Intent(v.getContext(), DealActivity.class);
            intent.putExtra("model",models.get(position));
            v.getContext().startActivity(intent);

        }

        private void showImage(String url){
            if(url!=null && url.isEmpty()==false){
                Picasso.get().load(url).resize(80, 80)
                        .centerCrop()
                        .into(imageView);
            }

        }
    }


}
