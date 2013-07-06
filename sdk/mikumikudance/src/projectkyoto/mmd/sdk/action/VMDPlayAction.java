/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.mmd.sdk.action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import projectkyoto.mmd.sdk.VMDDataObject;

@ActionID(category = "JME3",
id = "projectkyoto.mmd.sdk.action.VMDPlayAction")
@ActionRegistration(iconBase =
"projectkyoto/mmd/sdk/action/vmd16.png",
displayName = "#CTL_VMDPlayAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 3333),
    @ActionReference(path = "Toolbars/jMonkeyPlatform-Tools", position = -80),
    @ActionReference(path = "Loaders/application/x-mmd-vmd/Actions", position = 0)
})
@Messages("CTL_VMDPlayAction=Play")
public final class VMDPlayAction implements ActionListener {

    private final VMDDataObject context;

    public VMDPlayAction(VMDDataObject context) {
        this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
    }
}
