package Implementation;

import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

import java.util.HashMap;

/**
 * Created by robert on 6/18/15.
 */
public class OurSig {

    private int hashKey;
    private Sig it;
    private String label;
    private int value;
    private HashMap<String, String> relations = new HashMap<>(5);

    public OurSig (Sig s, String name, int val){
        this.hashKey = this.hashCode();
        this.it = s;
        this.label = name;
        this.value = val;
    }

    public void addRelation(String relative){
        this.relations.putIfAbsent(this.label, relative);
    }

    public int getHashKey() {
        return hashKey;
    }

    public Sig getIt() {
        return it;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }

    public HashMap<String, String> getRelations() {
        return relations;
    }

    public String toString(){
        String relationsString = "";

        if(!(relations == null)){
            for(String s : relations.keySet()){
                if(s != null){
                    relationsString += s + "->" + relations.get(s) + "\b";
                }
            }
        }


        String returnString = (((it == null)? "" : it.toString()) + " " + label + " " + value + "Relations:\n " + relationsString + "\n");


        return returnString;
    }
}
