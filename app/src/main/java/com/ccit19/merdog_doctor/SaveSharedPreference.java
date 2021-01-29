package com.ccit19.merdog_doctor;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String PREF_DOCTOR_NAME = "doctorname";
    static final String PREF_DOCTOR_NUM = "doctornum";
    static final String PREF_DOCTOR_STATE = "State";
    static final String PREF_DOCTOR_LOCATION = "doctorlocation";
    static final String PREF_DOCTOR_NOW_ADDRESS = "nowaddress";
    static final String PREF_DOCTOR_TOKEN = "doctortoken";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setDoctorInfo(Context ctx, String doctorName, Boolean state, String doctornum, String doctorlocation, String nowaddress, String doctortoken) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_DOCTOR_NUM, doctornum);
        editor.putString(PREF_DOCTOR_NAME, doctorName);
        editor.putString(PREF_DOCTOR_LOCATION, doctorlocation);
        editor.putString(PREF_DOCTOR_NOW_ADDRESS, nowaddress);
        editor.putBoolean(PREF_DOCTOR_STATE, state);
        editor.putString(PREF_DOCTOR_TOKEN, doctortoken);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getDoctorName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_DOCTOR_NAME, "");
    }

    // 저장된 정보 가져오기
    public static Boolean getState(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_DOCTOR_STATE, false);
    }

    public static String getdoctornum(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_DOCTOR_NUM, "");
    }

    public static String getlocation(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_DOCTOR_LOCATION, "");
    }

    public static String getnowaddress(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_DOCTOR_NOW_ADDRESS, "");
    }

    public static String gettoken(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_DOCTOR_TOKEN, "");
    }

    // 상태값 저장
    public static void editState(Context ctx, Boolean state) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_DOCTOR_STATE);
        editor.putBoolean(PREF_DOCTOR_STATE, state);
        editor.commit();
    }

    // 병원주소 저장 on 기본값
    public static void editLoction(Context ctx, String loction) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_DOCTOR_LOCATION);
        editor.putString(PREF_DOCTOR_LOCATION, loction);
        editor.commit();
    }

    // 위치값 저장 on한상태에서 지도나 현위치로 지정한 값
    public static void editNowAddress(Context ctx, String nowaddress) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_DOCTOR_NOW_ADDRESS);
        editor.putString(PREF_DOCTOR_NOW_ADDRESS, nowaddress);
        editor.commit();
    }
    // 로그아웃
    public static void clearDoctorName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}