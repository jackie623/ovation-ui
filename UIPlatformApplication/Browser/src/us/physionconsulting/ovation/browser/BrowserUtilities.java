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
import java.util.concurrent.ConcurrentHashMap;
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
    protected static Map<String, Node> browserMap = new ConcurrentHashMap<String, Node>();
    protected static Map<ExplorerManager, Boolean> registeredViewManagers = new HashMap<ExplorerManager, Boolean>();
    protected static QueryListener ql;
    
    public static Map<String, Node> getNodeMap()
    {
        return browserMap;
    }    
    
    protected static void initBrowser(final ExplorerManager em, 
                                   final boolean projectView)
    {
        registeredViewManagers.put(em, projectView);
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
        ConnectionListener cn = new ConnectionListener(new Runnable(){

            @Override
            public void run() {
                browserMap.clear();
                rootNode = null;
                try {
                    if (em.getRootContext() != null)
                    {
                        em.getRootContext().destroy();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                em.setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, projectView), true)));
            }
            
        });
        
        cp.addConnectionListener(cn);
        
        if (ql == null)
        {
            final ExpressionTreeProvider etp = Lookup.getDefault().lookup(ExpressionTreeProvider.class);
            if (etp != null) {
                ql = new QueryListener(new Runnable() {

                    @Override
                    public void run() {
                        browserMap.clear();
                        ExpressionTree result = etp.getExpressionTree();
                        for (ExplorerManager mgr : registeredViewManagers.keySet())
                        {
                            setTree(result, mgr, registeredViewManagers.get(mgr));
                        }
                    }
                });
                etp.addQueryListener(ql);
            }
        }

        if (rootNode == null) {
            em.setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, projectView), true)));
        }
    }
    
    protected static void resetView()
    {
        for (ExplorerManager mgr : registeredViewManagers.keySet()) {
            try {
                mgr.getRootContext().destroy();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            mgr.setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, registeredViewManagers.get(mgr)), false)));
        }
    }
    
    protected static void setTree(final ExpressionTree result,
                                  final ExplorerManager em,
                                  final boolean projectView)
    {
        if (result == null)
            return;
        
        em.setRootContext(new AbstractNode(new QueryChildren(projectView)));
        final IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        Iterator itr = dsc.getContext().query(result);
        EntityWrapperUtilities.createNodesFromQuery(em, itr);
    }
    
}
