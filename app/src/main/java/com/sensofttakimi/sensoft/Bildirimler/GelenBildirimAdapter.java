package com.sensofttakimi.sensoft.Bildirimler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.databinding.BildirimItemBinding;
import com.sensofttakimi.sensoft.databinding.RecyclerRowBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GelenBildirimAdapter extends RecyclerView.Adapter<GelenBildirimAdapter.GelenBildirimHolder>  {

    private ArrayList<Bildirimler> bildirimlerArrayList;
    public Context context;

    public GelenBildirimAdapter(ArrayList<Bildirimler> bildirimlerArrayList, Context context){
        this.bildirimlerArrayList = bildirimlerArrayList;
        this.context = context;
    }


    @NonNull
    @NotNull
    @Override
    public GelenBildirimHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BildirimItemBinding bildirimItemBinding = BildirimItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new GelenBildirimAdapter.GelenBildirimHolder(bildirimItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GelenBildirimAdapter.GelenBildirimHolder holder, int position) {
        holder.bildirimItemBinding.baslik.setText(bildirimlerArrayList.get(position).baslik.toUpperCase());
        holder.bildirimItemBinding.icerik.setText(bildirimlerArrayList.get(position).aciklama);
        holder.bildirimItemBinding.tarih.setText(bildirimlerArrayList.get(position).tarih);
        holder.bildirimItemBinding.saat.setText(bildirimlerArrayList.get(position).saat);
        Picasso.get().load(bildirimlerArrayList.get(position).resim).into(holder.bildirimItemBinding.resim);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(holder.itemView.getContext(),BildirimIcerigi.class);
//                intent.putExtra("baslik",holder.recyclerRowBinding.listBaslik.getText().toString());
//                holder.itemView.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return bildirimlerArrayList.size();
    }

    class GelenBildirimHolder extends RecyclerView.ViewHolder{

        BildirimItemBinding bildirimItemBinding;
        public GelenBildirimHolder(BildirimItemBinding bildirimItemBinding) {
            super(bildirimItemBinding.getRoot());
            this.bildirimItemBinding = bildirimItemBinding;

        }

    }
    public void removeItem(int position) {
        bildirimlerArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, bildirimlerArrayList.size());
    }
}
