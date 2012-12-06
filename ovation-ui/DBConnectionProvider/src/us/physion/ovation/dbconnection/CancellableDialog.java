/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

/**
 *
 * @author huecotanks
 */
interface CancellableDialog {
    public boolean isCancelled();
    public void showDialog();
    public void cancel();
}
