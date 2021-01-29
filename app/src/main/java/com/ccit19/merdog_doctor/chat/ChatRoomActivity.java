package com.ccit19.merdog_doctor.chat;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ccit19.merdog_doctor.AppController;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.SaveSharedPreference;

import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.ui.dashboard.DashboardFragment;
import com.ccit19.merdog_doctor.ui.notifications.NotificationsFragment;
import com.ccit19.merdog_doctor.variable.MyGlobals;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.Room;
import com.stfalcon.chatkit.messages.MessageInput;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import gun0912.tedbottompicker.TedBottomPicker;


public class ChatRoomActivity  extends AppCompatActivity implements RoomListener {
    private CustomAnimationDialog customAnimationDialog;
    private JSONArray id_type, send_id, message_type, message, date, chat_request_id;
    private String chat_room, chat_state = null;
    private String date_check, chat_request_id_check;
    private EditText chat_editText;
    private CardView gallery;
    private LinearLayout  chat_finish, chat_ing;
    private ImageButton chat_send, chat_img, send_camera, send_image;
    private ListView messages_view;
    private MessageAdapter adapter;
    private Message userList;
    private MessageInput MessageInput;
    private String channelID = "GJCIaUNfqtlHkHrF";
    private Scaledrone scaledrone;
    private final int GET_CAMERA_IMAGE = 0;
    private Date times, timea;
    String u = MyGlobals.getInstance().getData();


    private Uri selectedUri;
    private RequestManager requestManager;
    private ImageView iv_image;
    private ImageView iv_video;
    private ViewGroup mSelectedImagesContainer;
    private Bitmap bitmap_camera = null;
    private int check = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("채팅");
        actionBar.setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_chatroom);
        chat_editText = (EditText)findViewById(R.id.chat_editText);
        chat_send = (ImageButton)findViewById(R.id.chat_send);
        chat_img = (ImageButton)findViewById(R.id.chat_img);
        send_camera = (ImageButton)findViewById(R.id.send_camera);
        send_image = (ImageButton)findViewById(R.id.send_image);
        chat_ing = (LinearLayout)findViewById(R.id.chat_ing);
        chat_finish = (LinearLayout)findViewById(R.id.chat_finish);
        customAnimationDialog = new CustomAnimationDialog(ChatRoomActivity.this);

        messages_view = (ListView)findViewById(R.id.messages_view);
        //  MessageInput = (MessageInput)findViewById(R.id.input);
        gallery = (CardView)findViewById(R.id.gallery);

        adapter = new MessageAdapter(getApplicationContext());
        messages_view.setAdapter(adapter);

        requestManager = Glide.with(this);
        iv_image = findViewById(R.id.iv_image);
        iv_video = findViewById(R.id.iv_video);
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);



        // 종료된 상담방
        chat_state = getIntent().getExtras().getString("chat_state");
        if(chat_state.equals("finish")){
            chat_ing.setVisibility(View.GONE);
            chat_finish.setVisibility(View.VISIBLE);

        } else {
            chat_ing.setVisibility(View.VISIBLE);
            chat_finish.setVisibility(View.GONE);
        }

        //데이터 체크 기본값을 현재시간으로
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        date_check = sdf.format(new Date());

        //어댑터 초기화부분 userList와 어댑터를 연결해준다.
        chat_room = getIntent().getExtras().getString("chat_room");
        String url = u + "/chat/load";
        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        params.put("chat_room",chat_room);
        params.put("chat_state",chat_state);
        params.put("id_type","doctor");
        params.put("doctor_id", SaveSharedPreference.getdoctornum(getApplicationContext()));
        JsonObjectRequest chatroom = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success) {
                                id_type = response.getJSONArray("id_type");
                                send_id = response.getJSONArray("send_id");
                                message_type = response.getJSONArray("message_type");
                                message= response.getJSONArray("message");
                                chat_request_id = response.getJSONArray("chat_request_id");
                                date = response.getJSONArray("date");
                                for (int i=0;i<id_type.length();i++){
                                    // 어댑터 연결 리스트뷰
                                    boolean belongsToCurrentUser = id_type.get(i).equals("doctor");
                                    userList = new Message(message.get(i).toString(), send_id.get(i).toString() ,date.get(i).toString(), message_type.get(i).toString(), belongsToCurrentUser);
                                    adapter.add(userList);
                                    // 맨 아래로 보여준다
                                    messages_view.setSelection(messages_view.getCount() - 1);

                                    // 유저가 마지막으로 메시지를 보낸시간에 15분이 지나면 상담종료 버튼 보여짐
                                    if(id_type.get(i).equals("user")){
                                        date_check = date.get(i).toString();
                                    }
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    try {
                                        times = sdf.parse(date_check);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    chat_request_id_check = chat_request_id.get(i).toString();
                                }
//                                String ss= getIntent().getExtras().getString("chat_request_id");
//                                if(chat_request_id_check.equals(ss) && 1 < (new Date().getTime() - times.getTime()) / 60000)
//                                {
//                                    chat_finish_button.setVisibility(View.VISIBLE);
//                                } else {
//                                    chat_finish_button.setVisibility(View.GONE);
//                                }
//                                // 종료된 체팅방일시 상담종료 버튼 안보임
//                                if(chat_state.equals("finish"))
//                                {
//                                    chat_finish_button.setVisibility(View.GONE);
//                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance(getApplicationContext()).addToRequestQueue(chatroom);



        //보내기 레이아웃 표시
        chat_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.setVisibility(View.VISIBLE);
            }
        });
        // 사진 선택 보내기
        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedBottomPicker.with(ChatRoomActivity.this)
                        //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setSelectedUri(selectedUri)
                        .setPeekHeight(1200)
                        .show(uri -> {
                            Log.d("ted", "uri: " + uri);
                            Log.d("ted", "uri.getPath(): " + uri.getPath());
                            selectedUri = uri;

                            mSelectedImagesContainer.setVisibility(View.VISIBLE);
                            iv_image.setVisibility(View.VISIBLE);
                            iv_video.setVisibility(View.GONE);
                            chat_send.setVisibility(View.VISIBLE);
                            chat_editText.setEnabled(false);
                            chat_editText.getText().clear();
                            requestManager
                                    .load(uri)
                                    .into(iv_image);
                            bitmap_camera = resize(getApplicationContext(),selectedUri,500);
                            check = 1;
                        });
            }
        });
        // 동영상 보내기
        send_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedBottomPicker.with(ChatRoomActivity.this)
                        //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setSelectedUri(selectedUri)
                        .showVideoMedia()
                        .setTitle("동영상 선택")
                        .setPeekHeight(1200)
                        .showVideoMedia()
                        .show(uri -> {
                            Log.d("ted", "uri: " + uri);
                            Log.d("ted", "uri.getPath(): " + uri.getPath());
                            selectedUri = uri;

                            mSelectedImagesContainer.setVisibility(View.VISIBLE);
                            iv_image.setVisibility(View.GONE);
                            iv_video.setVisibility(View.VISIBLE);
                            chat_send.setVisibility(View.VISIBLE);
                            chat_editText.setEnabled(false);
                            chat_editText.getText().clear();
                            requestManager
                                    .load(uri)
                                    .into(iv_video);

                            check = 2;
                        });
            }
        });

        // 이미지 누르면 동영상 재생
        iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                intent.putExtra("Uri", selectedUri);
                startActivity(intent);
            }
        });


//        // 상담종료 버튼
//        chat_finish_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "http://jghee717.cafe24.com/chat/send";
//                /* Create request */
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("chat_room",chat_room);
//                params.put("chat_request_id", getIntent().getExtras().getString("chat_request_id"));
//                params.put("state","off");
//                JsonObjectRequest chatstate = new JsonObjectRequest(com.android.volley.Request.Method.POST,
//                        url, new JSONObject(params),
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                boolean success = false;
//                                try {
//                                    success = response.getBoolean("result");
//                                    if (success) {
//                                        onBackPressed();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Toast.makeText(getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof AuthFailureError) {
//                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ServerError) {
//                            Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof NetworkError) {
//                            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ParseError) {
//                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//                AppController.getInstance(getApplicationContext()).addToRequestQueue(chatstate);
//            }
//        });

        //체팅 입력되면 보내기 버튼 보이게
        chat_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                chat_send.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chat_editText.getText().toString().trim().length() == 0)  {
                    chat_send.setVisibility(View.GONE);
                } else  if (chat_editText.getText().toString().trim().length() > 0)  {
                    chat_send.setVisibility(View.VISIBLE);
                }
                check = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });

        //스퀘어 드론
        MemberData data = new MemberData(getRandomName(), getRandomColor());

        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                scaledrone.subscribe(chat_room, ChatRoomActivity.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });

    }//onCreat 끝

    //채팅방 메뉴 보이기
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.chatroom_menu, menu);

        return true;
    }

    //채팅방 메뉴 선택 상담종료
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG);

        switch(item.getItemId())
        {
            case R.id.chat_finish_button:
                String url = u + "/chat/add_time";
                /* Create request */
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_id",SaveSharedPreference.getdoctornum(getApplicationContext()));
                params.put("chat_request_id", getIntent().getExtras().getString("chat_request_id"));
                params.put("extra_time","10");
                JsonObjectRequest chatstate = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                        url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                boolean success = false;
                                try {
                                    success = response.getBoolean("result");
                                    if (success) {
                                        Toast.makeText(getApplicationContext(), "연장되었습니다.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                AppController.getInstance(getApplicationContext()).addToRequestQueue(chatstate);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        chat_state = "on";
        String message = null;
        String file_encode = null;
        String message_type = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        switch (check) {	// 조건
            case 0:		// 메시지 보낼때
                message_type = "text";
                message = chat_editText.getText().toString();
                file_encode = null;
                break;
            case 1:		// 갤러리로 보낼때
                message_type = "img";
                message = SaveSharedPreference.getdoctornum(getApplicationContext()) + timeStamp + ".jpeg";
                file_encode = getStringImage(bitmap_camera);
                mSelectedImagesContainer.setVisibility(View.GONE);
                iv_image.setVisibility((View.GONE));
                chat_send.setVisibility(View.GONE);
                gallery.setVisibility(View.GONE);
                chat_editText.setEnabled(true);
                    bitmap_camera = null;
                    selectedUri = null;
                break;
            case 2:// 비디오 보낼때
                message_type = "video";
                message = SaveSharedPreference.getdoctornum(getApplicationContext()) + timeStamp + ".mp4";
                file_encode = getStringvideo(selectedUri);
                mSelectedImagesContainer.setVisibility(View.GONE);
                iv_video.setVisibility((View.GONE));
                chat_send.setVisibility(View.GONE);
                gallery.setVisibility(View.GONE);
                chat_editText.setEnabled(true);
                selectedUri = null;
                break;
        }

        String url = u + "/chat/send";
        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        params.put("chat_room",chat_room);
        params.put("chat_request_id", getIntent().getExtras().getString("chat_request_id"));
        params.put("id_type","doctor");
        params.put("send_id", SaveSharedPreference.getdoctornum(getApplicationContext()));
        params.put("file_encode",file_encode);
        params.put("message_type",message_type);
        params.put("message",message);
        params.put("state",chat_state);
        JsonObjectRequest chatsend = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success) {
                            } else {
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        chatsend.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance(getApplicationContext()).addToRequestQueue(chatsend);
        chat_editText.getText().clear();
    }

    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
    }

    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {
        JsonParser parser = new JsonParser();
        JsonElement idObject = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("message");
        String msg = idObject.toString().replaceAll("\"", "");
        JsonElement sender = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("send_id");
        String send = sender.toString().replaceAll("\"", "");
        JsonElement dater = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("time");
        String date = dater.toString().replaceAll("\"", "");
        JsonElement id_typer = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("id_type");
        String id_type = id_typer.toString().replaceAll("\"", "");
        JsonElement message_typer = parser.parse(String.valueOf(receivedMessage.getData()))
                .getAsJsonObject().get("message_type");
        String message_type = message_typer.toString().replaceAll("\"", "");

        //상담종료 보이기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        date_check = date;
        try {
            times = sdf.parse(date_check);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        if(14 < (new Date().getTime() - times.getTime()) / 60000)
//        {
//            finish_button.setVisibility(View.VISIBLE);
//        } else {
//            finish_button.setVisibility(View.GONE);
//        }
        //보이기 끝

        final ObjectMapper mapper = new ObjectMapper();
        //final MemberData data = new MemberData();

        boolean belongsToCurrentUser = id_type.equals("doctor");
        final Message rmessage = new Message(msg, send,date,message_type, belongsToCurrentUser);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(rmessage);
//                messages_view.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//                adapter.notifyDataSetChanged();
//                messages_view.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                messages_view.setSelection(messages_view.getCount() - 1);
            }
        });

    }

    private String getRandomName() {
        String[] adjs = {"autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark", "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter", "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue", "billowing", "broken", "cold", "damp", "falling", "frosty", "green", "long", "late", "lingering", "bold", "little", "morning", "muddy", "old", "red", "rough", "still", "small", "sparkling", "throbbing", "shy", "wandering", "withered", "wild", "black", "young", "holy", "solitary", "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine", "polished", "ancient", "purple", "lively", "nameless"};
        String[] nouns = {"waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning", "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter", "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook", "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly", "feather", "grass", "haze", "mountain", "night", "pond", "darkness", "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder", "violet", "water", "wildflower", "wave", "water", "resonance", "sun", "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper", "frog", "smoke", "star"};
        return (
                adjs[(int) Math.floor(Math.random() * adjs.length)] +
                        "_" +
                        nouns[(int) Math.floor(Math.random() * nouns.length)]
        );
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }
    // 체팅방 나가기
    @Override
    public void onPause(){
        super.onPause();
        scaledrone.close();
    }

    // 이미지 인코딩해 문자열로 만들기
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    // 비디오 인코딩해 문자열로 만들기
    public String getStringvideo(Uri uri) {
        InputStream inputStream = null;

        try
        {
            inputStream = getContentResolver().openInputStream(uri);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int len = 0;
        try
        {
            while ((len = inputStream.read(buffer)) != -1)
            {
                byteBuffer.write(buffer, 0, len);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        String videoData="";
        //Converting bytes into base64
        videoData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
        Log.d("VideoData**>  " , videoData);

        String sinSaltoFinal2 = videoData.trim();
        String sinsinSalto2 = sinSaltoFinal2.replaceAll("\n", "");
        Log.d("VideoData**>  " , sinsinSalto2);

        String baseVideo = sinsinSalto2;
        return baseVideo;
    }

    //이미지 리사이즈
    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

}
