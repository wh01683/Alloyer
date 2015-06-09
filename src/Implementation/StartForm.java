package Implementation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

/**
 * Created by Lindsey on 6/8/2015.
 */
public class StartForm extends JFrame
{
    private JPanel panel1;
    private JButton btnLoad;
    private JButton saveButton;
    private JButton btnSignatures;
    private JButton btnPredicates;
    private JTextField txtFileName;
    private JLabel lblTitle;
    private JButton btnCreateNew;
    private MasterDomain domain;

    public StartForm () {

        this.setContentPane(panel1);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();

        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    domain = MasterDomain.load((txtFileName.getText() == "") ? null : txtFileName.getText());
                    btnPredicates.setVisible(true);
                    btnSignatures.setVisible(true);
                } catch (FileNotFoundException f) {
                    JOptionPane.showMessageDialog(panel1, "File not found. Please check your spelling.");
                    txtFileName.grabFocus();
                }
            }
        });

        btnSignatures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (domain == null) {
                    JOptionPane.showMessageDialog(panel1, "The current domain is empty. You should create a new one or load one from file.");
                } else {
                    SignatureCreation sig = new SignatureCreation(domain);
                }
            }
        });

        btnPredicates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(domain == null){
                    JOptionPane.showMessageDialog(panel1, "The current domain is empty. You should create a new one or load one from file.");
                }else{
                PredicateCreation preds = new PredicateCreation(domain);
            }}
        });

        btnCreateNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignatureCreation signatureCreation = new SignatureCreation(new MasterDomain());
            }
        });

    }
}
