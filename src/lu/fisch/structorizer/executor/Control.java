/*
    Structorizer
    A little tool which you can use to create Nassi-Schneiderman Diagrams (NSD)

    Copyright (C) 2009  Bob Fisch

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.structorizer.executor;

/******************************************************************************************************
 *
 *      Author:         Bob Fisch
 *
 *      Description:    This class represents the GUI controlling the execution of a diagram.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------			----			-----------
 *      Bob Fisch       2009.05.18      First Issue
 *      Kay Gürtzig     2015.11.05      Enhancement allowing to adopt edited values from Control (KGU#68)
 *      Kay Gürtzig     2015.11.14      New controls to display the call level for enhancement #9 (KGU#2)
 *      Kay Gürtzig     2016.03.06      New checkboxes for test coverage tracking mode
 *
 ******************************************************************************************************
 *
 *      Comment:  /
 *         
 ******************************************************************************************************///



import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import lu.fisch.structorizer.elements.Call;
import lu.fisch.structorizer.elements.Element;
import lu.fisch.structorizer.gui.IconLoader;


/**
 *
 * @author robertfisch
 */
public class Control extends javax.swing.JFrame implements PropertyChangeListener {

    /** Creates new form Control */
    public Control() {
        initComponents();
        this.setDefaultCloseOperation(Control.DO_NOTHING_ON_CLOSE);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

    	// START KGU#89 2015-11-25
    	this.setIconImage(IconLoader.ico004.getImage());
    	// END KGU#89 2015-11-25
        slSpeed = new javax.swing.JSlider();
        lblSpeed = new javax.swing.JLabel();
        // START KGU#89 2015-11-25
        lblSpeedValue = new javax.swing.JLabel();
        // END KGU#89 2015-11-25
        // START KGU#117 2016-03-06: Enh. #77 - Checkbox fpr Test coverage mode
        chkTestCoverage = new javax.swing.JCheckBox("Track Test Coverage");
        chkTestRecursive = new javax.swing.JCheckBox("Subroutine Coverage");
        // END KGU#117 2016-03-06
        btnStop = new javax.swing.JButton();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnStep = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVar = new javax.swing.JTable();
        // START KGU#2 (#9) 2015-11-14: Additional display of subroutine call level
        lblCallLevel = new javax.swing.JLabel(" Subroutine level:");
        txtCallLevel = new javax.swing.JTextField("0");
        txtCallLevel.setEditable(false);
        // END KGU#2 (#9) 2015-11-14

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        slSpeed.setMajorTickSpacing(100);
        slSpeed.setMaximum(2000);
        slSpeed.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                slSpeedMouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                slSpeedMouseDragged(evt);
            }
        });
        slSpeed.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                slSpeedPropertyChange(evt);
            }
        });
        slSpeed.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                slSpeedCaretPositionChanged(evt);
            }
        });
        slSpeed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                slSpeedMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                slSpeedMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                slSpeedMouseExited(evt);
            }
        });
        slSpeed.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                slSpeedMouseWheelMoved(evt);
            }
        });
        tblVar.addPropertyChangeListener(this);

        lblSpeed.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        // START KGU#89 2015-11-25
        //lblSpeed.setText(" Delay: 50");
        lblSpeed.setText(" Delay: ");
        lblSpeedValue.setText("50");
        // END KGU#89 2015-11-25
        
        // START KGU#117 2016-03-06: Enh. #77 Track test coverage mode change
        chkTestCoverage.addPropertyChangeListener(this);
        chkTestRecursive.addPropertyChangeListener(this);
        // END KGU#117 2016-03-06

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/structorizer/executor/stop.png"))); // NOI18N
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/structorizer/executor/play.png"))); // NOI18N
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });

        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/structorizer/executor/pause.png"))); // NOI18N
        btnPause.setEnabled(false);
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        btnStep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/structorizer/executor/next.png"))); // NOI18N
        btnStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepActionPerformed(evt);
            }
        });

        tblVar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Content"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblVar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                		// START KGU#89 2015-11-25: Speed label decomposed
                    .add(layout.createSequentialGroup()
                    	.add(lblSpeed /*, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE*/)
                    	.add(lblSpeedValue/*, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE*/))
                    	// END KGU#89 2015-11-25
                    	// START KGU#117 2016-03-06: Enh. #77
                    .add(layout.createSequentialGroup().add(chkTestCoverage))
                    	// END KGU#117 2016-03-06
                    .add(layout.createSequentialGroup()
                        .add(btnStop)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnPlay))
                // START KGU#2 (#9) 2015-11-14
                        .add(lblCallLevel))
                // END KGU#2 (#9) 2015-11-14
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    	// START KGU#117 2016-03-06: Enh. #77
                    .add(layout.createSequentialGroup().add(chkTestRecursive))
                    	// END KGU#117 2016-03-06
                    .add(layout.createSequentialGroup()
                        .add(btnPause)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnStep))
                // START KGU#2 (#9) 2015-11-14
                		.add(txtCallLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                // END KGU#2 (#9) 2015-11-14
// START KGU 2015-10-12: preferred size enhanced from 83 to 120
                    //.add(slSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(slSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
// END KGU 2015-0-12
                .add(jScrollPane1, 0, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(lblSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                		// START KGU#89 2015-11-25: New, separate value label
                    .add(lblSpeedValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                		// END KGU#89 2015-11-25
                    .add(slSpeed, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE)
                    .add(chkTestCoverage)
                    .add(chkTestRecursive))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnStop)
                    .add(btnPlay)
                    .add(btnPause)
                    .add(btnStep))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                // START KGU#2 (#9) 2015-11-14
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                		.add(lblCallLevel)
                		.add(txtCallLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                // END KGU#2 (#9) 2015-11-14
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void init()
    {
        btnStop.setEnabled(true);
        btnPlay.setEnabled(true);
        btnPause.setEnabled(false);
        btnStep.setEnabled(true);
        // START KGU#117 2016-03-06: Enh. #77
        chkTestCoverage.setEnabled(true);
        // END KGU#117 2016-03-06
        // empty table
        DefaultTableModel tm = (DefaultTableModel) tblVar.getModel();
        while(tm.getRowCount()>0) tm.removeRow(0);
    }

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnStopActionPerformed
    {//GEN-HEADEREND:event_btnStopActionPerformed
        Executor.getInstance().setStop(true);
        // START KGU#117 2016-03-06: Enh. #77
        chkTestCoverage.setEnabled(true);
        chkTestRecursive.setEnabled(chkTestCoverage.isSelected());
        // END KGU#117 2016-03-06
        this.setVisible(false);
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPlayActionPerformed
    {//GEN-HEADEREND:event_btnPlayActionPerformed
        btnPause.setEnabled(true);
        btnPlay.setEnabled(false);
        btnStep.setEnabled(false);
        // START KGU#117 2016-03-06: Enh. #77
        chkTestCoverage.setEnabled(false);
        chkTestRecursive.setEnabled(false);
        // END KGU#117 2016-03-06
        // START KGU#68 205-11-06: Enhancement - update edited values
    	if (varsUpdated)
    	{
    		Executor.getInstance().adoptVarChanges(varUpdates);
    	}
    	varsUpdated = false;
    	// END KGU#68 2015-11-06
        if(Executor.getInstance().isRunning()==false)
        {
            Executor.getInstance().start(false);
        }
        else
        {
            Executor.getInstance().setPaus(false);
        }
    }//GEN-LAST:event_btnPlayActionPerformed
    
    // START KGU 2015-10-12: Must be possible on breakpoints
    public void setButtonsForPause()
    {
        btnPause.setEnabled(false);
        btnPlay.setEnabled(true);
        btnStep.setEnabled(true);    	
    }
    // END KGU 2015-10-12

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPauseActionPerformed
    {//GEN-HEADEREND:event_btnPauseActionPerformed
    	// START KGU 2015-10-12
//        btnPause.setEnabled(false);
//        btnPlay.setEnabled(true);
//        btnStep.setEnabled(true);
    	setButtonsForPause();
    	// END KGU 2015-10-12
        Executor.getInstance().setPaus(!Executor.getInstance().getPaus());
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnStepActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnStepActionPerformed
    {//GEN-HEADEREND:event_btnStepActionPerformed
    	// START KGU#68 2015-11-06: Enhancement - update edited values
    	if (varsUpdated)
    	{
    		Executor.getInstance().adoptVarChanges(varUpdates);
    		varsUpdated = false;
    	}
    	// END KGU#68 2015-11-06
        // START KGU#117 2016-03-06: Enh. #77
        chkTestCoverage.setEnabled(false);
        chkTestRecursive.setEnabled(false);
        // END KGU#117 2016-03-06
        if(Executor.getInstance().isRunning()==false)
        {
            Executor.getInstance().start(true);
        }
        else
        {
            Executor.getInstance().doStep();
        }
    }//GEN-LAST:event_btnStepActionPerformed

    private void updateSpeed()
    {
        if(Executor.getInstance()!=null)
        {
            Executor.getInstance().setDelay(slSpeed.getValue());
        }
        // START KGU#89 2015-11-25
        //lblSpeed.setText(" Delay: "+slSpeed.getValue());
        lblSpeedValue.setText("" + slSpeed.getValue());
        // END KGU#89 2015-11-25
    }

    private void slSpeedCaretPositionChanged(java.awt.event.InputMethodEvent evt)//GEN-FIRST:event_slSpeedCaretPositionChanged
    {//GEN-HEADEREND:event_slSpeedCaretPositionChanged
        updateSpeed();
    }//GEN-LAST:event_slSpeedCaretPositionChanged

    private void slSpeedPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_slSpeedPropertyChange
    {//GEN-HEADEREND:event_slSpeedPropertyChange
        updateSpeed();
    }//GEN-LAST:event_slSpeedPropertyChange

    private void slSpeedMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_slSpeedMouseClicked
    {//GEN-HEADEREND:event_slSpeedMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_slSpeedMouseClicked

    private void slSpeedMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_slSpeedMouseExited
    {//GEN-HEADEREND:event_slSpeedMouseExited
        updateSpeed();
    }//GEN-LAST:event_slSpeedMouseExited

    private void slSpeedMouseMoved(java.awt.event.MouseEvent evt)//GEN-FIRST:event_slSpeedMouseMoved
    {//GEN-HEADEREND:event_slSpeedMouseMoved
        updateSpeed();
    }//GEN-LAST:event_slSpeedMouseMoved

    private void slSpeedMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_slSpeedMouseDragged
    {//GEN-HEADEREND:event_slSpeedMouseDragged
        updateSpeed();
    }//GEN-LAST:event_slSpeedMouseDragged

    private void slSpeedMouseWheelMoved(java.awt.event.MouseWheelEvent evt)//GEN-FIRST:event_slSpeedMouseWheelMoved
    {//GEN-HEADEREND:event_slSpeedMouseWheelMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_slSpeedMouseWheelMoved

    private void slSpeedMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_slSpeedMouseReleased
    {//GEN-HEADEREND:event_slSpeedMouseReleased
        updateSpeed();
    }//GEN-LAST:event_slSpeedMouseReleased

    public void updateVars(Vector<Vector> vars)
    {
    	// START KGU#68 2015-11-06: We want to keep track of changed variables
    	this.varsUpdated = false;
    	// END KGU#68 2015-11-06
        tblVar.setGridColor(Color.LIGHT_GRAY);
        tblVar.setShowGrid(true);
        DefaultTableModel tm = (DefaultTableModel) tblVar.getModel();
        // emtpy table
        while(tm.getRowCount()>0) tm.removeRow(0);
        // START KGU#68 2015-11-06: Preparation for variable editing
        //for(int i=0;i<vars.size();i++) tm.addRow(vars.get(i));
        varUpdates = new Object[vars.size()];
        for(int i=0; i<vars.size(); i++)
        {
        	tm.addRow(vars.get(i));
        	varUpdates[i] = null;
        }
        // END KGU#68 2015-11-06
    }
    
    // START KGU#2 (#9) 2015-11-14: Update method for subroutine level display
    public void updateCallLevel(int level)
    {
    	this.txtCallLevel.setText(Integer.toString(level));
    }
    // END KGU#2 (#9) 2015-11-14

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnStep;
    private javax.swing.JButton btnStop;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JLabel lblSpeed;
    // START KGU#89 2015-11-25: Is to ease localization (separate text and value)
    private javax.swing.JLabel lblSpeedValue;
    // END KGU#49 2015-11-25
    private javax.swing.JSlider slSpeed;
    // START KGU#117 2016-03-06: Enh. #77 - Checkbox fpr Test coverage mode
    public javax.swing.JCheckBox chkTestCoverage;
    public javax.swing.JCheckBox chkTestRecursive;
    // END KGU#117 2016-03-06
    private javax.swing.JTable tblVar;
    // End of variables declaration//GEN-END:variables
    // START KGU#2 (#9) 2015-11-14: Additional display of subroutine call level
    public javax.swing.JLabel lblCallLevel;
    private javax.swing.JTextField txtCallLevel;
    // END KGU#2 (#9) 2015-11-14
    
    // START KGU#68 2015-11-06: Register variable value editing events
    private Object[] varUpdates = null;
    private boolean varsUpdated = false;

    @Override
	public void propertyChange(PropertyChangeEvent pcEv) {
		// Check if it was triggered by the termination of some editing activity (i.e. the cell editor was dropped)
    	if (pcEv.getSource() == this.tblVar && pcEv.getPropertyName().equals("tableCellEditor") && pcEv.getNewValue() == null)
    	{
    		int rowNr = tblVar.getSelectedRow();
            DefaultTableModel tm = (DefaultTableModel) tblVar.getModel();
            Object val = tm.getValueAt(rowNr, 1);
            if (val != null)
            {
            	varsUpdated = true;
            	varUpdates[rowNr] = val;
            	//System.out.println(tm.getValueAt(rowNr, 0).toString() + " <- " + val.toString());
            }
    	}
    	// START KGU#117 2016-03-06: Enh. #77
    	else if (pcEv.getSource() == this.chkTestCoverage)
    	{
    		boolean isSelected = this.chkTestCoverage.isSelected();    		
    		Element.E_TESTCOVERAGEMODE = isSelected;
    		this.chkTestRecursive.setEnabled(isSelected && this.chkTestCoverage.isEnabled());
    	}
    	else if (pcEv.getSource() == this.chkTestRecursive)
    	{
    		Call.E_TESTCOVERAGERECURSIVE = this.chkTestRecursive.isSelected();
    	}
    	// END KGU#117 2016-03-06
		
	}
    // END KGU#68 2015-11-06

}
