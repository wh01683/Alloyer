package Implementation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Lindsey on 5/26/2015.
 */
public class Form1
{
    private JPanel panel1;
    private JButton createSignatureButton;
    private JTextField labelSig;
    private JRadioButton primaryRadioButton;
    private JRadioButton subsetRadioButton;
    private JCheckBox abstractCheckBox;
    private JButton createSigButton;
    private JComboBox comboBox1;


    public Form1()
    {


        ActionListener listener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                e
            }
        };
        subsetRadioButton.addActionListener(listener);
        subsetRadioButton.setActionCommand()
        primaryRadioButton.addActionListener(listener);
    }
}


