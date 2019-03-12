package com.liuwan.demo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jin on 3/12/2019
 */
public class TimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> timeList = new ArrayList<>();

    public TimeAdapter(List<String> timeList) {
        this.timeList = timeList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return createNewsViewHolder(viewGroup);
    }


    private TimeViewHolder createNewsViewHolder(ViewGroup viewGroup) {
        //2.实例化子布局
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_time_my, viewGroup, false);
        //3.获得一个ViewHolder实例
        return new TimeViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindViewForNews(holder, position);
    }


    private void bindViewForNews(RecyclerView.ViewHolder holder, int position) {
        TimeViewHolder timeViewHolder = (TimeViewHolder) holder;
        final String time = getItem(position);

        //将数据填充进去
        timeViewHolder.tv_time.setText(time);
        timeViewHolder.tv_delete.setTag(position);
        //点击事件
        timeViewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                removeData(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeList.size();
    }


    protected String getItem(int position) {
        return timeList.get(position);
    }


    //1.初始化自己的ViewHolder
    static class TimeViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_time;
        public TextView tv_delete;

        public TimeViewHolder(View itemView) {
            super(itemView);
            //获取子布局的控件实例
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_delete = itemView.findViewById(R.id.tv_delete);
        }
    }

    //添加数据
    public void addData(String time) {
        timeList.add(time);
        notifyDataSetChanged();
    }

    //  删除数据
    public void removeData(int position) {
        timeList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


}
