package com.rodz.info.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rodz.info.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table {
    Context ctx, _this;
    ArrayList<Float> widths= new ArrayList<Float>();
    public TableLayout view;
    //LinearLayout thead,tbody;
    boolean isDark;
    public Table(Context ctx, boolean isDark){
        this.ctx = _this = ctx;
        this.isDark = isDark;

        view = new TableLayout(_this);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setColumns(Map<String,Integer> cols){
        TableRow tr = new TableRow(_this);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tr.setOrientation(LinearLayout.HORIZONTAL);
        view.addView(tr);

        for (Map.Entry<String, Integer> col:cols.entrySet()){
            //float width = (float) (((double)col.getValue()) / 100);
            //widths.add(width);

            TextView text = new TextView(_this);
            text.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
            text.setTypeface(text.getTypeface(), Typeface.BOLD);
            text.setTextColor(Color.BLACK);
            text.setText(col.getKey());
            tr.addView(text);
        }
    }

    public void setTableData(ArrayList<String[]> data){
        int pos = 1;
        for (String[] row:data){
            LinearLayout tr = new LinearLayout(_this);
            tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            tr.setOrientation(LinearLayout.HORIZONTAL);
            view.addView(tr);

            if (pos % 2 == 0){
                tr.setBackgroundColor(isDark ? Color.parseColor("#212529") : Color.parseColor("#e2e3e5"));
            }

            for (int i = 0; i < row.length; i++){
                float width = widths.get(i);
                widths.add(width);

                LinearLayout td = new LinearLayout(_this);
                td.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, width));
                tr.addView(td);

                TextView text = new TextView(_this);
                text.setPadding(dpToPx(10),dpToPx(6),dpToPx(10),dpToPx(6));
                text.setText(row[i]);
                td.addView(text);
            }
            pos += 1;
        }
    }

    public void setTableDataCompat(ArrayList<Object[]> data){
        ProgressBar progressBar = new ProgressBar(_this);
        view.addView(progressBar);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LinearLayout vert = new LinearLayout(_this);
                vert.setOrientation(LinearLayout.VERTICAL);
                vert.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                int pos = 1;
                for (Object[] row:data){
                    TableRow tr = new TableRow(_this);
                    tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    tr.setOrientation(LinearLayout.HORIZONTAL);
                    vert.addView(tr);

                    if (pos % 2 == 0){
                        tr.setBackgroundColor(isDark ? Color.parseColor("#212529") : Color.parseColor("#e2e3e5"));
                    }

                    for (int i = 0; i < row.length; i++){
                        float width = widths.get(i);
                        widths.add(width);



                        if (row[i] instanceof View){
                            View view = (View)row[i];
                            tr.addView(view);
                        }
                        else {
                            TextView text = new TextView(_this);
                            text.setPadding(dpToPx(10), dpToPx(6), dpToPx(10), dpToPx(6));
                            text.setText(String.valueOf(row[i]));
                            tr.addView(text);
                        }
                    }
                    pos += 1;
                }

                ((Activity)_this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.addView(progressBar);
                        view.addView(vert);
                    }
                });
            }
        });
        thread.start();
    }

    public void setDataCompat(ArrayList<ArrayList<Object>> data){
        ProgressBar progressBar = new ProgressBar(_this);
        //view.addView(progressBar);
        @SuppressLint("ResourceType") int textColor = Color.parseColor(_this.getString(R.color.dark));

                int pos = 1;
                for (ArrayList<Object> row:data){
                    TableRow tr = new TableRow(_this);
                    tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    tr.setOrientation(LinearLayout.HORIZONTAL);
                    view.addView(tr);

                    if (pos % 2 == 0){
                        tr.setBackgroundColor(isDark ? Color.parseColor("#212529") : Color.parseColor("#e2e3e5"));
                    }

                    for (int i = 0; i < row.size(); i++){



                        if (row.get(i) instanceof View){
                            View view = (View)row.get(i);
                            tr.addView(view);
                        }
                        else {
                            TextView text = new TextView(_this);
                            text.setPadding(dpToPx(10), dpToPx(6), dpToPx(10), dpToPx(6));
                            text.setText(String.valueOf(row.get(i)));
                            text.setTextColor(textColor);
                            tr.addView(text);
                        }
                    }
                    pos += 1;
                }

    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = _this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void setWidth(int width){
        view.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
