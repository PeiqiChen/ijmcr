package trace;

import trace.AbstractNode.TYPE;

/**
 * a common interface for read and write events.
 * @author jeffhuang
 *
 */
public interface IMemNode {

    public String getAddr();
    public long getGID();
    public long getTid();
    public TYPE getType();
    public int getID();

    public long getPrevSyncId();
    public void setPrevSyncId(long id);
    public long getPrevBranchId();
    public void setPrevBranchId(long id);

}
