package com.ccit19.merdog_doctor;


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
import java.util.List;
import java.util.Locale;

public class RecordsAdapter extends BaseAdapter {
    private String timeStamp, datetime, dt, datetHour;
    private Date date;
    private int time, time2;
    LayoutInflater inflater = null;
    private ArrayList<RecordsViewHolder> m_oData = null;
    private ArrayList<RecordsViewHolder> arrayList;
    private int nListCnt = 0;
    Context mContext;

    public RecordsAdapter(ArrayList<RecordsViewHolder> m_oData, Context context) {
        this.m_oData = m_oData;
        this.nListCnt = m_oData.size();
        this.mContext = context;
        this.arrayList = new ArrayList<RecordsViewHolder>();
        this.arrayList.addAll(m_oData);
    }

    @Override
    public int getCount() {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position) {
        return m_oData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.row_recordslist, parent, false);
        }


        timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = sdf.parse(m_oData.get(position).date);
            dt = new SimpleDateFormat("yyyyMMdd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        time = Integer.parseInt(timeStamp);
        time2 = Integer.parseInt(dt);

//        if (timeStamp.equals(dt)) {
//            datetime = new SimpleDateFormat("a hh:mm").format(date);
//        } else if (time - time2 == 1) {
//            datetime = "어제";
//        } else {
        datetime = new SimpleDateFormat("MM월 dd일").format(date);
        datetHour = new SimpleDateFormat("HH시 mm분").format(date);
//        }


        TextView Records_Time = (TextView) convertView.findViewById(R.id.Records_Time);
//        TextView Records_Hour = (TextView) convertView.findViewById(R.id.Records_Hour);
        TextView Records_dog = (TextView) convertView.findViewById(R.id.Records_dog);
        TextView Records_name = (TextView) convertView.findViewById(R.id.Records_name);


        Records_Time.setText(datetime + "\n" + datetHour);
//        Records_Hour.setText(datetHour);
        Records_dog.setText(m_oData.get(position).pet_name);
        Records_name.setText(m_oData.get(position).user_name);

        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        m_oData.clear();
        if (charText.length() == 0) {
            m_oData.addAll(arrayList);
        } else {
            for (RecordsViewHolder RecordsViewHolder : arrayList) {
                if (RecordsViewHolder.get_user_name().toLowerCase().contains(charText)) {
                    m_oData.add(RecordsViewHolder);
                }
            }
        }
        notifyDataSetChanged();
    }
}

