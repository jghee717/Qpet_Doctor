package com.ccit19.merdog_doctor.drawal_account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ccit19.merdog_doctor.AppController;
import com.ccit19.merdog_doctor.MainActivity;
import com.ccit19.merdog_doctor.R;
import com.ccit19.merdog_doctor.SaveSharedPreference;
import com.ccit19.merdog_doctor.variable.MyGlobals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Drawal_list extends AppCompatActivity {
    String doctor_num;
    private ListView m_oListView = null;
    private TextView withdraw_id, price, fee, get_money, state, date;
    private JSONArray withdraw_ida, pricea, feea, get_moneya, statea, datea;
    private List<ItemData> m_oData;
    ArrayList<ItemData> oData = new ArrayList<>();
    String u = MyGlobals.getInstance().getData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        doctor_num = SaveSharedPreference.getdoctornum(getApplication().getApplicationContext());
        withdraw_id = findViewById(R.id.withdraw_id);
        price = findViewById(R.id.price);
        fee = findViewById(R.id.fee);
        get_money = findViewById(R.id.get_money);
        state = findViewById(R.id.state);
        date = findViewById(R.id.date);
        m_oData = new ArrayList<ItemData>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawal_list);
        String url = u + "/doctorapp/withdraw_list";

        Map<String, String> params = new HashMap<String, String>();
        params.put("doctor_id", doctor_num);
        JsonObjectRequest inForm = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("result");
                            if (success == true) {
                                withdraw_ida = response.getJSONArray("withdraw_id");
                                pricea = response.getJSONArray("price");
                                feea = response.getJSONArray("fee");
                                get_moneya = response.getJSONArray("get_money");
                                datea = response.getJSONArray("date");
                                statea = response.getJSONArray("state");
                                int count = 0;
                                while (count < withdraw_ida.length()) {
                                    String withdraw_id = withdraw_ida.getString(count);
                                    String price = pricea.getString(count);
                                    String fee = feea.getString(count);
                                    String get_money = get_moneya.getString(count);
                                    String state = statea.getString(count);
                                    String date = datea.getString(count);
                                    count++;

                                    ItemData oItem = new ItemData();
                                    oItem.withdraw_ids = withdraw_id;
                                    oItem.prices = price;
                                    oItem.fees = fee + "%";
                                    oItem.get_moneys = get_money;
                                    oItem.states = state;
                                    oItem.dates = date;
                                    oData.add(oItem);


                                    // ListView 생성
                                    m_oListView = (ListView) findViewById(R.id.listView);
                                    ListAdapter oAdapter = new ListAdapter(oData);
                                    m_oListView.setAdapter(oAdapter);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "출금내역이 없습니다", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish(); // Activity stack에서 제거
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance(getApplicationContext()).addToRequestQueue(inForm);
        // 데이터 생성.


    }
}