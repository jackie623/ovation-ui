/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import com.physion.ebuilder.ExpressionBuilder;
import com.physion.ebuilder.expression.ExpressionTree;
import java.util.*;
import javax.swing.ActionMap;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import ovation.IAuthenticatedDataStoreCoordinator;
import us.physion.ovation.interfaces.ConnectionListener;
import us.physion.ovation.interfaces.ConnectionProvider;

/**
 *
 * @author jackie
 */
public class BrowserUtilities {
    public static void recreateTreeComponent(ExplorerManager em, 
                                             Map<String, Node> browserMap, 
                                             boolean projectView)
    {
        em.setRootContext(new AbstractNode(Children.create(new EntityChildFactory(null, browserMap, projectView), true)));
    }
    public static void initBrowser(final ExplorerManager em, 
                                   final Map<String, Node> browserMap, 
                                   final boolean projectView)
    {
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
        ConnectionListener cn = new ConnectionListener(new Runnable(){

            @Override
            public void run() {
                browserMap.clear();
                recreateTreeComponent(em, browserMap, projectView);
            }
            
        });
        
        cp.addConnectionListener(cn);
        recreateTreeComponent(em, browserMap, projectView);
    }
    
    public static void queryActionPerformed(BeanTreeView btv, 
                                            ExplorerManager em,
                                            Map<String, Node> browserMap,
                                            boolean projectView)
    {
        IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        ExpressionTree result = ExpressionBuilder.editExpression().expressionTree;
        if (result == null)
            return;
        if (getSupportedClasses(projectView).contains(result.getClassUnderQualification()))
        {
            Iterator itr = dsc.getContext().query(result);
            //((BeanTreeView)treeViewPane).collapseNode(em.getRootContext());
            EntityWrapperUtilities.expandNodesFromQuery(browserMap, itr, btv, em);
        }
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
