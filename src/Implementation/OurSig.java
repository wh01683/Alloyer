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
    private HashMap<String, String> relations;

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
        for(String s : relations.keySet()){
            relationsString += s + "->" + relations.get(s) + "\b";
        }
        return (it.toString() + " " + label + " " + value + "Relations:\n " + relationsString);
    }
}
