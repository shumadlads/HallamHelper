package com.shumadlads.hallamhelper.hallamhelper.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.R;

import org.w3c.dom.Text;

import java.util.List;

public class RoomsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Room> mRoomList;



    public RoomsAdapter(Context context, List<Room> mRoomList) {
        this.mRoomList = mRoomList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mRoomList.size();
    }

    @Override
    public Object getItem(int i) {
        return mRoomList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mRoomList.get(i).getRoomId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext , R.layout.item_listview, null);
        TextView id = (TextView)v.findViewById(R.id.item_RoomID);
        TextView name = (TextView)v.findViewById(R.id.item_RoomName);
        id.setText(String.valueOf(mRoomList.get(i).getRoomId()));
        name.setText(String.valueOf(mRoomList.get(i).getRoomName()));
        return v;
    }
}
