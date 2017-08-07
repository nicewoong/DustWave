package kr.ac.knu.dustwave;

/**
 * Created by nicewoong on 2017. 6. 22..
 * 모든 버스정류장의 미세먼지 정보를 받아왔을 때 호출할 리스너
 */

public interface AllBusStopDustInfoHttpRequestListener {
    void onAllBusStopDustInfoDataStringArrived(String requestStream);
}
