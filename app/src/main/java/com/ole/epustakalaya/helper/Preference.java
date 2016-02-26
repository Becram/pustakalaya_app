package com.ole.epustakalaya.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bishnu on 10/20/14.
 */
public class Preference  {

    private static String prefFileName = "Settings";
    private static String keyLanguage = "KeyLanguage";
    private static String keyServerType = "KeyServerType";
    private static String keyServerURL = "KeyServerURL";
    private static final String keySortBy = "keySortByField";
    private static final String keySortOrder = "keySortOrderField";
    public static final String keyLastTab = "keyMyLastTab";


    private SharedPreferences preferences;
    private Context context;

    public Preference(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    public void setSortField(String sortField){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keySortBy,sortField);
        editor.commit();
    }
    public String getSortField(){
        return preferences.getString(keySortBy,"author");
    }

    public void setSortOrder(String sortOrder){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keySortOrder,sortOrder);
        editor.commit();
    }
    public String getSortOrder(){
        return preferences.getString(keySortOrder,"asc");
    }

    public void setMyLastTab(int myLastTab){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(keyLastTab,myLastTab);
        editor.commit();
    }

    public int getMyLastTab(){
        return preferences.getInt(keyLastTab,0);
    }

    public void setLanguage(String langCode){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyLanguage,langCode);
        editor.commit();
    }

    public String getLanguage(){
        return preferences.getString(keyLanguage,"en");
    }

    public void setServer(String serverType){
        this.setServer(serverType,"");
    }
    public void setServer(String serverType, String serverURL){
        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(keyServerType,server);

        if(serverType.equalsIgnoreCase("local")){
            editor.putString(keyServerType,"local");
        }else if(serverType.equalsIgnoreCase("remote")){
            editor.putString(keyServerType,"remote");
        }else if(serverType.equalsIgnoreCase("custom")){
            editor.putString(keyServerType,"custom");
            editor.putString(keyServerURL,serverURL);
        }
        editor.commit();
    }

    public String getServerType(){
        String serverType = preferences.getString(keyServerType,"remote");
        return  serverType;
    }

    public String getCustomServer(){
        return preferences.getString(keyServerURL,ServerSideHelper.PUST_SERVER);
    }

    public String getServer(){
       String server=preferences.getString(keyServerType,"");
        if(server.equalsIgnoreCase("local")) {
            return ServerSideHelper.SCHOOL_SERVER;
        } else if(server.equalsIgnoreCase("remote")){
            return ServerSideHelper.PUST_SERVER;
        }else if(server.equalsIgnoreCase("custom")){
            return preferences.getString(keyServerURL,ServerSideHelper.PUST_SERVER);
        }
        return ServerSideHelper.PUST_SERVER;
    }


}
