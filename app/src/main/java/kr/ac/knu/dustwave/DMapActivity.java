package kr.ac.knu.dustwave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


/**
 * 다음지도API를 활용해서 지도 화면을 보여주는 Activity.
 */

public class DMapActivity extends AppCompatActivity implements MapView.MapViewEventListener,  MapView.POIItemEventListener  {

    MapView mapView;
    ViewGroup mapViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmap);


        createMapView(); // 다음 지도를 생성해서 view 를 채운다.


    }

    /**
     * 다음지도API를 활용해서 맵뷰를 생성해서 activity 를 채운다.
     *
     */
    public void createMapView() {
        mapView = new MapView(this);
        mapView.setDaumMapApiKey(this.getResources().getString(R.string.daum_map_view_api_key)); // 다음 api key 를 인자로 넘겨준다.

        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        //이벤트리스너 등록
        mapView.setMapViewEventListener(this); // this에 MapView.MapViewEventListener 구현.
        mapView.setPOIItemEventListener(this);


    }


    /**
     * 지도의 중심을 어디로 해서 보여줄지 설정
     * todo 사용자의 현재위치를 받아서 중심점을 선택하는 기능으로 변경하기
     *
     */
    public void setMapCenter() {
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(35.8714354, 128.601445), true); // 대구광역시

        // 줌 레벨 변경
        mapView.setZoomLevel(7, true);

//        // 중심점 변경 + 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);

        // 줌 인
        mapView.zoomIn(true);

        // 줌 아웃
        mapView.zoomOut(true);
    }


    public void addMarker() {
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(35.8714354, 128.601445)); //대구 광역시 중심
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

    }


    // ============ MapView.MapViewEventListener 구현해야하는 메서드 ============ //

    @Override
    public void onMapViewInitialized(MapView mapView) {
        setMapCenter(); //지도의 중심점 설정
        addMarker();
    }


    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }


    // ============ MapView.POIItemEventListener 구현해야하는 메서드 ============ //

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
