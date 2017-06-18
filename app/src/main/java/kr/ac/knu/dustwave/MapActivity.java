package kr.ac.knu.dustwave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// NMapActivity를 활용해 간단히 지도를 전체화면으로 표시하는 예제
// 본 예제는 1개의 파일 MainActivity.java 로 구성되어 있습니다.
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;


public class MapActivity extends NMapActivity implements NMapView.OnMapStateChangeListener, NMapView.OnMapViewTouchEventListener {


    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "LdqlbbiP8UpymwK6wdLG";// 애플리케이션 클라이언트 아이디 값

    private NMapController mMapController;

    private NMapResourceProvider mMapViewerResourceProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);

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

        // register listener for map state changes
        mMapView.setOnMapStateChangeListener(this);
        mMapView.setOnMapViewTouchEventListener(this);

    }


    /**
     * 샘플 오버레이 아이템을 지도 위에 추가한다
     */
    public void addOverLayItem() {

    }





    /* =================== Listeners =================== */

    /**
     * 지도가 생성되어 준비됬을 때
     */
    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        if (nMapError == null) { // success
            mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
            Log.v(this.getLocalClassName(), "맵 최초 위치 설정하기 성공~ " );
        } else { // fail
            Log.e(this.getLocalClassName(), "onMapInitHandler: error=" + nMapError.toString());
        }
    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

    }

    @Override
    public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

    }

    @Override
    public void onLongPressCanceled(NMapView nMapView) {

    }

    @Override
    public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

    }

    @Override
    public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

    }

    @Override
    public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

    }

    @Override
    public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {

    }
}
