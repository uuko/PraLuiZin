package com.example.praluizin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    /*圖片要轉成bitmap才可以用*/
    private Bitmap bitmap;
    private ImageView imageView;
    private GridLayout gridLayout;
    private int phw,phh;
    /*有二十個矩陣*/
    EditText [] phss=new EditText[20];
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
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_change.setOnClickListener(this);
        btn_reset.setOnClickListener(this);

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

    private void initMatrix() {
        for (int x=0;x<20;x++){
            if (x%6 == 0){
                phss[x].setText(String.valueOf(1));
                phss[x].setText(String.valueOf(0));
            }
        }
    }

    private void setImgMatrix(){
        /*32位元*/
        Bitmap bmp=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
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
        //作用矩阵效果
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
