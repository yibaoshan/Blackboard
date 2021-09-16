package com.android.notebook.computer

class Main {

    /**
     * 计算机组成：
     *
     * 1. CPU(Central Processing Unit)：与或非逻辑运算
     * @see CPU
     *
     * 2. 内存
     * 供CPU读取/存储数据
     *
     * 3. 主板
     * 芯片组(Chipset)：决定数据从哪到哪
     * 总线(Bus)：传输数据
     *
     * 4. 磁盘
     * 输入输出I/O设备
     *
     * 5. 网卡
     *
     * 6. 声卡
     *
     * 7. 显卡
     * 在主板上集成成为板载显卡，独立插槽的为独立显卡，集成显卡是集成在CPU内部
     *
     * 注意点：
     * 1. 外部I/O设备一般在主板上的南桥芯片组(SouthBridge)和CPU来通信
     *
     * */

    /**
     * 冯·诺依曼体系结构(存储程序计算机)
     *
     * 1. 运算器
     * 2. 控制器
     * 3. 存储器
     * 4. 输入设备
     * 5. 输出设备
     *
     * 一般我们认为CPU包含了运算器和控制器的功能，运算器 + 控制器 = CPU
     * */

    /**
     * 图灵机
     *
     * 参考极客时间：冯诺依曼给出了计算机最优化的结构，而图灵给计算机能做的事情在数学上画出了一个边界：
     * 1. 世界上有很多问题，其中只有一小部分是数学问题；
     * 2. 在数学问题中，只有一小部分是有解的；
     * 3. 在有解的问题中，只有一部分是理想状态的图灵机可以解决的；
     * 4. 在后一类的问题中，又只有一部分是今天实际的计算机可以解决的。
     * 所以，人工智能是不可能斗得过人类的！！！
     * */

    private class CPU {

        /**
         * CPU主频是一个频率（frequency），频率的单位叫做赫兹（Hz）。
         * 意思是一秒内这个事情可以发生多少次。
         * 主频2.8GHz就代表一秒内晶振振动了2.8G次，这里的G其实就是10亿次，也就是28亿次。
         * 那么我们的时钟周期时间就是1/28亿秒。
         *
         * */

        /**
         * CPU超频，也就是改变晶振的频率，相当于把CPU内部的钟给调快了
         * */

        /**
         * 为什么CPU越小越好，9nm、7nm到现在的5nm，因为晶体管之间的距离越小，传输电信号的时间也就更短，因此速度会更快
         * */

        /**
         * 栈是为了解决什么问题？
         * for/while循环和switch等都是由if else goto语句组合而成
         * 那么如何我在函数内部调用其他函数，能不能在被调用方法执行完成之后接着执行之前的逻辑呢？
         * 可以使用返回地址，一级一级向上返回，但是这样CPU寄存器数量就不够，于是就发明了栈
         * 这便是栈的起源，栈帧(Frame)便是一个函数，栈帧占用大小就是函数内变量的内存大小
         * */

    }

}