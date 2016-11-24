package work.yeshu.waveview;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.SeekBar;

import work.yeshu.library.UIUtils;
import work.yeshu.library.WaveView;

public class MainActivity extends AppCompatActivity {
    private WaveView mWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaveView = (WaveView) findViewById(R.id.wave_view);
        SeekBar waveLengthBar = (SeekBar) findViewById(R.id.set_wavelength);
        SeekBar amplitudeBar = (SeekBar) findViewById(R.id.set_amplitude);
        SeekBar waterHeightBar = (SeekBar) findViewById(R.id.set_water_height);

        waveLengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveView.setWavelength(getScreenWidth() / 2 + progress * getScreenWidth() / 200);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        amplitudeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveView.setAmplitude(UIUtils.dp2px(getApplicationContext(), 16 + 16 * progress / 50));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        waterHeightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveView.setWaterHeight(UIUtils.dp2px(getApplicationContext(), 50 + 50 * progress / 50));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
