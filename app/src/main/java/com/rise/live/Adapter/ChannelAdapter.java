package com.rise.live.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rise.live.Model.Channel;
import com.rise.live.R;
import com.rise.live.Utils.PrefManager;

import java.util.ArrayList;
import java.util.List;


public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.CategoryViewHolder> implements Filterable{

    private Context mCtx;
    private List<Channel> wallpaperList;
    private List<Channel> searchList;
    private PrefManager prf;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Channel obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemOverflowClickListener) {
        this.mOnItemClickListener = mItemOverflowClickListener;
    }

    public ChannelAdapter(Context mCtx, List<Channel> wallpaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;
        this.searchList = wallpaperList;
        prf= new PrefManager(mCtx);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (prf.getString("wallpaperColumnsString").equals("List") ){
            View view = LayoutInflater.from(mCtx).inflate(R.layout.item_channel_list, parent, false);
            return new CategoryViewHolder(view);
        }else {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.item_channel_grid, parent, false);
            return new CategoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Channel channel = searchList.get(position);

        if (prf.getString("wallpaperColumnsString").equals("List")) {
            Glide.with(mCtx)
                    .load(channel.getChannelLanguage())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
        }else {

            Glide.with(mCtx)
                    .load(channel.getChannelImage())
                    .placeholder(R.drawable.logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
        }

        holder.textView.setText(channel.getChannelName());
        holder.mainCategory.setText(channel.getChannelCategory());


    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView imageView;
        TextView textView,mainCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.ivCategory);
            textView = itemView.findViewById(R.id.tvCategory);
            mainCategory = itemView.findViewById(R.id.mainCategory);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int p = getAdapterPosition();
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, searchList.get(p), p);
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    searchList = wallpaperList;
                } else {
                    ArrayList<Channel> filteredList = new ArrayList<>();
                    for (Channel row : wallpaperList) {
                        if (row.getChannelName().toLowerCase().contains(charString.toLowerCase())||row.getChannelCategory().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    searchList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = searchList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                searchList = (ArrayList<Channel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
