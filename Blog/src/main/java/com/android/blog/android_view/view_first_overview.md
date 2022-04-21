Android View系列(一)：图形架构概述

Android图形架构是个复杂的系统，设计到屏幕绘制、渲染引擎、窗口管理、输入input事件等等

View系列将会介绍，View体系是包含在图形系统当中

本篇文章的目的是为了解释清楚，View在Android图形系统中担任什么角色

Android 框架提供了各种用于 2D 和 3D 图形渲染的 API，可与制造商的图形驱动程序实现代码交互
因此，务必更好地了解这些 API 的工作原理。本页介绍了在其上构建这些驱动程序的图形硬件抽象层 (HAL)。

什么是驱动？

Android只是个操作系统，板子是人家的，屏幕是人家的，你在板子上加了个GPU芯片  
Android OS是不知道的，它只有基础协议，假设你怼了一个1080Ti上去，根本没法发挥GPU的性能对不对

了解了View在Android图形系统的所处的位置，对于我们后续开发、调试等有着很大的帮助