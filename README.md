##BouncingJellyView

###最新版本: 1.0.0

可以嵌套任何View，滑动到顶部或者底部，会有一个阻尼效果进行缩放整个页面，松开手指回弹。灵感来自于MIUI。

###效果图
####1. 普通的页面
![嵌套普通的View](BouncingJelly/Screenshot/bouncing-jelly-view%E6%99%AE%E9%80%9A%E6%83%85%E5%86%B5.gif)
####2. 嵌套RecyclerView
![嵌套RecyclerView](BouncingJelly/Screenshot/bouncing-jelly-view-recyclerview.gif)
###使用
1. 添加jcenter
	
		allprojects {
    		repositories {
       		 jcenter()
   			 }
		}
	
2. 在gradle中compile

		com.github.aohanyao:bouncing-jelly-view:1.0.0

3. xml
	
		因为BouncingJellyView是继承自ScrollView，所以必须要嵌套一层ViewGroup才能使用。


```xml
			
	<com.aohanyao.jelly.library.BouncingJellyView xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent" >

		<任意布局之一>
			.........你的内容
		</任意布局之一>

	</com.aohanyao.jelly.library.BouncingJellyView>

```


###联系我
[简书](http://www.jianshu.com/u/3e53005808b1)

[CSDN](http://blog.csdn.net/aohanyao)



###Version History
#### 2017年1月11日
1.0.0 

	初步完成，增加到仓库。