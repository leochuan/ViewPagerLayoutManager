Custom-Layout-Manager
======================
Building your own LayoutManager faster.

It will handle the recycling under the hood.
All you need to concern about is which the property you want to change and how it change accroding to the scroll offset.

![Example](resources/circle1.gif "working example") ![Example](resources/circle2.gif "working example") 
![Example](resources/circle3.gif "working example") ![Example](resources/circle4.gif "working example")

## Usage
#### Gradle
```Java
compile 'rouchuan.customlayoutmanager:customlayoutmanager:1.0.1'
```
####Default Properties 
```Java
protected Context context;

// Size of each items
protected int mDecoratedChildWidth;
protected int mDecoratedChildHeight;

protected int startLeft; //position x of first item
protected int startTop; // position y of first item
protected float offset; //The delta of property which will change when scroll

protected float interval; //the interval between each items
```
####Consturct
By default there are two constructs.isClockWise determine the way how each items align

```Java
//Default pass isClockWise true
public CustomLayoutManager(Context context){
    this(context,true);
}

public CustomLayoutManager(Context context, boolean isClockWise) {
    this.context = context;
    this.isClockWise = isClockWise;
}

```

####Methods must be implemented.
It will set the interval of each items.
Once it was set you can use the variable interval directly

```Java
protected abstract float setInterval();
```

You can set up your own properties or change the default properties like startLeft and startTop here

```Java
protected abstract void setUp();
```

You can set item's properties which is determined by target offset here 

```Java
protected abstract void setItemViewProperty(View itemView,float targetOffset);
```

####Methods you can override.
The max offset value of which the view should be removed

```Java
protected float maxRemoveOffset(){
    return getHorizontalSpace() - startLeft;
}
```

The min offset value of which the view should be removed

```Java
protected abstract minRemoveOffset(){
    return -mDecoratedChildWidth-getPaddingLeft() - startLeft;
}
```

You can calculate and set the postion x of each items here

```Java
protected int calItemLeftPosition(float targetOffset){
    return targetOffset;
}
```

You can calculate and set the postion y of each items here

```Java
protected int calItemTopPosition(float targetOffset){
    return 0;
}
```

Return the property which you want to change while scrolling

```Java
protected float propertyChangeWhenScroll(View itemView){
    return itemView.getLeft()-startLeft;
}
```

It return the (scroll dx / offset);

```Java
protected float getDistanceRatio(){
   return 1f;
}
```
####Enable springback
```Java
recyclerView.addOnScrollListener(new CenterScrollListener());
```

## License ##
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