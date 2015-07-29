package com.example.diotek.addressmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by diotek on 2015-07-21.
 */
public class InsertAdapter extends BaseAdapter implements BaseVariables {
    LayoutInflater mInflater = null;
    ArrayList<Item> mItems = null;
    int mLayout = 0;

    public InsertAdapter(Context context, int layout, ArrayList<Item> addressItems) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = addressItems;
        mLayout = layout;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //최초 호출이면 항목 뷰를 생성한다.
        //타입별로 뷰를 다르게 디자인할 수 있으며 높이가 달라도 상관없다.
        if (convertView == null) {
            convertView = mInflater.inflate(mLayout, parent, false);
        }

        TextView mAddressListText = (TextView) convertView.findViewById(R.id.addressListText);
        mAddressListText.setText(mItems.get(position).getmName());

        return convertView;
    }
}