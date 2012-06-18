/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import com.physion.ebuilder.ExpressionBuilder;
import com.physion.ebuilder.expression.ExpressionTree;
import java.awt.EventQueue;
import java.util.*;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import ovation.IAuthenticatedDataStoreCoordinator;
import us.physion.ovation.interfaces.ConnectionListener;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.ExpressionTreeProvider;
import us.physion.ovation.interfaces.QueryListener;

/**
 *
 * @author jackie
 */
public class BrowserUtilities {
    public static Node rootNode = null;
    
    public static void createTreeComponent(ExplorerManager em, 
                                             Map<String, Node> browserMap, 
                                             boolean projectView)
    {
        if (rootNode == null) {
            em.setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, browserMap, projectView), true)));
        }
    }
    public static void initBrowser(final BeanTreeView btv,
                                   final ExplorerManager em, 
                                   final Map<String, Node> browserMap, 
                                   final boolean projectView)
    {
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
        ConnectionListener cn = new ConnectionListener(new Runnable(){

            @Override
            public void run() {
                browserMap.clear();
                rootNode = null;
                createTreeComponent(em, browserMap, projectView);
            }
            
        });
        
        cp.addConnectionListener(cn);
        
        final ExpressionTreeProvider etp = Lookup.getDefault().lookup(ExpressionTreeProvider.class);
        if (etp != null) {
            QueryListener ql = new QueryListener(new Runnable() {
                @Override
                public void run() {
                    ExpressionTree result = etp.getExpressionTree();
                    setTree(result, btv, em, browserMap, projectView);
                }
            });
            etp.addQueryListener(ql);
        }
        createTreeComponent(em, browserMap, projectView);
    }
    
    protected static void setTree(final ExpressionTree result,
                                  final BeanTreeView btv, 
                                  final ExplorerManager em,
                                  final Map<String, Node> browserMap,
                                  final boolean projectView)
    {
        if (result == null)
            return;
        if (getSupportedClasses(projectView).contains(result.getClassUnderQualification()))
        {
            final IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
            //((BeanTreeView)treeViewPane).collapseNode(em.getRootContext());
            boolean b = SwingUtilities.isEventDispatchThread();
            Iterator itr = dsc.getContext().query(result);
            final Set<Node> selectedNodes = EntityWrapperUtilities.nodesFromQuery(browserMap, itr, em);
            EventQueue.invokeLater(new Runnable(){

                @Override
                public void run() {
                    EntityWrapperUtilities.expandNodes(selectedNodes, btv, em);
                }
            });
        }
    }
    public static void queryActionPerformed(BeanTreeView btv, 
                                            ExplorerManager em,
                                            Map<String, Node> browserMap,
                                            boolean projectView)
    {
        //TODO: figure out how to call your RunQuery action from here
        ExpressionTreeProvider etp = Lookup.getDefault().lookup(ExpressionTreeProvider.class);
        ExpressionTree prev = null;
        if (etp != null)
        {
            prev = etp.getExpressionTree();
        }
        ExpressionTree result = ExpressionBuilder.editExpression(prev).expressionTree;
        boolean b = SwingUtilities.isEventDispatchThread();
        setTree(result, btv, em, browserMap, projectView);
    }
    
    public static Set<String> getSupportedClasses(boolean projectView)
    {
        Set s = new HashSet<String>();
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
    }
}
