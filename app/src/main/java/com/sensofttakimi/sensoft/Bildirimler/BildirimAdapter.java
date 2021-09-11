package com.sensofttakimi.sensoft.Bildirimler;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sensofttakimi.sensoft.Model.Bildirimler;
import com.sensofttakimi.sensoft.R;
import com.sensofttakimi.sensoft.databinding.RecyclerRowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BildirimAdapter extends RecyclerView.Adapter<BildirimAdapter.BildirimHolder>{

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
        holder.recyclerRowBinding.listBaslik.setText(bildirimlerArrayList.get(position).baslik.toUpperCase());
        //holder.recyclerRowBinding.listAciklama.setText(bildirimlerArrayList.get(position).aciklama);
        //holder.recyclerRowBinding.listTarih.setText(bildirimlerArrayList.get(position).tarih.toString());
        if(bildirimlerArrayList.get(position).resim.equals("default")){
            holder.recyclerRowBinding.listResim.setImageResource(R.drawable.notification);
        }
        else {
            Picasso.get().load(bildirimlerArrayList.get(position).resim).into(holder.recyclerRowBinding.listResim);
        }
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(holder.itemView.getContext(),BildirimIcerigi.class);
//                intent.putExtra("baslik",holder.recyclerRowBinding.listBaslik.getText().toString());
//                holder.itemView.getContext().startActivity(intent);
//            }
//        });
       // holder.recyclerRowBinding.cardview11.setOnCreateContextMenuListener();
    }

    @Override
    public int getItemCount() {
        return bildirimlerArrayList.size();
    }


    class BildirimHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        RecyclerRowBinding recyclerRowBinding;
        CardView cardView;
        public BildirimHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
            recyclerRowBinding.cardview11.setOnCreateContextMenuListener(this);
            recyclerRowBinding.cardview11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),BildirimIcerigi.class);
                    intent.putExtra("baslik",recyclerRowBinding.listBaslik.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });


        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),121,0,"Sil");
            menu.add(this.getAdapterPosition(),122,1,"Güncelle");

        }
    }

    public void removeItem(int position) {
        bildirimlerArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, bildirimlerArrayList.size());
    }

    public String BaslikListele(int position){
        String baslik = bildirimlerArrayList.get(position).baslik;
        return baslik;
    }

}
