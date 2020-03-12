package Schedule;

import trace.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import Instrument.RVRunTime;
import Instrument.RVGlobalStateForInstrumentation;

public class MCRStrategy {
    private Queue<List<String>> toExplore;
    private static Trace currentTrace;
    public static List<Integer> choicesMade;
    public static List<String> schedulePrefix = new ArrayList<String>();
    public static final Boolean fullTrace = false;  //default

    private int count;
    public MCRStrategy() {
        count = 0;
    }

    public static Trace getTrace() {
        return currentTrace;
    }


    private void initTrace() {
        RVRunTime.init();
        TraceInfo traceInfo = new TraceInfo(
                RVGlobalStateForInstrumentation.variableIdSigMap,
                new HashMap<Integer, String>(),
                RVGlobalStateForInstrumentation.stmtIdSigMap,
                RVRunTime.threadTidNameMap);
        traceInfo.setVolatileAddresses(RVGlobalStateForInstrumentation.instance.volatilevariables);
        currentTrace = new Trace(traceInfo);
    }

    public ThreadInfo choose(ArrayList<ThreadInfo> choices){
        // 初始化
        int chosenIndex = 0;
        ThreadInfo choice=null;

        // 除此之外 做随机决定
        // 这个判断条件?
//        if (MCRStrategy.schedulePrefix.size() > RVRunTime.currentIndex){}

//        if (choices == null){
//            /**
//             * 这个逻辑？
//             * it might be that the wanted thread is blocked, waiting to be added to the paused threads
//             **/
//        }else{
//
//        }
        System.out.println("RVRunTime.currentIndex:"+RVRunTime.currentIndex /*+ ":" + choice.toString()*/);
        MCRStrategy.choicesMade.add(chosenIndex);
        return choice;
    }
}
