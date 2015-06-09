package Implementation;

import java.io.*;

/**
 * Created by robert on 6/9/15.
 */
public class DataIO  {

    static MasterDomain domain = new MasterDomain();
    static String saveAs = "domain";
    static String fileName = System.getProperty("user.dir") + "/"+saveAs+".dom";
    static File domainFile;

    public DataIO(MasterDomain newDomain){
        domain = newDomain;
    }

    public static void setSaveAs(String newSaveAs){
        saveAs = newSaveAs;
    }

    public static void setDomain(MasterDomain domain) {
        DataIO.domain = domain;
    }

    public String getSaveAs (){return saveAs;}

    public static void saveDomain(String newFileName) {
        try{

            if(newFileName != null){
                setSaveAs(newFileName);
            }

            MasterDomain nonStaticDomain = domain;

            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(nonStaticDomain);

            outputStream.close();
            fileOutputStream.close();

        }catch (FileNotFoundException f){
            System.out.printf("File not found.");
            f.printStackTrace();
        }catch (IOException i){
            System.out.printf("Something happened??");
            i.printStackTrace();
        }
    }

    public static MasterDomain loadDomain(String newFileName) throws FileNotFoundException{
        try {
            if(newFileName != null){
                setSaveAs(newFileName);
            }

            File newFile = new File(fileName);

            if(newFile == null){
                newFile.createNewFile();
            }

            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            domain = (MasterDomain)objectInputStream.readObject();


            return domain;


        } catch (IOException i) {
            System.out.printf("File does not exist.");
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.printf("Object in the file does not match current MasterDomain class.\nPerhaps the class was changed recently and you need to save a new version to file.");
        }
        return null;
    }

}
