package trace;


public class PropertyNode extends AbstractNode
{
    private String object_addr;

    public PropertyNode(long GID, long tid, int ID, String addr, TYPE type)
    {
        super(GID, tid, ID,type);
        this.object_addr = addr;
    }

    public String getAddr()
    {
        return object_addr;
    }
    public String toString()
    {
        return GID+": thread "+tid+" "+ID+" "+object_addr+" "+type;
    }
}
