package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.SafeList;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

/**
 * Created by Lindsey on 6/11/2015.
 */
public class AlloyerForm extends JFrame
{
    private static JFrame frame;
    private JTextField txtValue;
    private JButton btnRun;
    private JComboBox cmbSigPref;
    private JList lstSigValues;
    private JButton btnAddPref;
    private JCheckBox cbExact;
    private JComboBox cmbCircuits;
    private JPanel mainPanel;
    private JTextArea txtSolution;
    private JButton btnNext;
    private JCheckBox cbTextSolution;
    private JCheckBox cbGraphSolution;
    private JScrollPane scrlList;
    private JButton btnFindMatches;
    private JComboBox cmbSig1Watts;
    private JComboBox cmbSigRelate2;
    private JComboBox cmbSig2Watts;
    private JButton btnAddRel;
    private JComboBox cmbPrefVal;
    private JPanel pnlCircuits;
    private JPanel pnlPref;
    private JPanel pnlRelationships;
    private JPanel pnlRun;
    private JList lstRelationships;
    private JComboBox cmbSigRelate1;
    private JLabel lblWatts1;
    private JLabel lblArrow;
    private JLabel lblWatts2;
    private JPanel pnlNext;
    private JTextField txtName1;
    private JTextField txtName2;
    private JScrollPane scrlSolution;
    private ArrayList<String> relations = new ArrayList<>();

    DefaultListModel lstSigValuesModel;
    DefaultListModel lstRelationshipsModel;
    SafeList<Sig> sigsFromMeta;
    Hashtable<String, Sig> sigsDict;
    List<String> availableSigs;
    Command cmd;
    static A4Solution solution;
    static VizGUI currentModelForm;
    String sig1sig2Rel;


    public AlloyerForm()
    {
        this.setContentPane(mainPanel);
        this.getContentPane().setBackground(Color.gray);
        pnlCircuits.setBackground(Color.gray);
        pnlPref.setBackground(Color.gray);
        pnlRelationships.setBackground(Color.gray);
        pnlRelationships.setVisible(false);
        pnlRun.setBackground(Color.gray);
        pnlNext.setBackground(Color.gray);
        pnlNext.setVisible(false);

        GridMetamodel.setUp();
        sigsFromMeta = GridMetamodel.getSigs();
        sigsDict = new Hashtable<>();
        availableSigs = new ArrayList<>();
        loadSigCombo(sigsFromMeta);

        lstSigValues.setModel(new DefaultListModel());
        lstSigValuesModel = (DefaultListModel)lstSigValues.getModel();
        lstRelationships.setModel(new DefaultListModel());
        lstRelationshipsModel = (DefaultListModel)lstRelationships.getModel();

        //Add Preference Button
        btnAddPref.addActionListener(ae -> addToList(btnAddPref));

        //Delete Key
        lstSigValues.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) deleteFromList();
            }
            //other KeyEvents
            @Override
            public void keyTyped(KeyEvent e)
            {

            }
            @Override
            public void keyReleased(KeyEvent e)
            {

            }
        });

        //Add Relationship Button
        btnAddRel.addActionListener(ae->addToList(btnAddRel));

        //Combobox for relation sig1
        cmbSigRelate1.addItemListener(ie -> loadSig2Cmb(ie));

        //Text and Graph checkboxes
        cbTextSolution.addActionListener(ae -> cbSelected(cbTextSolution, cbGraphSolution));
        cbGraphSolution.addActionListener(ae -> cbSelected(cbGraphSolution, cbGraphSolution));

        //Run Button
        btnRun.addActionListener(ae -> runCommand());

       //Next Button
        btnNext.addActionListener(ae -> showNextSolution());

        //SigInfo Button
        btnFindMatches.addActionListener(ae -> findMatches());
    }

    public static void main(String[] args)
    {
        //Form Setup
        frame = new JFrame("Alloyer");
        //frame.setSize(240, frame.getHeight());
        frame.setContentPane(new AlloyerForm().getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
        frame.pack();
    }

    public void loadSigCombo(SafeList<Sig> sigs)
    {
        String sigLabel;
        if(sigs != null)
        {
            for(Sig s : sigs)
            {
                sigLabel = s.label.replace("this/", "");
                sigsDict.put(sigLabel, s);
                if(!sigLabel.equals("Grid") && !sigLabel.equals("Circuit"))
                {
                    cmbSigPref.addItem(sigLabel);
                    availableSigs.add(sigLabel);
                }
            }
        }
        cmbSigPref.setSelectedIndex(-1);
    }

    public void updateSigCombo(List<String> sigs)
    {
        if(sigs != null)
        {
            cmbSigPref.removeAllItems();
            for(String s : sigs)
            {
                cmbSigPref.addItem(s);
            }
        }
        cmbSigPref.setSelectedIndex(-1);
    }

    public void addToList(JButton button)
    {
        if(button == btnAddPref)
        {
            if(cmbSigPref.getSelectedIndex()>-1 && cmbPrefVal.getSelectedIndex() >-1)
            {
                String sig = (String) cmbSigPref.getSelectedItem();
                int val = Integer.parseInt((String) cmbPrefVal.getSelectedItem());
                boolean boolExact = cbExact.isSelected();
                String exact;

                if (boolExact) exact = "exact";
                else exact = "";

                int removeIndex = -1;
                lstSigValuesModel.addElement(sig + "  " + val + "  " + exact);

                int i = 0;
                for (String s : availableSigs)
                {
                    if (s.equals(sig)) removeIndex = i;
                    i++;
                }
                availableSigs.remove(removeIndex);
            }
            else JOptionPane.showMessageDialog(mainPanel, "Please choose a signature and value");

            updateSigCombo(availableSigs);
            cmbPrefVal.setSelectedIndex(-1);
            //cbExact.setSelected(false);
        }



        else if(button == btnAddRel)
        {
            String sig1 = (String)cmbSigRelate1.getSelectedItem();
            String sig2 = (String)cmbSigRelate2.getSelectedItem();
            String sig1Name = txtName1.getText();
            String sig2Name = txtName2.getText();
            int watts1 = cmbSig1Watts.getSelectedIndex();
            int watts2 = cmbSig2Watts.getSelectedIndex();
            String sig1WattsRel;
            String sig2WattsRel;

            //sig2 = sig2.replace(sig2.substring(0), (sig2.charAt(0) + "").toUpperCase());
            String l = sig2.substring(0,0).toUpperCase();
            sig2 = sig2.substring(1, sig2.length());
            relations.add(sig1 + "->" + l+sig2);

            if(watts1 > -1 && watts2 > -1)
            {
                lstRelationshipsModel.addElement(sig1Name + ", " + watts1 + " watts" + " \u2192 " + sig2Name + ", " + watts2 + " watts");

                sig1WattsRel = sig1 + "->" + watts1;
                sig2WattsRel = sig2 + "->" + watts2;

            }
            else if(watts1 == -1 && watts2 > -1)
            {
                lstRelationshipsModel.addElement(sig1Name + " \u2192 " + sig2Name + ", " + watts2 + " watts");
                sig1WattsRel = null;
                sig2WattsRel = sig2 + "->" + watts2;

            }
            else if(watts1 > -1 && watts2 == -1)
            {
                lstRelationshipsModel.addElement(sig1Name + ", " + watts1 + " watts" + " \u2192 " + sig2Name);
                sig1WattsRel = sig1 + "->"+ watts1;
                sig2WattsRel = null;
            }
            else
            {
                lstRelationshipsModel.addElement(sig1Name + " \u2192 " + sig2Name);
                sig1WattsRel = null;
                sig2WattsRel = null;
            }




            cmbSigRelate1.setSelectedIndex(-1);
            cmbSigRelate2.setSelectedIndex(-1);
            txtName1.setText("");
            txtName2.setText("");
            cmbSig1Watts.setSelectedIndex(-1);
            cmbSig2Watts.setSelectedIndex(-1);




        }
    }

    public void deleteFromList()
    {
        String selectedLine;
        String[] line;

        if(lstSigValues.getSelectedValue()!= null && lstRelationships.getSelectedValue() == null)
        {
            selectedLine = lstSigValues.getSelectedValue().toString();
            line = selectedLine.split("  ");
            String selectedSig = line[0];
            availableSigs.add(selectedSig);
            updateSigCombo(availableSigs);
            lstSigValuesModel.remove(lstSigValues.getSelectedIndex());
        }
        else if(lstRelationships.getSelectedValue() != null && lstSigValues.getSelectedValue()==null)
        {
            selectedLine = lstRelationships.getSelectedValue().toString();
            line = selectedLine.split(" ");
            // /TODO: Parse Line
            updateRelationCombos();
            lstRelationshipsModel.remove(lstRelationships.getSelectedIndex());
        }

        //TODO: make sure all possibilities covered

    }

    public void updateRelationCombos()
    {
        //TODO
    }


    public void cbSelected(JCheckBox cb, JCheckBox partner)
    {
        if(cb.isSelected() && !partner.isSelected()) btnRun.setEnabled(true);
        else if(!cb.isSelected() && partner.isSelected()) btnRun.setEnabled(true);
        else if(cb.isSelected() && partner.isSelected()) btnRun.setEnabled(true);
        else btnRun.setEnabled(false);
    }

    public void runCommand()
    {
        if(cmbCircuits.getSelectedIndex()>-1)
        {
            int numCircuits = Integer.parseInt((String) cmbCircuits.getSelectedItem());
            try
            {
                cmd = GridMetamodel.makeCommand(numCircuits);
            }
            catch (Err err)
            {
                err.printStackTrace();
            }
            if (!lstSigValuesModel.isEmpty())
            {
                String[] listContents;
                String listModel = lstSigValuesModel.toString();
                listModel = listModel.replace("[", "");
                listModel = listModel.replace("]", "");
                listContents = listModel.split(", ");

                boolean boolExact = false;

                for (String s : listContents)
                {
                    String[] current = s.split("  ");
                    String sigLabel = current[0];
                    String amt = current[1];

                    if (current.length == 3)
                    {
                        boolExact = true;
                    }
                    if (sigsDict.containsKey(sigLabel))
                    {
                        Sig sig = sigsDict.get(sigLabel);
                        int amount = Integer.parseInt(amt);
                        try
                        {
                            cmd = GridMetamodel.changeCommand(cmd, sig, boolExact, amount);
                        }
                        catch (Err err)
                        {
                            err.printStackTrace();
                        }
                    }
                }
            }

            try
            {
                solution = GridMetamodel.run(cmd);
                if (cbTextSolution.isSelected() && !cbGraphSolution.isSelected())
                {
                    System.out.print(solution.toString());
                }
                else if (!cbTextSolution.isSelected() && cbGraphSolution.isSelected())
                {
                    GridMetamodel.visualize(solution);
                }
                else if (cbTextSolution.isSelected() && cbGraphSolution.isSelected())
                {
                    System.out.print(solution.toString());
                    GridMetamodel.visualize(solution);
                }
            }
            catch (Err err)
            {
                err.printStackTrace();
            }

            pnlRelationships.setVisible(true);
            pnlNext.setVisible(true);
            try
            {
                loadRelationshipCmb();
            }
            catch(Err err) {}
            frame.pack();
        }
        else
        {
            JOptionPane.showMessageDialog(mainPanel, "Please select number of circuits");
        }
    }

    public void loadRelationshipCmb() throws Err
    {
        cmbSigRelate1.removeAllItems();
        cmbSigRelate2.removeAllItems();
        Object[] prefList = lstSigValuesModel.toArray();
        for(Object o : prefList)
        {
            String s = (String)o;
            String[] line = s.split("  ");
            String sigName = line[0];
            if(sigsDict.containsKey(sigName))
            {
                Sig.PrimSig sig = (Sig.PrimSig)sigsDict.get(sigName);
                if(sig.isAbstract == null)
                {
                    cmbSigRelate1.addItem(sig.toString().replace("this/", ""));

                }

                if (sig.isAbstract != null && sig.children() != null)
                {
                    for (Sig.Field f : sig.getFields())
                    {
                        if (!f.label.equals("watts"))
                        {
                            //cmbSigRelate2.addItem(f.label);
                        }
                    }
                    for (Sig child : sig.children())
                    {
                        cmbSigRelate1.addItem(child.toString().replace("this/", ""));
                        for (Sig.Field f : child.getFields())
                        {
                            if (!f.label.equals("watts"))
                            {
                                //cmbSigRelate2.addItem(f.label);
                            }
                        }
                    }

                }
            }

        }

    }

    public void loadSig2Cmb(ItemEvent ie)
    {
        if(ie.getStateChange() == ItemEvent.SELECTED)
        {
            cmbSigRelate2.removeAllItems();
            if(cmbSigRelate1.getSelectedIndex()>-1)
            {
                String strSig1 = (String)cmbSigRelate1.getSelectedItem();
                if(sigsDict.containsKey(strSig1))
                {
                    Sig.PrimSig sig1 = (Sig.PrimSig)sigsDict.get(strSig1);
                    if(sig1.getFields().size() > 0)
                    {
                        for (Sig.Field f : sig1.getFields())
                        {
                            if (!f.label.equals("watts"))
                            {
                                cmbSigRelate2.addItem(f.label);
                            }
                        }
                    }
                    else
                    {
                        Sig.PrimSig parent = sig1.parent;
                        for (Sig.Field f : parent.getFields())
                        {
                            if (!f.label.equals("watts"))
                            {
                                cmbSigRelate2.addItem(f.label);
                            }
                        }
                    }
                }

            }
        }
    }
    public void showNextSolution()
    {
        try
        {
            if(cbTextSolution.isSelected() && !cbGraphSolution.isSelected())
            {
                solution = GridMetamodel.getNext(solution);
                System.out.println(solution.toString());
            }
            else if(!cbTextSolution.isSelected() && cbGraphSolution.isSelected())
            {
                solution = GridMetamodel.visualizeNext(solution, currentModelForm);
            }
            else if(cbTextSolution.isSelected() && cbGraphSolution.isSelected())
            {
                solution = GridMetamodel.visualizeNext(solution, currentModelForm);
                System.out.println(solution.toString());
            }
            else
            {

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void findMatches()
    {
        String[] relationships = new String[1];
//        relationships = (String[])relations.toArray();

        relationships[0] = relations.get(0);

        A4Solution match = GridMetamodel.findSolution(solution, relationships, false, 85000);
        JOptionPane.showMessageDialog(frame,match);
    }

}
