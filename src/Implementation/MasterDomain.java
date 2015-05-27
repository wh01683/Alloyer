package Implementation;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

/**
 * Created by robert on 5/27/15.
 */
public class MasterDomain implements Domain {
    @Override
    public Sig.PrimSig addPrimSig() {
        return null;
    }

    @Override
    public Sig.PrimSig addChildSig() {
        return null;
    }

    @Override
    public void addPredicate() {

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
