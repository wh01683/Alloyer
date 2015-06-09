package Implementation;

import javax.swing.*;

/**
 * Created by Lindsey on 6/8/2015.
 */
public class PredicateCreation extends JFrame
{
    private JPanel mainPanel;
    private JTextField txtPredName;
    private JLabel lblTitle;
    private JLabel lblPredName;
    private JTextField textField1;
    private JComboBox comboBox1;
    private JButton addButton;
    private JList list1;
    private JButton deleteButton;
    private MasterDomain masterDomain;

    public PredicateCreation (MasterDomain domain){
        this.masterDomain = domain;
    }
}
