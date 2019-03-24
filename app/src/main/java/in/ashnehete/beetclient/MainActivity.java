package in.ashnehete.beetclient;

import android.os.Bundle;
import android.util.Log;

import com.star_zero.sse.EventSource;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import in.ashnehete.beetclient.db.entities.Checkpoint;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private EventSource eventSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventSource.close();
    }

    public void updateLocation(LiveData<Checkpoint> checkpoint) {
        checkpoint.observe(this, new Observer<Checkpoint>() {
            @Override
            public void onChanged(Checkpoint checkpoint) {
                if (checkpoint != null) {
                    Log.d(TAG, checkpoint.toString());
                } else {
                    Log.d(TAG, "onChange: checkpoint == null");
                }
            }
        });
    }
}
