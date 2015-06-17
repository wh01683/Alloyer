package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import java.util.List;

/**
 * Created by Lindsey on 6/11/2015.
 */
public class AlloyerForm extends JFrame
{

    private JPanel panel1;
    private JLabel Alloyer;
    private JButton btnRun;
    private JComboBox cmbSig;
    private JTextField txtValue;
    private JList lstSigValues;
    private JButton btnAdd;
    private JCheckBox exactCheckBox;
    private JLabel lblNumCircuits;
    private JComboBox cmbCircuits;

    DefaultListModel lstSigValuesModel;
    List<Sig> sigs;
    List<Sig> sigsList;
    Command cmd;

    public AlloyerForm()
    {
        this.setContentPane(panel1);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();

        sigs = null;
        sigsList = new ArrayList<>();

        //Try to run the main method of GridMetamodel class and get sigs
        try
        {
            GridMetamodel.setUp();
            sigs = GridMetamodel.getSigs();
        }
        catch(Exception err)
        {
            err.printStackTrace();
        }

        //Add sigs to the combo box
        if(sigs != null)
        {
            for(Sig s : sigs)
            {
                cmbSig.addItem(s.label);
                sigsList.add(s);
            }
        }
        cmbSig.updateUI();

        lstSigValues.setModel(new DefaultListModel());
        lstSigValuesModel = (DefaultListModel)lstSigValues.getModel();

        //Add button to add sigs and their respective values to list
        btnAdd.addActionListener(ae ->
        {
            String sig = (String)cmbSig.getSelectedItem();
            String amount = txtValue.getText();
            boolean boolExact = exactCheckBox.isSelected();
            String exact;

            if(boolExact == true)
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
                    lstSigValuesModel.addElement(sig + "  " + amt + "  " + exact);

                }
                else
                {
                    JOptionPane.showMessageDialog(panel1, "Please enter a valid number between -128 and 127");
                }
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(panel1, "Please enter a valid number between -128 and 127");
            }
            txtValue.setText("");
        });



        lstSigValues.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_DELETE)
                {
                    sigsList.remove(lstSigValues.getSelectedIndex());

                    lstSigValuesModel.remove(lstSigValues.getSelectedIndex());
                    lstSigValues.updateUI();
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {

            }
        });

        btnRun.addActionListener(ae ->
        {
            int circuits = Integer.parseInt((String) cmbCircuits.getSelectedItem());

            try
            {
               cmd = GridMetamodel.makeCommand(circuits);
            }
            catch (Err err)
            {
                err.printStackTrace();
            }


            String[] listContents = new String[lstSigValuesModel.size()];
            String listModel= lstSigValuesModel.toString();
            listModel = listModel.replace("[", "");
            listModel = listModel.replace("]", "");
            listContents = listModel.split(",");

            Sig sig = null;
            String amt = "";
            int amount = 0;
            boolean boolExact = false;

            int i = 0;
            for(String s : listContents)
            {
                String[] current = s.split("  ");
                amt = current[1];

                if(current.length == 3)
                {
                    boolExact = true;
                }
                sig = sigsList.get(i);
                amount = Integer.parseInt(amt);

                try
                {
                    cmd = GridMetamodel.changeCommand(cmd, sig, boolExact, amount);
                }
                catch (Err err)
                {
                    err.printStackTrace();
                }
                i++;
            }

            try
            {
                GridMetamodel.run(cmd);
            }
            catch (Err err)
            {
                err.printStackTrace();
            }

        });
    }




}
