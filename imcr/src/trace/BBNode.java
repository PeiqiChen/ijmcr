package trace;

/**
 * Basic block node -- used for tracking thread execution path.
 *
 * @author jeffhuang
 *
 */
public class BBNode extends AbstractNode {

    public BBNode(long GID, long tid, int ID, TYPE type)
    {
        super(GID, tid, ID,type);
    }

}

