package in.ashnehete.beetclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.star_zero.sse.EventHandler;
import com.star_zero.sse.EventSource;
import com.star_zero.sse.MessageEvent;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "http://192.168.1.5:6917/testtopic1";
    public static final String TAG = "MainActivity";
    private EventSource eventSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventSource = new EventSource(URL, new EventHandler() {
            @Override
            public void onOpen() {
                // run on worker thread
                Log.d(TAG, "Open");
            }

            @Override
            public void onMessage(MessageEvent messageEvent) {
                // run on worker thread
                Log.d(TAG, "Message: " + messageEvent.getData());
            }

            @Override
            public void onError(Exception e) {
                // run on worker thread
                Log.w(TAG, e);
            }
        });

        eventSource.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventSource.close();
    }
}
