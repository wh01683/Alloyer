package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

import javax.swing.*;
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
        createSigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createSig();
            }
        });



    }


    private Sig createSig(){

        String multiplicitySelection = String.valueOf(comboBox1.getSelectedItem());
        try {
            if (primaryRadioButton.isSelected()) {
                Sig sig = new Sig.PrimSig(labelSig.getText().toString(),
                        getMultiAttr(multiplicitySelection),
                        subsetRadioButton.isSelected() ? Attr.SUBSET : null,
                        abstractCheckBox.isSelected() ? Attr.ABSTRACT : null);
                return sig;

            }else{
                //ToDo: implement subset sig creation
                return null;
            }
        }catch (Err e){
            e.printStackTrace();
        }
        return null;
    }

    private Attr getMultiAttr(String selection){
        if(selection.equalsIgnoreCase("LONE")){
            return Attr.LONE;
        }
        else if(selection.equalsIgnoreCase("ONE")){
            return Attr.ONE;
        }
        else if (selection.equalsIgnoreCase("SOME")){
            return Attr.SOME;
        }
        else{
            return null;
        }
    }
}


