package com.example.spotify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify.R;
import com.example.spotify.models.Nghesi;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NgheSi_1Adapter extends RecyclerView.Adapter<NgheSi_1Adapter.NgheSi1ViewHolder> {
    private List<Nghesi> nghesiList;
    private onClickListener listener;
    public interface onClickListener{
        void onClickItem(Nghesi ns);
    }

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public NgheSi_1Adapter(List<Nghesi> nghesiList) {
        this.nghesiList = nghesiList;
    }
    public void updateData(List<Nghesi> newItems) {
        this.nghesiList.clear();
        this.nghesiList=newItems;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NgheSi_1Adapter.NgheSi1ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nghesi1,parent,false);
        return new NgheSi1ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NgheSi_1Adapter.NgheSi1ViewHolder holder, int position) {
        Nghesi ns = nghesiList.get(position);
        if(ns==null)
            return;
        holder.txt_ns.setText(ns.getTenNgheSi());
        Picasso.get().load(ns.getAVT()).into(holder.img_avt);
    }

    @Override
    public int getItemCount() {
        if(nghesiList!=null)
            return nghesiList.size();
        return 0;
    }

    public class NgheSi1ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_ns;
        ShapeableImageView img_avt;
        public NgheSi1ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_ns = itemView.findViewById(R.id.txt_itemnghesi1);
            img_avt = itemView.findViewById(R.id.img_itemnghesi1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int vitri = getBindingAdapterPosition();
                        if(vitri != RecyclerView.NO_POSITION)
                            listener.onClickItem(nghesiList.get(vitri));
                    }
                }
            });
        }
    }
}
