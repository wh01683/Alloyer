package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import javafx.scene.input.KeyCodeCombination;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Lindsey on 5/26/2015.
 */
public class Form1 {
    private JPanel panel1;
    private JButton createSignatureButton;
    private JTextField txtSigName;
    private JRadioButton primaryRadioButton;
    private JRadioButton subsetRadioButton;
    private JCheckBox abstractCheckBox;
    private JButton createSigButton;
    private JComboBox cmbMultiplicity;
    private JButton btnCreateSig;
    private JTable table1;
    private JList list1;
    MasterDomain domain = new MasterDomain();
    Object[] sigData;


    public Form1() {

    //Create Sig Button
        createSigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String sigName = txtSigName.getText();
                String type;

                boolean abstr;

                String multiplicity = cmbMultiplicity.getSelectedItem().toString();
                Attr multiplicityAttr = Attr.ONE;//multiplicity is one by default

                int numParents = 0;

                int numChildren = 0;

                //handles the abstract checkbox
                if (abstractCheckBox.isSelected())
                {
                    abstr = true;
                }
                else
                {
                    abstr = false;
                }

                //handles the multiplicity ComboBox
                if(multiplicity .equals("One"))
                {
                    multiplicityAttr = Attr.ONE;
                }
                else if(multiplicity.equals("Lone"))
                {
                    multiplicityAttr = Attr.LONE;
                }
                else if(multiplicity.equals("Some"))
                {
                    multiplicityAttr = Attr.SOME;
                }

                if(primaryRadioButton.isSelected()) {

                    type ="Primary";
                    try {
                        domain.addPrimSig(sigName,abstr, multiplicityAttr);
                    } catch (Err err) {
                        err.printStackTrace();
                    }
                }
                else
                {
                    type = "Subset";
                    domain.addSubsetSig(sigName,, multiplicityAttr);
                }


            }
        });


        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        String[] ColumnHeaders = {"NAME", "TYPE", "MULTIPLICITY", "#PARENTS", "#CHILDREN"};
        for (String s : ColumnHeaders)
        {
            model.addColumn(s);
        }




    }

}


