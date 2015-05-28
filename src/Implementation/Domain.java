package Implementation;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by robert on 5/27/15.
 */
public interface Domain extends Serializable{

    Sig.PrimSig addPrimSig(String name, boolean sigAbstract, Attr multiplicity) throws Err;
    Sig.PrimSig addChildSig(String name, Sig.PrimSig parent, boolean sigAbstract, Attr multiplicity) throws Err;

    Sig.SubsetSig addSubsetSig(String name, Collection<Sig> parents, Attr multiplicity) throws Err;

    Sig.Field addField();

    boolean addFact();

    A4Solution run(Expr e);




}
