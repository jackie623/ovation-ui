/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.*;
import javax.swing.JButton;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.openide.util.Lookup;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;

/**
 *
 * @author huecotanks
 */
public class TagsSet extends UserPropertySet{//TODO make a baseclass that they both inherit from!

    List<String> tags;
    
    TagsSet(User u, boolean isOwner, boolean currentUser, List<String> tags, Set<String> uris)
    {
        super(u, isOwner, currentUser, null, uris);
        this.tags = tags;
    }
    
    public void refresh(IAuthenticatedDataStoreCoordinator dsc) {
        DataContext c = dsc.getContext();
        User u = (User)c.objectWithURI(userURI);
        
        boolean owner = false;
        String uuid = u.getUuid();
        List<String> tags = new ArrayList<String>();
        for (String uri: uris)
        {
            IEntityBase eb = c.objectWithURI(uri);
            if (eb.getOwner().getUuid().equals(uuid))
            {
                owner = true;
            }
            if (eb instanceof ITaggableEntityBase)
            {
                Set<KeywordTag> keywords = ((ITaggableEntityBase)eb).getTagSet();
                for (KeywordTag keyword : keywords)
                {
                    if (keyword.getOwner().getURIString().equals(userURI))
                    {
                        tags.add(keyword.getTag());
                    }
                }
            }
        }
        
        username = u.getUsername();
        this.isOwner = owner;
        this.tags = tags;
        this.current = c.currentAuthenticatedUser().getUuid().equals(u.getUuid());
    }

    public String getDisplayName() {
        String s = username + "'s Tags";
        if (isOwner) {
            return s + " (owner)";
        }
        return s;
    }

    @Override
    public int compareTo(Object t) {
        if (t instanceof TagsSet)
        {
            TagsSet s = (TagsSet)t;
            
            if (s.isCurrentUser())
            {
                if (this.isCurrentUser())
                    return 0;
                return 1;
            }
            if (this.isCurrentUser())
                return -1;
            return this.getUsername().compareTo(s.getUsername());
        }
        else{
            throw new UnsupportedOperationException("Object type '" + t.getClass() + "' cannot be compared with object type " + this.getClass());
        }
    }
    
    @Override
    public TableModelListener createTableModelListener(ScrollableTableTree t, TableNode n) {
        if (isEditable())
        {
            return null;//new PropertyTableModelListener(uris, (ExpandableJTree)t.getTree(), n, Lookup.getDefault().lookup(ConnectionProvider.class).getConnection());
        }
        return null;
    }

    @Override
    public TableModel createTableModel() {
        Object[][] data  = new Object[tags.size()][1];
        int row = 0;
        for (String tag: tags)
        {
            data[row][0] = tag;
            /*JButton b = new JButton();
            b.setLabel("Press me");
            data[row++][1] = b;
            * 
            */
            
        }
        return new DefaultTableModel(data, new String[]{"Value"});
    }
}
