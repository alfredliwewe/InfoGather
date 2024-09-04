package com.rodz.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class Question {
    public LinearLayout view;
    public String name = "",type = "",id;
    Context _this, ctx;
    EditText entry;
    RadioGroup radioGroup;
    LinearLayout checkGroup;
    Spinner select;
    @SuppressLint("Range")
    public Question(Context ctx, Cursor all, SQLiteDatabase db, int position){
        view = new LinearLayout(ctx);
        view.setOrientation(LinearLayout.VERTICAL);
        _this = this.ctx = ctx;

        name = all.getString(all.getColumnIndex("name"));
        id = all.getString(all.getColumnIndex("webid"));

        TextView question = new TextView(_this);
        question.setTextColor(Color.BLACK);
        question.setText(position+". "+all.getString(all.getColumnIndex("question")));
        view.addView(question);

        type = all.getString(all.getColumnIndex("type"));
        String source = all.getString(all.getColumnIndex("source"));
        String[] stringArray = {"text", "integer"};
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(stringArray));
        if(inputs.contains(type)){
            entry = new EditText(_this);
            entry.setHint("Response");
            entry.setInputType(type.equals("text") ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_NUMBER);
            view.addView(entry);
        }
        else if (type.equals("radio")){
            radioGroup = new RadioGroup(_this);
            radioGroup.setOrientation(LinearLayout.VERTICAL);
            view.addView(radioGroup);

            Cursor data = db.rawQuery("SELECT * FROM series_data WHERE series = ?", new String[]{source});
            while (data.moveToNext()){
                RadioButton radioButton = new RadioButton(_this);
                radioButton.setText(data.getString(data.getColumnIndex("name")));
                radioButton.setTextColor(Color.BLACK);
                radioGroup.addView(radioButton);
            }
            data.close();
        }
        else if (type.equals("checkbox")){
            checkGroup = new LinearLayout(_this);
            checkGroup.setOrientation(LinearLayout.VERTICAL);

            Cursor data = db.rawQuery("SELECT * FROM series_data WHERE series = ?", new String[]{source});
            while (data.moveToNext()){
                CheckBox checkBox = new CheckBox(_this);
                checkBox.setText(data.getString(data.getColumnIndex("name")));
                checkBox.setTextColor(Color.BLACK);
                checkGroup.addView(checkBox);
            }
            view.addView(checkGroup);
            data.close();
        } else if (type.equals("options")) {
            select = new Spinner(_this);
            view.addView(select);
            //subs.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT));
            //subs.setBackgroundResource(R.drawable.border);
            ArrayList<String> subcategories = new ArrayList<>();
            Cursor read = db.rawQuery("SELECT * FROM series_data WHERE series = ?", new String[]{source});
            while(read.moveToNext()){
                subcategories.add(read.getString(read.getColumnIndex("name")));
            }
            read.close();
            ArrayAdapter aa1 = new ArrayAdapter(_this, android.R.layout.simple_spinner_item, subcategories);
            aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select.setAdapter(aa1);
        }
    }

    public String getText(){
        if (type.equals("text") || type.equals("integer")){
            return entry.getText().toString();
        }
        else if (type.equals("radio")){
            RadioButton selected =  ((Activity)_this).findViewById(radioGroup.getCheckedRadioButtonId());
            if (selected != null){
                return selected.getText().toString();
            }
        }
        else if (type.equals("checkbox")){
            ArrayList<String> selected = new ArrayList<>();
            for (int i = 0; i < checkGroup.getChildCount(); i++){
                CheckBox checkBox = (CheckBox) checkGroup.getChildAt(i);
                if (checkBox.isChecked()){
                    selected.add(checkBox.getText().toString());
                }
            }

            return Utilities.implode(";", selected);
        }
        else if (type.equals("options")) {
            return select.getSelectedItem().toString();
        }
        return "";
    }

    public boolean isAnswered(){
        return true;
    }
}
