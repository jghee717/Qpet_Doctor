package com.ccit19.merdog_doctor.chat;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ccit19.merdog_doctor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoomAdapter extends BaseAdapter {
    private String timeStamp, datetime, dt;
    private Date date;
    private int time, time2;
    LayoutInflater inflater = null;
    private ArrayList<ChatRoomView> m_oData = null;
    private int nListCnt = 0;
    Context mContext;

    public ChatRoomAdapter(ArrayList<ChatRoomView> _oData, Context context) {
        m_oData = _oData;
        nListCnt = m_oData.size();
        mContext = context;
    }

    @Override
    public int getCount() {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        }

        timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = sdf.parse(m_oData.get(position).get_date);
            dt = new SimpleDateFormat("yyyyMMdd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        time = Integer.parseInt(timeStamp);
        time2 = Integer.parseInt(dt);

        if (timeStamp.equals(dt)) {
            datetime = new SimpleDateFormat("a hh:mm").format(date);
        } else if (time - time2 == 1) {
            datetime = "어제";
        } else {
            datetime = new SimpleDateFormat("MM월 dd일").format(date);
        }


        TextView RoomTime = (TextView) convertView.findViewById(R.id.RoomTime);
        TextView RoomText = (TextView) convertView.findViewById(R.id.RoomText);
        TextView RoomTitle = (TextView) convertView.findViewById(R.id.RoomTitle);
        ImageView RoomAvatar = (ImageView) convertView.findViewById(R.id.RoomAvatar);

        String s = m_oData.get(position).get_chat_room;

        RoomTime.setText(datetime);
        RoomText.setText(m_oData.get(position).get_message);
        RoomTitle.setText(m_oData.get(position).get_pet_name);

        Glide.with(mContext).load(m_oData.get(position).get_pet_img).into(RoomAvatar);

        return convertView;
    }
}

