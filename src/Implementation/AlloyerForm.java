package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.SafeList;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by Lindsey on 6/11/2015.
 */
public class AlloyerForm extends JFrame
{
    //GUI Components
    private static JFrame frame;
    private JButton btnRun;
    private JComboBox cmbPreferenceSignature;
    private JList lstPreferences;
    private JButton btnAddPref;
    private JCheckBox cbExact;
    private JComboBox cmbCircuits;
    private JPanel mainPanel;
    private JButton btnFindMatches_Next;
    private JCheckBox cbTextSolution;
    private JCheckBox cbGraphSolution;
    private JScrollPane scrlList;
    private JButton btnFindMatches;
    private JComboBox cmbWatts1;
    private JComboBox cmbRelate2;
    private JComboBox cmbWatts2;
    private JButton btnAddRel;
    private JComboBox cmbPreferenceValue;
    private JPanel pnlCircuits;
    private JPanel pnlPref;
    private JPanel pnlRelationships;
    private JPanel pnlRun;
    private JList lstRelationships;
    private JComboBox cmbRelate1;
    private JLabel lblWatts1;
    private JLabel lblArrow;
    private JLabel lblWatts2;
    private JPanel pnlNext;
    private JTextField txtName1;
    private JTextField txtName2;
    private JPanel pnlBitwidth;
    private JComboBox cmbBitwidth;
    private JButton btnViewButton;
    private JScrollPane scrlSolution;
    private boolean hasBeenPressed;

    DefaultListModel lstSigValuesModel;
    DefaultListModel lstRelationshipsModel;
    int bitwidth;
    int maxInts;
    SafeList<Sig> sigsFromMeta;
    Hashtable<String, Sig> sigsDict;
    List<String> availableSigs;
    Command cmd;
    static A4Solution solution;
    static VizGUI currentModelForm;
    static Hashtable<String, Integer> occurrences = new Hashtable<>();
    static Hashtable<String, String> customNames = new Hashtable<>();
    static ArrayList<String> relationships = new ArrayList<>();
    static String[] relationshipArr;

    public AlloyerForm()
    {
        this.setContentPane(mainPanel);
        this.getContentPane().setBackground(Color.gray);
        pnlBitwidth.setBackground(Color.gray);
        pnlCircuits.setBackground(Color.gray);
        pnlPref.setBackground(Color.gray);
        pnlRelationships.setBackground(Color.gray);
        pnlRelationships.setVisible(false);
        pnlRun.setBackground(Color.gray);
        pnlNext.setBackground(Color.gray);
        pnlNext.setVisible(false);

        GridMetamodel.setUp();
        sigsFromMeta = GridMetamodel.getSigs();
        sigsDict = new Hashtable<>();
        availableSigs = new ArrayList<>();

        loadAvailableSigs(sigsFromMeta);

        lstPreferences.setModel(new DefaultListModel());
        lstSigValuesModel = (DefaultListModel) lstPreferences.getModel();
        lstRelationships.setModel(new DefaultListModel());
        lstRelationshipsModel = (DefaultListModel)lstRelationships.getModel();

        setBitwidth();

        //Bitwidth combobox
        cmbBitwidth.addItemListener(ie -> {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                setBitwidth();
            }
        });

        //Add Preference Button
        btnAddPref.addActionListener(ae -> addToList(btnAddPref));

        //Delete Key
        lstPreferences.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) deleteFromList();
            }

            //other KeyEvents
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        btnViewButton.addActionListener(ae->{
                GridMetamodel.visualize(solution);
            });

        //Add Relationship Button
        btnAddRel.addActionListener(ae->addToList(btnAddRel));

        //Combobox for relation sig1
        cmbRelate1.addItemListener(ie -> loadRelate2(ie));

        //Text and Graph checkboxes
        cbTextSolution.addActionListener(ae -> cbSelected(cbTextSolution, cbGraphSolution));
        cbGraphSolution.addActionListener(ae -> cbSelected(cbGraphSolution, cbGraphSolution));

        //Run Button
        btnRun.addActionListener(ae -> runCommand());

       //Next Button
        btnFindMatches_Next.addActionListener(ae ->{
                    findMatches(hasBeenPressed);
            if(!hasBeenPressed) {
                btnFindMatches_Next.setText("Find Next Match");
                hasBeenPressed = true;
            }

    }
        );


    }

    public static void main(String[] args)
    {
        //Form Setup
        frame = new JFrame("Alloyer");
        frame.setContentPane(new AlloyerForm().getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
        frame.pack();
    }

    public void setBitwidth()
    {

            cmbPreferenceValue.removeAllItems();
            cmbWatts1.removeAllItems();
            cmbWatts1.removeAllItems();

            bitwidth = Integer.parseInt((String) cmbBitwidth.getSelectedItem());
            maxInts = (int) (Math.pow(2, bitwidth) / 2) - 1;

            for (int i = 1; i <= maxInts; i++)
            {
                cmbPreferenceValue.addItem(i);
                cmbWatts1.addItem(i);
                cmbWatts2.addItem(i);
            }

    }
    public void loadAvailableSigs(SafeList<Sig> sigs)
    {
        String sigLabel;
        if(sigs != null)
        {
            for(Sig s : sigs)
            {
                sigLabel = s.label.replace("this/", "");
                sigsDict.put(sigLabel, s);
                if(!sigLabel.equals("Grid") && !sigLabel.equals("Circuit"))
                {
                    cmbPreferenceSignature.addItem(sigLabel);
                    availableSigs.add(sigLabel);
                }
            }
        }
        cmbPreferenceSignature.setSelectedIndex(-1);

    }

    public void updateSigCombo(List<String> sigs)
    {
        if(sigs != null)
        {
            cmbPreferenceSignature.removeAllItems();
            for(String s : sigs)
            {
                cmbPreferenceSignature.addItem(s);
            }
        }
        cmbPreferenceSignature.setSelectedIndex(-1);
    }

    public void addToList(JButton button)
    {
        if(button == btnAddPref)
        {
            if(cmbPreferenceSignature.getSelectedIndex()>-1 && cmbPreferenceValue.getSelectedIndex() >-1)
            {
                String sig = (String) cmbPreferenceSignature.getSelectedItem();
                int val = (int)cmbPreferenceValue.getSelectedItem();
                boolean boolExact = cbExact.isSelected();
                String exact;

                if (boolExact) exact = "exact";
                else exact = "";

                int removeIndex = -1;
                lstSigValuesModel.addElement(sig + "  " + val + "  " + exact);

                int i = 0;
                for (String s : availableSigs)
                {
                    if (s.equals(sig)) removeIndex = i;
                    i++;
                }
                availableSigs.remove(removeIndex);
            }
            else JOptionPane.showMessageDialog(mainPanel, "Please choose a signature and value");

            updateSigCombo(availableSigs);
            cmbPreferenceValue.setSelectedIndex(-1);
            //cbExact.setSelected(false);
        }

        else if(button == btnAddRel)
        {
            String relate1 = (String) cmbRelate1.getSelectedItem();
            String relate2 = (String) cmbRelate2.getSelectedItem();
            String relateName1 = txtName1.getText();
            String relateName2 = txtName2.getText();
            int watts1 = (int) cmbWatts1.getSelectedItem();
            int watts2 = (int) cmbWatts2.getSelectedItem();
            String wattRelate1;
            String wattRelate2;

            String defaultName1 = relate1+"$"+ occurrences.get(relate1);
            String defaultName2 = relate2+"$"+ occurrences.get(relate2);
            int def1 = occurrences.get(relate1);
            int def2 = occurrences.get(relate2);
            def1++;
            def2++;

            String entry = relate1 + "   " + (!relateName1.equals("") ? relateName1 : defaultName1) + ",  " + watts1 + " watts" + "   \u2192   " +
                           relate2 + "   " + (!relateName2.equals("") ? relateName2 : defaultName2) + ",  " + watts2 + " watts";

            relationships.add(defaultName1 + "->" + defaultName2);
            lstRelationshipsModel.addElement(entry);

            customNames.put(defaultName1,(!relateName1.equals("") ? relateName1 : defaultName1));
            customNames.put(defaultName2,(!relateName2.equals("") ? relateName2 : defaultName2));
            occurrences.remove(relate1);
            occurrences.remove(relate2);
            occurrences.put(relate1, def1);
            occurrences.put(relate2, def2);

            cmbRelate1.setSelectedIndex(-1);
            cmbRelate2.setSelectedIndex(-1);
            txtName1.setText("");
            txtName2.setText("");
            cmbWatts1.setSelectedIndex(-1);
            cmbWatts2.setSelectedIndex(-1);
        }
    }

    public void deleteFromList()
    {
        String selectedLine;
        String[] line;

        if(lstPreferences.getSelectedValue()!= null && lstRelationships.getSelectedValue() == null)
        {
            selectedLine = lstPreferences.getSelectedValue().toString();
            line = selectedLine.split("  ");
            String selectedSig = line[0];
            availableSigs.add(selectedSig);
            updateSigCombo(availableSigs);
            lstSigValuesModel.remove(lstPreferences.getSelectedIndex());
        }
        else if(lstRelationships.getSelectedValue() != null && lstPreferences.getSelectedValue()==null)
        {
            selectedLine = lstRelationships.getSelectedValue().toString();
            line = selectedLine.split(" ");
            // /TODO: Parse Line
            updateRelationCombos();
            lstRelationshipsModel.remove(lstRelationships.getSelectedIndex());
        }
        //TODO: make sure all possibilities covered
    }

    public void updateRelationCombos()
    {
        //TODO
    }


    public void cbSelected(JCheckBox cb, JCheckBox partner)
    {
        if(cb.isSelected() && !partner.isSelected()) btnRun.setEnabled(true);
        else if(!cb.isSelected() && partner.isSelected()) btnRun.setEnabled(true);
        else if(cb.isSelected() && partner.isSelected()) btnRun.setEnabled(true);
        else btnRun.setEnabled(false);
    }

    public void runCommand()
    {
        if(cmbCircuits.getSelectedIndex()>-1)
        {
            int numCircuits = Integer.parseInt((String) cmbCircuits.getSelectedItem());
            try
            {
                cmd = GridMetamodel.makeCommand(numCircuits);
            }
            catch (Err err)
            {
                err.printStackTrace();
            }
            if (!lstSigValuesModel.isEmpty())
            {
                String[] listContents;
                String listModel = lstSigValuesModel.toString();
                listModel = listModel.replace("[", "");
                listModel = listModel.replace("]", "");
                listContents = listModel.split(", ");

                boolean boolExact = false;

                for (String s : listContents)
                {
                    String[] current = s.split("  ");
                    String sigLabel = current[0];
                    String amt = current[1];

                    if (current.length == 3)
                    {
                        boolExact = true;
                    }
                    if (sigsDict.containsKey(sigLabel))
                    {
                        Sig sig = sigsDict.get(sigLabel);
                        int amount = Integer.parseInt(amt);
                        try
                        {
                            cmd = GridMetamodel.changeCommand(cmd, sig, boolExact, amount);
                        }
                        catch (Err err)
                        {
                            err.printStackTrace();
                        }
                    }
                }
            }

            try
            {
                solution = GridMetamodel.run(cmd);
                if (cbTextSolution.isSelected() && !cbGraphSolution.isSelected())
                {
                    System.out.print(solution.toString());
                }
                else if (!cbTextSolution.isSelected() && cbGraphSolution.isSelected())
                {
                    GridMetamodel.visualize(solution);
                }
                else if (cbTextSolution.isSelected() && cbGraphSolution.isSelected())
                {
                    System.out.print(solution.toString());
                    GridMetamodel.visualize(solution);
                }
            }
            catch (Err err)
            {
                err.printStackTrace();
            }

            loadRelationships();

            pnlRelationships.setVisible(true);
            pnlNext.setVisible(true);
            frame.pack();
        }
        else
        {
            JOptionPane.showMessageDialog(mainPanel, "Please select number of circuits");
        }
    }

    public void loadRelationships()
    {
         for(Sig s: sigsFromMeta)
         {
             if(s.isAbstract == null && !s.label.equals("this/Grid"))
             {
                 cmbRelate1.addItem(s.label.replace("this/", ""));
                 occurrences.put(s.label.replace("this/", ""), 0);
             }
         }

    }

    public void loadRelate2(ItemEvent ie)
    {
        if(ie.getStateChange() == ItemEvent.SELECTED)
        {
            cmbRelate2.removeAllItems();
            if(cmbRelate1.getSelectedIndex()>-1)
            {
                String strSig1 = (String) cmbRelate1.getSelectedItem();
                if(sigsDict.containsKey(strSig1))
                {
                    Sig.PrimSig sig1 = (Sig.PrimSig)sigsDict.get(strSig1);
                    if(sig1.getFields().size() > 0)
                    {
                        for (Sig.Field f : sig1.getFields())
                        {
                            if (!f.label.equals("watts"))
                            {
                                String label = f.label;
                                label = label.substring(0, 1).toUpperCase() + label.substring(1);
                                String[] capitalize = label.split("_");
                                if(capitalize.length == 2)
                                {
                                    label = capitalize[0].substring(0, 1).toUpperCase() + capitalize[0].substring(1);
                                    label += "_";
                                    label += capitalize[1].substring(0, 1).toUpperCase() + capitalize[1].substring(1);
                                }

                                cmbRelate2.addItem(label);
                            }
                        }
                    }
                    else
                    {
                        Sig.PrimSig parent = sig1.parent;
                        for (Sig.Field f : parent.getFields())
                        {
                            if (!f.label.equals("watts"))
                            {
                                String label = f.label;
                                label = label.substring(0, 1).toUpperCase() + label.substring(1);
                                String[] capitalize = label.split("_");
                                if(capitalize.length == 2)
                                {
                                    label += "_";
                                    label += capitalize[1].substring(0, 1).toUpperCase() + capitalize[1].substring(1);
                                }
                                cmbRelate2.addItem(label);

                            }
                        }
                    }
                }

            }
        }
    }

    public void showNextSolution()
    {
        try
        {
            if(cbTextSolution.isSelected() && !cbGraphSolution.isSelected())
            {
                solution = GridMetamodel.findSolution(solution, (String[]) relationships.toArray(), true, 0);
                System.out.println(solution.toString());
            }
            else if(!cbTextSolution.isSelected() && cbGraphSolution.isSelected())
            {
                solution = GridMetamodel.visualizeNext(solution, currentModelForm);
            }
            else if(cbTextSolution.isSelected() && cbGraphSolution.isSelected())
            {
                solution = GridMetamodel.visualizeNext(solution, currentModelForm);
                System.out.println(solution.toString());
            }
            else
            {

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void findMatches(boolean hasBeenPressed)
    {

        if(relationshipArr == null){
            relationshipArr = new String[relationships.size()];
            int i = 0;
            for(String s : relationships){
                relationshipArr[i]=s;
                i++;
            }
        }



        if(!hasBeenPressed) {
            solution = GridMetamodel.findSolution(solution, relationshipArr, true, 0);
            if (cbTextSolution.isSelected()) {
                System.out.println(solution.toString());
            }
            if (cbGraphSolution.isSelected()) {
                GridMetamodel.visualize(solution);
            }
        }else{
            solution = GridMetamodel.findSolution(GridMetamodel.getNext(solution), relationshipArr, true, 0);
            if (cbTextSolution.isSelected()) {
                System.out.println(solution.toString());
            }
            if (cbGraphSolution.isSelected()) {
                GridMetamodel.visualize(solution);
            }
        }
    }

}
