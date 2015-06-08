package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.*;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import java.util.*;

/**
 * Created by robert on 5/27/15.
 */
public class MasterDomain implements Domain {

    final String fileName = System.getProperty("user.dir") + "\\domain";
    Set<Sig> sigs = new LinkedHashSet<Sig>();
    Hashtable<Integer, Sig> sigHashtable = new Hashtable<>(5);

    @Override
    public Sig.PrimSig addPrimSig(String name, boolean sigAbstract, Attr multiplicity) throws Err {

        Sig.PrimSig sig = new Sig.PrimSig(name, (sigAbstract? Attr.ABSTRACT : null), multiplicity, Attr.SUBSIG);

        sigs.add(sig);
        sigHashtable.put(sig.hashCode(), sig);

        return sig;
    }

    @Override
    public Sig.PrimSig addChildSig(String name, Sig.PrimSig parent, boolean sigAbstract, Attr multiplicity) throws Err {

        Sig.PrimSig sig = new Sig.PrimSig(name, parent, (sigAbstract? Attr.ABSTRACT : null), multiplicity, Attr.SUBSIG);

        sigs.add(sig);
        sigHashtable.put(sig.hashCode(), sig);
        return sig;
    }

    @Override
    public Sig.SubsetSig addSubsetSig(String name, Collection<Sig> parents, Attr multiplicity) throws Err {

        Sig.SubsetSig sig = new Sig.SubsetSig(name, parents, multiplicity, Attr.SUBSET);

        //parents.add(sig);
        sigHashtable.put(sig.hashCode(), sig);
        return sig;
    }

    @Override
    public Sig.Field addField() {
        return null;
    }

    @Override
    public boolean addFact() {
        return false;
    }

    @Override
    public A4Solution run(Expr e) {
        return null;
    }

    public ArrayList<String[]> getTableEntries(){

        ArrayList<String[]> entries = new ArrayList<>(sigs.size());

        for (Sig s: sigs){
            entries.add(tableEntryBuilder(s));
        }

        return entries;
    }

    public String[] tableEntryBuilder(Sig s){

        String attributes = "";

        for (Attr a : s.attributes){
            if(a != null)
            {
                attributes += a.toString() + " ";
            }
            else
            {
                attributes += "null ";
            }
        }


        String[] entry = new String[6];

        String label = s.label;
        String type = "";
        String abstr = "not abstract";
        String mult = "one";
        String parents = "";
        String hashID =  getHashFromSig(s).toString();

        //Type
        if(s.isTopLevel() == true)
        {
            type = "Top-level Primary";
            parents = "none";
        }
        else if(attributes.contains("subsig"))
        {
            type = "Sub-level Primary";
            parents = s.getPrimParent().label;
        }
        else if(attributes.contains("subset"))
        {
            type = "Subset";
            for(Sig p : ((Sig.SubsetSig) s).getSubParents())
            {
                parents += p.label + " ";
            }
        }

        //Multiplicity
        if(attributes.contains(" one "))
        {
            mult = "One";
        }
        else if(attributes.contains("some"))
        {
            mult = "Some";
        }
        else if(attributes.contains(" lone "))
        {
            mult = "Lone";
        }

        entry[0] = label;
        entry[1] = type;
        entry[2] = abstr;
        entry[3] = mult;
        entry[4] = parents;
        entry[5] = hashID;

        return entry;
    }

    private String[] detailedTableEntryBuilder(Sig s){

        String fields = "\nFIELDS\n";
        String fieldDecls = "\nFIELD DECLARATIONS\n";
        String children = "\nCHILDREN\n";
        String facts = "\nFACTS\n";
        String attributes = "\nATTRIBUTES\n";

        //String attributes = "";

        for (Attr a : s.attributes){
            if(a != null)
            {
                attributes += a.toString() + " ";
            }
            else
            {
                attributes += "null ";
            }
        }



        for(Expr e : s.getFacts()){
            facts += e.toString() + " ";
        }

        for (Sig.Field f : s.getFields()){
            fields += f.toString() + " ";
        }

        for (Decl d : s.getFieldDecls()){
            fieldDecls += d.toString() + " ";
        }
        for (Browsable o : s.getSubnodes()){
            children += o.toString() + " ";
        }

        return new String[] {s.label , attributes, fields, fieldDecls, facts, children};

    }

    public ArrayList<Sig> getChildren(Sig.PrimSig s) {

        try {
            ArrayList<Sig> children = new ArrayList<Sig>(s.children().makeCopy());

            return children;

        }catch (Err e){
            e.printStackTrace();
            return null;
        }

    }

    //Sig label is not unique so we used hashcodes to generate a unique id
    public Sig getSigFromHash(Integer hashCode){

        if(sigHashtable.containsKey(hashCode)){
            return sigHashtable.get(hashCode);
        }else{
            return null;
        }

    }

    public Integer getHashFromSig(Sig s){
        return s.hashCode();
    }

    public void saveDomain(){


    }



}
