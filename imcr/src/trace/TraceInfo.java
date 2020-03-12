package trace;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class keeps the information associated with the trace such as the trace statistics,
 * shared variable signature, etc.
 *
 * @author jeffhuang
 *
 */
public class TraceInfo {

    //metadata
    HashMap<Long, String> threadIdNamemap;
    HashMap<Integer, String> sharedVarIdSigMap;
    HashMap<Integer, String> stmtIdSigMap;
    HashMap<Integer, String> volatileAddresses;

    HashSet<String> sharedAddresses = new HashSet<String>();
    HashSet<Long> threads = new HashSet<Long>();
    int num_br,num_sync,num_rw_shared,num_rw_local,num_w_init,num_prop;

    public TraceInfo(HashMap<Integer, String> sharedVarIdSigMap2,
                     HashMap<Integer, String> volatileAddresses2,
                     HashMap<Integer, String> stmtIdSigMap2,
                     HashMap<Long, String> threadIdNameMap2)
    {

        sharedVarIdSigMap = sharedVarIdSigMap2;
        volatileAddresses = volatileAddresses2;
        stmtIdSigMap = stmtIdSigMap2;
        threadIdNamemap = threadIdNameMap2;
    }

    public void updateIdSigMap(HashMap<Integer, String> m){
        this.stmtIdSigMap = m;
    }
    public HashMap<Integer, String> getSharedVarIdMap() {

        return sharedVarIdSigMap;
    }
    public HashMap<Integer, String> getStmtSigIdMap() {

        return stmtIdSigMap;
    }
    public HashMap<Long, String> getThreadIdNameMap()
    {
        return threadIdNamemap;
    }
    public void addSharedAddresses(HashSet<String> s)
    {
        sharedAddresses.addAll(s);
    }
    public void addThreads(HashSet<Long> s)
    {
        threads.addAll(s);
    }
    public int getTraceThreadNumber() {
        // TODO Auto-generated method stub
        return threads.size();
    }
    public int getTraceSharedVariableNumber() {
        // TODO Auto-generated method stub
        return sharedAddresses.size();
    }
    public boolean isAddressVolatile(String addr) {

        return volatileAddresses.containsKey(Integer.valueOf(addr));
    }
    public void incrementBranchNumber()
    {
        num_br++;
    }
    public void incrementSharedReadWriteNumber()
    {
        num_rw_shared++;
    }
    public void incrementSyncNumber()
    {
        num_sync++;
    }
    public void incrementPropertyNumber()
    {
        num_prop++;
    }
    public void incrementLocalReadWriteNumber() {
        num_rw_local++;
    }
    public void incrementInitWriteNumber() {
        num_w_init++;
    }

    public int getTraceBranchNumber() {
        return num_br;
    }
    public int getTraceSharedReadWriteNumber() {
        return num_rw_shared;
    }
    public int getTraceLocalReadWriteNumber() {
        return num_rw_local;
    }
    public int getTraceInitWriteNumber() {
        return num_w_init;
    }
    public int getTraceSyncNumber() {
        return num_sync;
    }
    public int getTracePropertyNumber() {
        return num_prop;
    }
    public void setVolatileAddresses(HashSet<String> volatilevariables) {
        // TODO Auto-generated method stub

    }


}
