package com.cwf.demo.rxjavademo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created at 陈 on 2016/6/29.
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public class MyAdapter extends BaseAdapter {
    private List<Item> itemList;

    public MyAdapter(List<Item> list) {
        itemList = list;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Item getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Glide.with(parent.getContext())
                .load(getItem(position).getUrl())
                .into(viewHolder.imageView);
        return convertView;
    }


    private class ViewHolder {
        ImageView imageView;
    }
}
