package com.rodz.info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    User user;
    Values values;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        DbHelper helper = new DbHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        user = new User(db);
        values = new Values(db);

        if (user.status){
            startActivity(new Intent(this, Home.class));
            finish();
        }

        EditText username = findViewById(R.id.username), password = findViewById(R.id.password);
        Button btn = findViewById(R.id.btn);
        TextView register = findViewById(R.id.register);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    //
                    Map<String,Object> params = new LinkedHashMap<>();
                    params.put("app_login_username", username.getText().toString());
                    params.put("password", password.getText().toString());

                    new Http(new HttpParams(MainActivity.this, Values.url, params){
                        @Override
                        public void onResponse(String text) {
                            try{
                                JSONObject res = new JSONObject(text);
                                if (res.getBoolean("status")){
                                    //save
                                    //save the user
                                    db.delete("user", "id != ?", new String[]{"0"});
                                    db.execSQL("INSERT INTO user (id,webid,name,phone,email,type,file) VALUES (NULL, ?,?,?,?,?,?)",
                                            new Object[]{res.getString("id"),res.getString("name"),res.getString("phone"),res.getString("email"),res.getString("type"),""});

                                    values.set("parent", res.getString("parent"));
                                    startActivity(new Intent(MainActivity.this, Home.class));
                                    finish();
                                }
                                else{
                                    printError(res.getString("message"));
                                }
                            }
                            catch (Exception e){
                                printError("Failed to login:: unknown error");
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else{
                    printError("Please fill out the fomr");
                }
            }
        });
    }



    private void printError(String str) {
        TextView error = findViewById(R.id.error);
        error.setText(str);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = dpToPx(10);
        error.setLayoutParams(layoutParams);
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}