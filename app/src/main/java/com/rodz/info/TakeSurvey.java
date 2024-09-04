package com.rodz.info;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;

public class TakeSurvey extends AppCompatActivity {
    SQLiteDatabase db;
    User user;
    Values values;
    LinearLayout main;
    ArrayList<Question> list = new ArrayList<>();
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_survey);

        DbHelper helper = new DbHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        user = new User(db);
        values = new Values(db);

        main = findViewById(R.id.main);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");


        Cursor all = db.rawQuery("SELECT * FROM questions WHERE parent = ?", new String[]{id});
        int position = 1;
        while (all.moveToNext()){
            Question question = new Question(this, all, db, position);
            main.addView(question.view);
            list.add(question);
            position += 1;
        }
        all.close();

        MaterialButton submit = new MaterialButton(this);
        submit.setText("Submit");
        //submit.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        main.addView(submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                db.execSQL("CREATE TABLE IF NOT EXISTS answers (id INTEGER PRIMARY KEY AUTOINCREMENT, form VARCHAR, question VARCHAR, name VARCHAR, answer VARCHAR, row_id VARCHAR, uploaded VARCHAR)");
                boolean canDo = true;

                for (Question q:list){
                    if (!q.isAnswered()){
                        canDo = false;
                    }
                }

                if (canDo){
                    //save the answers
                    String row_id = String.valueOf(Math.floor(Math.random()*100010010));
                    for (Question q:list){
                        db.execSQL("INSERT INTO answers (id, form, question, name, answer, row_id, uploaded) VALUES (NULL, ?, ?, ?, ?, ?, ?)",
                                new Object[]{id,q.id,q.name,q.getText(),row_id,"none"});
                    }

                    AlertDialog.Builder alert = new AlertDialog.Builder(TakeSurvey.this);
                    alert.setTitle("Success");
                    alert.setMessage("You have completed the form");
                    alert.setNegativeButton((CharSequence) "Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            finish();
                        }
                    });
                    alert.show();
                }
                else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(TakeSurvey.this);
                    alert.setTitle("Incomplete");
                    alert.setMessage("Please fill out the missing fields");
                    alert.setNegativeButton((CharSequence) "Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alert.show();
                }
            }
        });
    }
}