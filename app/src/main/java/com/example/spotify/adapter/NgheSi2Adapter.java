package com.example.spotify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.lifecycle.SavedStateHandle;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify.R;
import com.example.spotify.models.Nghesi;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NgheSi2Adapter extends RecyclerView.Adapter<NgheSi2Adapter.NgheSi2ViewHolder> {
    private List<Nghesi> nghesiList;
    private onClickListener listener;
    public interface onClickListener{
        void onClick(int vitri);
    }
    public NgheSi2Adapter(List<Nghesi> nghesiList) {
        this.nghesiList = nghesiList;
    }

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NgheSi2Adapter.NgheSi2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nghesi2,parent,false);
        return new NgheSi2ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NgheSi2Adapter.NgheSi2ViewHolder holder, int position) {
        Nghesi ns = nghesiList.get(position);
        if(ns==null)
            return;
        holder.txt_tenn.setText(ns.getTenNgheSi());
        Picasso.get().load(ns.getAVT()).into(holder.img_avt);
    }

    @Override
    public int getItemCount() {
        if(nghesiList!=null)
            return nghesiList.size();
        return 0;
    }

    public class NgheSi2ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView img_avt;
        TextView txt_tenn;
        ImageView img_close;
        public NgheSi2ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_avt = itemView.findViewById(R.id.img_item_nghesi2);
            txt_tenn = itemView.findViewById(R.id.txt_item_nghesi2);
            img_close = itemView.findViewById(R.id.img_close_item_nghesi2);
            img_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int vitri = getBindingAdapterPosition();
                        if(vitri!=RecyclerView.NO_POSITION)
                            listener.onClick(vitri);
                    }
                }
            });
        }
    }
}
