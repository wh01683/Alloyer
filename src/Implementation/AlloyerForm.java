package Implementation;

import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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


    DefaultComboBoxModel cmbSigModel;
    DefaultListModel lstSigValuesModel;


    public AlloyerForm()
    {

        this.setContentPane(panel1);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();

        List<Sig> sigs = GridMetamodel.getSigs();
        if(sigs != null)
        {
            for(Sig s : sigs)
            {
                cmbSig.addItem(s.label);
            }
        }


        lstSigValues.setModel(new DefaultListModel());
        lstSigValuesModel = (DefaultListModel)lstSigValues.getModel();

        btnAdd.addActionListener(ae ->
        {
            String sig = (String)cmbSig.getSelectedItem();
            String amount = txtValue.getText();
            try
            {
                int amt = Integer.parseInt(amount);
                if(amt >= -128 && amt <= 127)
                {
                    lstSigValuesModel.addElement(sig + " " + amt);
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

        btnRun.addActionListener(ae ->
        {

        });

        this.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_DELETE)
                {

                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {

            }
        });
    }
}
