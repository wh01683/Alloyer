package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

    MasterDomain domain;
    DefaultTableModel tableModel;
    ArrayList<String[]> sigData;
    int[] selectedSigs;



    public Form1(MasterDomain mdomain)
    {
        domain = mdomain;

        JFrame frame = new JFrame();
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

        tableModel = (DefaultTableModel)tblSignatures.getModel();

        //CreateSig Button
        btnCreateSig.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                String sigName = txtSigName.getText();
                String type;

                boolean abstr;

                String multiplicity = cmbMultiplicity.getSelectedItem().toString();
                Attr multiplicityAttr = Attr.ONE;//multiplicity is one by default

                int numParents = 0;

                int numChildren = 0;

                //handles the abstract checkbox
                if (abstractCheckBox.isSelected())
                {
                    abstr = true;
                } else
                {
                    abstr = false;
                }

                //handles the multiplicity ComboBox
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

                //PRIMARY RADIOBUTTON---
                if (primaryRadioButton.isSelected())
                {
                    type = "Primary";
                    try
                    {
                        //if no parents are selected, a top level primary sig is created
                        if(selectedSigs == null)
                        {
                            domain.addPrimSig(sigName, abstr, multiplicityAttr);
                        }
                        //if one parent is selected, a child primary sig is created
                        else if(selectedSigs.length == 1)
                        {
                            Integer parentID = (Integer) tableModel.getValueAt(selectedSigs[0], 5);//gets the "ID" aka hashcode of the parent sig
                            domain.addChildSig(sigName, (Sig.PrimSig)domain.getSigFromHash(parentID), abstr, multiplicityAttr);
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

                //SUBSET RADIOBUTTON---
                else if (subsetRadioButton.isSelected())
                {
                    try
                    {
                        type = "Subset";

                        //if no parents are selected, an error message is shown.
                        if (selectedSigs == null)
                        {
                            JOptionPane.showMessageDialog(frame,"Please select one or more parents for a subset signature.");
                        }

                        else
                        {
                            Integer[] parentIDs = new Integer[selectedSigs.length];
                            List<Sig> parentSigs = new ArrayList<Sig>();

                            //creates array of parent IDs from table
                            for (int i : selectedSigs)
                            {
                                parentIDs[i] = (Integer) tableModel.getValueAt(i, 5);
                            }
                            //creates arraylist of parent sigs from the IDs
                            for (Integer j : parentIDs)
                            {
                                parentSigs.add(j, domain.getSigFromHash(parentIDs[j]));
                            }

                            domain.addSubsetSig(sigName, parentSigs, multiplicityAttr);
                        }
                    }
                    catch(Err err)
                    {
                        err.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(frame, "Please select a signature type (Primary or Subset).");
                }

                updateTable();
            }
        });

        tblSignatures.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            //event listener for selected value changed
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting())
                {
                    selectedSigs = tblSignatures.getSelectedRows();//creates array of selected indices

                    if(selectedSigs.length == 1)
                    {
                        Integer parentID = (Integer)tableModel.getValueAt(selectedSigs[0],5);
                        updateChildList(parentID);
                    }
                }
            }
        });
    }

    public void updateTable()
    {
        //Clears table
        tblSignatures.selectAll();
        tblSignatures.clearSelection();

        //Adds columns
        String[] ColumnHeaders = {"NAME", "TYPE", "MULTIPLICITY", "#CHILDREN", "ID"};
        for (String s : ColumnHeaders)
        {
                tableModel.addColumn(s);//adds the columns to the table
        }

        sigData = domain.getTableEntries();

        for(String[] s : sigData)
        {
            tableModel.addRow(s);//adds the signatures to the table model
        }
        tblSignatures.updateUI();//updates the table being displayed
    }

    public void updateChildList(int parentHash)
    {
        DefaultListModel listModel = (DefaultListModel) lstChildren.getModel();

        listModel.clear();//clears elements of the list model
        String s = domain.getChildren(domain.getSigFromHash(parentHash));//gets the children of the selected parent
        String[] children = s.split(" ");
        for(String child : children)
        {
            listModel.addElement(child);//adds child elements to the list model
        }
        lstChildren.updateUI();//updates the table being displayed
    }






}



