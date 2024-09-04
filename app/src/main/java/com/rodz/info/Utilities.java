package com.rodz.info;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Utilities {
    public static JSONArray cursorToJSONArray(Cursor cursor) {

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        //Log.d(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

    }

    public static Bitmap createSquareBitmap(Bitmap rectangularBitmap) {
        // Get the dimensions of the original rectangular bitmap
        int originalWidth = rectangularBitmap.getWidth();
        int originalHeight = rectangularBitmap.getHeight();

        // Determine the size of the square
        int squareSize = Math.min(originalWidth, originalHeight);

        // Calculate the coordinates to create a square by cutting in the middle
        int left = (originalWidth - squareSize) / 2;
        int top = (originalHeight - squareSize) / 2;
        int right = left + squareSize;
        int bottom = top + squareSize;

        // Create a new square Bitmap
        Bitmap squareBitmap = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888);

        // Draw the portion of the rectangular bitmap into the square bitmap
        Canvas canvas = new Canvas(squareBitmap);
        Rect sourceRect = new Rect(left, top, right, bottom);
        Rect destRect = new Rect(0, 0, squareSize, squareSize);
        canvas.drawBitmap(rectangularBitmap, sourceRect, destRect, null);

        return squareBitmap;
    }

    public static String implode(String glue, String[] values){
        String ids = "";
        for (int i = 0; i < values.length; i++) {
            String id = values[i];
            if (ids.equals("")){
                ids = id;
            }
            else{
                ids += glue+id;
            }
        }
        return ids;
    }

    public static String implode(String glue, ArrayList<String> values){
        String ids = "";
        for (int i = 0; i < values.size(); i++) {
            String id = values.get(i);
            if (ids.equals("")){
                ids = id;
            }
            else{
                ids += glue+id;
            }
        }
        return ids;
    }

    public static String leadingZero(int num){
        return num < 10 ? "0"+num : String.valueOf(num);
    }
}
