package kr.ac.knu.dustwave;

/**
 * Created by nicewoong on 2017. 6. 20..
 * Local DB 에서 사용되는 column 변수명을 모음
 */

public class LocalDatabaseKey {


    //BUS STOP TABLE
    public static final String bus_stop_id = "S_ID";
    public static final String bus_stop_name = "NAME";
    public static final String bus_stop_latitude = "LAT";
    public static final String bus_stop_longitude = "LON";

    //DUST INFO TABLE
    public static final String dust_info_pm10 = "PM_10";
    public static final String dust_info_pm25 = "PM_25";
    public static final String dust_info_pm10_predict = "PM_10_PRED";
    public static final String dust_info_pm25_predict = "PM_25_PRED";
    public static final String dust_info_temperature = "TEMP";
    public static final String dust_info_wind_speed = "WIND_SPEED";
    public static final String dust_info_wind_direction = "WIND_DIRECTION";
    public static final String dust_info_humidity = "HUMIDITY";
    public static final String dust_info_weather = "WEATHER";
    public static final String dust_info_time = "TIME";


//
//
//
//
//    db.run("create table if not exists " +
//            "busstop " +
//            "( S_ID INT PRIMARY KEY, " +
//            "NAME CHAR(50), " +
//            "LON CHAR(50), " +
//            "LAT CHAR(50));");
//
//
//    db.run("create table if not exists dustinforecent" +
//            "(S_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
//            "PM_10 CHAR(50), " +
//            "PM_25 CHAR(50), " +
//            "PM_10_PRED CHAR(50)," +
//            "PM_25_PRED CHAR(50)," +
//            "TEMP INT," +
//            "WiNDSPEED CHAR(50)," +
//            "WiNDIRECTION CHAR(50)," +
//            "HUMIDITY INT," +
//            "WEATHER CHAR(50)," +
//            "TIME DATE );");
//
//    db.run("create table if not exists dustinfo( " +
//            "D_ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
//            "S_ID INT, " +
//            "PM_10 CHAR(50), " +
//            "PM_25 CHAR(50), " +
//            "PM_10_PRED CHAR(50)," +
//            "PM_25_PRED CHAR(50)," +
//            "TEMP INT," +
//            "WiNDSPEED CHAR(50)," +
//            "WiNDIRECTION CHAR(50)," +
//            "HUMIDITY INT," +
//            "WEATHER CHAR(50)," +
//            "TIME DATE );");
//

}
