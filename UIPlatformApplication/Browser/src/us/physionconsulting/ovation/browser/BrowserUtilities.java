/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import com.physion.ebuilder.ExpressionBuilder;
import com.physion.ebuilder.expression.ExpressionTree;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.*;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.IEntityBase;
import us.physion.ovation.interfaces.ConnectionListener;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.ExpressionTreeProvider;
import us.physion.ovation.interfaces.QueryListener;

/**
 *
 * @author jackie
 */
public class BrowserUtilities{
    protected static Node rootNode = null;
    protected static Map<String, Node> browserMap = new HashMap<String, Node>();
    protected static Set<ExplorerManager> registeredViewManagers;
    
    public static Map<String, Node> getNodeMap()
    {
        return browserMap;
    }    
    
    protected static void initBrowser(final ExplorerManager em, 
                                   final boolean projectView)
    {
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
        ConnectionListener cn = new ConnectionListener(new Runnable(){

            @Override
            public void run() {
                browserMap.clear();
                rootNode = null;
                em.setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, projectView), true)));
            }
            
        });
        
        cp.addConnectionListener(cn);
        
        final ExpressionTreeProvider etp = Lookup.getDefault().lookup(ExpressionTreeProvider.class);
        if (etp != null) {
            QueryListener ql = new QueryListener(new Runnable() {
                @Override
                public void run() {
                    ExpressionTree result = etp.getExpressionTree();
                    setTree(result, em);
                }
            });
            etp.addQueryListener(ql);
        }

        if (rootNode == null) {
            em.setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, projectView), true)));
        }
    }
    
    protected static void setTree(final ExpressionTree result,
                                  final ExplorerManager em)
    {
        if (result == null)
            return;
        
        /*try {
            em.getRootContext().destroy();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }*/
        em.setRootContext(new AbstractNode(new QueryChildren()));
        final IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        Iterator itr = dsc.getContext().query(result);
        browserMap = EntityWrapperUtilities.createNodesFromQuery(em, itr);
        /*
         * EventQueue.invokeLater(new Runnable(){
         *
         * @Override public void run() {
         * EntityWrapperUtilities.expandNodes(selectedNodes, btv, em); }
            });
         */
    }
    
    /*public static Set<String> getSupportedClasses(boolean projectView)
    {
        Set<String> s = new HashSet<String>();
        if (projectView)
        {
            s.add("Project");
            s.add("Experiment");
            s.add("EpochGroup");
            s.add("Epoch");
            
        } else {
            s.add("Source");
            s.add("Experiment");
            s.add("EpochGroup");
            s.add("Epoch");
        }
        return s;
    }*/
}
