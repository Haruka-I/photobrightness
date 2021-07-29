package test1.example.myapplication;

import java.io.FileNotFoundException;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

    Button btnLoadImage;
    TextView text1;
    ImageView imageResult;
    SeekBar hBar, sBar, vBar;
    TextView hueText, satText, valText;
    Button btnResetHSV;

    final int R_IMAGE1 = 1;

    Uri source;
    Canvas canvasMaster;
    Bitmap bitmapMaster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadImage = (Button) findViewById(R.id.loadingimage);
        text1 = (TextView) findViewById(R.id.text1);
        imageResult = (ImageView) findViewById(R.id.get);

        btnLoadImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, R_IMAGE1);
            }
        });

        hBar = (SeekBar) findViewById(R.id.hbar);
        sBar = (SeekBar) findViewById(R.id.sbar);
        vBar = (SeekBar) findViewById(R.id.vbar);
        hueText = (TextView) findViewById(R.id.texth);
        satText = (TextView) findViewById(R.id.texts);
        valText = (TextView) findViewById(R.id.textv);
        hBar.setOnSeekBarChangeListener(seekBarChangeListener);
        sBar.setOnSeekBarChangeListener(seekBarChangeListener);
        vBar.setOnSeekBarChangeListener(seekBarChangeListener);
        btnResetHSV = (Button)findViewById(R.id.resebthsv);
        btnResetHSV.setOnClickListener(arg0 -> {
            // シークバーリセット位置
            hBar.setProgress(250);
            sBar.setProgress(250);
            vBar.setProgress(250);

            loadBitmapHSV();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == R_IMAGE1) {
                source = data.getData();

                try {
                    bitmapMaster = BitmapFactory
                            .decodeStream(getContentResolver().openInputStream(
                                    source));

                    // reset SeekBars
                    hBar.setProgress(250);
                    sBar.setProgress(250);
                    vBar.setProgress(250);

                    loadBitmapHSV();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadBitmapHSV();
        }

    };

    private void loadBitmapHSV() {
        if (bitmapMaster != null) {

            int progressHue = hBar.getProgress() - 250;
            int progressSat = sBar.getProgress() - 250;
            int progressVal = vBar.getProgress() - 250;

            float h = (float) progressHue * 365 / 250;
            float s = (float) progressSat / 250;
            float v = (float) progressVal / 250;

            hueText.setText("色彩: " + String.valueOf(h));
            satText.setText("彩度: " + String.valueOf(s));
            valText.setText("明るさ: " + String.valueOf(v));

            imageResult.setImageBitmap(updateHSV(bitmapMaster, h, s, v));

        }
    }

    private Bitmap updateHSV(Bitmap src, float settingHue, float settingSat,
                             float settingVal) {

        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];



        float[] plHSV = new float[3];

        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {

                // カラー変換
                Color.colorToHSV(mapSrcColor[index], plHSV);

                // 調整
                plHSV[0] = plHSV[0] + settingHue;
                if (plHSV[0] < 0.0f) {
                    plHSV[0] = 0.0f;
                } else if (plHSV[0] > 365.0f) {
                    plHSV[0] = 365.0f;
                }

                plHSV[2] = plHSV[2] + settingVal;
                if (plHSV[2] < 0.0f) {
                    plHSV[2] = 0.0f;
                } else if (plHSV[2] > 1.0f) {
                    plHSV[2] = 1.0f;
                }

                plHSV[1] = plHSV[1] + settingSat;
                if (plHSV[1] < 0.0f) {
                    plHSV[1] = 0.0f;
                } else if (plHSV[1] > 1.0f) {
                    plHSV[1] = 1.0f;
                }



                // Convert back from HSV to Color
                mapDestColor[index] = Color.HSVToColor(plHSV);

                index++;
            }
        }

        return Bitmap.createBitmap(mapDestColor, w, h, Config.ARGB_8888);

    }

}