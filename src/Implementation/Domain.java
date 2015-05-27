package Implementation;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import java.io.Serializable;

/**
 * Created by robert on 5/27/15.
 */
public interface Domain extends Serializable {

    Sig.PrimSig addPrimSig();
    Sig.PrimSig addChildSig();
    void addPredicate();
    void addFunction();
    void addAssertion();
    void addCommand();
    Sig.SubsetSig addSubsetSig();
    Sig.Field addField();

    boolean addFact();

    A4Solution run(Expr e);




}
