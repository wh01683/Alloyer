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
import edu.mit.csail.sdg.alloy4compiler.translator.A4Tuple;
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

    private static OurSig wind$0 = new OurSig("this/Supply", "this/Wind", "Wind$0", 8);
    private static OurSig wind$1 = new OurSig("this/Supply", "this/Wind", "Wind$1", 8);
    private static OurSig load$0 = new OurSig("this/Load", "this/Wind", "Load$0", 0);
    private static OurSig load$1 = new OurSig("this/Load", null, "Load$1", 2);
    private static OurSig load$2 = new OurSig("this/Load", null, "Load$2", 1);
    private static ArrayList<OurSig> testConstraints = new ArrayList<>();


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

    private static File file = new File(System.getProperty("user.dir") + "/timesData.csv");
    private static Expr expression;
    private static Command command;
    private static A4Options options = new A4Options();
    //public static A4Solution solution;

//    private static String dirPath = "C:/Users/Lindsey/AppData/Local/Temp/alloy4tmp40-Lindsey/";
//    private static String alsDirPath = dirPath + "models/circuitry.als";
    private static String dirPath = "/tmp/alloy4tmp40-robert/";
    private static String alsDirPath = dirPath + "models/circuitry.als";
    private static Hashtable<String, Sig> namesToSig = new Hashtable<>(20); //will store a mapping of String type names to Signature objects
    private static ArrayList<OurSig> ourSigs;

    private static Module world;

    public static void main(String[] args){
        try {
            setUp();
            command = makeCommand(4);

            testConstraints.add(load$0);
            testConstraints.add(load$1);
            testConstraints.add(load$2);
            testConstraints.add(wind$0);
            testConstraints.add(wind$1);
            sendConstraints(testConstraints);

            evaluateSolutionPerformance(run(command), 10000);


        }catch (Err e){
            e.printStackTrace();
        }
    }

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


    public static Command makeCommand(int forInt) throws Err{
        expression = world.getAllSigs().get(1).some();
        command = new Command(false, forInt, 8, 7, expression);
        return command;
    }

    public static Command changeCommand(Command prev, Sig sig, boolean exact, int number) throws Err{
        command = prev.change(sig, exact, number);
        return command;
    }

    public static SafeList<Sig> getSigs(){
            return world.getAllSigs();
    }

    public static List<Command> getCommands (){
        return world.getAllCommands();
    }

    private static void makeCSVFile(Hashtable<Long, String> solutionInformation){

        try {
            PrintWriter writer = new PrintWriter(file);
            Iterator<Long> solutionIterator = solutionInformation.keySet().iterator();

            writer.println("iteration\t\\time\tuniqueTuples\ttotalTuples\tmaxArity");
            Long index = (long)0;
            while(solutionIterator.hasNext()){
                writer.print(index + "\t" + solutionInformation.get(index));
                index = solutionIterator.next();
            }

            writer.close();

        } catch (FileNotFoundException f){
            System.out.printf("File not found.");
            makeCSVFile(solutionInformation);
        }

    }

    /**
     * Local method used to populate the information table to print to a csv file using tab delimiters.
     * @param solution solution to obtain information from
     * @return StringBuilder containing some of the information to print to file. Returned in StringBuilder format so
     * other methods can add information before printing to file.
     */

    private static StringBuilder getSolutionInformation(A4Solution solution){

        StringBuilder solutionInformation = new StringBuilder();
        Hashtable<A4Tuple, Integer> uniqueTuples = new Hashtable<>();
        Vector<Integer> arities = new Vector<>(solution.getAllReachableSigs().size());

        for(Sig s : solution.getAllReachableSigs()) {
            for (Sig.Field f : s.getFields()) {
                for (A4Tuple t : solution.eval(f)) {
                    if (uniqueTuples.containsKey(t)) {
                        int tupleVal = uniqueTuples.get(t);
                        tupleVal++;
                        uniqueTuples.put(t, tupleVal);
                    } else {
                        uniqueTuples.put(t, 1);
                    }
                }
                arities.add(solution.eval(f).arity());
            }
        }

        Integer totalTuples = 0;
        for(Integer i : uniqueTuples.values()){
            totalTuples+= i;
        }
        //number of unique tuples in the solution
        solutionInformation.append(uniqueTuples.values().size() + "\t");
        //total number of tuples
        solutionInformation.append(totalTuples + "\t");
        //maximum arity in the solution
        solutionInformation.append(Collections.max(arities) + "\t");


        return solutionInformation;
    }

    public static A4Solution run(Command cmd) throws Err {

        List<Sig> sigs = world.getAllReachableSigs();
        return TranslateAlloyToKodkod.execute_command(A4Reporter.NOP, sigs, cmd, options);

    }

    /**
     * Uses the Alloy VizGUI class to create a graphical representation of the solution
     * @param solution solution to visualize
     */
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


    /**
     * Visualizes the next solution instance in the current solution's iteration set.
     * @param solution Current solution to iterate.
     * @param visualizer Current VizGUI to use.
     * @return returns the newly iterated solution and produces a visualization of the new solution.
     */
    public static A4Solution visualizeNext(A4Solution solution, VizGUI visualizer) {
        try{

                A4Solution next = getNext(solution);
                next.writeXML("xmlCircuitry.xml");
                //VizGUI visualizer = new VizGUI(true, "xmlCircuitry.xml", null);
                visualizer.doClose();
                visualizer = new VizGUI(false, "xmlCircuitry.xml", null);
                AlloyerForm.currentModelForm = visualizer;
                visualizer.doShowViz();
                return next;

        }catch (NullPointerException n){
            return null;
        } catch ( Err e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Evaluates the performance of a solution by recording execution information about the solution including the time
     * required per solution iteration and solution complexity information. After execution, the information is written
     * to a .csv file for analyzation using R or a spreadsheet program.
     * @param solution Solution to be analyzed (method will iterate through solutions starting from this one)
     * @param times Number of times to run through the solution.
     */
    private static void evaluateSolutionPerformance(A4Solution solution, long times){

                Hashtable<Long, String> solutionInfo = new Hashtable<>((int)times);
                long outerStart = System.currentTimeMillis();
                System.out.println("Start Time: " + outerStart);
                for(long i = 0; i < times; i++){
                    if(i%500 == 0){
                        System.out.println("Iteration number " + i + " reached.");
                    }
                    Long start = System.currentTimeMillis();
                    StringBuilder info = getSolutionInformation(solution);
                    solution = getNext(solution);
                    info.insert(0, System.currentTimeMillis() - start);
                    info.append("\n");
                    solutionInfo.put(i, info.toString());
                }

                makeCSVFile(solutionInfo);

                long outerEnd = System.currentTimeMillis();
                System.out.println("End Time: " + outerEnd);

    }

    /**
     * Returns the next iterable solution if the solution is satisfiable.
     * @param solution Solution to iterate.
     * @return Iterated solution. May be equal to the previous solution.
     */
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

    /**
     * This method will return information specific to the specific Sig relative to a solution instance
     * @param solution instance of the solution
     * @param sig Sig object to evaluate
     * @return String representation of an individual Sig's information
     */
    public String getSigInformation(A4Solution solution, Sig sig){
        StringBuilder sigInfo = new StringBuilder();

        int numberOfRelationships = 0;
        int numberOfSigInstances = 0;

        for(Sig.Field f : sig.getFields()){
            numberOfRelationships += solution.eval(f).size();
        }

        sigInfo.append(numberOfSigInstances + "\t" + numberOfRelationships);

        return sigInfo.toString();
    }



    /**
     * Updates the Grid Metamodel with constraints selected by the user through the GUI.
     * @param sigs ArrayList of OurSig objects to represent the constraints desired by the user.
     */
    private static void sendConstraints(ArrayList<OurSig> sigs){
        try {
            setOurSigs(sigs);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    /**
     * Sets the current list of simple Sig objects to the new one.
     * @param ourSigs Current ArrayList of OurSig objects.
     */
    private static void setOurSigs(ArrayList<OurSig> ourSigs) {
        GridMetamodel.ourSigs = ourSigs;
    }
    //    public static boolean checkSpecificConstraints(A4Solution solution){
//
//        boolean pass = true;
//
//        HashMap<String, OurSig> namesToInstanceOurSigs = parseSolution(solution);
//
//        Iterator<OurSig> sigsConstrainedEnumeration = namesToConstrainedOurSigs.values().iterator();
//
//        while(sigsConstrainedEnumeration.hasNext() && pass){
//            OurSig toCompareConstrained = sigsConstrainedEnumeration.next();
//
//            pass = namesToInstanceOurSigs.containsKey(toCompareConstrained.getLabel()) && namesToInstanceOurSigs.get(toCompareConstrained.getLabel()).isEqual(toCompareConstrained);
//
//        }
//
//        return pass;
//
//    }

//    private static HashMap<String, OurSig> parseSolution(A4Solution solution){
//
//        int numberOfRelationships = 0;
//        int numberOfSigInstances = 0;
//        int numberOfSigTypes = 0;
//        int numberOfConstrainedSigTypes = typesOfConstrainedSigs.size();
//
//        Long startTime = System.currentTimeMillis();
//        HashMap<String, OurSig> namesToInstanceOurSigs = new HashMap<>();
//        //ArrayList<String> linesOfSolution = new ArrayList<>(Arrays.asList(testSolution.split("\n")));
//        ArrayList<String> linesOfSolution = new ArrayList<>(Arrays.asList(solution.toString().split("\n")));
//
//        ArrayList<String> hasRelationshipAndConstrained = new ArrayList<>();
//
//        //for every line of a solution instance, check for the sequence :> denoting a "has" relationship
//        for(String s : linesOfSolution){
//            if(s.contains("<:")){
//                numberOfSigTypes++;
//                for(String l : typesOfConstrainedSigs){
//                    //now check the names of the constrained sigs. if the line is a "has" relationship for a constrained sig,
//                    //put it aside in a special arraylist
//                    if(s.contains(l+"<:")){
//                        hasRelationshipAndConstrained.add(s);
//                    }
//                }
//            }
//        }
//        //now for each of these lines, we're going to grab the integer values associated with the signature. in our case,
//        //these are wattages
//        for(String s : hasRelationshipAndConstrained){
//            //if the line specifies wattage
//            if(s.contains("watts")) {
//
//                ArrayList<String[]> specificSigsToWatts = new ArrayList<>();
//
//                //get the array of watt relationships to establish the values
//                specificSigsToWatts.add(s.split("[<:{\b\\->,?\b}]"));
//
//                for (String[] p : specificSigsToWatts) {
//                    String type = p[0].trim();
//                    for (int i = 3; i < p.length - 2; i+= 3) {
//                        OurSig instanceSig = new OurSig(type, p[i].trim().replaceAll("$\\d", ""), p[i].trim(), (p[i + 2].trim().equals(""))? 0 : Integer.parseInt(p[i + 2].trim()));
//                        numberOfSigInstances++;
//                        namesToInstanceOurSigs.putIfAbsent(instanceSig.getLabel(), instanceSig);
//                    }
//                }
//            }else{
//                //otherwise, the line does not contain a wattage relationship
//                //         this/Load<:supply={Load$0->Wind$1, Load$1->Wind$1, Load$2->Wind$1}
//                //TODO: handle relationships later
//                String[] relationships = s.split("[<:{\b\\->,?}]");
//                for(String relation : relationships){
//                    numberOfRelationships++;
//                }
//            }
//        }
//
//        StringBuilder solInfoString = new StringBuilder();
//        Long endTime = System.currentTimeMillis();
//        solInfoString.append((endTime - startTime) + "\t" + numberOfSigTypes + "\t" + numberOfSigInstances + "\t" + numberOfRelationships + "\t" + numberOfConstrainedSigTypes + "\n");
//        solInfo.put(numberOfSolutions, solInfoString.toString());
//        numberOfSolutions++;
//        return namesToInstanceOurSigs;
//    }




}
