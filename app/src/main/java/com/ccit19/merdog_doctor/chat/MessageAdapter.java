package com.ccit19.merdog_doctor.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.SaveSharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MessageAdapter extends BaseAdapter {
    private String id_type, datetime, defore_datetime = "", time, before_time = "";
    private Date date, before_date;
    private long all = 0;
    List<Message> messages = new ArrayList<Message>();
    Context context;


    public MessageAdapter(Context context) {
        this.context = context;
    }


    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    String timeStamp = new SimpleDateFormat("a hh:mm").format(new Date());

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);
        // 데이터 형식 변경

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            // 현재 yyyymmdd로된 날짜 형식으로 java.util.Date객체를 만든다.
            date = sdf.parse(message.getdate());
            time = new SimpleDateFormat("a hh:mm").format(date);
            datetime = new SimpleDateFormat("yyyy년 MM월 dd일 E요일").format(date);

            if (i > 0) {
                before_date = sdf.parse(messages.get(i - 1).getdate());
                before_time = new SimpleDateFormat("a hh:mm").format(before_date);
                defore_datetime = new SimpleDateFormat("yyyy년 MM월 dd일 E요일").format(before_date);
                all = (date.getTime() - before_date.getTime()) / 60000;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (message.isBelongsToCurrentUser()) {


            //아이디 타입이 의사일경우 아이디가 다른의사면 왼쪽 출력
            if (message.getMemberData().equals("**다른 의사**")) {
                convertView = messageInflater.inflate(R.layout.their_message, null);
                holder.avatar = (View) convertView.findViewById(R.id.avatar);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                holder.messageTime = (TextView) convertView.findViewById(R.id.message_time);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.iv_video = (ImageView) convertView.findViewById(R.id.iv_video);
                holder.name.setText(message.getMemberData());
                holder.messageTime.setText(time);
                holder.date.setText(datetime);
                switch (message.getMessageType()) {	// 조건
                    case "text": // 메시지 타입이 텍스트
                        holder.messageBody.setText(message.getText());
                        break;
                    case "img":  // 메시지 타입이 이미지
                        holder.messageBody.setVisibility(View.GONE);
                        holder.iv_video.setVisibility(View.GONE);
                        holder.iv_image.setVisibility(View.VISIBLE);
                        Glide.with(context).load(message.getText()).placeholder(R.drawable.imgloading).into( holder.iv_image);
                        holder.iv_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ImageActivity.class);
                                intent.putExtra("Uri", message.getText());
                                context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                            }
                        });
                        break;
                    case "video": // 메시지 타입이 비디오
                        holder.messageBody.setVisibility(View.GONE);
                        holder.iv_image.setVisibility(View.GONE);
                        holder.iv_video.setVisibility(View.VISIBLE);
                        Glide.with(context).load(message.getText()).placeholder(R.drawable.imgloading).into( holder.iv_video);
                        holder.iv_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, VideoActivity.class);
                                intent.putExtra("Uri", Uri.parse(message.getText()));
                                context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                            }
                        });
                        break;
                }
                convertView.setTag(holder);
                // 현재 들어온 아이디가 다른의사 이고 전 아이디도 다른의사이면 아이디를 지워줌
                if (i > 0 && messages.get(i - 1).getMemberData().equals("**다른 의사**")) {
                    holder.avatar.setVisibility(View.GONE);
                    holder.name.setVisibility(View.GONE);
                    // 현재 들어온 아이디가 다른의사이고 전 아이디가 다른의사가 아닐때 아이디랑 아바타를 뛰워줌
                } else if (i > 0 && !messages.get(i - 1).getMemberData().equals("**다른 의사**")) {
                    holder.avatar.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.name.setText(message.getMemberData());
                }
                if (all > 10) {
                    holder.avatar.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.name.setText(message.getMemberData());
                }
                if (i+1<messages.size()){
                    if (messages.get(i+1).getdate().equals(message.getdate())&&messages.get(i+1).getMemberData().equals(message.getMemberData())){
                        holder.messageTime.setVisibility(View.GONE);
                    }else {
                        holder.messageTime.setVisibility(View.VISIBLE);
                    }
                }else {
                    holder.messageTime.setVisibility(View.VISIBLE);
                }

                if (defore_datetime.equals("")) {
                    holder.date.setVisibility(View.VISIBLE);
                    holder.date.setText(datetime);
                } else if (datetime.equals(defore_datetime)) {
                    holder.date.setVisibility(View.GONE);
                } else {
                    holder.date.setVisibility(View.VISIBLE);
                    holder.date.setText(datetime);
                }

            } else {
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                holder.messageTime = (TextView) convertView.findViewById(R.id.message_time);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.iv_video = (ImageView) convertView.findViewById(R.id.iv_video);
                holder.messageTime.setText(time);
                holder.date.setText(datetime);
                switch (message.getMessageType()) {	// 조건
                    case "text": // 메시지 타입이 텍스트
                        holder.messageBody.setText(message.getText());
                        break;
                    case "img":  // 메시지 타입이 이미지
                        holder.messageBody.setVisibility(View.GONE);
                        holder.iv_video.setVisibility(View.GONE);
                        holder.iv_image.setVisibility(View.VISIBLE);
                        Glide.with(context).load(message.getText()).placeholder(R.drawable.imgloading).into( holder.iv_image);
                        holder.iv_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ImageActivity.class);
                                intent.putExtra("Uri", message.getText());
                                context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                            }
                        });
                        break;
                    case "video": // 메시지 타입이 비디오
                        holder.messageBody.setVisibility(View.GONE);
                        holder.iv_image.setVisibility(View.GONE);
                        holder.iv_video.setVisibility(View.VISIBLE);
                        Glide.with(context).load(message.getText()).placeholder(R.drawable.imgloading).into( holder.iv_video);
                        holder.iv_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, VideoActivity.class);
                                intent.putExtra("Uri", Uri.parse(message.getText()));
                                context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                            }
                        });
                        break;
                }
                convertView.setTag(holder);
                if (i+1<messages.size()){
                    if (messages.get(i+1).getdate().equals(message.getdate())&&messages.get(i+1).getMemberData().equals(message.getMemberData())){
                        holder.messageTime.setVisibility(View.GONE);
                    }else {
                        holder.messageTime.setVisibility(View.VISIBLE);
                    }
                }else {
                    holder.messageTime.setVisibility(View.VISIBLE);
                }

                if (defore_datetime.equals("")) {
                    holder.date.setVisibility(View.VISIBLE);
                    holder.date.setText(datetime);
                } else if (datetime.equals(defore_datetime)) {
                    holder.date.setVisibility(View.GONE);
                } else {
                    holder.date.setVisibility(View.VISIBLE);
                    holder.date.setText(datetime);
                }
            }
            // 아이디 타입이 유저일때
        } else {
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            holder.messageTime = (TextView) convertView.findViewById(R.id.message_time);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.iv_video = (ImageView) convertView.findViewById(R.id.iv_video);
            holder.name.setText(message.getMemberData());
            holder.messageTime.setText(time);
            holder.date.setText(datetime);
            switch (message.getMessageType()) {	// 조건
                case "text": // 메시지 타입이 텍스트
                    holder.messageBody.setText(message.getText());
                    break;
                case "img":  // 메시지 타입이 이미지
                    holder.messageBody.setVisibility(View.GONE);
                    holder.iv_video.setVisibility(View.GONE);
                    holder.iv_image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getText()).placeholder(R.drawable.imgloading).into( holder.iv_image);
                    holder.iv_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ImageActivity.class);
                            intent.putExtra("Uri", message.getText());
                            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                        }
                    });
                    break;
                case "video": // 메시지 타입이 비디오
                    holder.messageBody.setVisibility(View.GONE);
                    holder.iv_image.setVisibility(View.GONE);
                    holder.iv_video.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getText()).placeholder(R.drawable.imgloading).into( holder.iv_video);
                    holder.iv_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, VideoActivity.class);
                            intent.putExtra("Uri", Uri.parse(message.getText()));
                            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                        }
                    });
                    break;
            }
            convertView.setTag(holder);
            if (i > 0 && messages.get(i - 1).isBelongsToCurrentUser()) {
                holder.avatar.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(message.getMemberData());
            } else if (i > 0 && !messages.get(i - 1).isBelongsToCurrentUser()) {
                holder.avatar.setVisibility(View.GONE);
                holder.name.setVisibility(View.GONE);
            }
            if (all > 10) {
                holder.avatar.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(message.getMemberData());
                holder.date.setText(datetime);
            }

            if (i+1<messages.size()){
                if (messages.get(i+1).getdate().equals(message.getdate())&&messages.get(i+1).getMemberData().equals(message.getMemberData())){
                    holder.messageTime.setVisibility(View.GONE);
                }else {
                    holder.messageTime.setVisibility(View.VISIBLE);
                }
            }else {
                holder.messageTime.setVisibility(View.VISIBLE);
            }
            if (defore_datetime.equals("")) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(datetime);
            } else if (datetime.equals(defore_datetime)) {
                holder.date.setVisibility(View.GONE);
            } else {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(datetime);
            }
        }

        return convertView;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
    public TextView messageTime;
    public TextView date;
    public ImageView iv_image;
    public ImageView iv_video;
}
