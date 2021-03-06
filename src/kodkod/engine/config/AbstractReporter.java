/* 
 * Kodkod -- Copyright (c) 2005-2011, Emina Torlak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kodkod.engine.config;


import java.util.List;
import java.util.Set;

import kodkod.ast.Decl;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.bool.BooleanFormula;
import kodkod.instance.Bounds;
import kodkod.util.ints.IntSet;

/**
 * A skeleton implementation of the {@link Reporter} interface.
 * The default implementation for each method has an empty body.s
 * @author Emina Torlak
 */
public abstract class AbstractReporter implements Reporter {

	/**
	 * Constructs a new abstract reporter.
	 */
	protected AbstractReporter() {}
	
	/**
	 * {@inheritDoc}
	 * @see Reporter#detectingSymmetries(kodkod.instance.Bounds)
	 */
	public void detectingSymmetries(Bounds bounds){}
	
	/**
	 * {@inheritDoc}
	 * @see Reporter#detectedSymmetries(Set)
	 */
	public void detectedSymmetries(Set<IntSet> parts) {}
	
	/**
	 * @see Reporter#generatingSBP()
	 */
	public void generatingSBP() {}

	/**
	 * @see Reporter#flattening(BooleanFormula)
	 */
	public void flattening(BooleanFormula circuit) {}

	/**
	 * {@inheritDoc}
	 * @see Reporter#skolemizing(Decl, Relation, List)
	 */
	public void skolemizing(Decl decl, Relation skolem, List<Decl> context) {}

	/**
	 * @see Reporter#solvingCNF(int, int, int)
	 */
	public void solvingCNF(int primaryVars, int vars, int clauses) {}

	/**
	 * @see Reporter#optimizingBoundsAndFormula()
	 */
	public void optimizingBoundsAndFormula() {}

	/**
	 * @see Reporter#translatingToBoolean(Formula, kodkod.instance.Bounds)
	 */
	public void translatingToBoolean(Formula formula, Bounds bounds) {}

	/**
	 * @see Reporter#translatingToCNF(BooleanFormula)
	 */
	public void translatingToCNF(BooleanFormula circuit) {}

}
