package com.sensofttakimi.sensoft.Bildirimler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sensofttakimi.sensoft.databinding.RecyclerRowBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BildirimAdapter extends RecyclerView.Adapter<BildirimAdapter.BildirimHolder> {

    private ArrayList<Bildirimler> bildirimlerArrayList;
    public Context context;

    public BildirimAdapter(ArrayList<Bildirimler> bildirimlerArrayList, Context context){
        this.bildirimlerArrayList = bildirimlerArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BildirimHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new BildirimHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BildirimAdapter.BildirimHolder holder, int position) {
        holder.recyclerRowBinding.listBaslik.setText(bildirimlerArrayList.get(position).baslik);
        //holder.recyclerRowBinding.listAciklama.setText(bildirimlerArrayList.get(position).aciklama);
        //holder.recyclerRowBinding.listTarih.setText(bildirimlerArrayList.get(position).tarih.toString());
        Picasso.get().load(bildirimlerArrayList.get(position).resim).into(holder.recyclerRowBinding.listResim);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),BildirimIcerigi.class);
                intent.putExtra("baslik",holder.recyclerRowBinding.listBaslik.getText().toString());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bildirimlerArrayList.size();
    }

    class BildirimHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;
        public BildirimHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;

        }

    }
    public void removeItem(int position) {
        bildirimlerArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, bildirimlerArrayList.size());
    }
}
