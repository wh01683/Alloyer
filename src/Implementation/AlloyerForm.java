package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.SafeList;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizGUI;
import kodkod.engine.Solution;

import javax.swing.*;
import java.awt.*;
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
    private JButton btnSigInfo;
    private JComboBox cmbRelateWatt1;
    private JComboBox cmbSigRelate2;
    private JComboBox cmbRelateWatt2;
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
    private JScrollPane scrlSolution;


    DefaultListModel lstSigValuesModel;
    DefaultListModel lstRelationshipsModel;
    SafeList<Sig> sigsFromMeta;
    Hashtable<String, Sig> sigsDict;
    List<String> availableSigs;
    Command cmd;
    static A4Solution solution;
    static VizGUI currentModelForm;


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

        //Text and Graph checkboxes
        cbTextSolution.addActionListener(ae -> cbSelected(cbTextSolution, cbGraphSolution));
        cbGraphSolution.addActionListener(ae -> cbSelected(cbGraphSolution, cbGraphSolution));

        //Run Button
        btnRun.addActionListener(ae -> runCommand());

       //Next Button
        btnNext.addActionListener(ae -> showNextSolution());

        //SigInfo Button
        btnSigInfo.addActionListener(ae -> showSigInfo());
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
            cmbSigPref.addItem("");
            for(Sig s : sigs)
            {
                sigLabel = s.label.replace("this/", "");
                if(!sigLabel.equals("Grid")) //TODO: remove circuit from cmb
                {
                    cmbSigPref.addItem(sigLabel);
                    availableSigs.add(sigLabel);
                    sigsDict.put(sigLabel, s);
                }
            }
        }
        cmbSigPref.updateUI();
    }

    public void updateSigCombo(List<String> sigs)
    {
        if(sigs != null)
        {
            cmbSigPref.removeAllItems();
            cmbSigPref.addItem("");
            for(String s : sigs)
            {
                cmbSigPref.addItem(s);
            }
        }
        cmbSigPref.updateUI();
    }

    public void addToList(JButton button)
    {
        if(button == btnAddPref)
        {
            String sig = (String) cmbSigPref.getSelectedItem();
            String val = (String) cmbPrefVal.getSelectedItem();
            boolean boolExact = cbExact.isSelected();
            String exact;

            if (boolExact) exact = "exact";
            else exact = "";

            if (!sig.equals("") && !val.equals(""))
            {
                int amt = Integer.parseInt(val);

                int removeIndex = -1;
                lstSigValuesModel.addElement(sig + "  " + amt + "  " + exact);

                int i = 0;
                for (String s : availableSigs)
                {
                    if (s.equals(sig)) removeIndex = i;
                    i++;
                }
                if (removeIndex >= 0) availableSigs.remove(removeIndex);
            }
            else JOptionPane.showMessageDialog(mainPanel, "Please choose a signature and value");

            updateSigCombo(availableSigs);
            cmbPrefVal.setSelectedIndex(0);
            cbExact.setSelected(false);
        }
        else if(button == btnAddRel)
        {
            //TODO
        }
    }

    public void deleteFromList()
    {
        String selectedLine;
        String[] line;
        //if(getFocusOwner() == lstSigValues){
            selectedLine = lstSigValues.getSelectedValue().toString();
            line = selectedLine.split("  ");
            String selectedSig = line[0];
            availableSigs.add(selectedSig);
            updateSigCombo(availableSigs);
            lstSigValuesModel.remove(lstSigValues.getSelectedIndex());
        /*}
        else if(getFocusOwner() == lstRelationships)
        {
            selectedLine = lstRelationships.getSelectedValue().toString();
            line = selectedLine.split("");

        }*/
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
        if(!cmbCircuits.getSelectedItem().equals(""))
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
            populateTuples();
            frame.pack();


        }
        else
        {
            JOptionPane.showMessageDialog(mainPanel, "Please select number of circuits");
        }
    }

    public void populateTuples()
    {
        //TODO
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

    public void showSigInfo()
    {
        //String sigInfo = GridMetamodel.getSigInformation(solution, );
        //JOptionPane.showMessageDialog(mainPanel, sigInfo);
    }

}
