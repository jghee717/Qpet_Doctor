package com.ccit19.merdog_doctor.ui.dashboard.taps;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.AppController;
import com.ccit19.merdog_doctor.MainActivity;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.SaveSharedPreference;
import com.ccit19.merdog_doctor.chat.ChatRoomActivity;
import com.ccit19.merdog_doctor.chat.ChatRoomAdapter;
import com.ccit19.merdog_doctor.chat.ChatRoomView;
import com.ccit19.merdog_doctor.custom_dialog.CustomAnimationDialog;
import com.ccit19.merdog_doctor.ui.dashboard.DashboardViewModel;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

public class TabFragmentChatRoomFinish extends Fragment {
    private CustomAnimationDialog customAnimationDialog;
    private DashboardViewModel dashboardViewModel;
    private ListView room_view_finish;
    private ChatRoomAdapter adapter_finish;
    private LinearLayout room;
    private JSONArray pet_img, chat_room, pet_name, chat_state, chat_request_id, message, date;
    private ArrayList<ChatRoomView> RoomList_finish = new ArrayList<>();
    private long mLastClickTime = 0;
    private TextView finish;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tap_chatroom_finish, null);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();
        actionBar.setDisplayHomeAsUpEnabled(false);

        room_view_finish = (ListView)root.findViewById(R.id.room_view_finish);
        room = (LinearLayout)root.findViewById(R.id.room);
        finish = (TextView) root.findViewById(R.id.finish);
        customAnimationDialog = new CustomAnimationDialog(root.getContext());
//        room_view_ing.setAdapter(adapter_ing);
//        adapter_ing = new ChatRoomAdapter(root.getContext());
//        room_view_finish.setAdapter(adapter_finish);
//        adapter_finish = new ChatRoomAdapter(root.getContext());
        customAnimationDialog.show();
        String u = MyGlobals.getInstance().getData();
        String url = u + "/chat/list";
        /* Create request */
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_type", "doctor");
        params.put("doctor_id", SaveSharedPreference.getdoctornum(getActivity().getApplicationContext()));

        JsonObjectRequest idcheckForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success) {
                                chat_room = response.getJSONArray("chat_room");
                                pet_name = response.getJSONArray("pet_name");
                                chat_state = response.getJSONArray("chat_state");
                                chat_request_id = response.getJSONArray("chat_request_id");
                                message = response.getJSONArray("message");
                                date = response.getJSONArray("date");
                                pet_img = response.getJSONArray("pet_img");

                                int count = 0;
                                while (count < chat_room.length()){
                                    String get_chat_room = chat_room.getString(count);
                                    String get_pet_name = pet_name.getString(count);
                                    String get_pet_state = chat_state.getString(count);
                                    String get_chat_request_id = chat_request_id.getString(count);
                                    String get_message = message.getString(count);
                                    String get_date = date.getString(count);
                                    String get_pet_img = pet_img.getString(count);
                                    if(get_message.matches(".*jpeg*.")){
                                        get_message = "사진";
                                    }
                                    if(get_message.matches(".*mp4*.")){
                                        get_message = "동영상";
                                    }
                                    ChatRoomView oItem = new ChatRoomView();
                                    oItem.get_chat_room = get_chat_room;
                                    oItem.get_pet_name =get_pet_name;
                                    oItem.get_pet_state = get_pet_state;
                                    oItem.get_chat_request_id =get_chat_request_id;
                                    oItem.get_message =get_message;
                                    oItem.get_date =get_date;
                                    oItem.get_pet_img =get_pet_img;



                                    // ViewGroup.LayoutParams params2 = room_view_ing.getLayoutParams();
                                    // params2.height = height + (room_view_ing.getDividerHeight() * (room_view_ing.getCount() - 1));
                                    // room_view_ing.setLayoutParams(params2);

                                    //리스트 뷰 생성
                                    if (get_pet_state.equals("ing")) {

                                    } else {
                                        finish.setVisibility(View.GONE);
                                        RoomList_finish.add(oItem);
                                        adapter_finish = new ChatRoomAdapter(RoomList_finish, getContext());
                                        room_view_finish.setAdapter(adapter_finish);
                                        //.setSelection(chatLists.getCount() - 1);
                                        //. ListView와 연결되어 있는 adpater의 item의 개수를 통해 ListView의 height를 설정
                                       /* int height = 0;
                                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(room_view_finish.getWidth(), View.MeasureSpec.AT_MOST);
                                        for (int i = 0; i < adapter_finish.getCount(); i++) {
                                            View listItem = adapter_finish.getView(i, null, room_view_finish);
                                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                                            height += listItem.getMeasuredHeight();
                                        }
                                        ViewGroup.LayoutParams params = room_view_finish.getLayoutParams();
                                        params.height = height + 200 + (room_view_finish.getDividerHeight() * (room_view_finish.getCount() - 1));
                                        room_view_finish.setLayoutParams(params);
                                        room_view_finish.requestLayout();*/
                                    }
                                    count++;
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "false", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        customAnimationDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity().getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getActivity().getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getActivity().getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
                customAnimationDialog.dismiss();
            }
        });
        AppController.getInstance(getActivity().getApplicationContext()).addToRequestQueue(idcheckForm);


        room_view_finish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                intent.putExtra("chat_room", RoomList_finish.get(position).get_chat_room);
                intent.putExtra("chat_request_id",  RoomList_finish.get(position).get_chat_request_id);
                intent.putExtra("chat_state",  RoomList_finish.get(position).get_pet_state);
                startActivity(intent);
            }
        });

        return root;
    }
}