`MethodAdapter.visitFieldInsn()`

```java
/* opcode=181 */
String sig_var=...;
// 调用MethodVisitor不同数据类型的visitInsn方法 参数*LOAD
loadValue();
```

```java
/* opcode=180 */
// 调用MethodVisitor不同数据类型的visitInsn方法 参数*STORE
storeValue();
loadValue();
```

```java
// opCode定义
int GETFIELD = 180;
int PUTFIELD = 181;
```



```flow
st=>start: 开始
e=>end: 结束
op1=>operation: GETSTATIC(获取静态变量到栈顶)
op2=>operation: ASTORE(存储栈顶变量到变量表)
op3=>operation: ALOAD(取出局部变量到栈顶使用)

st->op1
op1->op2
op2->op3

```



打点的时机

```java
public void run() {
	if (this instanceof Runnable) {
		RVRunTime.logThreadBegin();
	}

	Scheduler.beforeFieldAccess(true, "edu/tamu/aser/tests/zzzzzzz/CUT", "money", "I");
	int var6;
	int var10000 = var6 = CUT.money;
	RVRunTime.logFieldAcc(26, (Object)null, 1, var6, false);
	int var7;
	var10000 = var7 = var10000 - 100;
	Scheduler.beforeFieldAccess(false, "edu/tamu/aser/tests/zzzzzzz/CUT", "money", "I");
	CUT.money = var10000;
	RVRunTime.logFieldAcc(26, (Object)null, 1, var7, true);
	if (this instanceof Runnable) {
		RVRunTime.logThreadEnd();
	}
}


//---------------------------------------------------------

RVRunTime.logBeforeStart(7, t1);
t1.start();
RVRunTime.logBeforeStart(8, t2);
t2.start();
RVRunTime.logJoin(9, t1);
RVRunTime.logJoin(10, t2);
Scheduler.beforeFieldAccess(true, "java/lang/System", "out", "Ljava/io/PrintStream;");
PrintStream var10;
PrintStream var10000 = var10 = System.out;
RVRunTime.logFieldAcc(11, (Object)null, 2, var10, false);
Scheduler.beforeFieldAccess(true, "edu/tamu/aser/tests/zzzzzzz/CUT", "money", "I");
int var11;
int var10001 = var11 = money;
RVRunTime.logFieldAcc(12, (Object)null, 1, var11, false);
var10000.println(var10001);

//---------------------------------------------------------

static {
	byte var6 = 0;
	Scheduler.beforeFieldAccess(false, "edu/tamu/aser/tests/zzzzzzz/CUT", "money", "I");
	money = 0;
	RVRunTime.logInitialWrite(16, (Object)null, 1, Integer.valueOf(var6));
}
```

猜测

Scheduler.beforeFieldAccess(.) 打出的点是调度的时机

Scheduler.beforeFieldAccess(**true**，.) 则不用store

Scheduler.beforeFieldAccess(**false**，.) 则store

##### RVRunTime的打点作用：

Schduler不读字节码，通过RVRunTime作为语言来交流，以进行插桩和online调度

通过ASM进行字节码的插桩打点，实现线程可控







##### Scheduler.java的 choose 方法

```java
public static Object choose(SortedSet<? extends Object> objectChoices, ChoiceType choiceType) 
```

输入的是⼀一个 ThreadInfo 的集合，在内部排序

返回值为object，即返回一个放⾏的ThreadInfo

锁无论如何都要释放

```java
if (objectChoices.isEmpty()) {
	throw new IllegalArgumentException("empty");
}
schedulerStateLock.lock();

try {
    
		...
      //调用MCRStrategy.java的choose方法
      
}finally {
	schedulerStateLock.unlock();
}
```

startingScheduleExecution()函数也是，Scheduler.startingScheduleExecution在其内部调用MCRStrategy的startingScheduleExecution()的实现

**即，插桩后字节码主要和Schduler打交道，**

MCRStrategy是实现线程调度的功能，而Schduler负责调用

##### MCRStrategy.java的choose方法

```java
public Object choose(SortedSet<? extends Object> objectChoices, ChoiceType choiceType)
```

对于这个判断条件

```java
// 除此之外 做随机决定
if (MCRStrategy.schedulePrefix.size() > RVRunTime.currentIndex){}
```



##### 对于 schedulePrefix 的操作

* MCRStrategy 对于 schedulePrefix 的操作1

  startingScheduleExecution() 
  
  ```java
  // startingScheduleExecution()
  List<String> prefix = this.toExplore.poll();
MCRStrategy.schedulePrefix.addAll(prefix);
  ```

  `toExplore`的定义是`private Queue<List<String>> toExplore`

  每次从原有的线程中删除第一个元素，即线程信息
  
  **每次删除第一个 即有某种排队方法对 toExplore 排队**



* MCRStrategy 对于 schedulePrefix 的操作2
  completedScheduleExecution()

  对 schedulePrefix 本身无影响

  ```java
  // completedScheduleExecution() 
  Vector<String> prefix = new Vector<String>();
  for (String choice : MCRStrategy.schedulePrefix) {
  	prefix.add(choice);
  }
  ```





RVRunTime里相关注释

```java
// updateStore() 函数
while(MCRStrategy.schedulePrefix.size()>RVRunTime.currentIndex)
  //comparing the field with the SID of the variable in the head of the Queue
  String s = MCRStrategy.schedulePrefix.get(RVRunTime.currentIndex);
```
```java
public static void bufferEmpty(){
    /*
     * only when out of the schedule prefix do the flush
     * since within the prefix, the order is always right
     */
    if (currentIndex >= MCRStrategy.schedulePrefix.size()) {
        ...  与TSO PSO约束相关
       }
}
```



该判断条件出现在的函数

logAfterStart  logJoin  logWait  logNotify  logNotifyAll logStaticSyncLock  logStaticSyncUnlock  logLock  logUnlock logArrayAcc 



其中 logStaticSyncLock  logStaticSyncUnlock  logLock  logUnlock logArrayAcc 

中有对currentIndex的操作（currId在很多地方先初始化为0）

```java
if (MCRStrategy.schedulePrefix.size() <= currentIndex++ || MCRStrategy.fullTrace) 
			// Already reached the end of prefix
```

这里用<=而不用<，与curID在这后要增加有关

 基本与 `Trace trace = MCRStrategy.getTrace()`连用

Trace 有对 AbstractNode 的集合操作，AbstractNode也有类型和EventDesc一样，但少于Desc

打印每次的trace发现一些调度点被自动跳过（schedule1和schedule2）

```java
<< Exploring trace executed along causal schedule 0: 
[0, 0, 0, 0, 0]
<< Exploring trace executed along causal schedule 1: 
[0, 2, 0, 0, 0, 0]
<< Exploring trace executed along causal schedule 2: 
[0, 2, 2, 1, 0]
<< Exploring trace executed along causal schedule 3: 


=============== EXPLORATION STATS ===============
NUMBER OF SCHEDULES: 4
EXPLORATION TIME: 0:00:00  + 299 milli sec
=================================================
Writing the #reads, #constraints, time to file ConstraintsProfile/edu.tamu.aser.tests.zzzzzzz.CUT
[0, 2, 1, 2, 1]
```

**即一些类型的事件不会被pause**





即每次调用函数

logAfterStart  logJoin  logWait  logNotify  logNotifyAll 

curID自增

curID的值与调用次数有关

**即schedulePrefix的条件判断和lock的的时机有关**

打印轨迹

```java
length of schedulerprefix: 0 logBeforeStart() currentIndex++: 1
length of schedulerprefix: 0 logBeforeStart() currentIndex++: 2
length of schedulerprefix: 0 logFieldAcc() currentIndex++: 3
length of schedulerprefix: 0 logFieldAcc() currentIndex++: 4
length of schedulerprefix: 0 logJoin() currentIndex++: 5
length of schedulerprefix: 0 logFieldAcc() currentIndex++: 6
length of schedulerprefix: 0 logFieldAcc() currentIndex++: 7
length of schedulerprefix: 0 logJoin() currentIndex++: 8
length of schedulerprefix: 0 logFieldAcc() currentIndex++: 9
length of schedulerprefix: 0 logFieldAcc() currentIndex++: 10
0
startingScheduleExecution() MCRStrategy.schedulePrefix.addAll(prefix), size:3
length of schedulerprefix: 3 RVRunTime.currentIndex: 1
length of schedulerprefix: 3 RVRunTime.currentIndex: 2
length of schedulerprefix: 3 logFieldAcc() currentIndex++: 4
length of schedulerprefix: 3 logFieldAcc() currentIndex++: 5
length of schedulerprefix: 3 logJoin() currentIndex++: 6
length of schedulerprefix: 3 logFieldAcc() currentIndex++: 7
length of schedulerprefix: 3 logJoin() currentIndex++: 8
length of schedulerprefix: 3 logFieldAcc() currentIndex++: 9
length of schedulerprefix: 3 logFieldAcc() currentIndex++: 10
-100
startingScheduleExecution() MCRStrategy.schedulePrefix.addAll(prefix), size:5
length of schedulerprefix: 5 RVRunTime.currentIndex: 1
length of schedulerprefix: 5 RVRunTime.currentIndex: 2
length of schedulerprefix: 5 RVRunTime.currentIndex: 3
length of schedulerprefix: 5 RVRunTime.currentIndex: 4
length of schedulerprefix: 5 logFieldAcc() currentIndex++: 6
length of schedulerprefix: 5 logJoin() currentIndex++: 7
length of schedulerprefix: 5 logJoin() currentIndex++: 8
length of schedulerprefix: 5 logFieldAcc() currentIndex++: 9
length of schedulerprefix: 5 logFieldAcc() currentIndex++: 10
-100
startingScheduleExecution() MCRStrategy.schedulePrefix.addAll(prefix), size:10
length of schedulerprefix: 10 RVRunTime.currentIndex: 1
length of schedulerprefix: 10 RVRunTime.currentIndex: 2
length of schedulerprefix: 10 RVRunTime.currentIndex: 3
length of schedulerprefix: 10 RVRunTime.currentIndex: 4
length of schedulerprefix: 10 RVRunTime.currentIndex: 5
```

schedulerprefix从prefix即toExplore中来，

toExplore的更新时机在completedScheduleExecution()





Scheduler维护三个容器

 `liveThreadInfos`

`pausedThreadInfos` 

`blockedThreadInfos`

来维护线程的状态



##### MCRStrategy 中的 schedulePrefix 和Scheduler中的各种ThreadInfos的关系：

```java
length of schedulerprefix: 3
length of liveinfos: 2
length of pauseinfos: 2
length of schedulerprefix: 3
length of liveinfos: 3
length of pauseinfos: 3
-100
length of schedulerprefix: 5
length of liveinfos: 2
length of pauseinfos: 2
length of schedulerprefix: 5
length of liveinfos: 3
length of pauseinfos: 3
length of schedulerprefix: 5
length of liveinfos: 3
length of pauseinfos: 3
length of schedulerprefix: 5
length of liveinfos: 2
length of pauseinfos: 2
-100
length of schedulerprefix: 10
length of liveinfos: 2
length of pauseinfos: 2
length of schedulerprefix: 10
length of liveinfos: 3
length of pauseinfos: 3
length of schedulerprefix: 10
length of liveinfos: 3
length of pauseinfos: 3
length of schedulerprefix: 10
length of liveinfos: 3
length of pauseinfos: 3
length of schedulerprefix: 10
length of liveinfos: 2
length of pauseinfos: 2
```

并不是对应的

infos是相对动态更新的，schedulerprefix相对来说，在某一过程内，只增不减








#### 对于pause的操作

RVRunTime中，这个lock从哪来的

*RVRunTime 在调用过程中同样会上锁 `schedulerStateLock.lock()`保证线程安全，调用完释放*

```java
//why this blocks the thread if the thread can acquire the lock???
Scheduler.performLock(lock);
```

定位到Scheduler中的一些函数，给它传一个lockObject，来创建LockEventDesc实例，然后调用`beforeEvent(lockEventDesc, true)` 

会从liveThreadInfos中获取当前线程`currentThreadInfo`，

##### EventDesc

* 有些事件只是

  ```java
  currentThreadInfo.setEventDesc(eventDesc);
  ```


* 有些事件还会触发pause的过程

  调用`currentThreadInfo.getPausingSemaphore().acquire();`暂停当前线程
  
  被pause的线程加到这里`pausedThreadInfos`

这些不会被pause的点，和最开始的opcode180 181不对应，

只有 pause 的点才会有180 181的选择







