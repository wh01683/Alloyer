package Implementation;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by robert on 5/27/15.
 */
public class Instance implements Domain {

    private Set<Sig> sigs = new LinkedHashSet<Sig>();

    @Override
    public Sig.PrimSig addPrimSig() {
        return null;
    }

    @Override
    public Sig.SubsetSig addSubsetSig() {
        return null;
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
