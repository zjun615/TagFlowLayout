# TagFlowLayout
TagFlowLayout is a ViewGroup for tag. 标签流布局，内部标签完全自定义

## 2种效果展示

### 1. Single Choose 单选
![pic1](https://github.com/zjun615/TagFlowLayout/blob/master/imgs/github1.png)

### 2. Multi Choose 多选
![pic2](https://github.com/zjun615/TagFlowLayout/blob/master/imgs/github2.png)


## Gradle依赖
> 在工程build.gradle中添加
```xml
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

> 添加依赖
```xml
implementation 'com.github.zjun615:TagFlowLayout:0.1'
```

## 使用（这里以单选标签为例）
### 1. 布局
> 标签流布局
```xml
<com.zjun.widget.tagflowlayout.TagFlowLayout
            android:id="@+id/tfl_tags_single"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:padding="10dp" />
```

> 单个标签布局item_tag_single_choose_name.xml
```xml
<TextView android:id="@+id/tv_name"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@drawable/selector_tag_bg"
    android:gravity="center"
    android:paddingEnd="8dp"
    android:paddingStart="8dp"
    android:paddingTop="4dp"
    android:paddingBottom="4dp"
    android:singleLine="true"
    android:ellipsize="end"
    android:text="测试"
    xmlns:android="http://schemas.android.com/apk/res/android" />
```

### 2. 创建适配器
继承TagFlowLayout.Adapter,实现其中的三个方法:
 - getViewCount()：标签的总个数
 - onCreateView()：创立每个标签的布局View
 - onBindView()：绑定标签对应的数据

至于点击事件，完全可以自定义
```java
// 测试数据
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
```

如果是延时加载数据，在数据加载完毕后，也可以使用adapter.setDataList()来刷新数据

## 属性说明
属性 | 说明 | 默认值
:------|:------|:------
horizontalInterval | 标签之间的水平间距 | 10dp
verticalInterval | 标签之间的垂直间距 | 与水平间距一样
