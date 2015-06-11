package Implementation;

/**
 * Created by robert on 6/10/15.
 */

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.Util;
import edu.mit.csail.sdg.alloy4compiler.ast.*;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

import java.util.List;

import static edu.mit.csail.sdg.alloy4.A4Reporter.NOP;
import static edu.mit.csail.sdg.alloy4compiler.ast.Sig.SIGINT;


/**
 * The following class will be the Grid Metamodel constructed using the API
 *
 * */
public class GridMetamodel {

    public static void main(String[] args) throws Err {


        A4Options options = new A4Options();
        options.solver = A4Options.SatSolver.SAT4J;

        Sig.PrimSig obj=null, dir=null, file=null, root=null;

        Sig.Field parent=null, contains=null;

        Expr fact= ExprConstant.TRUE;


        /*one sig Grid*/
        Sig.PrimSig Grid = new Sig.PrimSig("Grid", Attr.ONE);
        /*abstract sig Circuit*/
        Sig.PrimSig Circuit = new Sig.PrimSig("Circuit", Attr.ABSTRACT);

        //circuit: some Circuit in Grid
        Expr circuit = Grid.addField("circuit", Circuit.someOf());

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
        Expr load_watts = Supply.addField("watts", SIGINT.oneOf());

        /*supply: one Supply in Load*/
        Expr supply = Load.addField("supply", Supply.oneOf());

        /*watts: one Int in Load*/
        Expr supply_watts = Supply.addField("watts", SIGINT.oneOf());

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


        Decl l = Load.oneOf("l");
        Decl s = Supply.oneOf("s");
        Decl g = Grid.oneOf("g");
        Decl circ = Circuit.oneOf("c");


        Decl c_supply = Circuit.join(supply).oneOf("c.supply");
        Decl c_load = Circuit.join(load).oneOf("c.load");

        Decl g_circuit_load = Grid.join(circuit).join(load).oneOf("g.circuit.load");
        Decl g_circuit_supply = Grid.join(circuit).join(supply).oneOf("g.circuit.supply");

        Decl l_watts = Load.join(load_watts).oneOf("l.watts");
        Decl s_watts = Supply.join(supply_watts).oneOf("s.watts");






        /*fun s_watts [c: Circuit]: one Int
        *   sum s: c.supply | s.watts*/
        Decl sup = Circuit.join(supply).oneOf("s");
        Expr s_watts_body = supply_watts.sumOver(sup, c_supply);
        Func fun_s_watts = new Func(null, "s_watts", Util.asList(circ), SIGINT.oneOf(), s_watts_body);

        /*fun l_watts [c: Circuit]: one Int
        *   sum l: c.load | l.watts*/

        Decl load1 = Circuit.join(load).oneOf("l");
        Expr l_watts_body = load_watts.sumOver(load1, circ);
        Func fun_l_watts = new Func(null, "l_watts", Util.asList(circ), SIGINT.oneOf(), l_watts_body);

        /*fun grid_l_watts [g: Grid]: one Int
        *   sum l: g.circuit.load | l.watts*/

        Decl gr = Grid.join(circuit).join(load).oneOf("l");
        Expr grid_l_watts_body = load_watts.sumOver(gr, g);
        Func fun_grid_l_watts = new Func(null, "grid_l_watts", Util.asList(g), SIGINT.oneOf(), grid_l_watts_body);

        /*fun grid_s_watts [g: Grid]: one Int
        *   sum s: g.circuit.supply | s.watts*/

        Decl gs = Grid.join(circuit).join(supply).oneOf("s");
        Expr grid_s_watts_body = supply_watts.sumOver(gs, g);
        Func fun_grid_s_watts = new Func(null, "grid_s_watts", Util.asList(g), SIGINT.oneOf(), grid_s_watts_body);

        /*fun load_watts [l: Load]: one Int
        *   sum l.watts*/
        Decl lw = Load.join(load_watts).oneOf("lw");
        Expr load_watts_body = load_watts.sumOver(lw, l);
        Func fun_load_watts = new Func(null, "load_watts", Util.asList(l), SIGINT.oneOf(), load_watts_body);

        /*fun supply_watts [s: Supply]: one Int
        *   sum s.watts*/
        Decl sw = Supply.join(supply_watts).oneOf("sw");
        Expr supply_watts_body = supply_watts.sumOver(sw, s);
        Func fun_supply_watts = new Func(null, "supply_watts", Util.asList(s), SIGINT.oneOf(), supply_watts_body);













        /*all l: Load | (l.watts >= 0) and (l.watts =< 10)*/
        fact = l.get().join(load_watts.cast2int()).gte(ExprConstant.makeNUMBER(0)).forAll(l).and(fact);
        fact = l.get().join(load_watts).lte(ExprConstant.makeNUMBER(10)).forAll(l).and(fact);

        /*all s: Supply | (s.watts >= 0) and (s.watts =< 10)*/
        fact = s.get().join(supply_watts).gte(ExprConstant.makeNUMBER(0)).forAll(s).and(fact);
        fact = s.get().join(supply_watts).lte(ExprConstant.makeNUMBER(10)).forAll(s).and(fact);

        /*no g: Grid | g.circuit = g.circuit.nexts*/
        //fact = g.get().join(circuit).equal(g.get().join(circuit.nexts)).forNo(g).and(fact);

        /*all g: Grid | g.circuit = Circuit*/
        fact = g.get().join(circuit).equal(Circuit).forAll(g).and(fact);

        /*no g: Grid | (g.grid_l_watts > g.grid_s_watts)*/
        fact = g.get().join(fun_grid_l_watts.getBody().gt(fun_grid_s_watts.getBody())).forNo(g).and(fact);


        /*all c: Component | #c.containing_circuit > 0*/
        Decl c = Component.oneOf("c");
        fact = (c.get().join(containing_circuit)).cardinality().gt(ExprConstant.makeNUMBER(0)).forAll(c).and(fact);

        /*all s: Circuit.supply | (s.load.watts =< s.watts)*/

        Decl Circuit_supply = Circuit.join(supply).oneOf("Circuit.supply");
        fact = Circuit_supply.get().join(load).join(load_watts).gte(Circuit_supply.get().join(supply_watts)).forAll(Circuit_supply).and(fact);

        /*all l: Circuit.load, s: Circuit.supply | ((s.supply_watts - s.load.load_watts) >= l.watts) <=> (l in s.load)*/
        Decl Circuit_load = Circuit.join(load).oneOf("Circuit.load");
        fact = ((Circuit_supply.get().join(supply_watts).minus(Circuit_supply.get().join(load).join(load_watts)).gte(Circuit_load.get().join(load_watts))).iff(l.get().in(Circuit_supply.get().join(load)))).and(fact);


        /*all l: Load_Circuit, s: Supply_Circuit | ((s.s_watts - s.l_watts) >= (l.s_watts - l.l_watts)) <=> (l in s.load_circuit)*/
        fact = ((Circuit_supply.get().join(supply_watts).minus(Circuit_supply.get().join(load_watts)).gte(Circuit_load.get().join(supply_watts).minus(Circuit_load.get().join(load_watts)))).iff(l.get().in(Circuit_supply.get().join(load_circuit)))).and(fact);

        Func simplePred = new Func(null, "Simple_Predicate", Util.asList(g), null, circuit);

        Expr testBody = simplePred.call(Grid);


        List<Sig> sigs = Util.asList(Grid, Circuit, SupplyCircuit, LoadCircuit, Component, Load, Supply, Switch, GP, SP, Wind, Geo, Hydro);


        Command test = new Command(false, 3, 8, 4, testBody);



        A4Solution solution = TranslateAlloyToKodkod.execute_command(NOP, sigs, test, options);

        while(solution.satisfiable()){
            System.out.println("[Solution]:");
            System.out.println(solution.toString());
            solution = solution.next();
        }




    }






}
