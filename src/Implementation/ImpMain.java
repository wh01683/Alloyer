package Implementation;

import edu.mit.csail.sdg.alloy4.Err;

/**
 * Created by robert on 5/29/15.
 */
public class ImpMain {

    public static void main(String[] args) throws Err {

        MasterDomain domain = (MasterDomain.loadDomain(null) == null)? new MasterDomain() : MasterDomain.loadDomain(null);

        Form1 form1 = new Form1(domain);


       /* TestDomain x;
        x = new TestDomain();


        x.makeDomain();
        x.makeInstance1();

        x.runFor3(x.file.some());

        x = new TestDomain();
        x.makeDomain();
        x.makeInstance1();
        x.runFor3(TestDomain.acyclic(x.contains).not());*/

    }



}
