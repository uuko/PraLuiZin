package com.example.praluizin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    /*圖片要轉成bitmap才可以用*/
    private Bitmap bitmap;
    private ImageView imageView;
    private GridLayout gridLayout;
    private int phw,phh;
    private Button save;
    /*有二十個矩陣*/
    EditText [] phss=new EditText[20];
    Bitmap bmp;
    float[] mColorMatrix = new float[20];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*圖片先變成bitmap*/
        bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.iv_model);
        imageView = (ImageView) findViewById(R.id.iv_photo);
        gridLayout = (GridLayout) findViewById(R.id.matrix_layout);
        Button btn_change = (Button) findViewById(R.id.btn_change);
        save=findViewById(R.id.save);
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_change.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        imageView.setImageBitmap(bitmap);
        Button ch=findViewById(R.id.ch);
        final int TAKE_PICTURE = 1;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "potato" + File.separator;
                try {
                    File folder = new File(dir);
                    if(!folder.exists()){
                        folder.mkdir();
                    }

                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = sdf.format(currentTime);
                    File file = new File(dir + dateString + ".jpg");
                    if(file.exists()){
                        file.delete();
                    }
                    if(!file.exists()){
                        file.createNewFile();
                    }

                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] dinner = {"開檔案圖片","拍照"};
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
                dialog_list.setItems(dinner, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (dinner[which].equals("開檔案圖片")){
                            Toast.makeText(MainActivity.this, "你選的是" + dinner[which], Toast.LENGTH_SHORT).show();
                            openAlbum();
                        }
                        else if (dinner[which].equals("拍照")){
                            Toast.makeText(MainActivity.this, "你選的是" + dinner[which], Toast.LENGTH_SHORT).show();
                            openCamera();
                        }

                    }
                });
                dialog_list.show();
            }
        });
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,BaoHoActivity.class);
                startActivity(intent);
            }
        });
        /*拿寬高 用post可以確保呈現*/
        gridLayout.post(new Runnable() {
            @Override
            public void run() {
                phh=gridLayout.getHeight()/4;
                phw=gridLayout.getWidth()/5;
                addEditText();
                initMatrix();
            }

        });
    }

    /*動態添加*/
    private void addEditText() {
        for (int x=0;x<20;x++){
            EditText add=new EditText(this);
            add.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            phss[x]=add;
            gridLayout.addView(add,phw,phh);
        }
    }
    public void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    public void  openCamera(){
        startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), 1);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (100 == requestCode) {
            if (data != null) {
                try {
                    bitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(data.getData()));
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);//想图像显示在ImageView视图上，private ImageView img;
                File myCaptureFile = new File("sdcard/123456.jpg");
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                    /*壓縮*/
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                    /* 用flush()方法，更新BufferStream */
                    bos.flush();
                    bos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initMatrix() {
        for (int x=0;x<20;x++){
            if (x%6 == 0){
                phss[x].setText(String.valueOf(1));

            }else {
                phss[x].setText(String.valueOf(0));
            }
        }
    }

    private void setImgMatrix(){
        /*32位元*/
         bmp=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        ColorMatrix colorMatrix=new ColorMatrix();
        colorMatrix.set(mColorMatrix);

        Canvas canvas=new Canvas(bmp);
        Paint paint=new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap,0,0,paint);
        imageView.setImageBitmap(bmp);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change:
                break;
            case R.id.btn_reset:
                initMatrix();
                break;
        }
        getMatrix();
        setImgMatrix();
    }

    private void getMatrix() {
        for (int i=0;i<20;i++){
            String matrix =phss[i].getText().toString();
            boolean isNone=null ==matrix || "".equals(matrix);
            mColorMatrix[i]=isNone ? 0.0f :Float.valueOf(matrix);
            if (isNone){
                phss[i].setText("0");
            }
        }
    }
}
