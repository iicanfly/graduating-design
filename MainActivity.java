package robot.com.myapplication;

//import android.view.View;
//
////import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
////import java.text.SimpleDateFormat;
////import java.util.Date;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import java.util.TreeMap;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import com.blankj.utilcode.util.EncryptUtils;
//import com.blankj.utilcode.util.LogUtils;
//import com.blankj.utilcode.util.TimeUtils;

import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.support.annotation.RequiresApi;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
//import java.util.Base64;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.baidu.aip.speech.AipSpeech;
//import com.baidu.aip.util.Util;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.TimeUtils;

import com.tencentcloudapi.asr.v20190614.models.SentenceRecognitionRequest;
import com.tencentcloudapi.asr.v20190614.models.SentenceRecognitionResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.asr.v20190614.AsrClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;


//import com.tencentcloudapi.common.Credential;
//import com.tencentcloudapi.common.profile.ClientProfile;
//import com.tencentcloudapi.common.profile.HttpProfile;
//import com.tencentcloudapi.common.exception.TencentCloudSDKException;
//
//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
//import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
//import cafe.adriel.androidaudioconverter.model.AudioFormat;
//import it.sauronsoftware.jave.AudioAttributes;
//import it.sauronsoftware.jave.AudioUtils;
//import it.sauronsoftware.jave.Encoder;
//import it.sauronsoftware.jave.EncodingAttributes;
////import robot.com.myapplication.audio.AudioRecorder;
import tech.oom.idealrecorder.IdealRecorder;
import tech.oom.idealrecorder.StatusListener;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

public class MainActivity extends AppCompatActivity implements
        HttpGetDataListener,View.OnClickListener {

    private HttpData httpData;
    private HttpVoiceData httpVoiceData;
    private List<ListData> lists;
    private ListView lv;
    private EditText et_sendText;
    private Button btn_send;
    private Button btn_start;
    private Button btn_end;
    private Button btn_play;
    private Button btn_lo;
    private TextAdapter adapter;
    private String[] welcome_arry;
    private double currentTime,oldTime = 0;


    private File recordAudioFile;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private IdealRecorder idealRecorder;
    private IdealRecorder.RecordConfig recordConfig;

//    AudioRecorder audioRecorder;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    String randomStr = getRandomString(10);
    String randomSession = getRandomString(10);

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

            Toast.makeText(MainActivity.this,"纬度："+ latitude +"经度:" + longitude + "错误:" + errorCode, Toast.LENGTH_LONG).show();
            Log.i("loca_re:","纬度："+ latitude +"经度:" + longitude + "错误:" + errorCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IdealRecorder.getInstance().init(this);
        recordConfig = new IdealRecorder.RecordConfig(MediaRecorder.AudioSource.MIC, 16000, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
//        mLocationClient.getLastKnownLocation().getAltitude();
        //注册监听函数
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Log.i("haha7", "----------7");
        initView();
        Log.i("haha8", "----------8");
    }


    private void initView(){
        lists = new ArrayList<ListData>();
        lv = (ListView) findViewById(R.id.lv);
        et_sendText = (EditText) findViewById(R.id.et_sendText);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_end = (Button) findViewById(R.id.btn_end);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_lo = (Button) findViewById(R.id.btn_lo) ;
        btn_send.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        btn_end.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_lo.setOnClickListener(this);
        adapter = new TextAdapter(lists, this);
        lv.setAdapter(adapter);
        ListData listData;
        listData = new ListData(getRandomWelcomeTips(),ListData.RECEIVE, getTime());
        lists.add(listData);
//        audioRecorder = AudioRecorder.getInstance();



//        try {
//            Log.i("amrfilr:::", Environment.getExternalStorageDirectory().getCanonicalPath() + "/record1.amr");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public String getRandomWelcomeTips(){
//        String welcome_tip = null;
//        welcome_arry = this.getResources().getStringArray(getResources().getString(R.string.welcome_tips));
//        int index = (int)(Math.random()*(welcome_arry.length - 1));
//        welcome_tip = welcome_arry[index];
        return getResources().getString(R.string.welcome_tips);
    }


    private String getTime(){
        currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy--MM--dd   HH:mm:ss");
        Date curDate = new Date();
        String str = format.format(curDate);
        if(currentTime - oldTime >= 5*60*1000){
            oldTime = currentTime;
            return str;
        }else{
            return "";
        }
    }


    @Override
    public void getDataUrl(String data) {
        Log.i("haha---data=","------" + data);
        parseText(data);
    }

    public void parseText(String str){
        try {
            JSONObject jb = new JSONObject(str);
            ListData listData;
            listData = new ListData(jb.getString("data"),ListData.RECEIVE, getTime());
            lists.add(listData);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            /**   发送文本
             //   o(*￣▽￣*)o
             //     o(*￣▽￣*)o
             //       o(*￣▽￣*)o
             **/
            case R.id.btn_send:
                getTime();
                String ques = et_sendText.getText().toString().trim();
                et_sendText.setText("");
//        String dropk = content_str.replace(" ", "");
//        String droph = dropk.replace("\n", "");
                ListData listData;
                listData = new ListData(ques, ListData.SEND, getTime());
                lists.add(listData);
                Log.i("haha9", "----------content_str="+ ques);
                adapter.notifyDataSetChanged();

                Log.i("haha6", "----------content_str="+ ques);

//        long currTime = (long)new Date().getTime() / 1000;
                long currTime = (TimeUtils.getNowMills() / 1000);

                Map<String, Object> params = new HashMap<>();
                int APP_ID = 2124123394;
                params.put("app_id", APP_ID);
                params.put("time_stamp", currTime);
                params.put("nonce_str", randomStr);
                params.put("session", randomSession);
//        params.put("question", droph);
                params.put("question", ques);

                Map<String, Object> resultMap = sortMapByKey(params);
                Set<String> keySet = resultMap.keySet();
                StringBuilder sb = new StringBuilder();
                for (String key : keySet) {
                    Object value = resultMap.get(key);
                    try {
                        sb.append("&").append(key).append("=").append(URLEncoder.encode(value + "", "UTF-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sb.deleteCharAt(0);
                String APP_KEY = "86D4iw3qbe6umOzj";
                sb.append("&app_key=").append(APP_KEY);
                //  4. MD5运算
                String md5Sign = EncryptUtils.encryptMD5ToString(sb.toString());


                Log.i("sign", "is ======="+md5Sign);
                Log.i("time_stamp", "is ======="+currTime);

                httpData = (HttpData) new HttpData(
                        "https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat",
                        this,
                        APP_ID,
                        currTime,
                        randomStr,
                        md5Sign,
                        randomSession,
                        ques
                ).execute();


                Log.i("haha5", "----------"+httpData);

                if(lists.size() > 30){
                    for (int i = 0; i < lists.size(); i++) {
                        lists.remove(i);
                    }
                }
                break;

            /**   开始录音
             //   o(*￣▽￣*)o
             //     o(*￣▽￣*)o
             //       o(*￣▽￣*)o
             **/
            case R.id.btn_start:
//                try {
////                    recordAudioFile =  File.createTempFile(Environment.getExternalStorageDirectory().getCanonicalPath() + "/record",".amr");
//                    recordAudioFile =  new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/record1.amr");
//                    mediaRecorder = new MediaRecorder();
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    mediaRecorder.setAudioSamplingRate(8000);
//                    mediaRecorder.setAudioChannels(1);
//                    mediaRecorder.setAudioEncodingBitRate(8000 * 16 * 1);
//                    mediaRecorder.setOutputFile(recordAudioFile.getAbsolutePath());
//
//                    try {
//                        mediaRecorder.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mediaRecorder.start();
//                    Toast.makeText(this,"开始录音" + recordAudioFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


//                try {
//                    if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
//                        //初始化录音
//                        String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//                        audioRecorder.createDefaultAudio(fileName);
//                        audioRecorder.startRecord(null);
//                    }
//                } catch (IllegalStateException e) {
//                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//
                idealRecorder = IdealRecorder.getInstance();

                try {
                    idealRecorder.setRecordFilePath(Environment.getExternalStorageDirectory().getCanonicalPath() + "/test-16k.wav");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //如果需要保存录音文件  设置好保存路径就会自动保存  也可以通过onRecordData 回调自己保存  不设置 不会保存录音

                idealRecorder.setRecordConfig(recordConfig).setMaxRecordTime(20000).setVolumeInterval(200);
                //设置录音配置 最长录音时长 以及音量回调的时间间隔

//                idealRecorder.setStatusListener(statusListener);
                //设置录音时各种状态的监听

                idealRecorder.start();
                //开始录音
                break;


            /**   结束录音
             //   o(*￣▽￣*)o
             //     o(*￣▽￣*)o
             //       o(*￣▽￣*)o
             **/
            case R.id.btn_end:
//                if(mediaRecorder != null){
//                    mediaRecorder.stop();
//                    mediaRecorder.release();
//                    mediaRecorder = null;
////                    Toast.makeText(this,"停止录音",Toast.LENGTH_LONG).show();
//                }
//                audioRecorder.stopRecord();





                /**
                test sdk
                 **/
//                // 初始化一个AipSpeech
//                AipSpeech client = new AipSpeech("18340776", "pv2GNjZAG1AeEdXbYAplQWu5", "GQ4OoS01ZM7iyBD3KToLsSIpEG6FPBAp");
//
//                // 可选：设置网络连接参数
//                client.setConnectionTimeoutInMillis(2000);
//                client.setSocketTimeoutInMillis(60000);
//
//                // 可选：设置代理服务器地址, http和socket二选一，或者均不设置        			           		//client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理       				//client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
//                // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
//                //也可以直接通过jvm启动参数设置此环境变量
//                System.setProperty("aip.log4j.conf", "log4j.properties");
//
//                asr(client);


                //采用本地语音上传方式调用
                try{
                    String re;
//                    idealRecorder.stop();
                    //重要，此处<Your SecretId><Your SecretKey>需要替换成客户自己的账号信息，获取方法：
                    //https://cloud.tencent.com/document/product/441/6203
                    //具体路径：点控制台右上角您的账号-->选：访问管理-->点左边菜单的：访问密钥-->API 密钥管理
                    Credential cred = new Credential("AKIDg7HF3nnzOWFuKU9L0PXS7dsIVS8Xuaia", "oKdgSxDDyShhRxDG4ekcFhuwkLhPs65y");

                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("asr.tencentcloudapi.com");

                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    clientProfile.setSignMethod("TC3-HMAC-SHA256");
                    AsrClient client = new AsrClient(cred, "ap-shanghai", clientProfile);

                    String params_1 = "{\"ProjectId\":0,\"SubServiceType\":2,\"EngSerViceType\":\"16k\",\"SourceType\":1,\"Url\":\"\",\"VoiceFormat\":\"wav\",\"UsrAudioKey\":\"session-123\"}";
                    SentenceRecognitionRequest req = SentenceRecognitionRequest.fromJsonString(params_1, SentenceRecognitionRequest.class);

                    Log.i("file::::","begin convert");


//                    File file = new File(recordAudioFile.getAbsolutePath());
                    File file = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/test-16k.wav");
                    FileInputStream inputFile = new FileInputStream(file);
                    byte[] buffer = new byte[(int)file.length()];
                    req.setDataLen(file.length());
                    inputFile.read(buffer);
                    inputFile.close();
//                    String encodeData = Base64.getEncoder().encodeToString(buffer);
                    String encodeData = Base64.encodeToString(buffer, Base64.DEFAULT);
                    req.setData(encodeData);

                    SentenceRecognitionResponse resp = client.SentenceRecognition(req);

                    JSONObject temp_jb = new JSONObject(SentenceRecognitionRequest.toJsonString(resp));

                    Log.i("the return :::::::::",""+ SentenceRecognitionRequest.toJsonString(resp));
                    Toast.makeText(this,SentenceRecognitionRequest.toJsonString(resp),Toast.LENGTH_LONG).show();
                    re = temp_jb.getString("Result");
                    listData = new ListData(re, ListData.SEND, getTime());
                    lists.add(listData);
                    Log.i("re", "----------re="+ re);
                    adapter.notifyDataSetChanged();

                    Log.i("haha6", "----------content_str="+ re);

//        long currTime = (long)new Date().getTime() / 1000;
                    currTime = (TimeUtils.getNowMills() / 1000);

                    params = new HashMap<>();
                    APP_ID = 2124123394;
                    params.put("app_id", APP_ID);
                    params.put("time_stamp", currTime);
                    params.put("nonce_str", randomStr);
                    params.put("session", randomSession);
//        params.put("question", droph);
                    params.put("question", re);

                    resultMap = sortMapByKey(params);
                    keySet = resultMap.keySet();
                    sb = new StringBuilder();
                    for (String key : keySet) {
                        Object value = resultMap.get(key);
                        try {
                            sb.append("&").append(key).append("=").append(URLEncoder.encode(value + "", "UTF-8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    sb.deleteCharAt(0);
                    APP_KEY = "86D4iw3qbe6umOzj";
                    sb.append("&app_key=").append(APP_KEY);
                    //  4. MD5运算
                    md5Sign = EncryptUtils.encryptMD5ToString(sb.toString());


                    Log.i("sign", "is ======="+md5Sign);
                    Log.i("time_stamp", "is ======="+currTime);

                    httpData = (HttpData) new HttpData(
                            "https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat",
                            this,
                            APP_ID,
                            currTime,
                            randomStr,
                            md5Sign,
                            randomSession,
                            re
                    ).execute();


                    Log.i("haha5", "----------"+httpData);

                    if(lists.size() > 30){
                        for (int i = 0; i < lists.size(); i++) {
                            lists.remove(i);
                        }
                    }
                }
                catch (TencentCloudSDKException e) {
                    System.out.println(e.toString());
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



//                getTime();
//
////                adapter.notifyDataSetChanged();
//
//                currTime = (TimeUtils.getNowMills() / 1000);
//
//                params = new HashMap<>();
//                APP_ID = 2124123394;
//                int format = 3;
//                int rate = 8000;
////                String speech = file2Base64(recordAudioFile.getAbsolutePath());
////                String speech = Base64.encodeToString(recordAudioFile.getAbsolutePath(), Base64.DEFAULT)
//                String speech = null;
//                try {
//                    speech = encodeBase64File(recordAudioFile.getAbsolutePath());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                params.put("app_id", APP_ID);
//                params.put("time_stamp", currTime);
//                params.put("nonce_str", randomStr);
//                params.put("format", format);
//                params.put("speech",speech);
//                params.put("rate",rate);
//
////                Toast.makeText(this,"speech：" + speech,Toast.LENGTH_LONG).show();
//
//                resultMap = sortMapByKey(params);
//                keySet = resultMap.keySet();
//                sb = new StringBuilder();
//                for (String key : keySet) {
//                    Object value = resultMap.get(key);
//                    try {
//                        sb.append("&").append(key).append("=").append(URLEncoder.encode(value + "", "UTF-8"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                sb.deleteCharAt(0);
//                APP_KEY = "86D4iw3qbe6umOzj";
//                sb.append("&app_key=").append(APP_KEY);
//                //  4. MD5运算
//                md5Sign = EncryptUtils.encryptMD5ToString(sb.toString());
//
//
//                Log.i("sign", "is ======="+md5Sign);
//                Log.i("time_stamp", "is ======="+currTime);
//
//                httpVoiceData = (HttpVoiceData) new HttpVoiceData(
//                        "https://api.ai.qq.com/fcgi-bin/aai/aai_asr",
//                        this,
//                        APP_ID,
//                        currTime,
//                        randomStr,
//                        md5Sign,
//                        format,
//                        speech,
//                        rate
//                ).execute();
//
//                Log.i("haha5", "----------"+httpVoiceData);
//
//                if(lists.size() > 30){
//                    for (int i = 0; i < lists.size(); i++) {
//                        lists.remove(i);
//                    }
//                }

                break;

            /**   播放刚才录下的录音
            //   o(*￣▽￣*)o
            //     o(*￣▽￣*)o
            //       o(*￣▽￣*)o
            **/
            case R.id.btn_play:
//                mediaPlayer = new MediaPlayer();
//                try {
//                    mediaPlayer.setDataSource(recordAudioFile.getAbsolutePath());
////                    mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getCanonicalPath() + "/record1.amr");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    mediaPlayer.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                mediaPlayer.start();
                initLocation();
                mLocationClient.start();//开始定位
                //mLocationClient为第二步初始化过的LocationClient对象
                //调用LocationClient的start()方法，便可发起定位请求

                break;
            case R.id.btn_lo:
                mLocationClient.stop();
//                String strInfo = String.format("纬度：%f 经度：%f",
//                        geoCodeResult.getLocation().latitude, geoCodeResult.getLocation().longitude);
//                Log.d("data", strInfo);
//                Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }


    }


    /**
     * 使用 Map按key进行排序
     *
     * @param map x
     * @return x
     */
    public Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Object> sortMap = new TreeMap<>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    /**
     * 随机字符串
     *
     * @param length x
     * @return x
     */
    public String getRandomString(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }



    public static String encodeBase64File(String path) throws Exception {
        File  file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer,Base64.DEFAULT);
    }




//    public static void asr(AipSpeech client){
//        // 对本地语音文件进行识别
//        String path = "Environment.getExternalStorageDirectory().getCanonicalPath() + \"/record1.amr\"";
//        JSONObject asrRes = client.asr(path, "wav", 16000, null);
//        System.out.println(asrRes);
//
//        // 对语音二进制数据进行识别
//        byte[] data = new byte[0];
//        //readFileByBytes仅为获取二进制数据示例
//        try {
//            data = Util.readFileByBytes(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JSONObject asrRes2 = client.asr(data, "wav", 16000, null);
//        try {
//            System.out.println(asrRes.getInt("err_no")==0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            System.out.println(asrRes2.get("result"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

}
