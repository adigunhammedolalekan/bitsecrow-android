package com.bitsescrow.app.bitsescrow.models;

import com.bitsescrow.app.bitsescrow.core.RepositoryManager;
import com.bitsescrow.app.bitsescrow.core.Requests;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by Lekan Adigun on 5/18/2018.
 */

@Parcel
public class User {

    public int ID = 0;
    public String username = "";
    public String email = "";
    public String name = "";
    public String token = "";
    public String picture = "";
    public String phone = "";

    public static final String KEY = "UserKey";

    public User() {}

    public User(JSONObject jsonObject) throws JSONException {
        ID = jsonObject.getInt("ID");
        email = jsonObject.getString("email");
        name = jsonObject.getString("name");
        phone = jsonObject.getString("phone");
        token = jsonObject.getString("Token");
        picture = jsonObject.getString("picture");
        username = jsonObject.getString("username");
    }

    public String json() {
        return new Gson().toJson(this);
    }

    /*
    * Save user details into app prefs
    * */
    public void persist() {

        if (!token.isEmpty()) {
            RepositoryManager.manager().preferences().edit().putString(Requests.HEADER, token).apply();
        }

        RepositoryManager.manager().preferences().edit().putString(KEY, json()).apply();
    }

    /*
    * Create user object from pref
    * */
    public static User user() {
        return new Gson().fromJson(
                RepositoryManager.manager().preferences().getString(KEY, ""),
                User.class
        );
    }

    /*
    * Return users full name, returns username if fullname is not available
    * */
    public String name() {

        if (name.isEmpty())
            return username;

        return name;
    }

    /*
    * Returns true for user has photo
    * */

    public boolean hasPhoto() {
        return picture != null && !picture.isEmpty();
    }
}
