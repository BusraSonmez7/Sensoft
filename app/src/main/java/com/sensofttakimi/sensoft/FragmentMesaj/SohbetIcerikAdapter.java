package com.sensofttakimi.sensoft.FragmentMesaj;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sensofttakimi.sensoft.Bildirimler.BildirimIcerigi;
import com.sensofttakimi.sensoft.Model.SohbetIcerigi;
import com.sensofttakimi.sensoft.Model.Sohbetler;
import com.sensofttakimi.sensoft.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SohbetIcerikAdapter extends RecyclerView.Adapter<SohbetIcerikAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<SohbetIcerigi> mSohbetIcerigi;

    FirebaseUser firebaseUser;

    public SohbetIcerikAdapter(Context mContext, List<SohbetIcerigi> mSohbetler){
        this.mContext = mContext;
        this.mSohbetIcerigi = mSohbetler;
    }

    @NonNull
    @NotNull
    @Override
    public SohbetIcerikAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       if(viewType == MSG_TYPE_RIGHT){
           View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
           return new SohbetIcerikAdapter.ViewHolder(view);
       }
       else {
           View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
           return new SohbetIcerikAdapter.ViewHolder(view);
       }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SohbetIcerikAdapter.ViewHolder holder, int position) {
        SohbetIcerigi sohbetIcerigi = mSohbetIcerigi.get(position);
        holder.show_message.setText(sohbetIcerigi.getMesaj());
    }

    @Override
    public int getItemCount() {
        return mSohbetIcerigi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        public TextView show_message;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            show_message.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),121,0,"Sil");
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mSohbetIcerigi.get(position).getRol().equals("metin")){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
    public void removeItem(int position) {
        mSohbetIcerigi.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSohbetIcerigi.size());
    }

    public String BaslikListele(int position){
        String baslik = mSohbetIcerigi.get(position).getMesaj();
        return baslik;
    }

}