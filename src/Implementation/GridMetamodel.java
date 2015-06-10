package Implementation;

/**
 * Created by robert on 6/10/15.
 */

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.*;
import edu.mit.csail.sdg.alloy4viz.AlloyType;
import kodkod.engine.bool.Int;

/**
 * The following class will be the Grid Metamodel constructed using the API
 *
 * */
public class GridMetamodel {

    public static void main(String[] args) throws Err {


        Sig.PrimSig obj=null, dir=null, file=null, root=null;

        Sig.Field parent=null, contains=null;

        Expr fact= ExprConstant.TRUE;


        /*one sig Grid*/
        Sig.PrimSig Grid = new Sig.PrimSig("Grid", Attr.ONE);
        /*abstract sig Circuit*/
        Sig.PrimSig Circuit = new Sig.PrimSig("Circuit", Attr.ABSTRACT);

        //circuit: some Circuit in Grid
        Expr circuit = Grid.addField("circuit", Circuit.some());

        /*sig Load_Circuit extends Circuit*/
        Sig.PrimSig LoadCircuit = new Sig.PrimSig("Load_Circuit", Circuit);

        /*sig Supply_Circuit extends Circuit*/
        Sig.PrimSig SupplyCircuit = new Sig.PrimSig("Supply_Circuit", Circuit);

        /*supply_circuit: set Supply_Circuit in Load_Circuit*/
        Expr supply_circuit = LoadCircuit.addField("supply_circuit", SupplyCircuit.setOf());
        /*load_circuit: set Load_Circuit in Supply_Circuit*/
        Expr load_circuit = SupplyCircuit.addField("load_circuit", LoadCircuit.setOf());

        /*abstract sig Component*/
        Sig.PrimSig Component = new Sig.PrimSig("Component", Attr.ABSTRACT);
        /*containing_circuit: one Circuit*/
        Expr containing_circuit = Component.addField("containing_circuit", Circuit.oneOf());

        /*sig Switch extends Component*/
        Sig.PrimSig Switch = new Sig.PrimSig("Switch", Component);
        /*switch: Circuit -> Circuit*/
        Expr switchComp = Switch.addField("switch", Circuit.some_arrow_some(Circuit));


        /*abstract sig Supply extends Component*/
        Sig.PrimSig Supply = new Sig.PrimSig("Supply", Component, Attr.ABSTRACT);

        /*sig Load extends Component*/
        Sig.PrimSig Load = new Sig.PrimSig("Load", Component);

        /*load: set Load in Supply*/
        Expr load = Supply.addField("load", Load.setOf());

        /*watts: one Int in Supply*/
        Expr load_watts = Supply.addField("watts", oneOf(Int)));

        /*supply: one Supply in Load*/
        Expr supply = Load.addField("supply", Supply.oneOf());

        /*watts: one Int in Load*/
        Expr supply_watts = Supply.addField("watts", AlloyType.INT);

        /*sig GP extends Supply*/
        Sig.PrimSig GP = new Sig.PrimSig("GP", Supply);

        /*sig SP extends Supply*/
        Sig.PrimSig SP = new Sig.PrimSig("SP", Supply);

        /*sig Wind extends Supply*/
        Sig.PrimSig Wind = new Sig.PrimSig("Wind", Supply);

        /*sig Geo extends Supply*/
        Sig.PrimSig Geo = new Sig.PrimSig("Geo", Supply);

        /*sig Hydro extends Supply*/
        Sig.PrimSig Hydro = new Sig.PrimSig("Hydro", Supply);

        /*all l: Load | (l.watts >= 0) and (l.watts =< 10)*/

        Decl l = Load.oneOf("l");

        fact = l.get().join(load_watts).gte(0).forAll(l).and(fact);
        fact = l.get().join(load_watts).lte(10).forAll(l).and(fact);

        Decl s = Supply.oneOf("s");
        fact = l.get().join(supply_watts).gte(0).forAll(s).and(fact);
        fact = l.get().join(supply_watts).lte(10).forAll(s).and(fact);

        Decl g = Grid.oneOf("g");
        /*no g: Grid | g.circuit = g.circuit.nexts*/
        fact = g.get().join(circuit).equal(g.get().join(circuit.nexts)).forNo(g).and(fact);
        /*all g: Grid | g.circuit = Circuit*/
        fact = g.get().join(circuit).equal(Circuit).forAll(g).and(fact);
        /*no g: Grid | (g.grid_l_watts > g.grid_s_watts)*/



        /*all c: Component | #c.containing_circuit > 0*/
        Decl c = Component.oneOf("c");


        /*all s: Circuit.supply | (s.load.load_watts =< s.watts)*/
        Decl sup = supply.oneOf("s");

        /*all l: Circuit.load, s: Circuit.supply | ((s.supply_watts - s.load.load_watts) >= l.watts) <=> (l in s.load)*/
        Decl loa = load.oneOf("l");

        /*all l: Load_Circuit, s: Supply_Circuit | ((s.s_watts - s.l_watts) >= (l.s_watts - l.l_watts)) <=> (l in s.load_circuit)*/






    }






}
