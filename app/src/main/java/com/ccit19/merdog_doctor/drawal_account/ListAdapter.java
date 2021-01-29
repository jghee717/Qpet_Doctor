package com.ccit19.merdog_doctor.drawal_account;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ccit19.merdog_doctor.R;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    String doctor_num;
    private TextView withdraw_id, price, fee, get_money, state, date;
    LayoutInflater inflater = null;
    private ArrayList<ItemData> m_oData = null;
    private int nListCnt = 0;

    public ListAdapter(ArrayList<ItemData> _oData) {
        m_oData = _oData;
        nListCnt = m_oData.size();
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
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView withdraw_id = (TextView) convertView.findViewById(R.id.withdraw_id);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView fee = (TextView) convertView.findViewById(R.id.fee);
        TextView get_money = (TextView) convertView.findViewById(R.id.get_money);
        TextView state = (TextView) convertView.findViewById(R.id.state);
        TextView date = (TextView) convertView.findViewById(R.id.date);

        TextView withdraw_idtext = (TextView) convertView.findViewById(R.id.withdraw_idtext);
        TextView pricetext = (TextView) convertView.findViewById(R.id.pricetext);
        TextView feetext = (TextView) convertView.findViewById(R.id.feetext);
        TextView get_moneytext = (TextView) convertView.findViewById(R.id.get_moneytext);
        TextView statetext = (TextView) convertView.findViewById(R.id.statetext);
        TextView datetext = (TextView) convertView.findViewById(R.id.datetext);


        if (position > 0) {
            withdraw_idtext.setVisibility(View.GONE);
            pricetext.setVisibility(View.GONE);
            feetext.setVisibility(View.GONE);
            get_moneytext.setVisibility(View.GONE);
            statetext.setVisibility(View.GONE);
            datetext.setVisibility(View.GONE);
        }

        withdraw_id.setText(m_oData.get(position).withdraw_ids);
        price.setText(m_oData.get(position).prices);
        fee.setText(m_oData.get(position).fees);
        get_money.setText(m_oData.get(position).get_moneys);
        state.setText(m_oData.get(position).states);
        date.setText(m_oData.get(position).dates);

        return convertView;
    }
}

