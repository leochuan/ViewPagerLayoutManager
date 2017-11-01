# ViewPager-LayoutManager [![Download](https://api.bintray.com/packages/rouchuan/maven/viewpager-layout-manager/images/download.svg) ](https://bintray.com/rouchuan/maven/viewpager-layout-manager/_latestVersion)

[English](README.md) | **中文**

VPLM 实现了一些常见的动画效果，如果你有什么别的想要的效果欢迎给我提ISSUE以及PR

![circle](static/circle.gif) ![circle_scale](static/circle_scale.gif) ![carousel](static/carousel.gif) ![gallery](static/gallery.gif) ![rotate](static/rotate.gif) ![scale](static/scale.gif)

## 自定义属性
![customize](static/customize.gif)

各个`layoutmanager`都有各自的一些属性可以设置
比如：
* 半径
* 滚动速度
* 间隔
* 排列方向

可以运行下demo看下具体有哪些属性可以设置

## 循环列表

![infinite](static/infinite.gif)

## 自动回滚

![auto_center](static/auto_center.gif)

可通过给recyclerView添加`CenterScrollListener`实现
```java
recyclerView.addOnScrollListener(new CenterScrollListener());
```

## 安装

Gradle:

```groovy
repositories {
  jcenter()
}

dependencies {
  compile 'rouchuan.viewpagerlayoutmanager:viewpagerlayoutmanager:2.0.0'
}
```

Maven:

```xml
<dependency>
  <groupId>rouchuan.viewpagerlayoutmanager</groupId>
  <artifactId>viewpagerlayoutmanager</artifactId>
  <version>2.0.0</version>
  <type>pom</type>
</dependency>
```

## 快速开始

你可以通过新建一个`Builder`来设置各种属性:
```java
new CircleLayoutManager.Builder()
                .setAngleInterval(mAngle)
                .setMaxRemoveAngle(mMaxRemoveAngle)
                .setMinRemoveAngle(mMinRemoveAngle)
                .setMoveSpeed(mSpeed)
                .setRadius(mRadius)
                .setReverseLayout(true)
                .build();
```

或者只是简单的调用一下预设的构造方法:

```java
new CircleLayoutManager();
```

## License

Apache-2.0. 详情见 [LICENSE](LICENSE)