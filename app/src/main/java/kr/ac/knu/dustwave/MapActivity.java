package kr.ac.knu.dustwave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// NMapActivity를 활용해 간단히 지도를 전체화면으로 표시하는 예제
// 본 예제는 1개의 파일 MainActivity.java 로 구성되어 있습니다.
import android.os.Bundle;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;


public class MapActivity extends NMapActivity {


    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "LdqlbbiP8UpymwK6wdLG";// 애플리케이션 클라이언트 아이디 값


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        createMapView(); // create map view using NAVER map api

    }


    /**
     * create map view using NAVEr map api
     */
    public void createMapView() {
        mMapView = new NMapView(this);
        setContentView(mMapView);
        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
    }

}
