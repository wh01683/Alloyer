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

import java.util.Arrays;
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
        Expr field_circuit_GRID = Grid.addField("circuit", Circuit.someOf());

        /*sig Load_Circuit extends Circuit*/
        Sig.PrimSig LoadCircuit = new Sig.PrimSig("Load_Circuit", Circuit);

        /*sig Supply_Circuit extends Circuit*/
        Sig.PrimSig SupplyCircuit = new Sig.PrimSig("Supply_Circuit", Circuit);

        /*supply_circuit: set Supply_Circuit in Load_Circuit*/
        Expr field_supply_circuit_LOAD_CIRCUIT = LoadCircuit.addField("supply_circuit", SupplyCircuit.setOf());
        /*load_circuit: set Load_Circuit in Supply_Circuit*/
        Expr field_load_circuit_SUPPLY_CIRCUIT = SupplyCircuit.addField("load_circuit", LoadCircuit.setOf());

        /*abstract sig Component*/
        Sig.PrimSig Component = new Sig.PrimSig("Component", Attr.ABSTRACT);
        /*containing_circuit: one Circuit*/
        Expr field_containing_circuit_COMPONENT = Component.addField("containing_circuit", Circuit.oneOf());

        /*sig Switch extends Component*/
        Sig.PrimSig Switch = new Sig.PrimSig("Switch", Component);
        /*switch: Circuit -> Circuit*/
        Expr field_switch_CIRCUIT = Switch.addField("switch", Circuit.some_arrow_some(Circuit));


        /*abstract sig Supply extends Component*/
        Sig.PrimSig Supply = new Sig.PrimSig("Supply", Component, Attr.ABSTRACT);

        /*sig Load extends Component*/
        Sig.PrimSig Load = new Sig.PrimSig("Load", Component);

        /*load: set Load in Supply*/
        Expr field_load_SUPPLY = Supply.addField("load", Load.setOf());

        /*watts: one Int in Supply*/
        Expr field_watts_SUPPLY = Supply.addField("watts", SIGINT.oneOf());

        /*supply: one Supply in Load*/
        Expr field_supply_LOAD = Load.addField("supply", Supply.oneOf());

        /*watts: one Int in Load*/
        Expr field_watts_LOAD = Supply.addField("watts", SIGINT.oneOf());

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




        /*fun s_watts [c: Circuit]: one Int
        *   sum s: c.supply | s.watts*/

        Decl decl_oneOf_Circuit = Circuit.oneOf("c");
        Decl decl_circuit_supply = decl_oneOf_Circuit.get().join(field_supply_LOAD).oneOf("s");

        Expr s_watts_body = decl_circuit_supply.get().join(field_watts_SUPPLY).sumOver(decl_circuit_supply);
        Func fun_s_watts = new Func(null, "s_watts", Util.asList(), SIGINT.oneOf(), s_watts_body);

        /*fun l_watts [c: Circuit]: one Int
        *   sum l: c.load | l.watts*/
        Decl decl_c_load = decl_oneOf_Circuit.get().join(field_load_SUPPLY).oneOf("l");
        Expr l_watts_body = decl_c_load.get().join(field_watts_LOAD).sumOver(decl_c_load);
        Func fun_l_watts = new Func(null, "l_watts", Util.asList(decl_oneOf_Circuit), SIGINT.oneOf(), l_watts_body);

        /*fun grid_l_watts [g: Grid]: one Int
        *   sum l: g.circuit.load | l.watts*/

        Decl decl_one_Grid = Grid.oneOf("g");
        Decl decl_g_circuit_load = decl_one_Grid.get().join(field_circuit_GRID.join(field_load_SUPPLY)).oneOf("l");
        Expr grid_l_watts_body = decl_g_circuit_load.get().join(field_watts_LOAD).sumOver(decl_g_circuit_load);
        Func fun_grid_l_watts = new Func(null, "grid_l_watts", Util.asList(decl_one_Grid), SIGINT.oneOf(), grid_l_watts_body);

        /*fun grid_s_watts [g: Grid]: one Int
        *   sum s: g.circuit.supply | s.watts*/

        Decl decl_g_circuit_supply = decl_one_Grid.get().join(field_circuit_GRID).join(field_supply_LOAD).oneOf("s");
        Expr grid_s_watts_body = decl_g_circuit_supply.get().join(field_watts_SUPPLY).sumOver(decl_g_circuit_supply);
        Func fun_grid_s_watts = new Func(null, "grid_s_watts", Util.asList(decl_one_Grid), SIGINT.oneOf(), grid_s_watts_body);

        /*fun load_watts [l: Load]: one Int
        *   sum l.watts*/

        Decl decl_oneOf_Load = Load.oneOf("l");
        Expr load_watts_body = decl_oneOf_Load.get().join(field_watts_LOAD).sumOver(decl_oneOf_Load);
        Func fun_load_watts = new Func(null, "load_watts", Util.asList(decl_oneOf_Load), SIGINT.oneOf(), load_watts_body);

        /*fun supply_watts [s: Supply]: one Int
           sum s.watts*/
        Decl decl_oneOf_Supply = Supply.oneOf("s");
        Expr supply_watts_body = decl_oneOf_Supply.get().join(field_watts_SUPPLY).sumOver(decl_oneOf_Supply);
        Func fun_supply_watts = new Func(null, "supply_watts", Util.asList(decl_oneOf_Supply), SIGINT.oneOf(), supply_watts_body);












        Decl decl_OneOf_Load = Load.oneOf("l");
        //all l: Load | (l.watts >= 0) and (l.watts =< 10)
        fact = decl_OneOf_Load.get().join(field_watts_LOAD).gte(ExprConstant.makeNUMBER(0)).forAll(decl_OneOf_Load).and(fact);
        fact = decl_OneOf_Load.get().join(field_watts_LOAD).lte(ExprConstant.makeNUMBER(10)).forAll(decl_OneOf_Load).and(fact);

        //all s: Supply | (s.watts >= 0) and (s.watts =< 10)
        Decl decl_OneOf_Supply = Supply.oneOf("s");
        fact = decl_OneOf_Supply.get().join(field_watts_SUPPLY).gte(ExprConstant.makeNUMBER(0)).forAll(decl_OneOf_Supply).and(fact);
        fact = decl_OneOf_Supply.get().join(field_watts_SUPPLY).lte(ExprConstant.makeNUMBER(10)).forAll(decl_OneOf_Supply).and(fact);

        //no g: Grid | g.circuit = g.circuit.nexts
        Decl decl_OneOf_Grid = Grid.oneOf("g");
        //fact = decl_OneOf_Grid.get().join(field_circuit_GRID).equal(decl_OneOf_Grid.get().join(field_circuit_GRID.nexts)).forNo(decl_OneOf_Grid).and(fact);

        //all g: Grid | g.circuit = Circuit
        fact = decl_OneOf_Grid.get().join(field_circuit_GRID).equal(Circuit).forAll(decl_OneOf_Grid).and(fact);

        //no g: Grid | (g.grid_l_watts > g.grid_s_watts)
        fact = decl_OneOf_Grid.get().join(fun_grid_l_watts.getBody().gt(fun_grid_s_watts.getBody())).forNo(decl_one_Grid).and(fact);

        //all c: Component | #c.containing_circuit > 0
        Decl decl_oneOf_Component = Component.oneOf("c");
        fact = (decl_oneOf_Component.get().join(field_containing_circuit_COMPONENT)).cardinality().gt(ExprConstant.makeNUMBER(0)).forAll(decl_oneOf_Component).and(fact);

        //all s: Circuit.supply | (s.load.watts =< s.watts)
        Decl decl_oneOf_Circuit_supply = Circuit.join(field_supply_circuit_LOAD_CIRCUIT).oneOf("s");
        fact = decl_oneOf_Circuit_supply.get().join(field_load_SUPPLY).join(field_watts_LOAD).gte(decl_oneOf_Circuit_supply.get().join(field_watts_SUPPLY)).forAll(decl_oneOf_Circuit_supply).and(fact);

        //all l: Circuit.load, s: Circuit.supply | ((s.supply_watts - s.load.load_watts) >= l.watts) <=> (l in s.load)
        Decl decl_oneOf_Circuit_load = Circuit.join(field_load_circuit_SUPPLY_CIRCUIT).oneOf("l");
        fact = (decl_oneOf_Circuit_supply.get().join(field_watts_SUPPLY).minus(decl_oneOf_Circuit_supply.get().join(field_load_SUPPLY.join(field_watts_LOAD)))
                .gte(decl_oneOf_Circuit_load.get().join(field_watts_LOAD)).iff(decl_oneOf_Circuit_load.get().in(decl_oneOf_Circuit_supply.get().join(field_load_SUPPLY))))
                .forAll(decl_oneOf_Circuit_load, decl_oneOf_Circuit_supply).and(fact);


        //all l: Load_Circuit, s: Supply_Circuit | ((s.s_watts - s.l_watts) >= (l.s_watts - l.l_watts)) <=> (l in s.load_circuit)
        Decl decl_load_circuit = LoadCircuit.oneOf("load_circuit");
        Decl decl_supply_circuit = SupplyCircuit.oneOf("supply_circuit");

        fact = ((decl_supply_circuit.get().
                join(fun_s_watts.call(decl_supply_circuit.get()))
                .minus(decl_supply_circuit.get().
                        join(fun_l_watts.call(decl_supply_circuit.get()))
                ).gte(decl_load_circuit.get().join(fun_s_watts.call(decl_load_circuit.get())
                .minus(decl_load_circuit.get().join(fun_l_watts.call(decl_load_circuit.get()))))
                .iff(decl_load_circuit.get().in(decl_supply_circuit.get().join(field_load_circuit_SUPPLY_CIRCUIT))))));


        Func someGrid = new Func(null, "SomeG", null, null, Grid.some());

        // pred atMostThree[x:univ, y:univ] { #(x+y) >= 3 }
        Decl decl_loads = Load.someOf("l");
        Decl decl_supplies = Supply.someOf("s");
        Expr body = decl_loads.get().plus(decl_supplies.get()).cardinality().lte(ExprConstant.makeNUMBER(3));
        Func atMost3 = new Func(null, "atMost3", Util.asList(decl_loads,decl_supplies), null, body);

        List<Sig> sigs = Arrays.asList(new Sig[] {Grid, Circuit, SupplyCircuit, LoadCircuit, Component, Load, Supply, Switch, GP, SP, Wind, Geo, Hydro});

        // run { some A && atMostThree[B,B] } for 3 but 3 int, 3 seq
        Expr expr1 = Circuit.some().and(atMost3.call(SP,SP));
        Command cmd1 = new Command(false, 3, 8, 7, expr1);
        A4Solution sol1 = TranslateAlloyToKodkod.execute_command(NOP, sigs, cmd1, options);
        System.out.println("[Solution1]:");
        System.out.println(sol1.toString());


        A4Solution solution = TranslateAlloyToKodkod.execute_command(NOP, sigs, cmd1, options);

        while(solution.satisfiable()){
            System.out.println("[Solution]:");
            System.out.println(solution.toString());
            solution = solution.next();
        }




    }






}
