/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import com.physion.ebuilder.ExpressionBuilder;
import com.physion.ebuilder.expression.ExpressionTree;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
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
        registeredViewManagers.put(em, projectView);//TODO: don't need this. we should be able to look up the explorerManagers from TopComponents
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
        ConnectionListener cn = new ConnectionListener(new Runnable(){

            @Override
            public void run() {
                browserMap.clear();
                em.setRootContext(new AbstractNode(new EntityChildren(null, projectView)));
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
                        ExpressionTree result = Lookup.getDefault().lookup(ExpressionTreeProvider.class).getExpressionTree();
                        setTrees(result);
                    }
                });
                etp.addQueryListener(ql);
            }
        }

        em.setRootContext(new AbstractNode(new EntityChildren(null, projectView)));
    }
    
    protected static void resetView()
    {
        /*for (BrowserTopComponent btc : Lookup.getDefault().lookupAll(BrowserTopComponent.class)){
            btc.getExplorerManager().setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, true), false)));
        }
        for (SourceBrowserTopComponent btc : Lookup.getDefault().lookupAll(SourceBrowserTopComponent.class)){
            btc.getExplorerManager().setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, f), false)));
        }*/
        browserMap.clear();
        for (ExplorerManager mgr : registeredViewManagers.keySet()) {
            mgr.setRootContext(new AbstractNode(new EntityChildren(null, registeredViewManagers.get(mgr))));
        }
    }
    
    protected static void setTrees(final ExpressionTree result)
    {
        if (result == null)
            return;
        
        Set<ExplorerManager> mgrs = new HashSet<ExplorerManager>();
        for (ExplorerManager em : registeredViewManagers.keySet())
        {
            em.setRootContext(new AbstractNode(new QueryChildren(registeredViewManagers.get(em))));
            mgrs.add(em);
        }
        
        final IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        Iterator itr = dsc.getContext().query(result);
        
        EntityWrapperUtilities.createNodesFromQuery(mgrs, itr);
    }
    
}
