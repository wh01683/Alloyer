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

    private static File file = new File(System.getProperty("user.dir") + "/timesData.csv");
    private static Expr expression;
    private static Command command;
    private static A4Options options = new A4Options();

    private static String dirPath = "C:/Users/Lindsey/AppData/Local/Temp/alloy4tmp40-Lindsey/";
    private static String alsDirPath = dirPath + "models/circuitry.als";
//    private static String dirPath = "/tmp/alloy4tmp40-robert/";
//    private static String alsDirPath = dirPath + "models/circuitry.als";
    private static Hashtable<String, Sig> namesToSig = new Hashtable<>(20); //will store a mapping of String type names to Signature objects
    private static ArrayList<OurSig> ourSigs;
    public static Hashtable<Long, String> TEST_HASH_TABLE = new Hashtable<>(5000);
    private static Module world;
    private static String[] debugTestRelationships = {"Supply_Circuit$0->Wind$0"};

    /**
     * main method to initialize the GridMetamodel class.
     * @param args nullable, does nothing.
     */
    public static void main(String[] args){
        try {
            setUp();
            command = makeCommand(4);

            //evaluateSolutionPerformance(run(command), 10003);

            System.out.println(findSolution(run(command).next(), debugTestRelationships));



        }catch (Err e){
            e.printStackTrace();
        }
    }

    /**
     * Called in main
     * The initial setup function to establish the A4Options for creating solutions and to pick the SAT engine. This
     * method will also parse a given .als file and obtain the module from that file using the Alloy API.
     */
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


    /**
     * Creates a new static command using a given general for (some integer), a bitwidth of 8, a Seq/Int of 7, and an
     * unchanged expression equal to the Alloy, "Circuit.some()." In later versions, this should be both dynamic and
     * more complex.
     *
     * **NOTE: If you want to make a more complex command, make a new command and call the run(yourCommand) static method
     * to obtain your A4Solution.
     *
     * @param forInt the "X" in run command for X, but 8 Int
     * @return returns the newly created command
     * @throws Err thrown by Alloy API
     */
    public static Command makeCommand(int forInt) throws Err{
        expression = world.getAllSigs().get(1).some();
        command = new Command(false, forInt, 4, 3, expression);
        return command;
    }

    /**
     * A simplified version of the Alloy API method used to change a given command.
     * Example in Alloy:
     * run myPredicate for 4 but 4 SigX, exactly 3 SigY, 1 SigZ, 8 Int
     *
     * The keyword "but" signals this command change.
     *
     * @param prev Current command. This is the command to be changed.
     * @param sig The Sig object to specify the change of. (Ex. SigX, SigY, SigZ)
     * @param exact True: exactly 3 SigY. False: 3 SigY
     * @param number The 4 in "but 4 SigX"
     * @return Returns the new command. Note that due to the nature of this method, you may only change one Sig at a time,
     * but you may make more successive changes by calling this function iteratively on each new command created.
     * @throws Err thrown by Alloy API.
     */
    public static Command changeCommand(Command prev, Sig sig, boolean exact, int number) throws Err{
        command = prev.change(sig, exact, number);
        return command;
    }

    /**
     * Returns the SafeList object from the module parsed from the file. This list will include only Sigs created
     * in the .als file.
     * @return SafeList of Sigs from the parsed module.
     */
    public static SafeList<Sig> getSigs(){
            return world.getAllSigs();
    }

    /**
     * @return Returns the list of Command objects from the parsed module.
     */
    public static List<Command> getCommands (){
        return world.getAllCommands();
    }

    /**
     * Creates a csv file given a Hashtable mapping solution iteration number to the solution's information
     * @param solutionInformation Hashtable to create the .csv file from.
     */
    private static void makeCSVFile(Hashtable<Long, String> solutionInformation){

        try {
            PrintWriter writer = new PrintWriter(file);
            Iterator<Long> solutionIterator = solutionInformation.keySet().iterator();

            writer.println("iteration\trunTime\tuniqueTuples\ttotalTuples\tmaxArity");
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

    /**
     * Generates and returns a new A4Solution object using the Command object passed through params and the list of Sigs
     * obtained from the parsed module.
     * @param cmd Command to create the A4Solution with.
     * @return returns the newly created solution.
     * @throws Err thrown by the Alloy API.
     */
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
            isDuplicate(i, solution);
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
    public static String getSigInformation(A4Solution solution, Sig sig){

        StringBuilder sigInfo = new StringBuilder();

        for(Sig.Field f : sig.getFields()){
            sigInfo.append(solution.eval(f) + "\n");
        }

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

    /**
     * Currently, this method ONLY existed to check whether or not the solution iterator is producing duplicate solutions.
     * Keep in mind that this method uses a brute force search method, cycling through each previously constructed
     * solution to check for duplicates matching the passed solution.
     * @param iteration Current iteration number passed from the evaluateSolutionPerformance method.
     * @param solution Current solution to check.
     * @return returns false and adds the solution to the hashtable if the current solution is not in the hashtable.
     * If the passed solution matches one in the hashtable, the method will terminate the program.
     */
    private static boolean isDuplicate(long iteration, A4Solution solution){
        for(String sigInfo : TEST_HASH_TABLE.values()){
            if(iteration % 5000 == 0){
                printSolToFile((int)iteration, TEST_HASH_TABLE);
                TEST_HASH_TABLE = new Hashtable<>(5000);
            }
            if(sigInfo.equalsIgnoreCase(getAllSigInfo(solution))){
                System.out.println("Duplicate found. Iteration number: " + iteration);
                System.exit(10);
            }
            else{
                TEST_HASH_TABLE.put(iteration, getAllSigInfo(solution));
                return false;
            }
        }
        TEST_HASH_TABLE.put(iteration, getAllSigInfo(solution));
        return false;
    }

    private static void printSolToFile(int num, Hashtable<Long, String> solutions){
        try {
            PrintWriter writer = new PrintWriter(new File(System.getProperty("user.dir") + "/solutions" + num + ".txt"));

            for(String s : solutions.values()){
                writer.print(s + "\n");
            }

            writer.close();

        } catch (FileNotFoundException f){
            System.out.printf("File not found.");
            printSolToFile(num, solutions);
        }
    }

    private static String getAllSigInfo(A4Solution solution){
        StringBuilder info = new StringBuilder();
        for(Sig s : solution.getAllReachableSigs()){
            for(Sig.Field f : s.getFields()){
                info.append(solution.eval(f) + "\n");
            }
        }

        return info.toString();
    }

    public static String findSolution(A4Solution solution, String[] relationships){
        HashSet<String> relationHash = new HashSet<>(Arrays.asList(relationships));
        A4Solution localSolution = solution;
        boolean pass = false;
        int i = 0;

        while(!pass){
            i++;
            if(i%500 == 0) {
                System.out.println("Iteration " + i);
            }
            HashSet<String> tuples = new HashSet<>();
            for(Sig s : solution.getAllReachableSigs()){
                for(Sig.Field f : s.getFields()){
                    for(A4Tuple t : solution.eval(f)){
                    tuples.add(t.toString());
                    }
                }
            }
            if(tuples.containsAll(relationHash)){
                pass = true;
                StringBuilder ans = new StringBuilder();
                for(Sig s : solution.getAllReachableSigs()){
                    for(Sig.Field f : s.getFields()){
                        ans.append(solution.eval(f).toString() + "\n");
                    }
                }
                return ans.toString();
            }else{
                pass = false;
                localSolution = getNext(localSolution);
            }
        }

        //if we get here, no viable solution was found.
        return null;
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
