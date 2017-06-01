ViewPager-Layout-Manager
======================
## [English](README_EN.md)

![Example](resources/circle1.gif "working example") ![Example](resources/circle2.gif "working example") 
![Example](resources/circle3.gif "working example") ![Example](resources/circle4.gif "working example")

## 用法

### Gradle

如果想要自定义效果请在build.gradle文件中引入：

```Java
compile 'rouchuan.viewpagerlayoutmanager:viewpagerlayoutmanager-core:1.2.0'
```
[自定义教程](http://www.jianshu.com/p/b193319d35cf)

如果你想用上面提供的效果，请引入（不需要再引用core）：

```groovy
compile 'rouchuan.viewpagerlayoutmanager:viewpagerlayoutmanager-support:1.0.0'
```

### 注意!!!

#### 在定义样式item样式的时候请确保每个子view的大小相同，暂不支持不同大小的子view



### 启动回弹

```Java
recyclerView.addOnScrollListener(new CenterScrollListener());
```



### 展示滚动条

与recyclerView相同，默认平滑滚动，调用setSmoothScrollbarEnabled设置。

```xml
 <android.support.v7.widget.RecyclerView
        android:scrollbars="horizontal"
        android:id="@+id/recycler"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```



## 更新

1. 拆分了core与support
2. 修复了使用Universal-Image-Loader载入图片会导致跳动到第一项
3. 支持在view初始化完成之前对recyclerView进行scrollToPosition
4. 优化了布局算法的性能



## 接下来要做的事

1. 支持无限滚动
2. 进一步优化性能
3. 添加indicator
4. 支持不同大小的子View
5. 给support库添加其他效果(长期的课题)



## License

    Copyright 2016 shenruochuan
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.