/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.updater;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.JDialog;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import sun.net.www.http.HttpClient;

@ActionID(category = "Help",
id = "us.physion.ovation.updater.FeedbackAction")
@ActionRegistration(displayName = "#CTL_FeedbackAction")
@ActionReferences({
    @ActionReference(path = "Menu/Help", position = 350)
})
@Messages("CTL_FeedbackAction=Send Feedback")
public final class FeedbackAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {

        MailUsDialog d = new MailUsDialog();
        d.showDialog();

        /*
     String html = "<form method=\"post\" action=\"https://www.example.com/fogbugz/ScoutSubmit.asp\"> ";
     html += "<input type=\"text\" value=\"FogBUGZ User Name\" name=\""+ userName + "\">";
     html += "<input type=\"text\" value=\"Existing Project Name\" name=\"" + ScoutProject + "\">";
     html += "<input type=\"text\" value=\"Existing Area Name\" name=\"ScoutArea\">";
     html += "<input type=\"text\" value=\"Description\" name=\"Description\">";
     html += "<input type=\"text\" value=\"0\" name=\"ForceNewBug\">";
     html += "<input type=\"text\" value=\"1\" name=\"FriendlyResponse\">;"
             + "<input type=\"submit\"></form>\"";
     
     HttpClient httpClient = new HttpClient();
 
   /*Extra Info:
   <input type="text" value="extra info" name="Extra">
 
   Customer Email:
   <input type="text" value="customer@emailaddress.com" name="Email">
 
   Default Message:
   <input type="text" value="html Default Message" name="ScoutDefaultMessage">
 
  */
    }
}
