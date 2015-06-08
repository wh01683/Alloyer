package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Lindsey on 5/26/2015.
 */
public class Form1
{
    private JTextField txtSigName;
    private JRadioButton primaryRadioButton;
    private JRadioButton subsetRadioButton;
    private JCheckBox abstractCheckBox;
    private JComboBox cmbMultiplicity;
    private JButton btnCreateSig;
    private JTable tblSignatures;
    private JList lstChildren;
    private JTextArea lblChooseParent;
    public JPanel mainPanel;
    public JPanel creatorPanel;
    public JPanel allSigsPanel;
    public JPanel childrenPanel;
    private JButton btnShowChildren;

    MasterDomain domain;
    JFrame frame;
    DefaultTableModel tableModel;
    DefaultListModel listModel;
    //ArrayList<String[]> sigData;
    //int[] selectedSigs;
    Sig newSig;



    public Form1(MasterDomain mdomain)
    {
        domain = mdomain;

        //Creation of GUI
        frame = new JFrame();
        mainPanel = new JPanel();
        mainPanel.setVisible(true);
        frame.add(mainPanel);
        frame.setContentPane(mainPanel);
        mainPanel.add(creatorPanel);
        mainPanel.add(allSigsPanel);
        mainPanel.add(childrenPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        //tableModel for tblSignatures
        tableModel = (DefaultTableModel) tblSignatures.getModel();

        //listMolde for lstChildren
        listModel = (DefaultListModel) lstChildren.getModel();

        //Adds columns to table
        String[] ColumnHeaders = {"NAME", "TYPE", "ABSTRACT", "MULTIPLICITY", "PARENTS", "ID"};
        for (String s : ColumnHeaders)
        {
            tableModel.addColumn(s);//adds the columns to the table
        }

        //disables reordering and cell selection; enables row selection
        tblSignatures.getTableHeader().setReorderingAllowed(false);
        tblSignatures.setCellSelectionEnabled(false);
        tblSignatures.setRowSelectionAllowed(true);


////////Action Listener for primaryRadioButton
        primaryRadioButton.addActionListener(new ActionListener()
        {
            //occurs when an action is performed on primaryRadioButton
            public void actionPerformed(ActionEvent actionEvent)
            {
                //If primaryRadio button is selected, the abstractCheckBox is enabled
                if (primaryRadioButton.isSelected())
                {
                    abstractCheckBox.setEnabled(true);
                }
            }
        });

////////Action Listener for subsetRadioButton
        subsetRadioButton.addActionListener(new ActionListener()
        {
            //occurs when an action is performed on subsetRadioButton
            public void actionPerformed(ActionEvent actionEvent)
            {
                //If the subsetRadioButton is selected, deselect and disable abstractCheckBox
                if (subsetRadioButton.isSelected())
                {
                    abstractCheckBox.setSelected(false);
                    abstractCheckBox.setEnabled(false);
                }
            }
        });

////////Action Listener for btnCreateSig
        btnCreateSig.addActionListener(new ActionListener()
        {
            //occurs when btnCreateSig is pressed
            public void actionPerformed(ActionEvent actionEvent)
            {

                String sigLabel = txtSigName.getText();
                boolean abstr;
                String multiplicity = cmbMultiplicity.getSelectedItem().toString();
                Attr multiplicityAttr = Attr.ONE;

                //uses abstractCheckBox to determine whether sig is abstract
                if (abstractCheckBox.isSelected())
                {
                    abstr = true;
                } else
                {
                    abstr = false;
                }

                //uses multiplicity combo box result to set multiplicity attribute
                if (multiplicity.equals("One"))
                {
                    multiplicityAttr = Attr.ONE;
                } else if (multiplicity.equals("Lone"))
                {
                    multiplicityAttr = Attr.LONE;
                } else if (multiplicity.equals("Some"))
                {
                    multiplicityAttr = Attr.SOME;
                }

                ////////Sig is Primary
                if (primaryRadioButton.isSelected())
                {
                    //Check to see if there is another top level primary present
                    boolean topLevelPresent = false;
                    for (int row = 0; row < tableModel.getRowCount(); row++)
                    {
                        String s = (String) tableModel.getValueAt(row, 2);
                        if (s.equals("Top-level Primary"))
                        {
                            topLevelPresent = true;
                        }
                    }

                    try
                    {
                        //if no parents are selected, and no other top level primary is present, a top level primary sig is created
                        if (tblSignatures.getSelectedRowCount() == 0 && topLevelPresent == false)
                        {

                            newSig = domain.addPrimSig(sigLabel, abstr, multiplicityAttr);
                            addEntry();


                        }
                        //if one parent is selected, a subsig(primsig that is child of another primsig) is created
                        else if (tblSignatures.getSelectedRowCount() == 1)
                        {
                            Integer[] parentID = updateSelectedSigs();//gets the "ID" aka hashcode of the parent sig
                            if(domain.getSigFromHash(parentID[0]).isSubset == null){

                                newSig = domain.addChildSig(sigLabel, (Sig.PrimSig) domain.getSigFromHash(parentID[0]), abstr, multiplicityAttr);
                                addEntry();
                            }else{
                                JOptionPane.showMessageDialog(frame, "A Subset Signature may not be a parent");
                            }

                        }
                        //if more than one parent is selected, an error message is shown
                        else
                        {
                            JOptionPane.showMessageDialog(frame, "A maximum of one parent is permitted for a primary signature.");
                        }
                    }
                    catch (Err err)
                    {
                        err.printStackTrace();
                    }
                }

                ////////Sig is Subset
                else if (subsetRadioButton.isSelected())
                {
                    //subset cant be abstract so deselect and disable checkbox
                    abstractCheckBox.setSelected(false);
                    abstractCheckBox.setEnabled(false);

                    try
                    {
                        //if no parents are selected, an error message is shown.
                        if (tblSignatures.getSelectedRowCount() == 0)
                        {
                            JOptionPane.showMessageDialog(frame, "Please select one or more parents for a subset signature.");
                        } else
                        {
                            Integer[] parentIDs = updateSelectedSigs();
                            List<Sig> parentSigs = new ArrayList<Sig>();



                            //creates arraylist of parent sigs from the IDs
                            int i = 0;
                            for (Integer j : parentIDs)
                            {
                                parentSigs.add(i, domain.getSigFromHash(j));
                                i++;
                            }
                            if(!(containsSubsetSigs(parentSigs)))
                            {
                                newSig = domain.addSubsetSig(sigLabel, parentSigs, multiplicityAttr);
                                addEntry();

                            } else
                            {
                                JOptionPane.showMessageDialog(frame, "Parent signatures may not be subset signatures.");
                            }

                        }
                    }
                    catch (Err err)
                    {
                        err.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(frame, "Please select a signature type (Primary or Subset).");
                }
                //Add the entry to the table
            }
        });

////////Action Listener for btnShowChildren
        btnShowChildren.addActionListener(new ActionListener()
        {
            //occurs when btnShowChildren is pressed
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (tblSignatures.getSelectedRowCount() == 0)
                {
                    JOptionPane.showMessageDialog(frame, "Please select a signature from the table to view its children.");
                }
                else if (tblSignatures.getSelectedRowCount() != 1)
                {
                    JOptionPane.showMessageDialog(frame, "Please select only one signature from the table to view its children.");
                }
                else if (tblSignatures.getSelectedRowCount() == 1)
                {
                    Integer parentID = updateSelectedSigs()[0];
                    updateChildList(parentID);
                }
            }
        });
    }

    public void addEntry()
    {
        if (newSig != null)
        {
            String[] tableEntry = domain.tableEntryBuilder(newSig);
            tableModel.addRow(tableEntry);
            tblSignatures.updateUI();
        }
    }

    public void updateChildList(int parentHash)
    {
        //clears elements of the list model
        listModel.clear();

        String info = "";
        if(domain.getSigFromHash(parentHash).isSubset != null){
            listModel.addElement("Subset Signature - No Children");
            lstChildren.updateUI();
        }else {
            List<Sig> children = domain.getChildren((Sig.PrimSig) domain.getSigFromHash(parentHash));

            for (Sig c : children) {
                info += "Label: " + c.toString() + "\n";
                if (c.isSubset == null) {
                    for (Sig sig : domain.getChildren((Sig.PrimSig) c)) {
                        info += "\tChild: " + c.toString() + "\n";
                    }
                }
            }

            listModel.addElement(children);//adds child elements to the list model
            lstChildren.updateUI();//updates the table being displayed
        }
    }

    public Integer[] updateSelectedSigs (){


        if ((tblSignatures.getSelectedRowCount() > 0)){
            int[] selectedRows = tblSignatures.getSelectedRows();
            Integer[] ids = new Integer[selectedRows.length];
            for (int i = 0; i < ids.length; i++){
                ids[i] = Integer.parseInt(tableModel.getValueAt(selectedRows[i], 5).toString());
            }
            return ids;
        }
        else {
            return null; //do something if ids has stuff in it

        }
    }

    public boolean containsSubsetSigs(Collection<Sig> sigs){

        for (Sig s : sigs){
            if (s.isSubset != null){
                return true;
            }
        }
        return false;
    }



}



