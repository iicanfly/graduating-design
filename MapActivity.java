package robot.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

import robot.com.myapplication.overlayutil.PoiOverlay;

public class MapActivity extends Activity implements View.OnClickListener{
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    private double lat;
    private double lon;

    public int i = 0;
    public int totalPageNum = 1;
    private Button btn_search;
    private boolean first_done = false;
    private boolean done = true;

    //防止每次定位都重新设置中心点和marker
    private boolean isFirstLocation = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
//        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //普通地图 ,mBaiduMap是地图控制器对象
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //定位初始化
        mLocationClient = new LocationClient(this);
//
//    //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true); // 打开gps
//        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(1000);
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        int span = 0;
        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(false);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);

//
//    //设置locationClientOption
        mLocationClient.setLocOption(option);
//
//    //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
//    //开启地图定位图层
        mLocationClient.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
//        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_search:
                search_once();

                break;
            default:
                break;

        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            location.getTime();    //获取定位时间
            location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
            location.getLocType();    //获取定位类型
            location.getLatitude();    //获取纬度信息
            location.getLongitude();    //获取经度信息
            location.getRadius();    //获取定位精准度
            location.getAddrStr();    //获取地址信息
            location.getCountry();    //获取国家信息
            location.getCountryCode();    //获取国家码
            location.getCity();    //获取城市信息
            location.getCityCode();    //获取城市码
            location.getDistrict();    //获取区县信息
            location.getStreet();    //获取街道信息
            location.getStreetNumber();    //获取街道码
            location.getLocationDescribe();    //获取当前位置描述信息
            location.getPoiList();    //获取当前位置周边POI信息

            location.getBuildingID();    //室内精准定位下，获取楼宇ID
            location.getBuildingName();    //室内精准定位下，获取楼宇名称
            location.getFloor();    //室内精准定位下，获取当前位置所处的楼层信息
            //经纬度
            lat = location.getLatitude();
            lon = location.getLongitude();
            int errorCode = location.getLocType();
            Log.i("MapActivity.this","纬度："+ lat +"经度:" + lon + "类型:" + errorCode);
//            Toast.makeText(MapActivity.this,"纬度："+ lat +"经度:" + lon + "类型:" + errorCode, Toast.LENGTH_SHORT).show();
            //这个判断是为了防止每次定位都重新设置中心点和marker
            if (isFirstLocation) {
                isFirstLocation = true;
                //设置并显示中心点
                setPosition2Center(mBaiduMap, location, true);
            }
        }
    }

    /**
     * 设置中心点和添加marker
     *
     * @param map
     * @param bdLocation
     * @param isShowLoc
     */
    public void setPosition2Center(BaiduMap map, BDLocation bdLocation, Boolean isShowLoc) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(bdLocation.getRadius()).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        map.setMyLocationData(locData);

        if (false) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    public class OnGetPoiSearchResultListener1 implements OnGetPoiSearchResultListener
    {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                Log.i(
                        "MapActivity.this.poi",
                        "总共查到" + poiResult.getTotalPoiNum() + "个兴趣点,分为"
                                + poiResult.getTotalPageNum() + "页");
                Toast.makeText(
                        MapActivity.this,
                        "总共查到" + poiResult.getTotalPoiNum() + "个兴趣点,分为"
                                + poiResult.getTotalPageNum() + "页，" + "当前为" + poiResult.getCurrentPageNum() + "页", Toast.LENGTH_SHORT).show();
                totalPageNum = poiResult.getTotalPageNum();
                i = poiResult.getCurrentPageNum() + 1;

//                            mBaiduMap.clear();

                //创建PoiOverlay对象
                PoiOverlay poiOverlay = new PoiOverlay(mBaiduMap);

                //设置Poi检索数据
                poiOverlay.setData(poiResult);

                //将poiOverlay添加至地图并缩放至合适级别
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();
                if(i != totalPageNum) search_once();
            }
            else{
                Toast.makeText(
                        MapActivity.this,
                        "poi error" + String.valueOf(poiResult.error), Toast.LENGTH_SHORT).show();
                Log.i("提示：错误代码",String.valueOf(poiResult.error));
            }
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    }

    public void search_once(){
        PoiSearch mPoiSearch = PoiSearch.newInstance();
        OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener1();
        mPoiSearch.setOnGetPoiSearchResultListener(listener);
        /**
         *  PoiCiySearchOption 设置检索属性
         *  city 检索城市
         *  keyword 检索内容关键字
         *  pageNum 分页页码
         */
//                mPoiSearch.searchInCity(new PoiCitySearchOption()
//                        .city("北京") //必填
//                        .keyword("美食") //必填
//                        .pageNum(10));
//        first_done = false;
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(new LatLng(lat, lon))
                .radius(200)
                .keyword("餐厅")
                .pageNum(i % totalPageNum)
                .pageCapacity(2)
        );



        /**
         //                 * 设置矩形检索区域
         //                 */
//                LatLngBounds searchBounds = new LatLngBounds.Builder()
//                        .include(new LatLng( lat - 0.00001 * 100, lon - 0.00001 * 100))
//                        .include(new LatLng( lat + 0.00001 * 100, lon + 0.00001 * 100))
//                        .build();
//
///**
// * 在searchBounds区域内检索餐厅
// */
//                mPoiSearch.searchInBound(new PoiBoundSearchOption()
//                        .bound(searchBounds)
//                        .keyword("餐厅").pageNum(i % totalPageNum).pageCapacity(50));
    }
}
