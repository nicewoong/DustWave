package kr.ac.knu.dustwave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapView;

public class DMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmap);

        MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey(this.getResources().getString(R.string.daum_map_view_api_key)); // 다음 api key 를 인자로 넘겨준다.

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

    }
}
