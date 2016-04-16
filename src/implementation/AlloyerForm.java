package implementation;

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

    DefaultListModel lstPreferencesModel;
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
    static HashSet<String> customNames = new HashSet<>();
    static Hashtable<String, String> watts = new Hashtable<>();
    static Hashtable<String, String> mappedRelations = new Hashtable<>();
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

        implementation.GridMetamodel.setUp();
        sigsFromMeta = implementation.GridMetamodel.getSigs();
        sigsDict = new Hashtable<>();
        availableSigs = new ArrayList<>();
        loadAvailableSigs(sigsFromMeta);
        lstPreferences.setModel(new DefaultListModel());
        lstPreferencesModel = (DefaultListModel) lstPreferences.getModel();
        lstRelationships.setModel(new DefaultListModel());
        lstRelationshipsModel = (DefaultListModel)lstRelationships.getModel();

        cmbBitwidth.setSelectedIndex(-1);
        cmbCircuits.setSelectedIndex(-1);
        cmbPreferenceSignature.setSelectedIndex(-1);
        cmbPreferenceValue.setSelectedIndex(-1);
        cmbRelate1.setSelectedIndex(-1);
        cmbRelate2.setSelectedIndex(-1);
        cmbWatts1.setSelectedIndex(-1);
        cmbWatts2.setSelectedIndex(-1);


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


        btnViewButton.addActionListener(ae->GridMetamodel.visualize(solution));

        //Add Relationship Button
        btnAddRel.addActionListener(ae->addToList(btnAddRel));

        //Combobox for relation sig1
        cmbRelate1.addItemListener(ie -> loadRelate2(ie));

        //Run Button
        btnRun.addActionListener(ae -> runCommand());

       //Next Button
        btnFindMatches_Next.addActionListener(ae ->
        {
            findMatches(hasBeenPressed);
            if (!hasBeenPressed)
            {
                btnFindMatches_Next.setText("Find Next Match");
                hasBeenPressed = true;
            }
        });
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
            cmbWatts2.removeAllItems();

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
                lstPreferencesModel.addElement(sig + "  " + val + "  " + exact);

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

        }

        else if(button == btnAddRel)
        {
            if(!txtName1.getText().equals("") && !txtName2.getText().equals("") && cmbRelate1.getSelectedIndex()>-1 && cmbRelate2.getSelectedIndex()>-1)
            {
                String customName1 = txtName1.getText();
                String customName2 = txtName2.getText();

                if(!customNames.contains(customName1) && !customNames.contains(customName2))
                {
                    customNames.add(customName1);
                    customNames.add(customName2);

                    String relate1 = (String) cmbRelate1.getSelectedItem();
                    String relate2 = (String) cmbRelate2.getSelectedItem();

                    //get occurences of sig name to put into default name.
                    int def1 = occurrences.get(relate1);
                    int def2 = occurrences.get(relate2);

                    String defaultName1 = relate1 + "$" + def1;
                    String defaultName2 = relate2 + "$" + def2;

                    def1++;
                    def2++;

                    String watts1 = cmbWatts1.getSelectedItem() + "";
                    String watts2 = cmbWatts2.getSelectedItem() + "";

                    String entry = relate1 + "   " + customName1 + ",  " + watts1 + " watts" + "   \u2192   " +
                                   relate2 + "   " + customName2 + ",  " + watts2 + " watts";

                    lstRelationshipsModel.addElement(entry);


                    mappedRelations.put(defaultName1, defaultName2);

                    //incrementing values here
                    occurrences.remove(relate1);
                    occurrences.remove(relate2);
                    occurrences.put(relate1, def1);
                    occurrences.put(relate2, def2);

                    txtName1.setText("");
                    txtName2.setText("");
                }
                else
                {
                    JOptionPane.showMessageDialog(frame, "All member names must be unique.");
                }
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Please choose types and provide a name for each member of the relationship.");
            }
        }
    }

    public void deleteFromList()
    {

        if(frame.getFocusOwner() == lstPreferences)
        {
            String selectedLine = lstPreferences.getSelectedValue().toString();
            String[] line = selectedLine.split("  ");
            String selectedSig = line[0];
            availableSigs.add(selectedSig);
            updateSigCombo(availableSigs);
            lstPreferencesModel.remove(lstPreferences.getSelectedIndex());
        }
        else if(frame.getFocusOwner() == lstRelationships)
        {
            String selectedLine = lstRelationships.getSelectedValue().toString();
            String[] lines = selectedLine.split("→");

        }

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
        if(cmbBitwidth.getSelectedIndex()>-1)
        {
            if (cmbCircuits.getSelectedIndex() > -1)
            {
                //Number of circuits from combobox
                int numCircuits = Integer.parseInt((String) cmbCircuits.getSelectedItem());

                //Make base command using number of circuits and bitwidth
                try
                {
                    cmd = GridMetamodel.makeCommand(numCircuits, bitwidth);
                }
                catch (Err err)
                {
                    err.printStackTrace();
                }

                //If preferences list is not empty, parse the contents to get information that will be
                //used to modify the base command
                if (!lstPreferencesModel.isEmpty())
                {
                    String[] listContents;
                    String listModel = lstPreferencesModel.toString();
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

                //solution is
                try
                {
                    solution = GridMetamodel.run(cmd);
                    System.out.print(solution.toString());
                }
                catch (NullPointerException n)
                {
                    JOptionPane.showMessageDialog(frame, "No solutions found with current preferences.");
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
        else
        {
            JOptionPane.showMessageDialog(mainPanel, "Please select bitwidth");
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

    public void findMatches(boolean hasBeenPressed)
    {

        if(relationshipArr == null){
            relationshipArr = new String[relationships.size()];
            int i = 0;
            for(String s : relationships)
            {
                relationshipArr[i]=s;
                i++;
            }
        }

        try
        {
            if (!hasBeenPressed)
            {
                solution = GridMetamodel.findSolution(solution, relationshipArr, true, 20000);
                System.out.println(solution.toString());

                JTextArea textArea = new JTextArea(solution.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                scrollPane.setPreferredSize(new Dimension(500,500));
                JOptionPane.showMessageDialog(null, scrollPane, "Solution", JOptionPane.CLOSED_OPTION);
                //GridMetamodel.visualize(solution);
            }
            else
            {
                solution = GridMetamodel.findSolution(GridMetamodel.getNext(solution), relationshipArr, true, 0);
                System.out.println(solution.toString());
                JTextArea textArea = new JTextArea(solution.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                scrollPane.setPreferredSize(new Dimension(500, 500));
                JOptionPane.showMessageDialog(null, scrollPane, "Solution", JOptionPane.CLOSED_OPTION);

                //GridMetamodel.visualize(solution);
            }
        }
        catch(NullPointerException e)
        {
            JOptionPane.showMessageDialog(frame,"No solutions found with current preferences/relationships.");
        }
    }

}
