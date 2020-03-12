#### Instrumentor：

调用下方法若干次

##### storePropertyValues()

values:  "$Proxy"    toSet:  size = 1

values:  ""    toSet:  size = 0

后 开始

##### File dir = new File("out")





thread信息初始化？

#### ThreadInfo：

eventDesc: null

> EventDesc就是在Scheduler⼀一系列列插桩中的 桩点 









轮流调用若干次

#### Scheduler：

```
logBeforeStart()
 Scheduler.beforeForking((Thread) o)
 logThreadBegin()
 Scheduler.beginThread();
```

##### choose()

return chooseObject

#### ThreadInfo：

eventDesc: null

#### Scheduler：

```
logBeforeStart()
```

choose()

#### RVRunTime：

##### logFieldAcc()

Trace trace = MCRStrategy.getTrace();

#### Scheduler：

```
logThreadBegin
```

#### RVRunTime：

##### logFieldAcc()

Trace trace = MCRStrategy.getTrace();

#### Scheduler：

choose()

#### RVRunTime：

##### logFieldAcc()

Trace trace = MCRStrategy.getTrace();

#### Scheduler：

choose()

#### RVRunTime：

##### logFieldAcc()

Trace trace = MCRStrategy.getTrace();

#### Scheduler：

```
if(chosenPausedThreadInfo!=null)
```

####  RVRunTime：

##### logFieldAcc()

Trace trace = MCRStrategy.getTrace();