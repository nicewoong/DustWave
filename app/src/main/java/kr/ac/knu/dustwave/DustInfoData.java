package kr.ac.knu.dustwave;

/**
 * Created by nicewoong on 2017. 6. 21..
 * 서버에서 받아온 미세먼지정보를 변환하고 처리하고 Local database 의 DustInfo Table 에 접근해서 입출력을 총 담당하는 클래스
 */

public class DustInfoData {


    /*
     필요한 메서드

     -> request1에 대해서
        -




     -------------------------------

     -> request 2 에 대해서

         - request 요청하는 함수
            - 이름 : requestAllRecentDustData()
            - 모든 버스 정보를 요청하기만 함 HTTP 통신을 통해 AsyncTask 로 요청.
            - 콜백 메서드를 안에서 포함하겠군
            - 콜백 메서드 안에서 DATA 가 날라오면 string 으로 오는 모든 정류장 미세먼지 정보를 -> jason object  array 로 바꾸는 메서드 필요하네

         - string 의 모든 정류장 미세먼지 정보를 -> jason object array 로 바꿔주는 메서드
            - 이름 : convertAllRecentDustDataToJasonArray
            - 인자를 String 으로 받는다
            - 내부에서 Jason object로 바꿔서 array로 만든다음
            - return 을 Jason array 형식으로 한다

        - json object array 로 입력된 모든 정류장 미세먼지 정보를 -> Local DB Dust info table 에 저장하는 메서드
            - 이름 : saveAllRecentDustDataToLocalDB
            - Json Array 를 인자로 받는다
            - 반복문을 돌려서 object 하나씩 뽑은 다음 object 에서 원소 하나씩 뽑은 다음 기존 데이터 밀고 새로 집어넣는다 <= 우선은 이렇게 합시다:)
            -> 이거 호출한 다음에는 바로 view 갱신하는 메서드 호출해줘야겠네


        - Local DB 에서 모든 정류장 미세먼지 정보를 버스 stop 정보와 join 해서 Json object array 형태로 반환하는 메서드


        - json object array 를 인풋으로 받아서 지도에 띄워주는 메서드? ㅇㅇ 여기에서 map 을 인자로 받아서 .
            - 이름 : addAllDustInfoMarkerOnMap()
            - 인자 :


     */
}
