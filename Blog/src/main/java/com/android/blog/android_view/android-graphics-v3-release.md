Android图形系统（三）系统篇：闲聊View显示流程

对于应用开发工程师来说，虽然我们不需要写操作系统代码，但是了解View最终是如何显示到屏幕上还是非常有必要的

本篇是Android图形系列的第三篇文章，在之前的两篇文章中我们分别学习了屏幕的“显示原理”和屏幕的“刷新原理”，今天我们来一起学习Android系统的图形架构设计，聊一聊输送到屏幕的画面数据是如何诞生的

本文的目标是希望读者朋友能从宏观上构建一个Android图形子系统的清晰框架与认知，因此，文中不会包含太多的方法调用链以及代码逻辑，非Android开发工程师也可以放心食用

以下，enjoy：

我是概览图

### 一、开篇

Android图形子系统由Linux操作系统层、HAL硬件驱动层、Android Framework框架层几个部分组成，整个系统非常庞大，各个模块之间错综复杂，让人无从下手

中间掺杂着handler、binder等通信机制更让人头疼

不过，再复杂的软件设计，也离不开硬件的支持

今天，让我们化繁为简，从最基础的硬件组成开始，自下而上，看看Android图形子系统是如何一步步建立起来的

#### 硬件组成和HAL

我是图片

这是一张小米11的拆解图

从左到右分别是屏幕、后盖、主板（上）、电池（下）、石墨贴和边框，我们重点来看主板设计

我是主板图片

小米11使用了高通骁龙888处理器，

因为是开发板，所以板子上没有屏幕和摄像头，预留了MIPI/HDMI/DP等图像输出接口

抛开USB、type-c、tf卡槽、MIPI、HDMI、DP等I/O接口，开发板主要是由组成是，

这里需要重点关注的是GPU模块

了解主板上，我们的指令是哪个芯片在执行

我们常常听到的OpenGL ES就是由GPU驱动来实现，包括Android 7.0宣布支持Vulkan协议，同样也是由GPU驱动实现

我们可以去RK3889的用户手册

图片来源：datasheet

瑞芯微这块开发板使用的是Arm的亲儿子mali，同时支持OpenGL 12Vulkan

也可以说，OpenGL ES一定程度上指导了电路板的设计

#### Hardware Abstraction Layer

##### 1、HWC

瑞芯微开发板关于HWC部分实现点[[这里]](https://gitlab.com/TeeFirefly/firenow-oreo-rk3399/-/tree/master/hardware/rockchip/hwcomposer)

通常情况下，HWC是由display controler实现，回到骁龙888的GPU部分

#### Framework层库支持

消费级的联发科/骁龙的用户手册都要花钱买，我们就找个工业级的，演示一下

我们知道Android的HAL，在图形系统中，另一个技术名词也会经常被体积：OpenGL ES

#### 什么是HAL？

实现HAL和OpenGL ES/Vulkan协议，是嵌入式驱动工程师需要完成的工作

CPU内存可以组建出一台计算机，

Android图形子系统，

从HAL入手，看看是什么支撑起Android图形系统

尽量的

本篇文章我们将尝试把图形系统化繁为简，

当我们打开Android开发者



HAL和OpenGL ES一样，指导驱动层的设计，驱动又建立在硬件之上，在一定程度上也可以说是指导硬件的电路设计

电路怎么设计我不管，驱动怎么写我也不管

Andorid设备基于硬件，驱动层

在文章的开头我们先来聊一聊一块普通的开发板/手机主板上都有哪些

2D图形是3D图形的子集



本篇文章大部分源码都来自7.0版本，选择7.0的原因很简单，因为它包含了4.1黄油计划和5.0的RenderThread又没有高版本的复杂逻辑，非常适合我们简单了解图形系统的设计

#### 硬件介绍

##### 1、GPU

##### 2、DPU

#### Android层级设计

##### 1、libui.so

###### 1.1 Fence机制

###### 1.2 Gralloc机制

###### 1.3 GraphicBuffer

GraphicBuffer是整个图形系统的核心，所以的渲染操作都将在此对象上进行，包括同步给GPU以及HWC

在流转的过程中，GraphicBuffer不但要跨进程传递，还涉及到跨硬件，因此，GraphicBuffer中也会保存fence的状态

拥有了libui库以后，我们就可以通过操作系统来调用屏幕驱动，从而直接去显示画面

现在大多数开发板都实现了FB框架，所以我们可以打开fb0设备进行写入，感兴趣的可以点击[这里](https://www.youtube.com/watch?v=BUPPyR6VasI)查看视频

Google在2018年发布的Pixel 3首次使用了DRM/KMS框架，

我自己的pixel用的是DRM框架，我尝试了一下没有成功，懒得折腾了

关于DRM可以查看何小龙的视频

##### 2、libgui.so

###### 2.1 BufferQueue

从名字就可以看出来，GraphicBuffer是一个GraphicBuffer队列

在7.0中用BufferItem进行二次封装，其中的mslot表示buffer的状态，bufferitem在不同版本命名可能不一样，注意区分

在BufferQueue包装中，赋予了GraphicBuffer的几种状态，它们分别是

###### 2.2 Surface

Surface中持有BufferQueue的引用，并且封装了出列、入列等一系列的操作

###### 2.3 DisplayEventReceiver



认识硬件设计和层级设计非常重要，建议读者在阅读本文时同时打开以下网页对比着看，在阅读过程中不知道回过头看看在系统设计的哪一层

- libui.so：Fence、Gralloc、GraphicBuffer
- libgui.so：BufferQueue和Surface
- surfaceflinger进程：Layer和DispSync
- SystemServer进程：ams、wms等常用服务

Google提供了[libui.so](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/libs/ui/)和[libgui.so](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/libs/gui/)库，厂商提供了[hwcomposer.so](http://www.aospxref.com/android-7.1.2_r39/xref/external/drm_hwcomposer/)和[gralloc.so](http://www.aospxref.com/android-7.1.2_r39/xref/external/drm_gralloc/)以及GPU的[libEGL.so](https://source.android.com/devices/graphics/implement-opengl-es?hl=zh-cn)库，这几个库为Android的图形系统打下了坚实的基础，几乎所有的图形显示都得依靠他们哥几个才能完成

有了这几个库支持，Android系统才能

### 二、Vsync发生前：系统启动

介绍完Android设备的硬件组成和图形库设计，接下来我们开始分析系统的启动流程，看看系统在开机的过程中都做了哪些工作

#### 启动surface_flinger进程

在系统启动的一系列进程中，和图形相关的进程主要有两个：[surface_flinger进程](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/)（以下简称sf进程）和[system_server进程](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/services/java/com/android/server/SystemServer.java)

sf进程负责接受来自APP进程的图形数据，并调用hwc进行合成与最终的送显，从模块关系来看，sf进程在系统中起到一个承上启下的作用

system_server进程负责管理有哪些APP进程可以进行绘图操作以及各个图层的优先级，依靠[AMS（ActivityManagerService）](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java)和[WMS（WindowManagerService）](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java)两个服务来实现

#### 

我们先来看sf进程，Android 7.0以后对init.rc脚本进行了重构，sf进程的启动从[init.rc](http://androidxref.com/6.0.1_r10/xref/system/core/rootdir/init.rc)文件配置到了[surfaceflinger.rc](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/surfaceflinger.rc)文件，依旧由init进程拉起

sf的入口函数：

```c++
/frameworks/native/services/surfaceflinger/main_surfaceflinger.cpp
  
int main(int, char**) {

    // instantiate surfaceflinger
    sp<SurfaceFlinger> flinger = new SurfaceFlinger();

    // initialize before clients can connect
    flinger->init();

    // publish surface flinger
    sp<IServiceManager> sm(defaultServiceManager());
    sm->addService(String16(SurfaceFlinger::getServiceName()), flinger, false);

    // publish GpuService
    sp<GpuService> gpuservice = new GpuService();
    sm->addService(String16(GpuService::SERVICE_NAME), gpuservice, false);

    // run surface flinger in this thread
    flinger->run();

    return 0;
}
```

main函数的主要作用是创建SurfaceFlinger对象并初始化，要完成的工作都在SurfaceFlinger对象中的初始化函数中：

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
  
//利用RefBase首次引用机制来做一些初始化工作，这里是初始化Handler机制
void SurfaceFlinger::onFirstRef()
{
    mEventQueue.init(this);
}

//初始化
void SurfaceFlinger::init() {
    {
        // initialize EGL for the default display
      	//初始化OpenGL 图形库相关配置
        mEGLDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        eglInitialize(mEGLDisplay, NULL, NULL);

        // start the EventThread
        //启动事件分发线程，提供给APP进程注册事件回调
        sp<VSyncSource> vsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                vsyncPhaseOffsetNs, true, "app");
        mEventThread = new EventThread(vsyncSrc, *this);
        //又启动一个事件分发线程，并将自己注册到hwc中，用于sf进程监听vsync信号
        sp<VSyncSource> sfVsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                sfVsyncPhaseOffsetNs, true, "sf");
        mSFEventThread = new EventThread(sfVsyncSrc, *this);
        mEventQueue.setEventThread(mSFEventThread);

        // Get a RenderEngine for the given display / config (can't fail)
        mRenderEngine = RenderEngine::create(mEGLDisplay,
                HAL_PIXEL_FORMAT_RGBA_8888);
    }

    // Drop the state lock while we initialize the hardware composer. We drop
    // the lock because on creation, it will call back into SurfaceFlinger to
    // initialize the primary display.
    //初始化HWC对象，加载hwcomposer.so的动作在HWComposer的初始化函数中
    mHwc = new HWComposer(this);
    //将自己注册到hwc的回调函数中，其内部分别调用registerHotplugCallback、registerRefreshCallback、registerVsyncCallback三个回调方法
    mHwc->setEventHandler(static_cast<HWComposer::EventHandler*>(this));

    // retrieve the EGL context that was selected/created
    mEGLContext = mRenderEngine->getEGLContext();

    // make the GLContext current so that we can create textures when creating
    // Layers (which may happens before we render something)
    getDefaultDisplayDevice()->makeCurrent(mEGLDisplay, mEGLContext);

    mEventControlThread = new EventControlThread(this);
    mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);

    // set initial conditions (e.g. unblank default device)
    initializeDisplays();

}

void SurfaceFlinger::run() {
    do {
        waitForEvent();
    } while (true);
}

//等待消息唤醒
void SurfaceFlinger::waitForEvent() {
    do {
        IPCThreadState::self()->flushCommands();
        int32_t ret = mLooper->pollOnce(-1);
        } while (true);
}
```

初始化函数稍微有点长，我们一步步拆开来看

##### 1、初始化Handler机制

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
//利用RefBase首次引用机制来做一些初始化工作，这里是初始化Handler机制
void SurfaceFlinger::onFirstRef()
{
    mEventQueue.init(this);
}

/frameworks/native/services/surfaceflinger/MessageQueue.cpp
void MessageQueue::init(const sp<SurfaceFlinger>& flinger)
{
    mFlinger = flinger;
    mLooper = new Looper(true);
    mHandler = new Handler(*this);
}
```

在SurfaceFlinger中，利用了RefBase首次引用机制来做一些初始化工作，这里是初始化Handler机制

Android是消息驱动的操作系统，APP的UI线程的设计如此，native的系统进程也是如此

在sf进程中，Handler主要处理两件事：提供给APP进程发送“我要刷新”的消息以及提供给vsync线程发送“开始合成工作”的消息

##### 2、初始化EGL环境

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
void SurfaceFlinger::init() {
    {
        // initialize EGL for the default display
        mEGLDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        eglInitialize(mEGLDisplay, NULL, NULL);
      	...
        // Get a RenderEngine for the given display / config (can't fail)
        mRenderEngine = RenderEngine::create(mEGLDisplay,
                HAL_PIXEL_FORMAT_RGBA_8888);
    }

    // retrieve the EGL context that was selected/created
    mEGLContext = mRenderEngine->getEGLContext();

}
```

初始化工作的第二步是配置EGL环境，eglGetDisplay()方法最终会调用到位于/frameworks/native/opengl/libs/EGL/Loader.cpp的load_driver()方法来加载libEGL.so

[EGL](https://www.khronos.org/registry/EGL/)是OpenGL ES在每一部Android设备中的具体实现，这一步执行完成以后用户空间就可以调用egl进行绘图，接着调用eglSwapBuffers()方法将其送到屏幕显示

##### 3、启动事件分发线程

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
void SurfaceFlinger::init() {
    {
      	...
        // start the EventThread
        //启动事件分发线程，提供给APP进程注册事件回调
        sp<VSyncSource> vsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                vsyncPhaseOffsetNs, true, "app");
        mEventThread = new EventThread(vsyncSrc, *this);
        //又启动一个事件分发线程，并将自己注册到hwc中，用于sf进程监听vsync信号
        sp<VSyncSource> sfVsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                sfVsyncPhaseOffsetNs, true, "sf");
        mSFEventThread = new EventThread(sfVsyncSrc, *this);
        mEventQueue.setEventThread(mSFEventThread);

    }

    mEventControlThread = new EventControlThread(this);
    mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);

}
```

在Android图形系统中，Vsync信号不管是硬件产生还是软件模拟，都是交由DispSync来管理

##### 4、初始化HWComposer

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
void SurfaceFlinger::init() {
  	...
    // Drop the state lock while we initialize the hardware composer. We drop
    // the lock because on creation, it will call back into SurfaceFlinger to
    // initialize the primary display.
    //初始化HWC对象，加载hwcomposer.so的动作在HWComposer的初始化函数中
    mHwc = new HWComposer(this);
    //将自己注册到hwc的回调函数中，其内部分别调用registerHotplugCallback、registerRefreshCallback、registerVsyncCallback三个回调方法
    mHwc->setEventHandler(static_cast<HWComposer::EventHandler*>(this));
}
```

mHwc可以说是sf进程中的核心人物了，不管是接受硬件的vsync信号，还是完成图层合成工作以及最终的送显

##### 5、进入睡眠 等待唤醒

```c++
/frameworks/native/services/surfaceflinger/main_surfaceflinger.cpp
int main(int, char**) {
  	...
    // run surface flinger in this thread
    flinger->run();
    return 0;
}  

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp  
void SurfaceFlinger::run() {
    do {
        waitForEvent();
    } while (true);
}

//等待消息唤醒
void SurfaceFlinger::waitForEvent() {
    do {
        IPCThreadState::self()->flushCommands();
        int32_t ret = mLooper->pollOnce(-1);
        } while (true);
}
```

在完成所有初始化工作后，sf进程进入睡眠状态，等待唤醒

整个调用流程可以简化为

我是图片

> 之前写的，总的来说，sf进程在图形处理相关方面一共做了三件事
>
> 1. 注册vsync信号回调，如果硬件不支持，启用VSyncThread线程模拟
> 2. 启动vsync信号线程（如果硬件支持的话）
> 3. 初始化HWComposer对象，并且注册HWC回调
> 4. 提供链接方法，等待APP端跨进程调用
> 5. 睡觉，等待消息
>
> 关于第4点要着重强调一遍，APP进程申请Surface成功后，经过一系列的方法调用，最终会在sf进程中创建对应的Layer，这个Layer会保存在mLayers中
>
> 注意，每个版本的surfaceflinger代码都在变，对不上的话可以检查源码版本

#### 启动system_server进程

[system_server](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/services/java/com/android/server/SystemServer.java)进程中运行着AMS、WMS等常见服务，这些服务都是由java代码实现的，需要一个jvm的运行环境

因此，system_server进程必须要等到zygote进程创建DVM/ART虚拟机以后，再由zygote进程fork而来：

```java
/frameworks/base/services/java/com/android/server/SystemServer.java
  
/**
 * The main entry point from zygote.
 */
public static void main(String[] args) {
    new SystemServer().run();
}

private void run() {
  	...
    Looper.prepareMainLooper();
  	startBootstrapServices();
		startOtherServices();
  	// Loop forever.
    Looper.loop();
}  

//启动AMS
private void startBootstrapServices() {
  	// Activity manager runs the show.
 	 mActivityManagerService = mSystemServiceManager.startService().getService();
}  

//启动WMS
private void startOtherServices() {
  	WindowManagerService.main();//ps：同样是系统服务，待遇差距为什么这么大？我wms差哪了？不服.jpg
}  

```

zygote进程是如何启动并最终拉起system_server进程这里不展开，我们重点关注SystemServer的run函数

##### 1、初始化ActivityManagerService

AMS是Android系统最为核心的服务之一，其职责包括四大核心组件与进程的管理



从服务的名称也可以看出来，主要是管理Activity

所有的页面都是以Activity作为页面的承载，四大组件是Android系统为应用开发者提供

提供给上层业务开发，也就是为应用开发提供接口

在图形系统中AMS有什么作用呢？管理页面是否可见，举个例子：

当Activity完全不可见时，页面有个无限循环动画在执行，操作系统会绘制它吗？

这也是为什么当我们启动一个透明态的Activity时，原本的Activity只会执行onPause()而不执行onStop()回调方法的原因

##### 2、初始化WindowManagerService

理论上，可以不通过AMS实现页面展示

在本小节中聊聊WMS在图形系统当中的作用

activity、dialog、toast等等

我们可以跳过AMS直接向WMS添加一个View，这个View能够

思考一个问题，在activity中显示一个dialog弹窗，虽然启动dialog需要activity的上下文，这个弹窗显然并不归activity管理

用户点击返回键时，接收事件的肯定是最上层的弹窗，接着dismiss()

##### 3、进入睡眠 等待唤醒

详细的启动流程这里不展开，我们着重关注system_server进程中的AMS和WMS这两个服务



> - AMS负责组件（主要是Activity）的启动、切换、调度工作，简单来说就是管理组件是否有绘制权限，如果应用被切换到后台，就没必要绘制图形了
> - PMS负责为APP分配图层，并确定每个图层的深度，除此以外，PMS还负责分发触摸信号、垂直同步信号等工作



#### 启动app进程

APP的启动过程这里同样不展开，对于Activity启动流程不熟悉的同学可以去网上搜索文章看完再回来（不必拘泥于细节，了解大致流程即可）

在APP创建完成以后，会启动AndroidManifest中配置的默认Activity，拉起Activity过程中，一共完成了三件事：

1. 调用setContentView加载视图，创建ViewRootImpl
2. 调用ViewRootImpl.requestLayout()方法请求Vsync信号
3. 主线程Looper.loop()进入休眠，等待Vsync到来

##### 1、setContentView加载视图

Android开发中设置视图不外乎于两种方式：xml文件和Java编码

不管是使用xml还是View对象，都需要调用setContentView()方法将视图绑定到Activity当中：

```java
\frameworks\base\core\java\android\app\Activity.java

public void setContentView(View view) {
    getWindow().setContentView(view);
}

\frameworks\base\core\java\com\android\internal\policy\PhoneWindow.java
@Override
public void setContentView(View view) {
  ...
}

\frameworks\base\core\java\android\view\WindowManagerGlobal.java

public void addView() {
  ...
  root = new ViewRootImpl()//创建了关键的viewrootimpl
  root.setView();
}

frameworks\base\core\java\android\view\ViewRootImpl.java

public void setView(View view) {
    mView = view;//将DecorView保存到ViewRootImpl的成员变量mView中
    requestLayout();//请求vsync信号
  	res = mWindowSession.addToDisplay();//背后又是老长一串调用链，就不展开跟了，大致流程是在wms服务中创建了WindowState对象
}
```

如代码所示，经过一系列的方法调用后，最终会执行ViewRootImpl.setView()方法，将DecorView保存到ViewRootImpl的成员变量mView中；同时，setView()方法会将视图同步给WMS，在WMS中对应创建了一个WindowState对象

除此以外，ViewRootImpl还包含Choreographer对象，它是ViewRootImpl几个关键成员变量之一：

```java
\frameworks\base\core\java\android\view\ViewRootImpl.java
  
public ViewRootImpl(){
  //获取Choreographer单例对象
  mChoreographer = Choreographer.getInstance();
}

/frameworks/base/core/java/android/view/Choreographer.java
  
private Choreographer(Looper looper) {
  mHandler = new FrameHandler(looper);
  //初始化之后通过jni调用到DisplayEventDispatcher初始化，接着创建libgui.so中DisplayEventReciver对象
  //再往下的代码就不跟了，感兴趣的同学可以自己去看，大致流程是创建了一个与sf进程的连接并注册到EventThread线程中，从而获得vsync感知能力
  mDisplayEventReceiver =  new FrameDisplayEventReceiver(looper);
}  
```

ViewRootImpl在构造函数中获取了Choreographer实例对象，紧接着在Choreographer的构造函数中又创建了FrameDisplayEventReceiver对象

FrameDisplayEventReceiver对应的实现是开篇中提到的libgui.so中DisplayEventReciver对象，到这里我们就不向下继续追踪，以免引入过多的角色导致文章的阅读体验下降

总之，FrameDisplayEventReceiver让Choreographer对象拥有了感知Vsync信号的能力

ViewRootImpl另一个需要关注的成员变量是mSurface，它是View能显示的基础

```java
\frameworks\base\core\java\android\view\ViewRootImpl.java
  
public final Surface mSurface = new Surface();
```

总结一下在setContentView阶段发生的事情：

- 创建ViewRootImpl对象并将DecorView绑定到mView成员变量中
- 创建Choreographer对象并注册一系列回调方法
- 创建了Surface对象交给ViewRootImpl成员变量mSurface

##### 2、请求vsync信号

在ViewRootImpl首次加载视图的时候，需要注意一个细节

在ViewRootImpl.setView()方法中，

```java
View.invalidate()/requestLayout()
	->
```

invalidate 和 requestLayout最终都会调用到viewrootimpl.scheduleTraversals()方法，在此方法中会调用requestNextVsync()

ViewRootImpl#performTraversals

scheduleTraversals() 





##### 4、进入睡眠 等待唤醒

> 之前写的，从zygote进程中fork出APP进程后，WindowManagerGlobal
>
> Activity的创建过程的调用链有点长，这里先忽略掉，在AMS和WMS的通力合作下创建出Activity实例对象
>
> 这个实例在AMS保存为ActivityRecord对象，在WMS中保存为WindowState对象
>
> ViewRootImpl中持有两个非常重要的对象：Choreographer和Surface
>
> Choreographer中也有一个非常重要的对象：DisplayEventReceiver
>
> DisplayEventReceiver完成对gui.so中的DisplayEventReceiver封装，
>
> 还记得DisplayEventReceiver吗？
>
> 这就是为什么Choreographer也能接收到vsync信号的关键
>
> ```c++
> frameworks/native/libs/gui/DisplayEventReceiver.cpp
> DisplayEventReceiver::DisplayEventReceiver() {
>     sp<ISurfaceComposer> sf(ComposerService::getComposerService());
>     if (sf != NULL) {
>         mEventConnection = sf->createDisplayEventConnection();
>         if (mEventConnection != NULL) {
>             mDataChannel = mEventConnection->getDataChannel();
>         }
>     }
> }
> ```
>
> ViewRootImpl本身由WMS管理，一个Activity对应一个ViewRootImpl
>
> 从这里我们也可以两位
>
> AMS负责管理组件状态，WMS负责管理视图状态
>
> 我们这里一笔带过，简单来说是通过AMS创建了

### 三、Vsync发生后：系统的指挥官

好了，万事俱备，只欠东风，APP进程和SF进程都一同等待着Vsync信号的到来

Drawing with VSync

#### APP进程

创建Surface，创建BufferQueue，SF对应创建Layer，每一个Surface创建成功后，经过一系列的方法调用，最终会被同步到sf进程，并创建Layer，就将会被把书翻到第一章第二节的，surface

前面我们提到了eventthread，

- ViewRootImpl.requestLayout()
- ViewRootImpl.scheduleTraversals()
- ViewRootImpl.doTraversal()
- ViewRootImpl.performTraversals()

> perfromDraw()
>
> ​	->draw()
>
> ​		->drawSoftware()	



##### 1、发送同步消息屏障

##### 2、draw

Android 5.0以后的View体系中加入了RenderThread，也就是渲染线程

支持硬件加速的情况下，渲染过程和UI线程分离了，UI线程负责将onDraw中的绘制命令（被称为RenderNode）收集到DisplayList，接着调用syncAndDrawFrame()方法将命令同步给RenderThread，随后执行渲染任务

引入渲染线程的好处有两个：

一是可以防止重复绘制，比如

二是留给UI线程更多的时间来处理messagequeue中的消息，

##### 3、取消同步消息屏障

#### SF进程



至此，整个vsync周期发生的都已经完成，我们来梳理显示流程

下面几个问题是我个人在学习图形系统中比较疑惑的点，在此分享希望能够帮助到其他人

如何暂停接收Vsync信号？

我们打开APP后没有进行任何操作，APP还会执行渲染流程吗？

答案显然是否定的

sf和app都需要调用requestNextVsync方法请求下一次同步信号

vsync信号由DispSyncSource和EventThread来分发

比如录屏软件就会调用获取当前的buffer

sf的两个回调：

**MessageQueue::invalidate**

当layer有变化时，messagequeue会收到invalidate的消息

在invalidate回调中，sf回去请求请求一次vsync callback回调

没有layer请求，就永远不会有vsync回调

而invalidate回调，可能是app进程画面有更新，要去合成

也可能画面没更新，虚拟屏幕或者录屏软件在发消息

**MessageQueue::vsyncCallback**

走合成流程



### 四、结语

AMS/WMS在图形系统中的作用

WMS管理的窗口类型可以分为三种，应用窗口、子窗口（需要父窗口）和系统窗口

我们的应用属于应用窗口，Dialog则是子窗口，Toast属于系统窗口

创建更新删除工作，还管理者每一个Window的深度Z-Order

Activity/Dialog/Toast/Window之间的区别？

Surface/Layer/GraphicBuffer/ButterQueue之间的联系？

**SurfaceView和普通View的区别**



总结一下View的显示流程，分三步走：

- 第一步，系统启动阶段
  1. 启动sf进程，初始化hwc、egl环境等，启动vsync线程并注册回调
  2. 启动zygote进程，初始化jvm环境加载常用jni，最后拉起system进程启动常用的ams、wms服务等
  3. 启动launch，加载
- 第二部，请求vsync信号阶段
  1. 通过chro讲触摸事件同步给APP进程，如果

创建APP进程并加载xml文件

1. wms为activity创建viewrootimpl
2. viewrootimpl持有surface，

时序图就不给大家画了，画了也记不住，每个版本还都不一样

友情提醒，在分析调用链的过程中，时刻谨记当前的方法运行在哪个进程，发送的指令是哪个芯片执行的，这对我们理解图形系统有着非常大的帮助

再次强调一遍，文章中源码版本是7.0

隐藏了非常多的细节

了解了GraphicBuffer的流转过程，也就明白了Android系统的显示流程

总结一下，SurfaceFlinger是Android图形系统的核心进程，在整个图形系统中起承上启下的作用

在Android系统中，sf是关键，通过sf可以申请到一个layer

server进程中管理组件的状态，没有

总结一下vsync到来之前和到来之后发生的事情

Android启动时会创建两大进程，其中常见的ams和wms运行在server进程

当APP启动以后，不管是因为input事件还是fling或者主动发起更新请求，最终都会调用到requestNextVsync()方法中

以大部分应用开发来举例：

从主要角色延伸出来

为了过于臃肿，文章删减了许多辅助角色

本文更多的是以进程的视角，以分层的架构来解释Android图形子系统的全貌，为此删减了许多辅助角色

这些辅助角色都是由

为了避免引入过多的角色导致阅读体验不佳，尽可能用最简单的来解释，我在各个层级中删减掉许多辅助类/文件，如果对于大致不是很熟悉的情况下去跟源码中可能会感到困惑

但这样的做法会给想要阅读源码的小伙伴感到困惑，造成不小的麻烦

本文从进程的角度分析了Android的显示流程，文章中省略了许多重要的知识点，方法的调用链，比如：

> - 合并到SurfaceFlinger.cpp
> - 合并到gui/Surface.cpp
>
>   > GraphicBuffer这块内存需要在几个不同的硬件中流转，被不同的硬件识别并使用需要实现不同的协议
>   >
>   > ANativeWindow简单来说是公共协议OpenGL ES的具体实现类
>   >
>   > Surface继承自ANativeWindow，这样Surface中的GraphicBuffer这块内存就能被GPU所识别并使用
> - SurfaceControl用于管理Surface的创建与sf进程的链接
> - 合并EventThread到sf中
> - 合并SurfaceControl.cpp到Surface.cpp中
> - 合并DisplayEventReceiver.cpp到Surface.cpp
> - HWC、EGL等系统关键模块初始化的时机以及创建过程
> - GraphicBuffer的流转中包含了Fence的状态
> - BufferQueue的核心在BufferQueueCore类中，也无法直接操作BufferQueue，GraphicBuffer被封装为Solt
> - Android S更改了显示流程
>   - 删除ION驱动改为dma_buf直接驱动
>   - 删除DisySync类，笔者暂时没找到新的替换类
>   - GraphicBuffer队列改为App控制

在这其中的任何一个环节都可以写篇文章来介绍，作者能力有限，无法将以上细节全部解释清楚

Android图形子系列横跨硬件驱动、Linux内核、Framework框架三层，虽然每个模块设计的比较合理，命名比较混乱，但命名真的是过于混乱，以至于稍不留意就迷失在源码当中

这就导致想要理清它们之间的关系变成一件比较困难的事情，好在已经有各位前辈铺好了路

希望本文能够抛砖引玉，为屏幕前的读者朋友提供一点点帮助

全文完

### 五、参考资料

- [《深入理解Android内核设计思想》- 林学森](https://book.douban.com/subject/25921329/)
- [《深入理解Android 卷III》- 张大伟](https://book.douban.com/subject/26598458/)
- [《Weishu's Notes》- 田维术（太极/两仪作者）](https://weishu.me/)
- [《Android显示系列》- 努比亚团队](https://www.jianshu.com/c/3a4d92743e88)
- [《Systrace系列》- 高爷](https://www.androidperformance.com/2019/05/28/Android-Systrace-About) 
- [《Android 12 BlastBufferQueue系列》- 大天使之剑](https://www.jianshu.com/u/124e5f361305)
- [《DRM与BufferQueue系列》- 何小龙](https://blog.csdn.net/hexiaolong2009?type=blog)
- [《Android图形显示系列》- 夕阳叹](https://blog.csdn.net/jxt1234and2010/category_2826805.html)
- [《王小二的Android站》- 王小二（TCL王总）](https://www.jianshu.com/u/fd0b722ce11f)
- [《Silence.Slow.Simple专栏》- simowce](https://blog.simowce.com/archives/)
- [《Android SurfaceFlinger 学习之路》- windrunnerlihuan](https://windrunnerlihuan.com/archives/page/2/)
- [《Android 12(S) 图像显示系统》- 二的次方](https://www.cnblogs.com/roger-yu/p/15641545.html)
- [深入理解Flutter的图形图像绘制原理 - OPPO数智技术](https://segmentfault.com/a/1190000038827450)
- [Android性能优化：定性和定位Android图形性能问题 - 飞起来_飞过来](https://juejin.cn/post/7096288511053004830)
