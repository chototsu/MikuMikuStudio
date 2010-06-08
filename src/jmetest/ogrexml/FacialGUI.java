package jmetest.ogrexml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.SWT;

import com.jmex.model.ogrexml.anim.MeshAnimation;
import com.jmex.model.ogrexml.anim.PoseTrack.PoseFrame;




/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class FacialGUI extends org.eclipse.swt.widgets.Composite {

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}
	

	private static TestFacialAnimation facialAnim;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/

	private ArrayList<Slider> sliders;
	private Button checkbox;
	private Label label1;
	private static Display display;
	/**
	* Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
	*/	
	protected void checkSubclass() {
	}
	
	/**
	* Auto-generated method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	 * @param facialAnimation 
	 * @param swtDisplay 
	 * @param swtShell 
	 * @param mManualKeyFrame 
	 * @param headMesh 
	*/
	public static void showGUI(TestFacialAnimation facialAnimation) {
		facialAnim = facialAnimation;
		display = Display.getDefault();
		Shell shell = new Shell(display);
		FacialGUI inst = new FacialGUI(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
	}

	public static void updateGUI()
	{
		display.readAndDispatch();
	}
	
	public FacialGUI(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			this.setSize(new org.eclipse.swt.graphics.Point(400,900));
			this.setBackground(SWTResourceManager.getColor(192, 192, 192));
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			{
				checkbox = new Button(this, SWT.CHECK | SWT.LEFT);
				FormData checkboxLData = new FormData();
				checkboxLData.left =  new FormAttachment(0, 1000, 312);
				checkboxLData.top =  new FormAttachment(0, 1000, 0);
				checkboxLData.width = 80;
				checkboxLData.height = 16;
				checkbox.setLayoutData(checkboxLData);
				checkbox.setText("manual");
				checkbox.setBackground(SWTResourceManager.getColor(192, 192, 192));
				
				checkbox.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent evt) {
						System.out.println("checkbox.widgetSelected, event="+evt);
						Button b = (Button)evt.getSource();
						if (b.getSelection())
						{
							for (Slider slider : sliders)
							{
								slider.setEnabled(true);
							}
							facialAnim.playAnimation(false);
						}
						else
						{
							for (Slider slider : sliders)
							{
								slider.setEnabled(false);
							}
							facialAnim.playAnimation(true);
						}
						
					}
				});
			}
			sliders = new ArrayList<Slider>();
			int i = 0;
			
			final MeshAnimation manualMeshAnimation = facialAnim.getManualMeshAnimation();
			for (final String poseName : facialAnim.getFace().getPoseNames())
			{
//				String sliderName = "Poser" + i;
//				String poseName ="Slider"+i;
				{
					label1 = new Label(this, SWT.NONE);
					FormData label1LData = new FormData();
					label1LData.left =  new FormAttachment(0, 1000, 15);
					label1LData.top =  new FormAttachment(0, 1000, 34+i*25);
					label1LData.width = 100;
					label1LData.height = 17;
					label1.setLayoutData(label1LData);
					label1.setText(poseName);
					label1.setFont(SWTResourceManager.getFont("Tahoma", 9, 1, false, false));
					label1.setBackground(SWTResourceManager.getColor(192, 192, 192));
					label1.setAlignment(SWT.RIGHT);
				}
				{
					// get all pose-frames at time 0
					final List<PoseFrame> poseWeightFrames = manualMeshAnimation.getPoseWeightFrame(0);
					FormData slider1LData = new FormData();
					slider1LData.left =  new FormAttachment(0, 1000, 120);
					slider1LData.top =  new FormAttachment(0, 1000, 34+i*25);
					slider1LData.width = 145;
					slider1LData.height = 17;
					final Slider slider = new Slider(this, SWT.NONE);
					sliders.add(slider);
					slider.setLayoutData(slider1LData);
					slider.setMaximum(110);
					slider.setMinimum(0);
					slider.setEnabled(false);
					
					slider.addDragDetectListener(new DragDetectListener() {
						public void dragDetected(DragDetectEvent evt) {
							System.out.println("slider1.widgetSelected, event="+evt);
							System.out.println(slider.getSelection()/100.0f);
							// set weight
							manualMeshAnimation.setWeight(poseName,poseWeightFrames,slider.getSelection()/100.0f);
						}
					});
				}
				i++;
			}
			
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
