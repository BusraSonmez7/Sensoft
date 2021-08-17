package com.sensofttakimi.sensoft.FragmentMesaj;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sensofttakimi.sensoft.Bildirimler.BildirimIcerigi;
import com.sensofttakimi.sensoft.Model.Sohbetler;
import com.sensofttakimi.sensoft.R;
import com.sensofttakimi.sensoft.UygulamaSayfasi;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SohbetAdapter extends RecyclerView.Adapter<SohbetAdapter.ViewHolder> {

    private Context mContext;
    private List<Sohbetler> mSohbetler;

    public SohbetAdapter(Context mContext, List<Sohbetler> mSohbetler){
        this.mContext = mContext;
        this.mSohbetler = mSohbetler;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sohbet_item,parent,false);
        return new SohbetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SohbetAdapter.ViewHolder holder, int position) {
        Sohbetler sohbetler = mSohbetler.get(position);
        holder.sohbet_baslik.setText(sohbetler.getSohbet_baslik());
        holder.sohbet_tarih.setText(sohbetler.getSohbet_tarihi().toString());
        if(sohbetler.getSohbet_resmi().equals("default")){
            holder.sohbet_resim.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(mContext).load(sohbetler.getSohbet_resmi()).into(holder.sohbet_resim);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(holder.itemView.getContext(), SohbetMesajlariActivity.class);
                intent.putExtra("baslik",sohbetler.getSohbet_baslik());
                intent.putExtra("sohbetID","true");
                holder.itemView.getContext().startActivity(intent);
            }
        });
        //holder.sohbet_resim.setText(sohbetler.getSohbet_resmi());

    }

    @Override
    public int getItemCount() {
        return mSohbetler.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sohbet_baslik;
        public TextView sohbet_tarih;
        public ImageView sohbet_resim;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            sohbet_baslik = itemView.findViewById(R.id.sohbet_baslik);
            sohbet_tarih = itemView.findViewById(R.id.sohbet_tarihi);
            sohbet_resim = itemView.findViewById(R.id.sohbet_resim);
        }
    }
}
