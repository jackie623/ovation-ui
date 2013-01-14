/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import us.physion.ovation.interfaces.InsertEntityIterator;

/**
 *
 * @author jackie
 */
public class ImagePanelIterator {
    
    public ImagePanelIterator(List<WizardDescriptor.Panel<WizardDescriptor>> panels)
    {
        this.panels = panels;
        if (panels == null)
        {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        }
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.                                                                                                        
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components                                                                                               
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
    }
    private int index;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
        
    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        }
        return panels;
    }

    public WizardDescriptor.Panel<WizardDescriptor> current() {
        if (index == 0)
        {
            //return first;
        }
        return getPanels().get(index);
    }

    public String name() {
        return index + 1 + " of " + getPanels().size();
    }

    public boolean hasNext() {
        return index < getPanels().size() - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (index == 0)
        {
            
        }
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }


    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:                                                                                              
    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.                                                                                       
    // the number of panels changes in response to user input, then use                                                                                             
    // ChangeSupport to implement add/removeChangeListener and call fireChange                                                                                      
    // when needed
}
