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

//    private static String dirPath = "C:/Users/Lindsey/AppData/Local/Temp/alloy4tmp40-Lindsey/";
//    private static String alsDirPath = dirPath + "models/circuitry.als";
   private static String dirPath = "/tmp/alloy4tmp40-robert/";
   private static String alsDirPath = dirPath + "models/circuitry.als";
    private static Hashtable<String, Sig> namesToSig = new Hashtable<>(20); //will store a mapping of String type names to Signature objects
    public static Hashtable<Long, String> TEST_HASH_TABLE = new Hashtable<>(5000);
    private static Module world;
    private static String[] debugTestRelationships = {"Supply_Circuit->Wind", "Switch->Supply_Circuit"};
    private static Hashtable<String[], A4Solution> successfulSolutions = new Hashtable<>();

    /**
     * main method to initialize the GridMetamodel class.
     * @param args nullable, does nothing.
     */
    public static void main(String[] args){
        try {
            setUp();
            command = makeCommand(4);

            //evaluateSolutionPerformance(run(command), 10003);

            System.out.println(findSolution(run(command).next(), debugTestRelationships, false, 70000));



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

    public Hashtable<String[], A4Solution> getSuccessfulSolutions() {
        return successfulSolutions;
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

    /**
     * Prints prints a hashtable of String solutions mapped to Long values. These strings can be the entire A4Solution to
     * String or just desired relationships.
     * @param num number to append to the end of the file name. format: solutions+num+.txt
     * @param solutions Hashtable to print to file.
     */
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


    /**
     * Returns String representation of a specific solution's evaluated fields on separate lines. These are essentially
     * the "have" relationships of the solution, denoted in the A4Solution.toString() as " :> "
     * @param solution Solution to get the relationships from
     * @return returns a String representation of all relationships in the given solution.
     */
    public static String getAllSigInfo(A4Solution solution){
        StringBuilder info = new StringBuilder();
        for(Sig s : solution.getAllReachableSigs()){
            for(Sig.Field f : s.getFields()){
                info.append(solution.eval(f) + "\n");
            }
        }

        return info.toString();
    }

    /**
     * Finds a solution matching the given String array of relationships by iterating through the solutions, storing
     * the new solution's relationships in a Hashset, and checking the new Hashset to see if it contains all the String
     * relationships in the array. If param useSpecificNames is false, the method will ignore labels such as Wind$0,
     * focusing only on "Wind". If specific label matching is required, useSpecificNames must be true.
     * @param solution Initial solution to iterate through
     * @param relationships String array of relationships to check for, in the format, String[1] = {Wind$0->Load$2} and etc.
     * @param useSpecificNames If false, method will ignore specific labels (like Wind$0->Load$2 and will instead look for Wind->Load)
     * @param iterationCap Limit on number of iterations to cycle through before giving up. Default is 85000, as Java runs
     *                     out of memory at 85500 due to Tuple set cloning.
     * @return returns the Solution containing the relationships requested.
     */
    public static A4Solution findSolution(A4Solution solution, String[] relationships, boolean useSpecificNames, int iterationCap) {

        Long l = new Long(0);
        A4Solution localSolution = solution;
        int cap = (iterationCap == 0)? 85000 : iterationCap;
        int iteration = 0;

        if (useSpecificNames) {

            HashSet<String> relationHash = new HashSet<>(Arrays.asList(relationships));

            boolean pass = false;

            while (!pass && localSolution != null && iteration < cap) {

                StringBuilder tupe = new StringBuilder();
                HashSet<String> tuples = new HashSet<>();

                    for (Sig s : localSolution.getAllReachableSigs()) {
                        for (Sig.Field f : s.getFields()) {
                            for (A4Tuple t : localSolution.eval(f)) {
                                tupe.append(t.toString());
                                tuples.add(t.toString());
                            }
                        }
                    }

                if (tuples.containsAll(relationHash)) {
                    pass = true;
                    return localSolution;
                } else {
                    pass = false;
                    successfulSolutions.put(relationships, localSolution);
                    localSolution = getNext(localSolution);
                }
                iteration ++;
            }

            //if we get here, no viable solution was found.
            return null;
        } else {

            HashSet<String> relationHash = new HashSet<>();

            for (String s : relationships) {
                relationHash.add(ignoreLabels(s));
            }


            boolean pass = false;

            while (!pass && localSolution != null && iteration < cap) {

                HashSet<String> tuples = new HashSet<>();
                for (Sig s : localSolution.getAllReachableSigs()) {
                    for (Sig.Field f : s.getFields()) {
                        for (A4Tuple t : localSolution.eval(f)) {
                            tuples.add(ignoreLabels(t.toString()));
                        }
                    }
                }
                if (tuples.containsAll(relationHash)) {

                    return localSolution;
                } else {
                    pass = false;
                    successfulSolutions.put(relationships, localSolution);
                    localSolution = getNext(localSolution);
                }
                iteration++;
            }
            //if we get here, no viable solution was found.
            return null;
        }
    }

    /**
     * Private utility function to get rid of the $n label attachments on the String relationship. Used by the findSolution
     * method if usesSpecificNames is set to false.
     * @param labeledRelationship Single string representation of a relationship, or one index of the Relationships array. (Wind$0->Load$2)
     * @return returns the trimmed String (Wind->Load)
     */
    private static String ignoreLabels(String labeledRelationship){

        String[] pieces = labeledRelationship.split("[$\\d]");
        StringBuilder ans = new StringBuilder();

        for(String s : pieces) {
            ans.append(s);
        }

        return ans.toString();


    }
}
