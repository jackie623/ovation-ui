package us.physion.ovation.interfaces;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.Runnable;
import javax.swing.SwingUtilities;
import java.util.concurrent.FutureTask;

//TODO: call it something other than a listner
public class QueryListener {//implements PropertyChangeListener{

    private FutureTask<Boolean> toRun;

    public QueryListener(Runnable r)
    {
        toRun = new FutureTask<Boolean>(r, true);
    }
    
    public FutureTask<Boolean> run()
    {
	toRun.run();  
        return toRun;
    }

    public boolean cancel()
    {
	if (toRun.isCancelled() || toRun.isDone())
	    return true;
	return toRun.cancel(true);
    }


    /*@Override
	public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals("ovation.queryChanged"))
	    {
		toRun.run();
	    }
    }*/

}