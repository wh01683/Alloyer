package Implementation;

import javax.swing.*;

/**
 * Created by Lindsey on 6/8/2015.
 */
public class PredicateCreation
{
    private JPanel mainPanel;
    private JTextField txtPredName;
    private JLabel lblPredTitle;
    private JLabel lblPredName;
    private JTextField txtParamName;
    private JComboBox comboBox1;
    private JButton btnAdd;
    private JList lstParams;
    private JButton btnDelete;
    private JLabel lblParam;
    private JLabel lblRequired;
    private JTable tblPreds;
    private JButton btnSaveContinue;
    private MasterDomain masterDomain;

    public PredicateCreation (MasterDomain domain){
        this.masterDomain = domain;
    }
}
