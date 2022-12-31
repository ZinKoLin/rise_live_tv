package com.rise.live.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rise.live.Model.Category;
import com.rise.live.R;
import com.rise.live.Utils.PrefManager;

import java.util.ArrayList;
import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {

    private Context mCtx;
    private List<Category> categoryList;
    private List<Category> searchList;
    private PrefManager prf;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Category obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemOverflowClickListener) {
        this.mOnItemClickListener = mItemOverflowClickListener;
    }

    public CategoryAdapter(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;
        this.searchList = categoryList;
        this.prf = new PrefManager(mCtx);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category c = searchList.get(position);

        holder.textView.setText(c.name);

        Glide.with(mCtx)
                .load(c.thumb)
                .placeholder(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);


        String[] mColors = {
                "#FFF175",
                "#75EAFF",
                "#B991FF",
                "#8BFF8F",
                "#FF9B94",
                "#E656FF",
                "#7E92FF",
                "#F27DBD",
                "#F07146",
                "#7EAB1D",
                "#4F9A9F",
                "#E707A5",
                "#4E0698",
                "#2E5AE1",
                "#25685D",
                "#5C5DBB",
                "#FE6106",
                "#920363",
                "#ECB10B",
                "#0587D5",
                "#C02642",
                "#B50DF9",
                "#E7A1D5",
                "#673AB7"
        };

        holder.colorBackground.setBackgroundColor(Color.parseColor(mColors[position % 24]));

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        ImageView imageView;
        RelativeLayout colorBackground;

        public CategoryViewHolder(View itemView) {
            super(itemView);


            textView = itemView.findViewById(R.id.tvCategory);
            imageView = itemView.findViewById(R.id.ivCategory);
            colorBackground = itemView.findViewById(R.id.colorBackground);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int p = getAbsoluteAdapterPosition();
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
                    searchList = categoryList;
                } else {
                    ArrayList<Category> filteredList = new ArrayList<>();
                    for (Category row : categoryList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())||row.getDesc().toLowerCase().contains(charString.toLowerCase())) {
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
                searchList = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
