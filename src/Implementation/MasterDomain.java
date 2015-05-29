package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import java.util.*;

/**
 * Created by robert on 5/27/15.
 */
public class MasterDomain implements Domain {

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

        parents.add(sig);
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

    private String[] tableEntryBuilder(Sig s){

        String attributes = "";

        for (Attr a : s.attributes){
            attributes += a.toString()+" ";
        }

        String[] entry = new String[] {s.label, s.type().toString(), attributes, s.getSubnodes().size() + ""};

        return entry;

    }

    private String[] detailedTableEntryBuilder(Sig s){

        String fields = "\nFIELDS\n", fieldDecls = "\nFIELD DECLARATIONS\n", children = "\nCHILDREN\n", facts = "\nFACTS\n", attributes = "\nATTRIBUTES\n";

        for (Attr a : s.attributes){
            attributes += a.toString() + " ";
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
        for (Object o : s.getSubnodes()){
            children += o.toString() + " ";
        }

        return new String[] {s.label , attributes, fields, fieldDecls, facts, children};

    }

    public Sig getSigFromHash(Integer hashCode){

        if(sigHashtable.contains(hashCode)){
            return sigHashtable.get(hashCode);
        }else{
            return null;
        }

    }

    public Integer getHashFromSig(Sig s){
        return s.hashCode();
    }


}
