package com.example.spotify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotify.R;
import com.example.spotify.models.DSPhat;

import java.util.List;

public class DSphatAdapter extends RecyclerView.Adapter<DSphatAdapter.DSphatViewHolder> {
    private List<DSPhat> dsPhatList;

    public DSphatAdapter(List<DSPhat> dsPhatList) {
        this.dsPhatList = dsPhatList;
    }

    @NonNull
    @Override
    public DSphatAdapter.DSphatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdsphat,parent,false);
        return new DSphatViewHolder(v) ;
    }

    @Override
    public void onBindViewHolder(@NonNull DSphatAdapter.DSphatViewHolder holder, int position) {
        DSPhat dsPhat = dsPhatList.get(position);
        if(dsPhat==null)
            return;
        holder.txt_name.setText(dsPhat.getTenDSPhat());
        Glide.with(holder.itemView.getContext()).load(dsPhat.getAnh()).into(holder.img_anh);
    }

    @Override
    public int getItemCount() {
        if(dsPhatList!=null)
            return dsPhatList.size();
        return 0;
    }

    public class DSphatViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_anh, img_them;
        private TextView txt_name;
        public DSphatViewHolder(@NonNull View itemView) {
            super(itemView);
            img_anh = itemView.findViewById(R.id.img_itemdsphat);
            txt_name = itemView.findViewById(R.id.txt_dsphat_name);
        }
    }
}
