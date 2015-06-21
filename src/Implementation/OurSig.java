package Implementation;

import java.util.HashMap;

/**
 * Created by robert on 6/18/15.
 */
public class OurSig {

    private int hashKey;
    private String label;
    private String type;
    private String subType;
    private int value;
    private HashMap<String, String> relations = new HashMap<>(5);

    public OurSig(String type, String subType, String name, int val){
        this.hashKey = this.hashCode();
        this.type = type;
        this.label = name;
        this.value = val;
    }

    public void addRelation(String relative){
        this.relations.putIfAbsent(this.label, relative);
    }

    public int getHashKey() {
        return hashKey;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }

    public String getSubType() {
        return subType;
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


        String returnString = ("Type: " + this.type + " Name: " + label + " Value: " + value + " Relations:\n " + relationsString + "\n");


        return returnString;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public boolean isEqual(OurSig sig){
        return ((sig.getLabel().equalsIgnoreCase(this.getLabel())) && (sig.getValue() == this.getValue()));
    }
}
