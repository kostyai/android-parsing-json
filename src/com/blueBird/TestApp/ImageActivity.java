package com.blueBird.TestApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by Sergey on 04.11.2014.
 */
public class ImageActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);
        Intent intent = getIntent();
        String url = intent.getExtras().getString("image");
        ImageView im = (ImageView)findViewById(R.id.imageView);
        im.setImageBitmap(MainActivity.getBitmapFromURL(url, 400, 400));
    }
}
