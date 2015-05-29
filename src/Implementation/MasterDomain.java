package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by robert on 5/27/15.
 */
public class MasterDomain implements Domain {

    public Set<Sig> sigs = new LinkedHashSet<Sig>();

    @Override
    public Sig.PrimSig addPrimSig(String name, boolean sigAbstract, Attr multiplicity) throws Err {

        Sig.PrimSig sig = new Sig.PrimSig(name, (sigAbstract? Attr.ABSTRACT : null), multiplicity, Attr.SUBSIG);

        sigs.add(sig);

        return sig;
    }

    @Override
    public Sig.PrimSig addChildSig(String name, Sig.PrimSig parent, boolean sigAbstract, Attr multiplicity) throws Err {

        Sig.PrimSig sig = new Sig.PrimSig(name, parent, (sigAbstract? Attr.ABSTRACT : null), multiplicity, Attr.SUBSIG);

        sigs.add(sig);

        return sig;
    }

    //this will only be used when sig has multiple parents
    @Override
    public Sig.SubsetSig addSubsetSig(String name, Collection<Sig> parents, Attr multiplicity) throws Err {

        Sig.SubsetSig sig = new Sig.SubsetSig(name, parents, multiplicity, Attr.SUBSET);

        parents.add(sig);

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

}
