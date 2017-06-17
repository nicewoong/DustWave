package kr.ac.knu.dustwave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;


public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        createMapView(); // create map view using DAUM map api

    }


    /**
     * create map view using DAUM map api
     */
    public void createMapView() {

    }

}
