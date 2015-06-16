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
import edu.mit.csail.sdg.alloy4whole.SimpleGUI;

import java.util.Arrays;
import java.util.List;

import static edu.mit.csail.sdg.alloy4.A4Reporter.NOP;
import static edu.mit.csail.sdg.alloy4compiler.ast.Sig.SIGINT;



/**
 * The following class will be the Grid Metamodel constructed using the API
 *
 * */
public class GridMetamodel {

    public static Expr expression;
    public static Command command;
    public static A4Options options = new A4Options();
    public static Sig.PrimSig Grid, Circuit, SupplyCircuit, LoadCircuit, Component, Load, Supply, Switch, GP, SP, Wind, Geo, Hydro;
    public static List<Sig> staticSigs;






    public static void main(String[] args) throws Err {



        //each domain that you make needs A4Options, used to make the solution

        //just telling the option to use the SAT4J solver
        options.solver = A4Options.SatSolver.SAT4J;

        Expr fact = ExprConstant.TRUE;

        /**one sig Grid*/
            //making a new Primsig called Grid, there is exactly one grid in any instance
         Grid = new Sig.PrimSig("Grid", Attr.ONE);


        /**abstract sig Circuit*/
            //making new sig Circuit, which is abstract
         Circuit = new Sig.PrimSig("Circuit", Attr.ABSTRACT);

        /**circuit: some Circuit in Grid*/
            //this is the body of the Grid sig declaration. each grid has at least one Circuit
        Expr field_circuit_GRID = Grid.addField("circuit", Circuit.someOf());

        /**sig Load_Circuit extends Circuit*/
            //child circuit of Circuit, load circuit must have a negative watt supply (supply less than they consume)
         LoadCircuit = new Sig.PrimSig("Load_Circuit", Circuit);

        /**sig Supply_Circuit extends Circuit*/
            //child circuit of Circuit, supply circuits produce 0 or positive wattage
         SupplyCircuit = new Sig.PrimSig("Supply_Circuit", Circuit);

        /**supply_circuit: set Supply_Circuit in Load_Circuit*/
            //each load circuit can have 0 or more supply circuits
        Expr field_supply_circuit_LOAD_CIRCUIT = LoadCircuit.addField("supply_circuit", SupplyCircuit.setOf());

        /**load_circuit: set Load_Circuit in Supply_Circuit*/
            //each supply circuit contains 0 or more load circuits
        Expr field_load_circuit_SUPPLY_CIRCUIT = SupplyCircuit.addField("load_circuit", LoadCircuit.setOf());

        /**abstract sig Component*/
            //parent sig for Load, supply, and switch
         Component = new Sig.PrimSig("Component", Attr.ABSTRACT);
        /**containing_circuit: one Circuit*/
            //every component must belong to a circuit
        Expr field_containing_circuit_COMPONENT = Component.addField("containing_circuit", Circuit.oneOf());

        /**sig Switch extends Component*/
            //switch is a child of Component, switch links other circuits
         Switch = new Sig.PrimSig("Switch", Component);
        /**switch: Circuit -> Circuit*/
            //the body of the switch sig, a switch is a mapping of one Circuit to another
        Expr field_switch_CIRCUIT = Switch.addField("switch", Circuit.some_arrow_some(Circuit));


        /**abstract sig Supply extends Component*/
            //child of Component, represents a power source
         Supply = new Sig.PrimSig("Supply", Component, Attr.ABSTRACT);

        /**sig Load extends Component*/
            //child of component, represents a power consumer like a house, hospital, etc
         Load = new Sig.PrimSig("Load", Component);

        /**load: set Load in Supply*/
            //every supply has 0 or more load
        Expr field_load_SUPPLY = Supply.addField("load", Load.setOf());

        /**watts: one Int in Supply*/
            //every supply has one Integer representing the number of watts they produce, used in facts
        Expr field_watts_SUPPLY = Supply.addField("watts", SIGINT.oneOf());

        /**supply: one Supply in Load*/
            //each load belongs to just one supply source, representing a power plant
        Expr field_supply_LOAD = Load.addField("supply", Supply.oneOf());

        /**watts: one Int in Load*/
            //int representing number of wattage demanded
        Expr field_watts_LOAD = Supply.addField("watts", SIGINT.oneOf());

        /**sig GP extends Supply*/
            //all of the below signatures are children of supply
         GP = new Sig.PrimSig("GP", Supply);

        /**sig SP extends Supply*/
         SP = new Sig.PrimSig("SP", Supply);

        /**sig Wind extends Supply*/
         Wind = new Sig.PrimSig("Wind", Supply);

        /**sig Geo extends Supply*/
         Geo = new Sig.PrimSig("Geo", Supply);

        /**sig Hydro extends Supply*/
         Hydro = new Sig.PrimSig("Hydro", Supply);




        /**fun s_watts [c: Circuit]: one Int
        *   sum s: c.supply | s.watts*/

        //declare "c" as equal to One Circuit sig (it's one by default), everything inside the brackets are parameters of the function
        Decl decl_oneOf_Circuit = Circuit.oneOf("c");
        //declare "s" as c.supply, which is accomplished by calling this .join() operator on the variable above
        //since supply (lowercase) is a field belonging to the Circuit sig, we can only pass a field through the .join() method
        //we are only working with one supply at a time
        Decl decl_circuit_supply = decl_oneOf_Circuit.get().join(field_supply_LOAD).oneOf("s");
        //Expr is an Expression, the body of the function
        //for each Decl (declaration) you must call the .get() method to use it in Expressions
        //we sum the wattage of the supply's wattage by calling the .sumOver() method on the right hand side Decl and passing the left
        // hand side declarations through the parenthesis
        Expr s_watts_body = decl_circuit_supply.get().join(field_watts_SUPPLY).sumOver(decl_circuit_supply);
        //make a new function by passing down null (default), string name of the function, the list of parameters of the function, made into a
        // list by calling the Util.asList() static method and passing in the parameter declarations, the return value (null for preds), and
        //the body of the function
        Func fun_s_watts = new Func(null, "s_watts", Util.asList(decl_oneOf_Circuit), SIGINT.oneOf(), s_watts_body);

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
        fact = (decl_oneOf_Component.get().join(field_containing_circuit_COMPONENT)).cardinality()
                .gt(ExprConstant.makeNUMBER(0)).forAll(decl_oneOf_Component).and(fact);

        //all s: Circuit.supply | (s.load.watts =< s.watts)
        Decl decl_oneOf_Circuit_supply = Circuit.join(field_supply_circuit_LOAD_CIRCUIT).oneOf("s");
        fact = decl_oneOf_Circuit_supply.get().join(field_load_SUPPLY).join(field_watts_LOAD).
                lte(decl_oneOf_Circuit_supply.get().join(field_watts_SUPPLY)).forAll(decl_oneOf_Circuit_supply).and(fact);

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
                .iff(decl_load_circuit.get().in(decl_supply_circuit.get().join(field_load_circuit_SUPPLY_CIRCUIT)))))).and(fact);


        /**Predicate: body = g: some Grid*/

        List<Sig> sigs = Arrays.asList(new Sig[]{Grid, Circuit, SupplyCircuit, LoadCircuit, Component, Load, Supply, Switch, GP, SP, Wind, Geo, Hydro});


        expression = Circuit.some();
        command = makeCommand(4);
        command = changeCommand(command, Grid, false, 1);

        Func someGrid = new Func(null, "SomeG", null, null, expression);

        staticSigs = sigs;

        run(sigs);



    }

    public static Command makeCommand(int forInt) throws Err{
        return new Command(false, forInt, 8, 7, expression);
    }

    public static Command changeCommand(Command prev, Sig sig, boolean exact, int number) throws Err{
        return prev.change(sig, exact, number);
    }

    public static List<Sig> getSigs(){
            return staticSigs;

    }

    public static void run(List<Sig> sigs) throws Err {

        try {



            A4Solution solution = TranslateAlloyToKodkod.execute_command(NOP, sigs, command, options);

            SimpleGUI.main(new String[]{solution.toString()});


            System.out.println("[Solution]:");
            System.out.println(solution.toString());
        /*while(solution.satisfiable()){
            System.out.println("[Solution]:");
            System.out.println(solution.toString());
            solution = solution.next();
        }*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }






}
