package Instrument;

import Schedule.Scheduler;
import trace.Trace;
import trace.*;
import Schedule.MCRStrategy;

import java.util.HashMap;
import java.util.Vector;




public class RVRunTime {
    public static HashMap<Long, String> threadTidNameMap;
    public static HashMap<Long, Integer> threadTidIndexMap;
    final static String MAIN_NAME = "0";
    public static long globalEventID;
    public static int currentIndex = 0;

    public static Vector<String> failure_trace = new Vector<String>();

    private static HashMap<Integer, Object> staticObjects= new HashMap<Integer,Object>();

    private static Object getObject(int SID)
    {
        Object o = staticObjects.get(SID);
        if(o==null)
        {
            o = new Object();
            staticObjects.put(SID,o);
        }
        return o;
    }

    public static void init() {
        long tid = Thread.currentThread().getId();
        threadTidNameMap = new HashMap<Long, String>();
        threadTidNameMap.put(tid, MAIN_NAME);
        threadTidIndexMap = new HashMap<Long, Integer>();
        threadTidIndexMap.put(tid, 1);
        globalEventID = 0;
    }




    public static void logFieldAcc(int ID, final Object o, int SID, final Object v, final boolean write) {

        Trace trace = MCRStrategy.getTrace();

        StackTraceElement frame = Thread.currentThread().getStackTrace()[2];
        String fileName = frame.getFileName();
        int line = frame.getLineNumber();
        String label = fileName+":"+Integer.toString(line);

        String threadName = Thread.currentThread().getName().toString();
        String type = null;
        if (write) {
            type="write";
        }
        else
            type = "read";

        RVRunTime.failure_trace.add(threadName + "_" + label + ":" + type);

        if ( MCRStrategy.schedulePrefix.size() <= currentIndex++|| MCRStrategy.fullTrace)
        {
            globalEventID++;
            if (isPrim(v))
            {
                if (write)
                {
                    WriteNode writeNode = new WriteNode(globalEventID, Thread.currentThread().getId(), ID,
                            o == null ? "." + SID: System.identityHashCode(o) + "." + SID,
                            v + "",AbstractNode.TYPE.WRITE,label);
                    trace.addRawNode(writeNode);

                } else {
                    ReadNode readNode = new ReadNode(globalEventID,
                            Thread.currentThread().getId(), ID, o == null ? "." + SID
                            : System.identityHashCode(o) + "." + SID, v + "",
                            AbstractNode.TYPE.READ,
                            label);
                    trace.addRawNode(readNode);
//					if (o==null)
//					    System.out.println(readNode.toString());
                }
            }
            else {
                if (write) {
                    WriteNode writeNode = new WriteNode(globalEventID,
                            Thread.currentThread().getId(),
                            ID,
                            o == null ? "_."+ SID : System.identityHashCode(o) + "_." + SID,
                            System.identityHashCode(v) + "_",
                            AbstractNode.TYPE.WRITE,
                            label);
                    trace.addRawNode(writeNode);
                    // db.saveEventToDB(tid, ID,
                    // o==null?"_."+SID:hashcode_o+"_."+SID,
                    // isPrim(v)?v+"":System.identityHashCode(v)+"_",
                    // write?db.tracetypetable[2]: db.tracetypetable[1]);
                } else {
                    ReadNode readNode = new ReadNode(
                            globalEventID,              //index of this event in the trace
                            Thread.currentThread().getId(),
                            ID,               //id of the variable
                            o == null ? "_."+ SID : System.identityHashCode(o) + "_." + SID,   //addr
                            System.identityHashCode(v) + "_",           //value
                            AbstractNode.TYPE.READ,
                            label);
                    trace.addRawNode(readNode);
//					System.out.println(readNode.toString());
                }
            }

        }
        else {
            // Not added to trace but update initial memory write.
            if (write) {
                if (isPrim(v)) {

                    trace.updateInitWriteValueToAddress(o == null ? "." + SID
                            : System.identityHashCode(o) + "." + SID, v + "");
                } else {
                    trace.updateInitWriteValueToAddress(o == null ? "_." + SID
                                    : System.identityHashCode(o) + "_." + SID,
                            System.identityHashCode(v) + "_");
                }
            }
        }
    }


    private static boolean isPrim(Object o) {
        if (o instanceof Integer || o instanceof Long || o instanceof Byte
                || o instanceof Boolean || o instanceof Float
                || o instanceof Double || o instanceof Short
                || o instanceof Character)
            return true;

        return false;
    }



}


