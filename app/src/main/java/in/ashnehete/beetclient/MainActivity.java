package in.ashnehete.beetclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.star_zero.sse.EventSource;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "http://192.168.1.5:6917/locations";
    public static final String TAG = "MainActivity";
    private EventSource eventSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventSource = new EventSource(URL, new LocationEventHandler(this.getApplicationContext()));

        eventSource.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventSource.close();
    }
}
