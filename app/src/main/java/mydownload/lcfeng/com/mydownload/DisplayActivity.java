package mydownload.lcfeng.com.mydownload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class DisplayActivity extends AppCompatActivity {

    private ImageView imageView;
    private  String TAG = "lcfeng";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        imageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        Log.i(TAG, "onCreate: "+uri);
        imageView.setImageBitmap(BitmapFactory.decodeFile(uri));
    }
}
