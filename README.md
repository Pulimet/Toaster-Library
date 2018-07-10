# Toaster-Library
[ ![Download](https://api.bintray.com/packages/pulimet/utils/toaster/images/download.svg) ](https://bintray.com/pulimet/utils/toaster/_latestVersion)      [![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)    [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Toaster-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/6690)


Toaster is android library that helps in showing toast and dialog.

# Installation

- Add the dependency from jCenter to your app's (not project) build.gradle file:

```sh
repositories {
    jcenter()
}

dependencies {
    compile 'net.alexandroid.utils:toaster:2.0.1'
}
```




# Release notes
* 2.0.1 - Added onOutOfTheBoundClick callBack, exposed isVisible() method, bug fix, animation time changes
* 1.4 - Handle animation off issue (Thanks to [@kamilj](https://github.com/kamilj))
* 1.3 - Custom toast layout support
* 1.2 - Basic enhancements 



# How to use it
* Toast:
<img align="right" width ="300" src="https://raw.githubusercontent.com/Pulimet/Toaster-Library/master/art/first.png">

```java
 Toaster.showToast(this, "This is our awesome toast");
 // 300 - animation duration, 500 - visible duration
 Toaster.showToast(this, "This is our awesome toast", 300, 500);  
```

* Custom layout for toast:

```java
 Toaster.showToast(this, "This is our awesome toast", 300, 500, R.layout.custom_t);
```

https://github.com/Pulimet/Toaster-Library/blob/master/app/src/main/res/layout/custom_d.xml


* Dialog:
<img align="right" width ="300" src="https://raw.githubusercontent.com/Pulimet/Toaster-Library/master/art/second.png">

```java
private Toaster mToaster;
...
private void showDialog() {
    mToaster = new Toaster.Builder(this)
            .setTitle("Dialog title")
            .setText("Text of the dialog here")
            .setPositive("OK")
            .setNegative("CANCEL")
            .setAnimationDuration(300)
            .setCallBack(this).build();
    mToaster.show();
}  

@Override
public void onBackPressed() {
   if (mToaster == null || mToaster.onBackPressed()) {
       super.onBackPressed();
    }
}

// CallBack
@Override
public void onPositiveClick() {
    Log.d("TAG", "Positive");
}

@Override
public void onNegativeClick() {
    Log.d("TAG", "Negative");
}

@Override
public void onOutOfTheBoundClick() {
    Log.d("TAG", "onOutOfTheBoundClick");
    mToaster.hide();
}


```

* Custom layout for dialog:

<img align="right" width ="300" src="https://raw.githubusercontent.com/Pulimet/Toaster-Library/master/art/third.png">

```java
 mToaster = new Toaster.Builder(this)
.setCustomLayout(R.layout.custom)
...
```

https://github.com/Pulimet/Toaster-Library/blob/master/app/src/main/res/layout/custom_d.xml

 <br>  <br>  <br> 
# License

```
Copyright 2018 Alexey Korolev

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
