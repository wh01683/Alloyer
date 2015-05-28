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
public class Instance implements Domain {

    private Set<Sig> sigs = new LinkedHashSet<Sig>();

    @Override
    public Sig.PrimSig addPrimSig(String name, boolean sigAbstract, Attr multiplicity) throws Err {
        return null;
    }

    @Override
    public Sig.PrimSig addChildSig(String name, Sig.PrimSig parent, boolean sigAbstract, Attr multiplicity) throws Err {
        return null;
    }

    @Override
    public Sig.SubsetSig addSubsetSig(String name, Attr multiplicity, Collection<Sig> parents) throws Err {
        return null;
    }

    @Override
    public Func addPredicate() {

    }

    @Override
    public void addFunction() {

    }

    @Override
    public void addAssertion() {

    }

    @Override
    public void addCommand() {

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
