package com.example.msentry;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    ActionBar actionBar;
    ImageView imageView;
    SeekBar seekBar;

    Button buttonchoose;

    Bitmap bitmapImage;

    public static SharedPreferences pref;

    private static String WebLink = "http://c.msch.or.kr/";

    String b_image = "";

    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF01579B")));

        //status 바 색상 변경
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF01579B"));


        imageView = findViewById(R.id.imageView);

        buttonchoose = findViewById(R.id.buttonChoose);

        buttonchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmapImage != null) {
                    savePreferences();
                    Toast.makeText(MainActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
                } else if (bitmapImage == null) {
                   // Toast.makeText(MainActivity.this, "바코드 이미지를 설정해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getPreferences();

        seekBar = findViewById(R.id.seekBar);
        //  seekBar.setProgress(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setBrightness(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //  seekBar.setProgress(50);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.reissue:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebLink));

                startActivity(intent);

                break;

            case R.id.gallery:

                CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);


//                Intent intent = new Intent();
//                intent.setType("image/*");
//                // intent.putExtra(Intent.EXTRA_Si, true);
//
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, REQUEST_CODE);

                break;


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            //크롭 성공시
            if (resultCode == RESULT_OK) {
                //((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                imageView.setImageURI(result.getUri());

                bitmapImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();


                //실패시
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "갤러리 접근에 실패했습니다. 설정 권한을 확인해주세요 :(", Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void setBrightness(int value) {

        if (value < 50) {
            value = 50;
            seekBar.setProgress(50);
        } else if (value > 100) {

            value = 100;
        }
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (float) value / 100;
        getWindow().setAttributes(params);

    }

    // String 값을 Bitmap으로 전환하기
    public Bitmap StringtoBitmap(String encodedString) {
        try {
            byte[] encodedByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.length);
            return bitmap;

        } catch (Exception e) {
            e.getMessage();
            return null;

        }

    }

    // 값 불러오기
    private void getPreferences() {
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences("image", MODE_PRIVATE);
        String image = pref1.getString("imageString", "");
        Bitmap bitmap = StringtoBitmap(image);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        bitmap = null;
    }

    // 값 저장하기
    private void savePreferences() {
        // String image = BitmapToString(bitmap);

        // bitmapImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        String image = BitmapToString(bitmapImage);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("image", MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = pref.edit();
        editor.putString("imageString", image);
        editor.commit();
    }

    /*
     * Bitmap을 String형으로 변환
     * */
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;

    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int maxHeight) {
        double ratio = bitmap.getHeight() / (double) maxHeight;
        int width = (int) (bitmap.getWidth() / ratio);
        return Bitmap.createScaledBitmap(bitmap, width, maxHeight, false);
    }

}