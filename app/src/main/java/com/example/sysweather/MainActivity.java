package com.example.sysweather;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import java.io.IOException;
import com.alibaba.fastjson.JSONObject;
import com.lljjcoder.citypickerview.widget.CityPicker;

import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_response;
    private EditText Ccode;
    private CityPicker mCP;
    private TextView address;
    //private TextView Gaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendRequest = findViewById(R.id.send_request);
        Button chose_city = findViewById(R.id.chose_city);
        Button get_adcode = findViewById(R.id.get_adcode);
        Ccode = (EditText) findViewById(R.id.cityC);
        //Gaddress = (TextView) findViewById(R.id.address);
        sendRequest.setOnClickListener(this);
        chose_city.setOnClickListener(this);
        get_adcode.setOnClickListener(this);
        this.tv_response = findViewById(R.id.response);
        this.address = findViewById(R.id.address);
    }
    //选城市（滚轮
    public void initCityPicker() {
        //滚轮文字的大小
        //滚轮文字的颜色
        //省份滚轮是否循环显示
        //城市滚轮是否循环显示
        //地区（县）滚轮是否循环显示
        //滚轮显示的item个数
        //滚轮item间距
        mCP = new CityPicker.Builder(MainActivity.this)
                .textSize(20)//滚轮文字的大小
                .title("地址选择")
                .backgroundPop(0xa0000000)
                .titleBackgroundColor("#99CC99")
                .titleTextColor("#000000")
                .backgroundPop(0xa0000000)
                .confirTextColor("#000000")
                .cancelTextColor("#000000")
                .province("xx省")
                .city("xx市")
                .district("xx区")
                .textColor(Color.parseColor("#000000"))//滚轮文字的颜色
                .provinceCyclic(true)//省份滚轮是否循环显示
                .cityCyclic(false)//城市滚轮是否循环显示
                .districtCyclic(false)//地区（县）滚轮是否循环显示
                .visibleItemsCount(7)//滚轮显示的item个数
                .itemPadding(10)//滚轮item间距
                .onlyShowProvinceAndCity(false)
                .build();
        mCP.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];

                address.setText(province + city + district);

            }

            @Override
            public void onCancel() {

            }
        });
    }

    //第一个切片
    public String tranString(String qp){
        qp = qp.substring(qp.indexOf("[") + 1 ,qp.indexOf("]"));
        W tq = JSONObject.parseObject(qp,W.class);
        return tq.toString();
    }
    //第二次切片
    public String tranString1(String qp1){
        qp1 = qp1.substring(qp1.indexOf("adcode") + 7 ,qp1.indexOf("adcode") + 13);
        return qp1;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.chose_city:
                initCityPicker();
                mCP.show();
                break;
            case R.id.send_request:
                String GCcode = String.valueOf(Ccode.getText());
                //初始化OKHttp客户端
                OkHttpClient client = new OkHttpClient();
                //构造Request对象，采⽤建造者模式，链式调⽤指明进⾏Get请求,传⼊Get的请求地址
                Request request = new Request.Builder().get()
                        .url("https://restapi.amap.com/v3/weather/weatherInfo?city="+GCcode+"&key=8135e185ba8b802963c934645ef86b2b&extensions=base")
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //失败处理
                        ToastUtils.showToast(MainActivity.this, "Get请求失败");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //返回结果处理
                        final String responseStr = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(responseStr.length()<10){
                                    ToastUtils.showToast(MainActivity.this, "城市不存在");
                                }
                                else
                                tv_response.setText(tranString(responseStr));
                            }
                        });
                    }
                });
                break;
            case R.id.get_adcode:
                //初始化OKHttp客户端
                OkHttpClient client1 = new OkHttpClient();
                //构造Request对象，采⽤建造者模式，链式调⽤指明进⾏Get请求,传⼊Get的请求地址
                Request request1 = new Request.Builder().get()
                        .url("https://restapi.amap.com/v3/geocode/geo?address="+address.getText()+"&output=XML&key=8135e185ba8b802963c934645ef86b2b&extensions=base")
                        .build();
                Call call1 = client1.newCall(request1);
                call1.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //失败处理
                        ToastUtils.showToast(MainActivity.this, "Get请求失败");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //返回结果处理
                        final String responseStr1 = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Ccode.setText(tranString1(responseStr1));
                            }
                        });
                    }
                });
                break;



        }
    }
}