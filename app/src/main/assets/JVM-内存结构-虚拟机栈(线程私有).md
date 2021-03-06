### Overview
1. 虚拟机栈介绍
2. 栈的存储单位
3. 栈的运行原理
4. 栈帧的内部结构
    4.1. 局部变量表
    4.2. 操作数栈
        4.2.1. 概述
        4.2.2. 栈顶缓存
    4.3. 动态链接
        4.3.1. jvm如何执行方法调用
        4.3.2. 虚方法和非虚方法
        4.3.3. 虚方法表
    4.4. 方法返回地址
5. 常见问题

### 一、什么是虚拟机栈

每个线程在创建的时候都会创建一个虚拟机栈，其内部保存一个个的栈帧 (Stack Frame），对应着一次次 Java 方法调用

### 二、栈的存储单位

栈中存储什么？栈帧是一个内存区块，是一个数据集，维系着方法执行过程中的各种数据信息

### 三、栈的运行原理

- JVM 直接对 Java 栈的操作只有两个，对栈帧的压栈和出栈
- Java 方法有两种返回函数的方式，一种是正常的函数返回，使用 return 指令，另一种是抛出异常，不管用哪种方式，都会导致栈帧被弹出
- IDEA 在 debug 时候，可以在 debug 窗口看到 Frames 中各种方法的压栈和出栈情况

### 四、栈帧的内部结构

每个栈帧中都有：

1. 局部变量表：保存着入参进来的值和方法里面定义的值，如果是对象，那么持有的是引用类型
通常情况下，局部变量表的大小在编辑器就可以确定

2. 操作数栈：保存中间结果(i=1,j=2,k=i+1保存i和j的值)

- 当一个方法刚开始执行的时候，一个新的栈帧也会随之被创建出来，此时这个方法的操作数栈是空的
- 我们说Java 虚拟机的解释引擎是基于栈的执行引擎，其中的栈指的就是操作数栈

2.1 栈顶缓存
由于操作数是存储在内存中的，因此频繁的执行读/写操作必然会影响执行速度。为了解决这个问题，HotSpot JVM设计者提出栈顶缓存技术，将栈顶元素全部缓存在物理CPU的寄存器中

3. 动态链接
invokevirtual

4. 方法返回地址

### 五、常见问题

- 为什么会抛出stackOverFlowError错误
答：-Xss来设置线程的最大栈空间，超过该值会抛错

虚拟机栈是什么？为什么要有虚拟机栈？虚拟机栈的数据结构？
栈帧是什么？由什么组成的？栈帧的数据结构？