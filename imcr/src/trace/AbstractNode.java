package trace;

public abstract class AbstractNode{
    /**
     * There are three kinds of mems: SPE, thread object id, ordinary object id
     */
    /**
     *
     */
    protected long GID;
    protected int ID;
    protected long tid;
    protected TYPE type;

    protected String label;

    public AbstractNode(long GID, long tid, int ID, TYPE type)
    {
        this.GID = GID;
        this.tid = tid;
        this.ID = ID;
        this.type = type;

        this.label = "other nodes";
    }

    public AbstractNode(long GID, long tid, int ID, TYPE type, String label)
    {
        this.GID = GID;
        this.tid = tid;
        this.ID = ID;
        this.type = type;

        this.label = label;
    }

    public long getGID()
    {
        return GID;
    }
    public int getID()
    {
        return ID;
    }
    public long getTid()
    {
        return tid;
    }

    public void setTid(int tid)
    {
        this.tid = tid;
    }
    public boolean equals(AbstractNode node)
    {
        if(this.GID == node.getGID())
        {
            return true;
        }
        else
            return false;
    }
    public TYPE getType()
    {
        return type;
    }

    public  String getLabel() {
        return label;
    }

    public enum TYPE
    {
        INIT,READ,WRITE,LOCK,UNLOCK,WAIT,NOTIFY,START,JOIN,BRANCH,BB,PROPERTY
    }
    public String toString()
    {
        return GID+": thread "+tid+" "+ID+" "+type;
    }
}
