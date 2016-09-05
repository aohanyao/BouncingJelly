package com.aohanyao.bouncingjelly.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aohanyao.bouncingjelly.R;

/**
 * <p>作者：江俊超 on 2016/8/31 11:00</p>
 * <p>邮箱：928692385@qq.com</p>
 * <p></p>
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;

    public RecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_list_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 13;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
