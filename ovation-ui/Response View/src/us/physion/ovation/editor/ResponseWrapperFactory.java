/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.util.Collection;
import javax.imageio.ImageIO;
import org.openide.util.Lookup;
import ovation.Response;

/**
 *
 * @author huecotanks
 */
public class ResponseWrapperFactory {
    public static VisualizationFactory create(Response r)
    {
        Collection<? extends VisualizationFactory> factories = Lookup.getDefault().lookupAll(VisualizationFactory.class);
        int preference = 0;
        VisualizationFactory vis = null;
        for (VisualizationFactory f : factories)
        {
            int factoryPref = f.getPreferenceForDataContainer(r);
            if (factoryPref > preference)
            {
                preference = factoryPref;
                vis = f;
            }
        }
        return vis;
    }
}
