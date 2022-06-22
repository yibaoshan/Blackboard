

标题：为什么关机了还能显示充电画面？

#### 前言

> - 前面讲了画面撕裂的原因，framebuffer可以理解为屏幕每个像素点的值，包含颜色，深度等信息
> - 本章的重点是关心view是如何转变为屏幕像素点数据的？
> - 屏幕显示的元素很多，我们自己写的APP一个页面都有好几层视图，这几层是如何变成一副图像信息的呢？这一帧像素矩阵信息是怎么来的呢？
> - 众所周知，Google为了改进Android流畅度，在Android 4.1版本发布了project buffer黄油计划，希望Android系统能够像黄油一样丝滑
> - 在黄油计划中，Android的绘制被分成了三步，绘制、合成、显示，增加了，对应的vsync被分成了两份，一部分，今天我们来聊一聊
> - 发展到2022年，Android图形系统更加复杂，为了减少GPU的压力，设计了HWC的HAL抽象层进行合成，绘制端也加入了UI Renderer
> - 至此，文章的标题就有答案了，同理工厂模式也是直接
> - 图形系统真他妈复杂，全文完

#### 难点

> - Android图形系统框架一直在改进，由fb改为drm，加入了hwc，所以市面上也没有相关书籍可以从上到下的学习view的绘制流程

#### 疑问区

> - HWC到底是什么？GPU吗？HWC的KMS又是什么？
> - view和canvas之间的关系，view是一张画布，对应的是surface吗？
> - 一个Window上有好多个view吧，Window对应的是啥
> - Java Window和Native surface和Native layer的对应关系？
> - buffer是什么？是像素点数据吗？
> - 三重缓存和vsync和framebuffer对应Android里面的啥
> - 猜想：view不调用失效invalided方法，那么该view就不用重新绘制，调用合成就好了，举例来说，假设你的APP，为了性能考虑，当页面不可见时所有的动画都应该取消，不然一直调用
> - 自定义view中，谁来调用onDraw()方法ss

#### 知识区

> - View使用skia，打开硬件加速使用hwui，也就是GPU
> - GPU厂家，除了骁龙使用自家的Adreno系列之外，华为的麒麟、三星的Exynos、联发科的天玑使用的都是ARM亲儿子：mali
> - PC中Linux系统在2012年就已经全部使用DRM框架了
> - PC端的显卡 = 移动端GPU+ Display Processor + Video Processor
> - Android的显示流程分为三个部分，绘制、合成、显示

Android使用的图形框架

> 原子显示框架ADF：Atomic Display Framework

Andorid图形框架发展史

> Vulkan驱动

OpenGL ES代码在frameworks/native/opengl

Vulkan代码在frameworks/native/vulkan

SurfaceFlinger代码在framework/native/services/surfaceflinger

大致流程

> Android系统提供了三种绘图方式，分别是：Canvas、OpenGL ES和Vulkan
>
> 无论使用哪种，最终内容都会被渲染到surface上，然后交给surfaceflinger管理
>
> **图像生产者**
>
> 图像流生产方可以是生成图形缓冲区以供消耗的任何内容。例如 OpenGL ES、Canvas 2D 和 mediaserver 视频解码器。
>
> **图像消费者**
>
> 图像流的最常见消耗方是 SurfaceFlinger，该系统服务会消耗当前可见的 Surface，并使用窗口管理器中提供的信息将它们合成到屏幕
>
> SurfaceFlinger 是可以修改所显示部分内容的唯一服务。SurfaceFlinger 使用 OpenGL 和 Hardware Composer 来合成一组 Surface
>
> 其他 OpenGL ES 应用也可以消耗图像流，例如相机应用会消耗相机预览图像流。非 GL 应用也可以是使用方，例如 ImageReader 类

合成机制、显示机制

### 概述

#### 操作系统

Android系统是由三个部分组成，看gityuan的吧，这部分不用写了

曾经被问过一个问题，Activity和AndroidX包中的AppCompatActivity的类加载器是同一个吗？

我说是，然后面试官就让我回家等通知..

#### BufferQueue和Gralloc

- Gralloc 

  > Android系统在硬件抽象层中提供了一个Gralloc模块，封装了对帧缓冲区的所有访问操作。用户空间的应用程序在使用帧缓冲区之间，首先要加载Gralloc模块，并且获得一个gralloc设备和一个fb设备。有了gralloc设备之后，用户空间中的应用程序就可以申请分配一块图形缓冲区，并且将这块图形缓冲区映射到应用程序的地址空间来，以便可以向里面写入要绘制的画面的内容。最后，用户空间中的应用程序就通过fb设备来将已经准备好了的图形缓冲区渲染到帧缓冲区中去，即将图形缓冲区的内容绘制到显示屏中去。相应地，当用户空间中的应用程序不再需要使用一块图形缓冲区的时候，就可以通过gralloc设备来释放它，并且将它从地址空间中解除映射。

#### Surface和SufaceHolder

#### SurfaceFlinger和WindowManager

- SurfaceFlinger

  > SurfaceFlinger中需要显示的图层（layer）将通过DisplayDevice对象传递到OpenGLES中进行合成，合成之后的图像再通过HWComposer对象传递到Framebuffer中显示。DisplayDevice对象中的成员变量mVisibleLayersSortedByZ保存了所有需要显示在本显示设备中显示的Layer对象，同时DisplayDevice对象也保存了和显示设备相关的显示方向、显示区域坐标等信息。
  >
  > > ​    DisplayDevice是显示设备的抽象，定义了3中类型的显示设备。引用枚举类位于frameworks/native/services/surfaceflinger/DisplayDevice.h中，定义枚举位于hardware/libhardware/include/hardware/Hwcomposer_defs.h中：
  > >
  > > - DISPLAY_PRIMARY：主显示设备，通常是LCD屏
  > > - DISPLAY_EXTERNAL：扩展显示设备。通过HDMI输出的显示信号
  > > - DISPLAY_VIRTUAL：虚拟显示设备，通过WIFI输出信号

#### 硬件混合渲染器HAL（HWC）

### 渲染引擎

#### Skia库：Canvas 2D绘制

#### OpenGL ES/Vulkan：3D渲染

#### Mediaserver：视频解码器

Android 2D API，代码在/external/skia中，canvas调用的API底层就是由skia库来实现

正文

- 一、开篇

  - 应用开发者的工作中，想更新view很简单，只需要调用invalidate()方法，让当前view失效即可

  - 在Android 4.1黄油计划中，加入了choreographer，它的作用是将vsync信号同步给应用层，目的是把合成和显示分开，减轻GPU的计算量

  - 介绍当前图形框架的发展历史，最终引申到surfaceflinger

  - 对于应用开发者来说，我们只需要关surfaceflinger的工作流程即可

  - 图像流中最常见的消费者是surfaceflinger，同时他也是我们应用开发者需要关注的服务

    sufaceflinger消耗当前可见的surface

- 低级别组件之surfaceflinger

- Choreographer