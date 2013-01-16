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
import java.util.concurrent.*;
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
import org.openide.windows.TopComponent;
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
    protected static ExecutorService executorService = Executors.newFixedThreadPool(2);
    protected static BrowserCopyAction browserCopy = new BrowserCopyAction();
    
    protected static ConnectionListener cn = new ConnectionListener(new Runnable(){

            @Override
            public void run() {
                resetView();
            }
            
        });
    
    public static Map<String, Node> getNodeMap()
    {
        return browserMap;
    } 
    
    //TODO: extend default CopyAction somehow
    /*public static BrowserCopyAction myCopyAction()
    {
        return browserCopy;
    }*/
    
    static void submit(Runnable runnable) {
        executorService.submit(runnable);
    }
    
    public static void initBrowser(final ExplorerManager em, 
                                   final boolean projectView)
    {
        registeredViewManagers.put(em, projectView);//TODO: don't need this. we should be able to look up the explorerManagers from TopComponents
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
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
        em.setRootContext(new EntityNode(new EntityChildren(null, projectView, null), null));
        resetView(em, projectView);
    }
    
    public static void resetView()
    {
        browserMap.clear();
        for (ExplorerManager mgr : registeredViewManagers.keySet()) {
            mgr.setRootContext(new EntityNode(new EntityChildren(null, registeredViewManagers.get(mgr), null), null));
        }
    }
    
    public static void switchToSourceView()
    {
        Set<TopComponent> components = TopComponent.getRegistry().getOpened();
        for (TopComponent c : components)
        {
            if (c instanceof SourceBrowserTopComponent)
            {
                c.toFront();
                break;
            }
        }
    }
    public static void switchToProjectView()
    {
        Set<TopComponent> components = TopComponent.getRegistry().getOpened();
        for (TopComponent c : components)
        {
            if (c instanceof BrowserTopComponent)
            {
                c.toFront();
                break;
            }
        }
    }

    protected static void resetView(ExplorerManager e, boolean projectView)
    {
        e.setRootContext(new EntityNode(new EntityChildren(null, projectView, null), null));
    }
    
    protected static void setTrees(final ExpressionTree result)
    {
        if (result == null)
            return;
        
        Set<ExplorerManager> mgrs = new HashSet<ExplorerManager>();
        for (ExplorerManager em : registeredViewManagers.keySet())
        {
            em.setRootContext(new EntityNode(new QueryChildren(registeredViewManagers.get(em)), null));
            mgrs.add(em);
        }
        
        final IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        Iterator itr = dsc.getContext().query(result);
        
        EntityWrapperUtilities.createNodesFromQuery(mgrs, itr);
    }

    public static void runOnEDT(Runnable r)
    {
        if (EventQueue.isDispatchThread())
        {
            r.run();
        }
        else{
            SwingUtilities.invokeLater(r);
        }
    }
    
}
