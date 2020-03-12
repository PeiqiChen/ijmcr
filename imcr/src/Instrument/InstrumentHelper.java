package Instrument;

import Schedule.Scheduler;



/**
 * 接收类名 函数符啥的
 */
public class InstrumentHelper {
    public static String CLASSRECEIVER = Scheduler.class.getName().replace(".","/");
    public static String BEFOREFIELDACCESS = "beforeFieldAccess";
    public static String BOOL_3String_INT_VOID = "(ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V";
}

