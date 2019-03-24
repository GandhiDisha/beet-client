package in.ashnehete.beetclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import in.ashnehete.beetclient.R;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    Button btnTrackOnMap, btnGpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTrackOnMap = (Button) findViewById(R.id.btnTrackOnMap);
        btnGpsTracker = (Button) findViewById(R.id.btnGpsTracker);

        btnTrackOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        btnGpsTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TrackerActivity.class);
                startActivity(intent);
            }
        });
    }
}
