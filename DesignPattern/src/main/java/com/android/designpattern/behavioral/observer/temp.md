
事件订阅者、监听者、Event-Subscriber、Listener、Observer

观察者模式是较为抽象的设计模式，即，没有固定的实现方式。根据业务场景的不同，实现方式也不同。

实际生活中：GitHub的watch，RSS Feeds、YouTube/小破站订阅

源码应用，Android onClickListener()、BroadcastReceiver、RecyclerView.notifyDataSetChanged()、AndroidX DataBinding的Observable系列等待LiveData等

在并发环境中，或者说将观察者和被观察者加入消息队列解耦之后，就比较像生产者消费者模式

事件驱动/消息驱动/观察者与被观察者/生产消费模型/发布订阅

update：事件驱动和消息驱动没什么区别。事件/消息驱动举例，当手指按下屏幕后，显然是一个事件发生了，屏幕的芯片将坐标信息传递给驱动，驱动再传递给framework，
若最终判定是点击事件，则传递给setOnClickListener方法

由于消费者模式不属于23种设计模式中，姑且放在观察者模式中

行为上，观察者属于关联行为，非组合关系，即观察者和被观察者是可拆分的

这两个模式确实有点相似，都为了实现程序的解耦产生的，
观察者一般又称发布/订阅模式，它一般是有一个主题对象，然后有多个订阅者去关注它，当它的状态发生变化时，会自动通知这些订阅者；
而消费者模式类似一个缓存队列的概念，它也称为生产者/消费者模式，生产者只负责生产数据不去做处理（缓解高并发的问题），
而消费者只从消费中间件里拿到所要处理的数据，并进行相应的逻辑处理工作，生产者与消费者是相互不知道对方的存在的，
或者说他们可以是不同平台的，不同语言的，即解耦的！

在实际应该中，我们也会把观察者（发布/订阅）做成多个发布，一个订阅的变态模式，当然，大叔认为他们是可以多对多的。

观察者模式与消费者模式的区别

订阅者肯定是个消费者，但消费者不一定是订阅者，发布者一定是个生产者，但生产者不一定是个发布者。

订阅发布者模式有时也称为观察者模式，订阅发布者（观察这和被观察者）存在着主动 被动的关系，而生产者消费者比较中性吧。 订阅发布模式定义了一种一对多的依赖关系，让多个订阅者对象同时监听某一个主题对象。这个主题对象在自身状态变 化时，会通知所有订阅者对象，使它们能够自动更新自己的状态。而生产者消费者关系可以是1对1，1对多，多对1，多对多关系

在23种设计模式中的观察者模式中，并没有中间介-队列的概念，但生产者消费者模式再多线程环境下好像天生就有队列的概念。在订阅发布者之间引入消息队列后，可以实现订阅者和发布者之间的解耦，任务可以很好的以异步方式进行处理，所以说是否有中间队列不是订阅发布者模式和生产者消费者模式的区别！

观察者模式缺点，当根据注册链分发消息时，只要有一个耗时久就会卡住整个分发链

在JDK的java.util包中提供了Observable类以及Observer接口，它们构成了Java语言对观察者模式的支持。
但需要注意，Observable内部集合使用的是Vector实现，并且其主要方法都加入synchronized关键字，在单线程环境中的性能可能会有影响

