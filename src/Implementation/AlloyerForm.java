package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.SafeList;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

/**
 * Created by Lindsey on 6/11/2015.
 */
public class AlloyerForm extends JFrame
{

    private JPanel innerPanel;
    private JLabel lblTitle;
    private JButton btnRun;
    private JComboBox cmbSig;
    private JTextField txtValue;
    private JList lstSigValues;
    private JButton btnAdd;
    private JCheckBox exactCheckBox;
    private JLabel lblNumCircuits;
    private JComboBox cmbCircuits;
    private JPanel mainPanel;
    private JTextArea txtSolution;
    private JButton btnNext;
    private JRadioButton rbTextSolution;
    private JRadioButton rbGraphSolution;
    private JButton showGraphButton;

    DefaultListModel lstSigValuesModel;
    SafeList<Sig> sigsFromMeta;
    Hashtable<String, Sig> sigsDict;
    List<String> availableSigs;
    Command cmd;
    A4Solution solution;



    public AlloyerForm()
    {
        //Form Setup
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();
        this.setResizable(false);

        GridMetamodel.setUp();
        sigsFromMeta = GridMetamodel.getSigs();
        sigsDict = new Hashtable<>();
        availableSigs = new ArrayList<>();
        loadSigCombo(sigsFromMeta);
        lstSigValues.setModel(new DefaultListModel());
        lstSigValuesModel = (DefaultListModel)lstSigValues.getModel();



        //Add Button
        btnAdd.addActionListener(ae -> addToList());

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

        //Run Button
        btnRun.addActionListener(ae -> runCommand());

        //Next Button
        btnNext.addActionListener(ae -> showNextSolution());
    }

    public static void main(String[] args)
    {
        AlloyerForm af = new AlloyerForm();
    }

    public void loadSigCombo(SafeList<Sig> sigs)
    {
        String sigLabel;
        if(sigs != null)
        {
            cmbSig.addItem("");
            for(Sig s : sigs)
            {
                sigLabel = s.label.replace("this/", "");
                if(!sigLabel.equals("Grid") && !sigLabel.equals("Circuit"))
                {
                    cmbSig.addItem(sigLabel);
                    availableSigs.add(sigLabel);
                    sigsDict.put(sigLabel, s);
                }
            }
        }
        cmbSig.updateUI();
    }

    public void updateSigCombo(List<String> sigs)
    {
        if(sigs != null)
        {
            cmbSig.removeAllItems();
            cmbSig.addItem("");
            for(String s : sigs)
            {
                cmbSig.addItem(s);
            }
        }
        cmbSig.updateUI();
    }

    public void addToList()
    {
        String sig = (String)cmbSig.getSelectedItem();
        String amount = txtValue.getText();
        boolean boolExact = exactCheckBox.isSelected();
        String exact;

        if(boolExact)
        {
            exact = "exact";
        }
        else
        {
            exact = "";
        }

        try
        {
            int amt = Integer.parseInt(amount);
            if(amt >= -128 && amt <= 127)
            {
                int removeIndex = -1;
                lstSigValuesModel.addElement(sig + "  " + amt + "  " + exact);

                int i = 0;
                for(String s : availableSigs)
                {
                    if(s.equals(sig))
                    {
                        removeIndex = i;
                    }
                    i++;
                }
                if(removeIndex>=0)
                {
                    availableSigs.remove(removeIndex);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(mainPanel, "Please enter a valid number between -128 and 127");
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(mainPanel, "Please enter a valid number between -128 and 127");
        }
        txtValue.setText("");
        updateSigCombo(availableSigs);
        exactCheckBox.setSelected(false);
    }

    public void deleteFromList()
    {
        String selectedLine = lstSigValues.getSelectedValue().toString();
        String[] line = selectedLine.split("  ");
        String selectedSig = line[0];
        availableSigs.add(selectedSig);
        updateSigCombo(availableSigs);

        lstSigValuesModel.remove(lstSigValues.getSelectedIndex());
        lstSigValues.updateUI();
    }

    public void runCommand()
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

        if(!lstSigValuesModel.isEmpty())
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
            if(rbTextSolution.isSelected())
            {
                txtSolution.setText(solution.toString());
                rbTextSolution.setEnabled(false);
                rbGraphSolution.setEnabled(false);
            }
            else if(rbGraphSolution.isSelected())
            {
                GridMetamodel.visualize(solution);
                rbTextSolution.setEnabled(false);
                rbGraphSolution.setEnabled(false);
            }
        }
        catch (Err err)
        {
            err.printStackTrace();
        }
    }

    public void showNextSolution()
    {
        try
        {
            if(rbTextSolution.isSelected())
            {
                solution = GridMetamodel.getNext(solution);
                txtSolution.setText(solution.toString());
            }
            else if(rbGraphSolution.isSelected())
            {
                solution = GridMetamodel.visualizeNext(solution);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
