package robot.com.myapplication;

import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class HttpVoiceData extends AsyncTask<String, Void, String> {

    private HttpClient httpClient;
    private HttpPost httpPost;
    private HttpResponse httpResponse;
    private HttpEntity httpEntity;
    private InputStream in;

    private HttpGetDataListener listener;
    private String url;
    private  int app_id;
    private  long time_stamp;
    private  String nonce_str;
    private  String sign;
    private int format;
    private String speech;
    private int rate;


    public HttpVoiceData(String url ,HttpGetDataListener listener, int app_id, long time_stamp, String nonce_str,
                    String sign, int format, String speech , int rate) {
        this.url = url;
        this.listener = listener;
        this.app_id = app_id;
        this.time_stamp = time_stamp;
        this.nonce_str = nonce_str;
        this.sign = sign;
        this.format = format;
        this.speech = speech;
        this.rate = rate;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
            Log.i("test", "we enter here");
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(url);
            List<NameValuePair> pairs = null;
            pairs = new ArrayList<NameValuePair>(6);
            pairs.add(new BasicNameValuePair("app_id","" + app_id));
            pairs.add(new BasicNameValuePair("time_stamp","" + time_stamp));
            pairs.add(new BasicNameValuePair("nonce_str","" + nonce_str));
            pairs.add(new BasicNameValuePair("sign","" + sign));
            pairs.add(new BasicNameValuePair("format","" + format));
            pairs.add(new BasicNameValuePair("speech","" + speech));
            pairs.add(new BasicNameValuePair("rate","" + rate));
            System.out.println(pairs);

            Log.i("test","pairs ok");
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            Log.i("test","set entity ok");
            httpResponse = httpClient.execute(httpPost);
            Log.i("test","http client execute ok");
            httpEntity = httpResponse.getEntity();
            Log.i("test","get entity ok");

//            String get_url = url + "?" + "app_id=" + app_id + "&" + "time_stamp=" + time_stamp + "&" +
//                    "nonce_str=" + nonce_str + "&" + "sign=" + sign + "&" + "session=" + session + "&" + "question=" + question;
//            get_url= get_url.replaceAll(" ", "%20");
//            httpGet =  new HttpGet(get_url);
//			httpGet = new HttpGet("http://www.tuling123.com/openapi/api?key=02dfb86de93f8a3e81dabd214a50c8fa&info=hi&userid=15602229049");
//			httpPost =  new HttpPost(url);
//			String paramJson="{\"app_id\"" + ":" + "\"" + app_id + "\"" + "," +
//					"\"time_stamp\"" + ":" + "\"" + time_stamp + "\"" + "," +
//					"\"nonce_str\"" + ":" + "\"" + nonce_str + "\"" + "," +
//					"\"sign\"" + ":" + "\"" + sign + "\"" + "," +
//					"\"session\"" + ":" + "\"" + session + "\"" + "," +
//					"\"question\"" + ":" + "\"" + question + "\"" + "}";
//			StringEntity requestEntity = new StringEntity(paramJson,"UTF-8");


//            Log.i("test","url is " + get_url);
//            Log.i("test","url ok");

//			httpPost.setEntity(requestEntity);
// 			Log.i("test","set entity ok");
//            httpResponse = httpClient.execute(httpGet);
//            Log.i("test","http client execute ok");
//            httpEntity = httpResponse.getEntity();
//            Log.i("test","get entity ok");
//            List<NameValuePair> paraList = new ArrayList<NameValuePair>();
//            Set<Map.Entry<String,String>> mapSet = params.entrySet();
//            Iterator<Map.Entry<String,String>> iterator = mapSet.iterator();
//            while(iterator.hasNext()){
//                Map.Entry<String,String> elem =  iterator.next();
//                paraList.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
//            }
//            UrlEncodedFormEntity entity = new UrlEncodedFormEntity()

            Log.i("haha1", "----------"+httpEntity.toString());

            in = httpEntity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            Log.i("haha2", "----------"+br.toString());

            String line = null;
            StringBuffer sb = new StringBuffer();
            while((line  = br.readLine()) != null){
                sb.append(line);
            }

            Log.i("haha3", "----------"+sb.toString());

            return sb.toString();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        listener.getDataUrl(result);

        Log.i("haha4", "----------"+result);

        super.onPostExecute(result);
    }
}
