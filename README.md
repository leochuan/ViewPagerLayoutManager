# ViewPager-LayoutManager [![Download](https://api.bintray.com/packages/leochuan/maven/viewpager-layout-manager/images/download.svg) ](https://bintray.com/leochuan/maven/viewpager-layout-manager/_latestVersion) ![build](https://travis-ci.org/leochuan/ViewPagerLayoutManager.svg?branch=master)

**English** | [中文](README_ZH.md)

![logo](static/logo.png)

VPLM is a `ViewPager` like `LayoutManager` which implements some common animations. If you need some other effects feel free to raise an issue or PR.

![circle](static/circle.gif) ![circle_scale](static/circle_scale.gif) ![carousel](static/carousel.gif) ![gallery](static/gallery.gif) ![rotate](static/rotate.gif) ![scale](static/scale.gif)

## Customzie

![customize](static/customize.gif)

Each `layoutmanager` has bunch of different properties to customize.

Such as:
* radius
* scroll speed
* space
* orientation

Run the demo to see more details.

## Infinite Scroll

![infinite](static/infinite.gif)

## Auto Center

![auto_center](static/auto_center.gif)

You can make the current position move to center automaticlly by:
```java
recyclerView.addOnScrollListener(new CenterScrollListener());
```

## Set Max Visible Item Count
```java
layoutmanager.setMaxVisibleItemCount(count);
```

## Download

Gradle:

```groovy
repositories {
  jcenter()
}

dependencies {
  compile 'rouchuan.viewpagerlayoutmanager:viewpagerlayoutmanager:2.0.4'
}
```

Maven:

```xml
<dependency>
  <groupId>rouchuan.viewpagerlayoutmanager</groupId>
  <artifactId>viewpagerlayoutmanager</artifactId>
  <version>2.0.4</version>
  <type>pom</type>
</dependency>
```

## Quick Start
Make sure that each item has the same size, or something unpredictable may happen.

You can warm up your layoutmanager by `Builder`.

```java
new CircleLayoutManager.Builder(context)
                .setAngleInterval(mAngle)
                .setMaxRemoveAngle(mMaxRemoveAngle)
                .setMinRemoveAngle(mMinRemoveAngle)
                .setMoveSpeed(mSpeed)
                .setRadius(mRadius)
                .setReverseLayout(true)
                .build();
```

Or just simply call the construct.

```java
new CircleLayoutManager(context);
```

## License

Apache-2.0. See [LICENSE](LICENSE) file for detail