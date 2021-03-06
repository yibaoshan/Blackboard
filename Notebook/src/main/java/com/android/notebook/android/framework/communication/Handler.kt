package com.android.notebook.android.framework.communication

class Handler {

    /**
     * Handler-Looper使用流程
     * 1. Looper.prepare()：持有queue，构造函数初始化
     * 2. Looper.loop()：
     * 3. new Handler().sendMessage()/postDelayed()
     *
     * 在Android系统当中，大多数进程间通信都是使用的binder，同样的，大多数线程间通信使用的就是我们今天的主角：Handler
     *
     * 1. Handler：负责发送动作和处理消息
     * # mLooper: Looper
     * # mQueue: MessageQueue
     * # mCallback: Callback
     * + handleMessage()
     * + dispatchMessage(Message msg)
     *      1. msg.callback不为空则说明是runnable，执行run方法
     *      2. handle本地变量mCallback不为空，交给mCallback的handleMessage执行
     *      3. 上述条件不满足则自己处理
     * + post()
     * + postDelayed()
     * @see com.android.aosp.frameworks.android.os.Handler
     *
     * 2. Looper：负责消息循环，处理消息分发
     * # sThreadLocal: static ThreadLocal<Looper>
     * # sMainLooper: private static Looper
     * # sObserver: private static Observer
     * # mQueue: MessageQueue
     * + prepare(quitAllowed)
     * + getMainLooper()
     * + loop()
     * + quit()
     * @see com.android.aosp.frameworks.android.os.Looper
     *
     * 3. Message：消息内容
     * # what: int
     * # arg1: int
     * # arg2: int
     * # when: long
     * # target: Handler
     * # callback: Runnable
     * # next: Message
     * + obtain()
     * @see com.android.aosp.frameworks.android.os.Message
     *
     * 4. MessageQueue：提供消息池缓存，压入和读取消息
     * + isIdle()
     * + next()
     * + quit()
     * + enqueueMessage()
     * @see com.android.aosp.frameworks.android.os.MessageQueue
     *
     * TODO：
     * 1. 如何发送同步消息？
     * 2. handler消息分发流程是怎样的
     * 3. handler提交消息如何保证线程安全的？
     *  答：在调用enqueueMessage()时，方法里面加锁了
     *
     * */

    /**
     * 首页handler注意事项
     *
     * Looper：
     * 1. Looper里常见方法都是静态方法
     * 2. Looper里的threadLocal对象get方法和set方法里面会自动查找当前线程保存的对象
     *
     *
     *
     * */

}