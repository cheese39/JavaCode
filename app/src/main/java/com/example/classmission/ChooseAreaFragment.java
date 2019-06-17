package com.example.classmission;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classmission.db.City;
import com.example.classmission.db.County;
import com.example.classmission.db.Province;
import com.example.classmission.util.HttpUtil;
import com.example.classmission.util.JsonUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressBar;
    private TextView textView;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /*
     *省份表
     */
    private List<Province> provinceList;
    /*
     *市列表
     */
    private List<City> cityList;
    List<County> countyList;
    //选中省份
    private Province selectedProince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        textView = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProince = provinceList.get(position);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryCounties();
            } else {
                //在滑动菜单上对主屏幕进行响应
                String weatherId = countyList.get(position + 1).getWeatherId();
                WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                weatherActivity.closeDrawers();
                weatherActivity.refresh.setRefreshing(true);
                weatherActivity.requestWeather(weatherId);
            }
        });
        backButton.setOnClickListener((v) -> {
            if (currentLevel == LEVEL_COUNTY) queryCities();
            else if (currentLevel == LEVEL_CITY) queryProvinces();
            else {
                WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                weatherActivity.closeDrawers();
            }
        });
        queryProvinces();
    }

    /*
     *查询全国所有的省，优先从数据库中查询，如果没有查询到再到服务器上查询
     */
    private void queryProvinces() {
        textView.setText("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            querryFromServer(address, "province");
        }

    }

    /*
     *查询选中省内所有的市，优先从数据库总查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        textView.setText(selectedProince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport
                .where("provinceid = ?", String.valueOf(selectedProince.getId()))
                .find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            querryFromServer(address, "city");
        }
    }

    /*
     *查询选中省内所有的县，优先从数据库总查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        textView.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        /*
         *县列表
         */
        countyList = DataSupport
                .where("cityid = ?", String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (int i = 1; i < countyList.size(); i++) {
                dataList.add(countyList.get(i).getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            querryFromServer(address, "county");
        }
    }


    /*
     *根据服务器传入的地址和类型从服务器上查询省市县数据
     */
    private void querryFromServer(String address, final String type) {
        showProgressBar();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //通过runOnUiThread方法返回到主线程处理逻辑
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    closeProgressBar();
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responText = response.body().string();
                boolean result = false;
                if ("province".equals(type))
                    result = JsonUtil.handleProvinceResponse(responText);
                else if ("city".equals(type))
                    result = JsonUtil.handleCityResponse(responText, selectedProince.getId());
                else if ("county".equals(type)) {
                    result = JsonUtil.handleCountyResponse(responText, selectedCity.getId());
                }

                if (result) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        closeProgressBar();
                        if ("province".equals(type)) queryProvinces();
                        else if ("city".equals(type)) queryCities();
                        else queryCounties();
                    });
                }
            }
        });
    }

    /*
     *显示对话框
     */
    private void showProgressBar() {
        if (progressBar == null) {
            progressBar = new ProgressDialog(getActivity());
            progressBar.setMessage("正在加载....");
            progressBar.setCanceledOnTouchOutside(false);
        }
        progressBar.show();
    }

    private void closeProgressBar() {
        if (progressBar != null) {
            progressBar.dismiss();
        }
    }
}
