/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.query;

import com.objy.db.app.ooId;
import com.physion.ebuilder.ExpressionBuilder;
import com.physion.ebuilder.expression.ExpressionTree;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.*;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.ExpressionTreeProvider;
import us.physion.ovation.interfaces.QueryListener;

@ActionID(category = "Query",
id = "us.physion.ovation.query.RunQuery")
@ActionRegistration(iconBase = "us/physion/ovation/query/query.png",
displayName = "#CTL_RunQuery")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 0),
    @ActionReference(path = "Toolbars/Find", position = 10),
    @ActionReference(path = "Shortcuts", name = "M-R")
})
@Messages("CTL_RunQuery=Run Query")
public final class RunQuery implements ActionListener {

    protected QueryProvider getQueryProvider()
    {
         ExpressionTreeProvider etp = Lookup.getDefault().lookup(ExpressionTreeProvider.class);
         if (etp instanceof QueryProvider) {
             return (QueryProvider)etp;
         }
         return null;
    }
    
    public void actionPerformed(ActionEvent e) {
        
        ExpressionTreeProvider etp = Lookup.getDefault().lookup(ExpressionTreeProvider.class);
        ExpressionTree et = etp.getExpressionTree();
        
        IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        final ExpressionTree result = ExpressionBuilder.editExpression(et).expressionTree;
       
        if (result == null)
            return;
        
        ProgressHandle ph = null;
        if (!EventQueue.isDispatchThread())
        {
            ph = ProgressHandleFactory.createHandle("Querying");
            ph.setDisplayName("Querying");
            ph.switchToIndeterminate();
            ph.start();
        }
        
        if (etp instanceof QueryProvider) {
            QueryProvider qp = (QueryProvider)etp;
            qp.setExpressionTree(result);
            for (QueryListener listener : qp.getListeners()) {
                FutureTask task = listener.run();
                try {
                    task.get();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        if (!EventQueue.isDispatchThread())
        {
            ph.finish();
        }
    }
    
    private static Runnable createProgressHandle = new Runnable(){

        @Override
        public void run() {
            ProgressHandle ph = ProgressHandleFactory.createHandle("Querying"); 
            ph.setDisplayName("Querying");
            ph.switchToIndeterminate();
            ph.start();
            try {
                Thread.currentThread().sleep(10000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
    };
    
    private static Runnable removeProgressHandle = new Runnable(){

        @Override
        public void run() {
            ProgressHandle ph = ProgressHandleFactory.createHandle("Querying");
            ph.finish();
        }
        
    };
       
}
