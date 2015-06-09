package Implementation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Lindsey on 6/8/2015.
 */
public class PredicateCreation extends JFrame
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
    private JLabel lblAllParams;
    private JButton btnSave;
    private JButton btnSignatureCreation;
    private MasterDomain mdomain;

    public PredicateCreation (MasterDomain domain){
        this.mdomain = domain;

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mdomain.saveDomain(null);
            }
        });

        btnSignatureCreation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignatureCreation signatureCreation = new SignatureCreation(mdomain);
            }
        });

        btnSaveContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mdomain.saveDomain(null);

            }
        });
    }


}
