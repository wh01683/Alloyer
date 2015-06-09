package Implementation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Lindsey on 6/8/2015.
 */
public class StartForm extends JFrame
{
    private JPanel panel1;
    private JButton loadButton;
    private JButton saveButton;
    private JButton signaturesButton;
    private JButton predicatesButton;
    private MasterDomain domain;

    public StartForm () {

        this.setContentPane(panel1);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: get file input from user
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (domain == null) {
                    JOptionPane.showMessageDialog(panel1, "The current domain is empty. You should create a new one or load one from file.");
                }
            }
        });

        signaturesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (domain == null) {
                    JOptionPane.showMessageDialog(panel1, "The current domain is empty. You should create a new one or load one from file.");
                }else{
                SignatureCreation sig = new SignatureCreation(domain);
            }}
        });

        predicatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(domain == null){
                    JOptionPane.showMessageDialog(panel1, "The current domain is empty. You should create a new one or load one from file.");
                }else{
                PredicateCreation preds = new PredicateCreation(domain);
            }}
        });
    }
}
