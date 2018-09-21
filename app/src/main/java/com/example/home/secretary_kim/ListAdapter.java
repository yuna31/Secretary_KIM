package com.example.home.secretary_kim;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private String[][] arrayList;
    private LayoutInflater inflater;
    private boolean isListView;
    private SparseBooleanArray mSelectedItemsIds;

    public ListAdapter(Context context, String[][] arrayList, boolean isListView) {
        this.context = context;
        this.arrayList = arrayList;
        this.isListView = isListView;
        inflater = LayoutInflater.from(context);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        int cnt = 0;
        for(int i=0 ; i<arrayList.length; i++) {
            if(arrayList[i] != null) {
                cnt++;
            }
        }

        return cnt;
        //return 0;
    }

    @Override
    public Object getItem(int i) {
        return arrayList[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();

            //inflate the layout on basis of boolean
            if (isListView)
                view = inflater.inflate(R.layout.item_listview, viewGroup, false);
            else
                view = inflater.inflate(R.layout.item_listview, viewGroup, false);

            viewHolder.label = (TextView) view.findViewById(R.id.label);
            viewHolder.data = (TextView) view.findViewById(R.id.data);
            viewHolder.photo = (ImageView) view.findViewById(R.id.photo);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();


        viewHolder.label.setText(arrayList[i][0]);
        viewHolder.data.setText(arrayList[i][1]);
        viewHolder.photo.setImageResource(R.drawable.ic_user);

        viewHolder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long onClickNumtemp = getItemId(i);
                int onClickNum = onClickNumtemp.intValue();
                detailView(onClickNum, !mSelectedItemsIds.get(i));
            }
        });

        viewHolder.data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long onClickNumtemp = getItemId(i);
                int onClickNum = onClickNumtemp.intValue();
                detailView(onClickNum, !mSelectedItemsIds.get(i));
            }
        });

        viewHolder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long onClickNumtemp = getItemId(i);
                int onClickNum = onClickNumtemp.intValue();
                detailView(onClickNum, !mSelectedItemsIds.get(i));
            }
        });

        return view;
    }

    private class ViewHolder {
        private TextView label;
        private TextView data;
        private ImageView photo;
    }

    public void detailView(int position, boolean value) {
        Intent intent = new Intent(this.context, S3DownloadActivity.class);
        intent.putExtra("UserMail", arrayList[position][2]);
        context.startActivity(intent);
    }
}
