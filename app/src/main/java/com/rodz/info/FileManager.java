package com.rodz.info;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Base64;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;

//import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileManager{
    Context ctx;
    ContextWrapper cw;
    String path;

    public FileManager(Context ctx){
        this.ctx = ctx;

        cw = new ContextWrapper(this.ctx);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        path = directory.getAbsolutePath();
    }

    public Bitmap getBitmap(String filename){
        try{
            File f = new File(path, filename);
            FileInputStream is = new FileInputStream(f);
            Bitmap b = BitmapFactory.decodeStream(is);

            if (b.getWidth() > 600){
                Double height = b.getHeight() * 1.0;
                Double width = b.getWidth() * 1.0;
                int new_height = (int) (height / width * 600);
                b = Bitmap.createScaledBitmap(b, 600, new_height, false);
            }
            is.close();
            return b;
        }
        catch(Exception ee){
            return null;
        }
    }

    public void saveImage(String filename, Bitmap bitmapImage){
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, filename);

        FileOutputStream fos = null;

        try{
            fos = new FileOutputStream(myPath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                fos.close();
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    /*public void saveFile(String filename, byte[] byte_array){
        try {
            File file2 = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file2 = new File(Environment.getStorageDirectory()+ Environment.DIRECTORY_DOWNLOADS + filename);
            }
            else{
                file2 = new File(Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_DOWNLOADS+filename);
            }
            FileOutputStream os = new FileOutputStream(file2, true);
            FileUtils.writeByteArrayToFile(file2, byte_array);
            //os.write(decodedString);
            os.close();
            System.out.println("File saved in "+file2.getAbsolutePath());
        } catch (Exception alf) {
            alf.printStackTrace();
        }
    }

    public void saveFile(String filename, String base64_string){
        try {
            byte[] decodedString = Base64.decode(base64_string, Base64.DEFAULT);

            File file2 = null;
            //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                System.out.println("Files saved");
                file2 = new File(Environment.DIRECTORY_DOWNLOADS+ filename);
                FileOutputStream os = new FileOutputStream(file2, true);
                FileUtils.writeByteArrayToFile(file2, decodedString);

                //os.write(decodedString);
                os.close();
                System.out.println("Files saved");
            /*}
            else{
                System.out.println("Old android");
            }
        } catch (Exception alf) {
            alf.printStackTrace();
        }
    }*/

    public void downloadImage(String link, String filename, ShapeableImageView myImage){
        final String fname = filename;
        final ShapeableImageView iv = myImage;
        //final User user = new User(db);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("file", fname);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL newurl = new URL(link + filename);
                    Bitmap decodedByte = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    if (decodedByte != null){
                        ((Activity)ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv.setImageBitmap(decodedByte);
                                //saveToInternalStorage(decodedByte, fname);
                                FileManager.this.saveImage(fname, decodedByte);
                            }
                        });
                    }
                }
                catch(Exception alf){
                    alf.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void downloadImage(String link, String filename){
        final String fname = filename;
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("file", fname);

        new Http(new HttpParams(ctx, link, params){
            @Override
            public void onResponse(String response){
                try{
                    byte[] decodedString = Base64.decode(response, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    //saveToInternalStorage(decodedByte, fname);
                    FileManager.this.saveImage(fname, decodedByte);
                }
                catch(Exception alf){
                    alf.printStackTrace();
                }
            }
        });
    }
}