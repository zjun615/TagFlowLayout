package com.zjun.demo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zjun.widget.tagflowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TagFlowLayout tfl_tags_single;
    private TagFlowLayout tfl_tags_multi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tfl_tags_single = findViewById(R.id.tfl_tags_single);
        tfl_tags_multi = findViewById(R.id.tfl_tags_multi);

        testSingleChoose();
        testMultiChoose();
    }

    private void testMultiChoose() {
        final MultiAdapter adapter = new MultiAdapter(this, null);
        tfl_tags_multi.setAdapter(adapter);

        // delay load data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<City> list = createTestData();
                adapter.setDataList(list);
            }
        }, 2000);

    }

    private void testSingleChoose() {
        final List<City> tagList = createTestData();
        TagFlowLayout.Adapter adapter = new TagFlowLayout.Adapter(this) {

            private View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = (int) v.getTag();
                    final City city = tagList.get(position);
                    if (city.isChosen()) {
                        logD("onClick>>>this city has chosen...");
                        return;
                    }
                    for (City c : tagList) {
                        c.setChosen(false);
                    }
                    city.setChosen(true);
                    notifyDataSetChanged();
                    toast(city.getId() + ": " + city.getName());
                }
            };

            @Override
            public int getViewCount() {
                return tagList.size();
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.item_tag_single_choose_name, parent, false);
            }

            @Override
            public void onBindView(View view, int position) {
                TextView textView = (TextView) view;
                City city = tagList.get(position);
                textView.setText(city.getName());
                textView.setTag(position);
                textView.setSelected(city.isChosen());
                textView.setOnClickListener(onClickListener);
            }
        };

        tfl_tags_single.setAdapter(adapter);
    }

    class MultiAdapter extends TagFlowLayout.Adapter {

        private List<City> dataList;

        public MultiAdapter(@NonNull Context context, List<City> dataList) {
            super(context);
            this.dataList = dataList;
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = (int) v.getTag();
                final City city = dataList.get(position);
                city.setChosen(!city.isChosen());
                notifyDataSetChanged();
                toast(city.getId() + ": " + city.getName());
            }
        };


        @Override
        public int getViewCount() {
            return dataList == null ? 0 : dataList.size();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
            return inflater.inflate(R.layout.item_tag_multi_choose_name, parent, false);
        }

        @Override
        public void onBindView(View view, int position) {
            TextView textView = (TextView) view;
            City city = dataList.get(position);
            textView.setText(city.getName());
            textView.setTag(position);
            textView.setSelected(city.isChosen());
            textView.setOnClickListener(onClickListener);
        }

        public void setDataList(List<City> dataList) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }
    }

    private List<City> createTestData() {
        final List<City> cityList = new ArrayList<>();
        cityList.add(new City(1, "来一个超级长的标签，你能全部显示出来吗，看一下你的效果咯"));
        cityList.add(new City(2, "北京"));
        cityList.add(new City(3, "上海"));
        cityList.add(new City(4, "黑龙江"));
        cityList.add(new City(5, "新疆维吾尔自治区"));
        cityList.add(new City(6, "内蒙古"));
        cityList.add(new City(7, "浙江z"));
        cityList.add(new City(8, "江西"));
        cityList.add(new City(9, "四川"));
        cityList.add(new City(10, "中国台湾省"));
        cityList.add(new City(11, "澳洲"));
        cityList.add(new City(12, "香港"));
        cityList.add(new City(13, "广西壮族自治区"));
        cityList.add(new City(14, "钓鱼岛"));
        cityList.add(new City(15, "深圳"));

        return cityList;
    }

    private void logD(String format, Object... args) {
        Log.d("MainActivity", String.format(format, args));
    }

    private Toast mToast;
    private void toast(String format, Object... args) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setText(String.format(format, args));
        mToast.show();
    }
}
