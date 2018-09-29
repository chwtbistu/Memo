package com.example.memo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class QueryAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Memory> list;

    public QueryAdapter(Context mContext, ArrayList<Memory> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null) {
            view = View.inflate(mContext, R.layout.item, null);
        }
        else {
            view = convertView;
        }
        TextView title = (TextView)view.findViewById(R.id.memo_title);
        TextView content = (TextView)view.findViewById(R.id.memo_content);
        TextView date = (TextView)view.findViewById(R.id.memo_date);
        Memory memo = list.get(position);
        if(!memo.getTitle().isEmpty())
            title.setText(memo.getTitle());
        else
            title.setText("标题未定义");
        content.setText(memo.getContent());
        date.setText(memo.getDate());
        return view;
    }
}
