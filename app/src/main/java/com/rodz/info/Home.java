package com.rodz.info;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    SQLiteDatabase db;
    User user;
    Values values;
    RelativeLayout mainRl,transparent;
    FileManager fileManager;
    View modal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DbHelper helper = new DbHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        user = new User(db);
        values = new Values(db);
        fileManager = new FileManager(this);

        mainRl = (RelativeLayout) findViewById(R.id.mainRl);

        downloadData();
        getCompanyData();

        if (!values.get("company").equals("")){
            try{
                JSONObject co = new JSONObject(values.get("company"));

                ShapeableImageView logo = findViewById(R.id.logo);
                TextView company = findViewById(R.id.company), description = findViewById(R.id.description);
                company.setText(co.getString("company"));
                description.setText(co.getString("description"));

                Bitmap bitmap = fileManager.getBitmap(co.getString("logo"));
                if (bitmap != null){
                    logo.setImageBitmap(bitmap);
                }
                else{
                    //download the logo
                    String filename = co.getString("logo");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL newurl = new URL(Values.uploadsDir + filename);
                                Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                                fileManager.saveImage(filename, mIcon_val);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        logo.setImageBitmap(mIcon_val);
                                    }
                                });
                            }
                            catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private void getCompanyData() {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("getCompanyData", user.id);

        new Http(new HttpParams(this, Values.url, params){
            @Override
            public void onResponse(String text) {
                try{
                    JSONObject res = new JSONObject(text);
                    if (res.getBoolean("status")){
                        values.set("company", text);

                        if (!values.get("company").equals("")){
                            try{
                                JSONObject co = new JSONObject(values.get("company"));

                                ShapeableImageView logo = findViewById(R.id.logo);
                                TextView company = findViewById(R.id.company), description = findViewById(R.id.description);
                                company.setText(co.getString("company"));
                                description.setText(co.getString("description"));

                                Bitmap bitmap = fileManager.getBitmap(co.getString("logo"));
                                if (bitmap != null){
                                    logo.setImageBitmap(bitmap);
                                }
                                else{
                                    //download the logo
                                    String filename = co.getString("logo");
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                URL newurl = new URL(Values.uploadsDir + filename);
                                                Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                                                fileManager.saveImage(filename, mIcon_val);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        logo.setImageBitmap(mIcon_val);
                                                    }
                                                });
                                            }
                                            catch(Exception ex){
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                                    thread.start();
                                }
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(text);
                    e.printStackTrace();
                }
            }
        });
    }

    private void downloadData() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("downloadData", values.get("parent"));

        new Http(new HttpParams(this, Values.url, params){
            @Override
            public void onResponse(String text) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        try{
                            JSONObject obj = new JSONObject(text);

                            JSONArray questionnaires = obj.optJSONArray("questionnaires");
                            for (int i = 0; i < questionnaires.length(); i++){
                                JSONObject row = questionnaires.getJSONObject(i);

                                Cursor check = db.rawQuery("SELECT * FROM questionnaires WHERE webid = ?", new String[]{row.getString("id")});
                                if (check.getCount() == 0){
                                    db.execSQL("INSERT INTO questionnaires (id,webid, title, description, date, status) VALUES (NULL, ?, ?, ?, ?, ?)",
                                            new Object[]{row.getString("id"), row.getString("title"), row.getString("description"), row.getString("date"), row.getString("status")});
                                }
                                check.close();
                            }

                            JSONArray questions = obj.optJSONArray("questions");
                            for (int i = 0; i < questions.length(); i++){
                                JSONObject row = questions.getJSONObject(i);

                                Cursor check = db.rawQuery("SELECT * FROM questions WHERE webid = ?", new String[]{row.getString("id")});
                                if (check.getCount() == 0){
                                    db.execSQL("INSERT INTO questions (id, webid, type, question, name, units, status, source,parent) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)",
                                            new Object[]{row.getString("id"), row.getString("type"), row.getString("question"), row.getString("name"), row.getString("units"), row.getString("status"), row.getString("source"), row.getString("parent")});
                                }
                                check.close();
                            }

                            JSONArray series = obj.optJSONArray("series");
                            for (int i = 0; i < series.length(); i++){
                                JSONObject row = series.getJSONObject(i);

                                Cursor check = db.rawQuery("SELECT * FROM series WHERE webid = ?", new String[]{row.getString("id")});
                                if (check.getCount() == 0){
                                    db.execSQL("INSERT INTO series (id, webid, name) VALUES (NULL, ?, ?)",
                                            new Object[]{row.getString("id"), row.getString("name")});
                                }
                                check.close();
                            }

                            JSONArray series_data = obj.optJSONArray("series_data");
                            for (int i = 0; i < series_data.length(); i++){
                                JSONObject row = series_data.getJSONObject(i);

                                Cursor check = db.rawQuery("SELECT * FROM series_data WHERE webid = ?", new String[]{row.getString("id")});
                                if (check.getCount() == 0){
                                    db.execSQL("INSERT INTO series_data (id, webid, series, name) VALUES (NULL, ?, ?, ?)",
                                            new Object[]{row.getString("id"), row.getString("series"), row.getString("name")});
                                }
                                check.close();
                            }
                        }
                        catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
                thread.start();

            }
        });
    }

    public void homeClick(View view){
        int id = view.getId();
        if (id == R.id.questionnaires){
            Intent intent = new Intent(this, GeneralPurpose.class);
            intent.putExtra("action", "questionnaires");
            startActivity(intent);
        }
        else if(id == R.id.updates){
            uploadAnswers();
        }
    }

    private void uploadAnswers() {
        Cursor cursor = db.rawQuery("SELECT * FROM answers WHERE uploaded = ?", new String[]{"none"});
        if (cursor.getCount() > 0){
            Toast.makeText(this, "Please wait..", Toast.LENGTH_SHORT).show();

            JSONArray rows = Utilities.cursorToJSONArray(cursor);
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("uploadAnswers", rows.toString());
            params.put("user", user.id);

            new Http(new HttpParams(this, Values.url, params){
                @Override
                public void onResponse(String text) {
                    try{
                        JSONObject res = new JSONObject(text);
                        if (res.getBoolean("status")){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("uploaded", "yes");
                            db.update("answers",contentValues, "uploaded = ?", new String[]{"none"});
                            Toast.makeText(Home.this, "Data is up to date..", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Home.this, res.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(Exception exception){
                        Toast.makeText(Home.this, "Failed", Toast.LENGTH_SHORT).show();
                        System.out.println(text);
                        exception.printStackTrace();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Data is up to date..", Toast.LENGTH_SHORT).show();
            //nothing
        }
    }

    public void openMenu(View view){
        transparent = new RelativeLayout(this);
        transparent.setBackgroundColor(Color.parseColor("#4d000000"));
        RelativeLayout.LayoutParams t = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        t.topMargin = 0;
        t.leftMargin = 0;

        mainRl.addView(transparent, t);

        int fifty = (int)(0.8 * getScreenWidth());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(fifty, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = 0;
        params.topMargin = 0;

        modal = getLayoutInflater().inflate(R.layout.menu, null);
        mainRl.addView(modal, params);

        modal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do nothing
            }
        });

        //attach the close
        ImageView close = new ImageView(this);
        close.setImageResource(R.drawable.ic_cancel);
        //close.setColorFilter(Color.WHITE);
        //close.setLayoutParams(new RelativeLayout.LayoutParams(dpToPx(25), dpToPx(25)));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRl.removeView(modal);
                mainRl.removeView(transparent);
            }
        });

        transparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRl.removeView(modal);
                mainRl.removeView(transparent);
            }
        });
        RelativeLayout.LayoutParams close_params = new RelativeLayout.LayoutParams(dpToPx(25), dpToPx(25));
        close_params.leftMargin = fifty - dpToPx(32);
        close_params.topMargin = dpToPx(7);
        //dialog_rl.addView(close, close_params);
    }

    public void closeMenu(){
        try{
            mainRl.removeView(modal);
            mainRl.removeView(transparent);
        }
        catch(Exception ex){
            //do nothing
        }
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void openQuestionnaires(View view) {
        closeMenu();
        Intent intent = new Intent(this, GeneralPurpose.class);
        intent.putExtra("action", "questionnaires");
        startActivity(intent);
    }

    public void progress(View view) {
        closeMenu();
        Intent intent = new Intent(this, GeneralPurpose.class);
        intent.putExtra("action", "answers");
        startActivity(intent);
    }

    public void notifications(View view) {
    }

    public void updates(View view) {
    }

    public void recover(View view) {
    }
}