package Implementation;

/**
 * Created by robert on 6/10/15.
 */

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.SafeList;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


/**
 * The following class will be the Grid Metamodel constructed using the API
 *
 * */
public class GridMetamodel {

    private static OurSig wind$0 = new OurSig("this/Supply", "Wind$0", 8);
    private static OurSig wind$1 = new OurSig("this/Supply", "Wind$1", 8);
    private static OurSig load$0 = new OurSig("this/Load", "Load$0", 0);
    private static OurSig load$1 = new OurSig("this/Load", "Load$1", 2);
    private static OurSig load$2 = new OurSig("this/Load", "Load$2", 1);
    public static ArrayList<OurSig> testConstraints = new ArrayList<>();


    public static String testSolution = "---INSTANCE---\n" +
            "integers={-128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -76, -75, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63, -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, -43, -42, -41, -40, -39, -38, -37, -36, -35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127}\n" +
            "univ={-1, -10, -100, -101, -102, -103, -104, -105, -106, -107, -108, -109, -11, -110, -111, -112, -113, -114, -115, -116, -117, -118, -119, -12, -120, -121, -122, -123, -124, -125, -126, -127, -128, -13, -14, -15, -16, -17, -18, -19, -2, -20, -21, -22, -23, -24, -25, -26, -27, -28, -29, -3, -30, -31, -32, -33, -34, -35, -36, -37, -38, -39, -4, -40, -41, -42, -43, -44, -45, -46, -47, -48, -49, -5, -50, -51, -52, -53, -54, -55, -56, -57, -58, -59, -6, -60, -61, -62, -63, -64, -65, -66, -67, -68, -69, -7, -70, -71, -72, -73, -74, -75, -76, -77, -78, -79, -8, -80, -81, -82, -83, -84, -85, -86, -87, -88, -89, -9, -90, -91, -92, -93, -94, -95, -96, -97, -98, -99, 0, 1, 10, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 11, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 12, 120, 121, 122, 123, 124, 125, 126, 127, 13, 14, 15, 16, 17, 18, 19, 2, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 3, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 4, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 5, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 6, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 7, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 8, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 9, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, Grid$0, Load$0, Load$1, Load$2, Load_Circuit$0, Load_Circuit$1, Load_Circuit$2, Supply_Circuit$0, Wind$0, Wind$1, boolean/False$0, boolean/True$0, ord/Ord$0}\n" +
            "Int={-1, -10, -100, -101, -102, -103, -104, -105, -106, -107, -108, -109, -11, -110, -111, -112, -113, -114, -115, -116, -117, -118, -119, -12, -120, -121, -122, -123, -124, -125, -126, -127, -128, -13, -14, -15, -16, -17, -18, -19, -2, -20, -21, -22, -23, -24, -25, -26, -27, -28, -29, -3, -30, -31, -32, -33, -34, -35, -36, -37, -38, -39, -4, -40, -41, -42, -43, -44, -45, -46, -47, -48, -49, -5, -50, -51, -52, -53, -54, -55, -56, -57, -58, -59, -6, -60, -61, -62, -63, -64, -65, -66, -67, -68, -69, -7, -70, -71, -72, -73, -74, -75, -76, -77, -78, -79, -8, -80, -81, -82, -83, -84, -85, -86, -87, -88, -89, -9, -90, -91, -92, -93, -94, -95, -96, -97, -98, -99, 0, 1, 10, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 11, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 12, 120, 121, 122, 123, 124, 125, 126, 127, 13, 14, 15, 16, 17, 18, 19, 2, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 3, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 4, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 5, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 6, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 7, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 8, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 9, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99}\n" +
            "seq/Int={0, 1, 2, 3}\n" +
            "String={}\n" +
            "none={}\n" +
            "this/Grid={Grid$0}\n" +
            "this/Grid<:circuit={Grid$0->Load_Circuit$0, Grid$0->Load_Circuit$1, Grid$0->Load_Circuit$2, Grid$0->Supply_Circuit$0}\n" +
            "this/Circuit={Load_Circuit$0, Load_Circuit$1, Load_Circuit$2, Supply_Circuit$0}\n" +
            "this/Circuit<:supply={Load_Circuit$1->Wind$0, Load_Circuit$1->Wind$1}\n" +
            "this/Circuit<:load={Load_Circuit$0->Load$2, Supply_Circuit$0->Load$1}\n" +
            "this/Load_Circuit={Load_Circuit$0, Load_Circuit$1, Load_Circuit$2}\n" +
            "this/Load_Circuit<:supply_circuit={}\n" +
            "this/Supply_Circuit={Supply_Circuit$0}\n" +
            "this/Supply_Circuit<:load_circuit={Supply_Circuit$0->Load_Circuit$0, Supply_Circuit$0->Load_Circuit$2}\n" +
            "this/Component={Load$0, Load$1, Load$2, Wind$0, Wind$1}\n" +
            "this/Component<:containing_circuit={Load$0->Load_Circuit$2, Load$1->Load_Circuit$2, Load$2->Supply_Circuit$0, Wind$0->Load_Circuit$2, Wind$1->Supply_Circuit$0}\n" +
            "this/Switch={}\n" +
            "this/Switch<:switch={}\n" +
            "this/Supply={Wind$0, Wind$1}\n" +
            "this/Supply<:load={Wind$0->Load$1, Wind$0->Load$2, Wind$1->Load$1, Wind$1->Load$2}\n" +
            "this/Supply<:watts={Wind$0->8, Wind$1->8}\n" +
            "this/GP={}\n" +
            "this/SP={}\n" +
            "this/Wind={Wind$0, Wind$1}\n" +
            "this/Geo={}\n" +
            "this/Hydro={}\n" +
            "this/Load={Load$0, Load$1, Load$2}\n" +
            "this/Load<:supply={Load$0->Wind$1, Load$1->Wind$1, Load$2->Wind$1}\n" +
            "this/Load<:watts={Load$0->0, Load$1->2, Load$2->1}\n" +
            "boolean/Bool={boolean/False$0, boolean/True$0}\n" +
            "boolean/True={boolean/True$0}\n" +
            "boolean/False={boolean/False$0}\n" +
            "ord/Ord={ord/Ord$0}\n" +
            "ord/Ord<:First={ord/Ord$0->Supply_Circuit$0}\n" +
            "ord/Ord<:Next={ord/Ord$0->Load_Circuit$0->Load_Circuit$1, ord/Ord$0->Load_Circuit$2->Load_Circuit$0, ord/Ord$0->Supply_Circuit$0->Load_Circuit$2}\n";

    public static File file = new File(System.getProperty("user.dir") + "/timesData.csv");
    public static Expr expression;
    public static Command command;
    public static A4Options options = new A4Options();
    public static Sig.PrimSig Grid, Circuit, SupplyCircuit, LoadCircuit, Component, Load, Supply, Switch, GP, SP, Wind, Geo, Hydro;
    public static List<Sig> staticSigs;
//    private static String dirPath = "C:/Users/Lindsey/AppData/Local/Temp/alloy4tmp40-Lindsey/";
//    private static String alsDirPath = dirPath + "models/circuitry.als";
    private static String dirPath = "/tmp/alloy4tmp40-robert/";
    private static String alsDirPath = dirPath + "models/circuitry.als";
    private static ArrayList<OurSig> constrainedSigs = new ArrayList<>(10); //will store the list of signatures constrained by the specifications
    private static ArrayList<String> namesOfConstrainedSigs = new ArrayList<>();
    private static ArrayList<String> typesOfConstrainedSigs = new ArrayList<>();
    private static Hashtable<String, Sig> namesToSig = new Hashtable<>(20); //will store a mapping of String type names to Signature objects
    private static HashMap<String, OurSig> namesToConstrainedOurSigs = new HashMap<>();

    private static Module world;

    public static void setUp(){
        try {
            options.solverDirectory = dirPath + "binary";
            options.tempDirectory = dirPath + "tmp";
            options.solver = A4Options.SatSolver.SAT4J;

            world = CompUtil.parseEverything_fromFile(A4Reporter.NOP, null, alsDirPath);

            for(Sig s : world.getAllSigs()){
                namesToSig.putIfAbsent(s.toString(), s);
            }


        }catch (Err e){
            e.printStackTrace();
        }
    }


/*
    public static void ancientMain(String[] args) throws Err {



        //each domain that you make needs A4Options, used to make the solution

        //just telling the option to use the SAT4J solver

        Expr fact = ExprConstant.TRUE;

        */
/**one sig Grid*//*

            //making a new Primsig called Grid, there is exactly one grid in any instance
         Grid = new Sig.PrimSig("Grid", Attr.ONE);


        */
/**abstract sig Circuit*//*

            //making new sig Circuit, which is abstract
         Circuit = new Sig.PrimSig("Circuit", Attr.ABSTRACT);

        */
/**circuit: some Circuit in Grid*//*

            //this is the body of the Grid sig declaration. each grid has at least one Circuit
        Expr field_circuit_GRID = Grid.addField("circuit", Circuit.someOf());

        */
/**sig Load_Circuit extends Circuit*//*

            //child circuit of Circuit, load circuit must have a negative watt supply (supply less than they consume)
         LoadCircuit = new Sig.PrimSig("Load_Circuit", Circuit);

        */
/**sig Supply_Circuit extends Circuit*//*

            //child circuit of Circuit, supply circuits produce 0 or positive wattage
         SupplyCircuit = new Sig.PrimSig("Supply_Circuit", Circuit);

        */
/**supply_circuit: set Supply_Circuit in Load_Circuit*//*

            //each load circuit can have 0 or more supply circuits
        Expr field_supply_circuit_LOAD_CIRCUIT = LoadCircuit.addField("supply_circuit", SupplyCircuit.setOf());

        */
/**load_circuit: set Load_Circuit in Supply_Circuit*//*

            //each supply circuit contains 0 or more load circuits
        Expr field_load_circuit_SUPPLY_CIRCUIT = SupplyCircuit.addField("load_circuit", LoadCircuit.setOf());

        */
/**abstract sig Component*//*

            //parent sig for Load, supply, and switch
         Component = new Sig.PrimSig("Component", Attr.ABSTRACT);
        */
/**containing_circuit: one Circuit*//*

            //every component must belong to a circuit
        Expr field_containing_circuit_COMPONENT = Component.addField("containing_circuit", Circuit.oneOf());

        */
/**sig Switch extends Component*//*

            //switch is a child of Component, switch links other circuits
         Switch = new Sig.PrimSig("Switch", Component);
        */
/**switch: Circuit -> Circuit*//*

            //the body of the switch sig, a switch is a mapping of one Circuit to another
        Expr field_switch_CIRCUIT = Switch.addField("switch", Circuit.some_arrow_some(Circuit));


        */
/**abstract sig Supply extends Component*//*

            //child of Component, represents a power source
         Supply = new Sig.PrimSig("Supply", Component, Attr.ABSTRACT);

        */
/**sig Load extends Component*//*

            //child of component, represents a power consumer like a house, hospital, etc
         Load = new Sig.PrimSig("Load", Component);

        */
/**load: set Load in Supply*//*

            //every supply has 0 or more load
        Expr field_load_SUPPLY = Supply.addField("load", Load.setOf());

        */
/**watts: one Int in Supply*//*

            //every supply has one Integer representing the number of watts they produce, used in facts
        Expr field_watts_SUPPLY = Supply.addField("watts", SIGINT.oneOf());

        */
/**supply: one Supply in Load*//*

            //each load belongs to just one supply source, representing a power plant
        Expr field_supply_LOAD = Load.addField("supply", Supply.oneOf());

        */
/**watts: one Int in Load*//*

            //int representing number of wattage demanded
        Expr field_watts_LOAD = Supply.addField("watts", SIGINT.oneOf());

        */
/**sig GP extends Supply*//*

            //all of the below signatures are children of supply
         GP = new Sig.PrimSig("GP", Supply);

        */
/**sig SP extends Supply*//*

         SP = new Sig.PrimSig("SP", Supply);

        */
/**sig Wind extends Supply*//*

         Wind = new Sig.PrimSig("Wind", Supply);

        */
/**sig Geo extends Supply*//*

         Geo = new Sig.PrimSig("Geo", Supply);

        */
/**sig Hydro extends Supply*//*

         Hydro = new Sig.PrimSig("Hydro", Supply);




        */
/**fun s_watts [c: Circuit]: one Int
        *   sum s: c.supply | s.watts*//*


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

        */
/*fun l_watts [c: Circuit]: one Int
        *   sum l: c.load | l.watts*//*

        Decl decl_c_load = decl_oneOf_Circuit.get().join(field_load_SUPPLY).oneOf("l");
        Expr l_watts_body = decl_c_load.get().join(field_watts_LOAD).sumOver(decl_c_load);
        Func fun_l_watts = new Func(null, "l_watts", Util.asList(decl_oneOf_Circuit), SIGINT.oneOf(), l_watts_body);

        */
/*fun grid_l_watts [g: Grid]: one Int
        *   sum l: g.circuit.load | l.watts*//*


        Decl decl_one_Grid = Grid.oneOf("g");
        Decl decl_g_circuit_load = decl_one_Grid.get().join(field_circuit_GRID.join(field_load_SUPPLY)).oneOf("l");
        Expr grid_l_watts_body = decl_g_circuit_load.get().join(field_watts_LOAD).sumOver(decl_g_circuit_load);
        Func fun_grid_l_watts = new Func(null, "grid_l_watts", Util.asList(decl_one_Grid), SIGINT.oneOf(), grid_l_watts_body);

        */
/*fun grid_s_watts [g: Grid]: one Int
        *   sum s: g.circuit.supply | s.watts*//*


        Decl decl_g_circuit_supply = decl_one_Grid.get().join(field_circuit_GRID).join(field_supply_LOAD).oneOf("s");
        Expr grid_s_watts_body = decl_g_circuit_supply.get().join(field_watts_SUPPLY).sumOver(decl_g_circuit_supply);
        Func fun_grid_s_watts = new Func(null, "grid_s_watts", Util.asList(decl_one_Grid), SIGINT.oneOf(), grid_s_watts_body);

        */
/*fun load_watts [l: Load]: one Int
        *   sum l.watts*//*


        Decl decl_oneOf_Load = Load.oneOf("l");
        Expr load_watts_body = decl_oneOf_Load.get().join(field_watts_LOAD).sumOver(decl_oneOf_Load);
        Func fun_load_watts = new Func(null, "load_watts", Util.asList(decl_oneOf_Load), SIGINT.oneOf(), load_watts_body);

        */
/*fun supply_watts [s: Supply]: one Int
           sum s.watts*//*

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


        */
/**Predicate: body = g: some Grid*//*


        List<Sig> sigs = Arrays.asList(new Sig[]{Grid, Circuit, SupplyCircuit, LoadCircuit, Component, Load, Supply, Switch, GP, SP, Wind, Geo, Hydro});


        expression = Circuit.some();
        command = makeCommand(4);
        command = changeCommand(command, Grid, false, 1);

        Func someGrid = new Func(null, "SomeG", null, null, expression);

        staticSigs = sigs;

        //run(sigsFromMeta, command);




    }
*/

    public static void main(String[] args){
        try {
            setUp();
            Command cmd = makeCommand(3);

            A4Solution solution = run(cmd);
            testConstraints.add(load$0);
            testConstraints.add(load$1);
            testConstraints.add(load$2);
            testConstraints.add(wind$0);
            testConstraints.add(wind$1);
            sendConstraints(testConstraints);

            while (!checkSpecificConstraints(solution)){
                checkSpecificConstraints(getNext(solution));
            }



        }catch (Err e){
            e.printStackTrace();
        }
    }

    public static Command makeCommand(int forInt) throws Err{
        expression = world.getAllSigs().get(1).some();
        return new Command(false, forInt, 8, 7, expression);
    }

    public static Command changeCommand(Command prev, Sig sig, boolean exact, int number) throws Err{
        return prev.change(sig, exact, number);
    }

    public static SafeList<Sig> getSigs(){
            return world.getAllSigs();
    }

    public static List<Command> getCommands (){
        return world.getAllCommands();
    }

    public static boolean makeCSVFile(ArrayList<Long> times) throws Err{

        try {
            PrintWriter writer = new PrintWriter(file);
            long solutionNumber = 0;
            for(Long l : times){
                writer.println(solutionNumber + "\t" + l);
                solutionNumber ++;
            }
            writer.close();
            return true;
        } catch (FileNotFoundException f){
            System.out.printf("File not found.");
            makeCSVFile(times);
            return false;
        }

    }

    public static A4Solution run(Command cmd) throws Err {

        List<Sig> sigs = world.getAllReachableSigs();
        A4Solution solution = (TranslateAlloyToKodkod.execute_command(A4Reporter.NOP, sigs, cmd, options));
        return solution;

    }

    public static void visualize(A4Solution solution) {
        try{

            solution.writeXML("xmlCircuitry.xml");
            VizGUI visualizer = new VizGUI(false, "xmlCircuitry.xml", null);
            AlloyerForm.currentModelForm = visualizer;
            visualizer.doShowViz();
        }catch (NullPointerException n){
        } catch ( Err e){
            e.printStackTrace();
        }
    }
    public static A4Solution visualizeNext(A4Solution solution, VizGUI visualizer) {
        try{
            if(solution.satisfiable()){
                A4Solution next = solution.next();
                next.writeXML("xmlCircuitry.xml");
                //VizGUI visualizer = new VizGUI(true, "xmlCircuitry.xml", null);
                visualizer.doClose();
                visualizer = new VizGUI(false, "xmlCircuitry.xml", null);
                AlloyerForm.currentModelForm = visualizer;
                visualizer.doShowViz();
                return next;
            }else{
                return null;
            }
        }catch (NullPointerException n){
            return null;
        } catch ( Err e){
            e.printStackTrace();
            return null;
        }
    }

    public static void evaluateSolutionPerformance(A4Solution solution, long times){

            ArrayList<Long> timesList = new ArrayList<Long>((int)times);
            try {
                long i = 0;

                while (solution.satisfiable() && i < times) {
                    long start = System.currentTimeMillis();

                    solution = solution.next();
                    long end = System.currentTimeMillis();
                    i++;
                    timesList.add(end - start);
                    System.out.printf("\nSolution: % d; Time between solutions: %d", i, (end - start));
                }
                makeCSVFile(timesList);

            }catch (Err e){
                e.printStackTrace();
            }
    }

    public static A4Solution getNext(A4Solution solution){
        try{
            if(solution.satisfiable()){
                return solution.next();
            }else{
                return null;
            }

        }catch (Err e){
            e.printStackTrace();
            return null;
        }
    }


    public static void sendConstraints(ArrayList<OurSig> sigs){
        constrainedSigs = sigs;
        for(OurSig o : constrainedSigs){
            namesOfConstrainedSigs.add(o.getLabel());
            typesOfConstrainedSigs.add(o.getType());
            namesToConstrainedOurSigs.putIfAbsent(o.getLabel(), o);
        }
    }

    public static A4Solution getNewInstance(A4Solution solution){
        try {
            while (!checkConstraints(solution) && solution.satisfiable()) {
                A4Solution temp = solution.next();
                if(checkConstraints(temp)){
                    return temp;
                }else if (temp.satisfiable() && !checkConstraints(temp)){
                    getNewInstance(temp.next());
                }else{
                    return null;
                }
            }
            return null; //shouldn't get here unless there are no more satisfying instances
        }catch (Err e ){
            e.printStackTrace();
            return null;
        }

    }

    public static boolean checkSpecificConstraints(A4Solution solution){

        boolean pass = true;

        HashMap<String, OurSig> namesToInstanceOurSigs = new HashMap<>();
        ArrayList<String> linesOfSolution = new ArrayList<>(Arrays.asList(testSolution.split("\n")));
        ArrayList<String> hasRelationshipAndConstrained = new ArrayList<>();

        //ArrayList<String> linesOfSolution = new ArrayList<>(Arrays.asList(solution.toString().split("\n")));

        //for every line of a solution instance, check for the sequence :> denoting a "has" relationship
        for(String s : linesOfSolution){
            if(s.contains("<:")){
                for(String l : typesOfConstrainedSigs){
                    //now check the names of the constrained sigs. if the line is a "has" relationship for a constrained sig,
                    //put it aside in a special arraylist
                    if(s.contains(l+"<:")){
                        hasRelationshipAndConstrained.add(s);
                    }
                }
            }
        }
        //now for each of these lines, we're going to grab the integer values associated with the signature. in our case,
        //these are wattages
        for(String s : hasRelationshipAndConstrained){
            //if the line specifies wattage
            if(s.contains("watts")) {

                ArrayList<String[]> specificSigsToWatts = new ArrayList<>();

                //get the array of watt relationships to establish the values
                specificSigsToWatts.add(s.split("[<:{\b\\->,?\b}]"));

                for (String[] p : specificSigsToWatts) {
                    String type = p[0].trim();
                    for (int i = 3; i < p.length - 2; i+= 3) {
                        OurSig instanceSig = new OurSig(type, p[i].trim(), Integer.parseInt(p[i + 2].trim()));
                        namesToInstanceOurSigs.putIfAbsent(instanceSig.getLabel(), instanceSig);
                    }
                }
            }else{
                //otherwise, the line does not contain a wattage relationship
                //         this/Load<:supply={Load$0->Wind$1, Load$1->Wind$1, Load$2->Wind$1}
                //TODO: handle relationships later
                String[] relationships = s.split("[<:{\b\\->,?}]");
                for(String relation : relationships){
                    //System.out.printf(relation + "\n");
                }
            }
        }

        Iterator<OurSig> sigsConstrainedEnumeration = namesToConstrainedOurSigs.values().iterator();

        while(sigsConstrainedEnumeration.hasNext() && pass){
            OurSig toCompareConstrained = sigsConstrainedEnumeration.next();
            OurSig toCompareInstance = namesToInstanceOurSigs.get(toCompareConstrained.getLabel());

            pass = namesToInstanceOurSigs.containsKey(toCompareConstrained.getLabel()) && namesToInstanceOurSigs.get(toCompareConstrained.getLabel()).isEqual(toCompareConstrained);

        }

        System.out.printf("Result: %s\n", pass);

        return pass;

    }

    public static boolean checkConstraints(A4Solution solution){
        boolean pass = false;
        ArrayList<OurSig> sigsFromSolution = new ArrayList<>();


        //String[] templines = solution.toString().split("\n");

        //splits the solution into single lines
        String[] templines = testSolution.split("\n");
        ArrayList<String> linesArrayList = new ArrayList<String>(templines.length);

        //add lines to an array list
        for(String s : templines){
            linesArrayList.add(s);
        }

        //get all of the "<:" (has) relationships and store them in a single string
        StringBuilder relationships = new StringBuilder();
        for(String s : linesArrayList){
            if(s.contains("<:")){
                relationships.append(s + "\n");
            }
        }

        //this is an overwritten array from earlier
        templines = relationships.toString().split("\n");

        ArrayList<ArrayList<String>> relations = new ArrayList<>();

        for(String s : templines){
            ArrayList<String> temp = new ArrayList<>();
            //get the lines in which watts is assigned
                if(s.contains("watts")){

                    ArrayList<String[]> specificSigsToWatts = new ArrayList<>();

                    specificSigsToWatts.add(s.split("[<:{\\->,\b}]"));

                    for(String[] p : specificSigsToWatts){
                        System.out.printf("\n");
                        for(String ind : p){
                            System.out.printf(ind + " ");
                        }
                    }
                    //a print statement for testing
                    //to stop null pointer problems here
                    /*if(w.length() > 0){
                       // Wind$0->8, Wind$1->8
                        //splits Wind$0->8, Wind$1->8 into String[] {Wind$0->8, Wind$1->8}
                    for(String q : w.split("[,\b]")){
                        String[] info = q.split("[->]"); //String[] {Wind$1, "", 8}
                        //name = Wind$1
                        String name = info[0];
                        //type = this/Wind
                        String type = "this/" + name.split("[$\\d]")[0];
                        //num = 8
                        int num = Integer.parseInt(info[2]);
                        //adds new simple sig to our temp hashtable
                        sigsFromSolution.add(new OurSig(namesToSig.get(type), name, num));
                    }}*/

                }
            //for every other line that does not contain watts, it's just a relationship
            for(String s2 : (s.split("[<:=\\{,?\\}]"))){
                temp.add(s2);
            }
            relations.add(temp);
        }

        for(ArrayList<String> lines : relations){
            for(String word : lines){
                if(word.contains("->")){
                    //splits the arrow to get the parent and the child
                    String ourSigName = word.split("->")[0];
                    String childSigName = word.split("->")[1];

                    for(OurSig p : sigsFromSolution){
                        if(p.getLabel() == ourSigName){
                            p.addRelation(childSigName);
                        }
                    }

                }
            }

        }

        for(OurSig l : sigsFromSolution){
            System.out.printf(l.toString());
        }

        return pass;

    }






}
