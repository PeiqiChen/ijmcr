package trace;

public class WriteNode extends AbstractNode implements IMemNode
{
    private long prevSyncId,prevBranchId;

    private String value;
    private String addr;
    private String label;

    public WriteNode(long GID, long tid, int ID, String addr, String value, TYPE type, String label)
    {
        super(GID, tid, ID, type, label);
        this.addr = addr;
        this.value = value;

        this.label = label;
    }

    public String getValue()
    {
        return value;
    }
    public String getLabel(){
        return label;
    }

    public String getAddr()

    {
        return addr;
    }

    public String toString()
    {

        return GID+": thread "+tid+ " "+ID+" "+addr+" "+value+" "+type;
    }

    @Override
    public long getPrevSyncId() {
        // TODO Auto-generated method stub
        return prevSyncId;
    }


    @Override
    public void setPrevSyncId(long id) {
        // TODO Auto-generated method stub
        prevSyncId = id;
    }


    @Override
    public long getPrevBranchId() {
        // TODO Auto-generated method stub
        return prevBranchId;
    }


    @Override
    public void setPrevBranchId(long id) {
        // TODO Auto-generated method stub
        prevBranchId = id;
    }

}
