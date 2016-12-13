package se.hellsoft.textoverimages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ImageView) findViewById(R.id.first_photo)).setImageResource(R.drawable.first_photo);
        Bitmap firstPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.first_photo);
        setTextColorForImage((TextView) findViewById(R.id.first_text), firstPhoto);

        ((ImageView) findViewById(R.id.second_photo)).setImageResource(R.drawable.second_photo);
        Bitmap secondPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.second_photo);
        setTextColorForImage(((TextView) findViewById(R.id.second_text)), secondPhoto);
    }

private void setTextColorForImage(final TextView textView, Bitmap firstPhoto) {
    Palette.from(firstPhoto)
            .generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = palette.getVibrantSwatch();
                    if (swatch == null && palette.getSwatches().size() > 0) {
                        swatch = palette.getSwatches().get(0);
                    }

                    int titleTextColor = Color.WHITE;
                    if (swatch != null) {
                        titleTextColor = swatch.getTitleTextColor();
                        titleTextColor = ColorUtils.setAlphaComponent(titleTextColor, 255);
                    }

                    textView.setTextColor(titleTextColor);
                }
            });
}
}
