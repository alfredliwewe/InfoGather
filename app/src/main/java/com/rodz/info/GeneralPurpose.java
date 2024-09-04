package com.rodz.info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.rodz.info.views.Table;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeneralPurpose extends AppCompatActivity {
    SQLiteDatabase db;
    User user;
    Values values;

    LinearLayout main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_purpose);

        DbHelper helper = new DbHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        user = new User(db);
        values = new Values(db);

        main = findViewById(R.id.main);

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");

        switch (action){
            case "questionnaires":
                printQuestionnaires();
                break;

            case "answers":
                printQuestionnairesList();
                break;

            case "viewAnswers":
                viewAnswers(intent.getStringExtra("id"));
                break;
        }
    }

    @SuppressLint("Range")
    private void viewAnswers(String id) {
        HorizontalScrollView hs = new HorizontalScrollView(this);
        LinearLayout responsinve = new LinearLayout(this);
        responsinve.setOrientation(LinearLayout.HORIZONTAL);
        hs.addView(responsinve);
        main.addView(hs);

        Table table = new Table(this, false);

        Map<String,Integer> columns = new LinkedHashMap<>();

        Cursor cursor = db.rawQuery("SELECT * FROM questions WHERE parent = ?", new String[]{id});
        int all = cursor.getCount();
        int pos = 1;
        columns.put("#", 10);
        while (cursor.moveToNext()){
            columns.put(cursor.getString(cursor.getColumnIndex("name")), 100/all);
        }
        cursor.close();
        table.setColumns(columns);
        responsinve.addView(table.view);

        //lets read the data
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        Cursor dis = db.rawQuery("SELECT DISTINCT row_id FROM answers WHERE form = ? ", new String[]{id});
        int position = 1;
        while (dis.moveToNext()){
            ArrayList<Object> row = new ArrayList();
            row.add(position);
            String row_id = dis.getString(dis.getColumnIndex("row_id"));
            Map<String,String> cols = new LinkedHashMap<>();
            Cursor rd = db.rawQuery("SELECT * FROM answers WHERE form = ? AND row_id = ?", new String[]{id,row_id});
            while (rd.moveToNext()){
                cols.put(rd.getString(rd.getColumnIndex("name")),rd.getString(rd.getColumnIndex("answer")));
                row.add(rd.getString(rd.getColumnIndex("answer")));
            }
            rd.close();
            position += 1;
            data.add(row);
        }
        dis.close();
        table.setDataCompat(data);

    }

    @SuppressLint({"Range", "ResourceType"})
    private void printQuestionnaires() {
        //getSupportActionBar().show();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.HORIZONTAL);
        heading.setPadding(dpToPx(12),dpToPx(12),dpToPx(12),dpToPx(12));
        main.addView(heading);
        heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView back = new ImageView(this);
        back.setLayoutParams(new ViewGroup.LayoutParams(dpToPx(27),dpToPx(27)));
        back.setImageResource(R.drawable.ic_arrow_back);
        heading.addView(back);

        TextView label = new TextView(this);
        label.setText("Questionnaires");
        label.setTextColor(Color.BLACK);
        label.setPadding(dpToPx(15),0,0,0);
        label.setTextSize(17.0f);
        heading.addView(label);

        Cursor cursor = db.rawQuery("SELECT * FROM questionnaires", null);
        int pos = 1;
        while (cursor.moveToNext()){
            LinearLayout cont = new LinearLayout(this);
            cont.setOrientation(LinearLayout.VERTICAL);
            cont.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
            main.addView(cont);

            String id = cursor.getString(cursor.getColumnIndex("webid"));
            cont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GeneralPurpose.this, TakeSurvey.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });

            TextView title = new TextView(this);
            title.setText(pos+". "+cursor.getString(cursor.getColumnIndex("title")));
            title.setTextColor(Color.parseColor(getString(R.color.dark)));
            cont.addView(title);

            TextView description = new TextView(this);
            description.setText(cursor.getString(cursor.getColumnIndex("description")));
            description.setTextColor(Color.parseColor(getString(R.color.secondary)));
            cont.addView(description);

            // count questions
            Cursor c = db.rawQuery("SELECT * FROM questions WHERE parent = ?", new String[]{id});
            int count = c.getCount();
            Chip ques = new Chip(this);
            ques.setText(count+" questions");
            ques.setChipBackgroundColor(ContextCompat.getColorStateList(this, R.color.secondary));
            cont.addView(ques);
            c.close();
        }
        cursor.close();
    }

    @SuppressLint({"Range", "ResourceType"})
    private void printQuestionnairesList() {
        //getSupportActionBar().show();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.HORIZONTAL);
        heading.setPadding(dpToPx(12),dpToPx(12),dpToPx(12),dpToPx(12));
        main.addView(heading);
        heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView back = new ImageView(this);
        back.setLayoutParams(new ViewGroup.LayoutParams(dpToPx(27),dpToPx(27)));
        back.setImageResource(R.drawable.ic_arrow_back);
        heading.addView(back);

        TextView label = new TextView(this);
        label.setText("Choose Questionnaire");
        label.setTextColor(Color.BLACK);
        label.setPadding(dpToPx(15),0,0,0);
        label.setTextSize(17.0f);
        heading.addView(label);

        Cursor cursor = db.rawQuery("SELECT * FROM questionnaires", null);
        int pos = 1;
        while (cursor.moveToNext()){
            LinearLayout cont = new LinearLayout(this);
            cont.setOrientation(LinearLayout.VERTICAL);
            cont.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
            main.addView(cont);

            String id = cursor.getString(cursor.getColumnIndex("webid"));
            cont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GeneralPurpose.this, GeneralPurpose.class);
                    intent.putExtra("action", "viewAnswers");
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });

            TextView title = new TextView(this);
            title.setText(pos+". "+cursor.getString(cursor.getColumnIndex("title")));
            title.setTextColor(Color.parseColor(getString(R.color.dark)));
            cont.addView(title);

            TextView description = new TextView(this);
            description.setText(cursor.getString(cursor.getColumnIndex("description")));
            description.setTextColor(Color.parseColor(getString(R.color.secondary)));
            cont.addView(description);

            // count questions
            Cursor c = db.rawQuery("SELECT DISTINCT row_id FROM answers WHERE form = ?", new String[]{id});
            int count = c.getCount();
            Chip ques = new Chip(this);
            ques.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ques.setChipBackgroundColor(ContextCompat.getColorStateList(this, R.color.secondary));
            ques.setText(count+" answers");
            cont.addView(ques);
            c.close();
        }
        cursor.close();
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}