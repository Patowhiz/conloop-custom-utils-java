package conloop;

import java.io.IOException;
import java.io.InputStream;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
//import votebook.forms.VoteBook;

/**
 *
 * @author PatoWhiz 24/04/2018 04:11 AM
 */
public class FXFormLoader {

    private FXMLLoader loader;
    private Parent rootNode;
    private VBox rootNodeAsVBox;
    private String filePathAndName;
    private final Class clazz;

    public FXFormLoader(Class clazz) {
        this.clazz = clazz;
        this.rootNode = null;
        this.rootNodeAsVBox = null;
    }

    public FXFormLoader(Class clazz, String strfilePathAndName) {
        this(clazz);
        this.filePathAndName = strfilePathAndName;
    }

    //changing the file path automatically invalidates the current root node
    public void setFilePathAndName(String filePathAndName) {
        this.filePathAndName = filePathAndName;
        this.rootNode = null;
    }

    public String getFilePathAndName() {
        return this.filePathAndName;
    }

    public void buildForm() throws IOException {
        // Parent root = FXMLLoader.load(  ParentForm.class.getResource(filePathAndName));
        InputStream in;
        loader = new FXMLLoader();
        in = clazz.getResourceAsStream(filePathAndName);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(clazz.getResource(filePathAndName));
        try {
            rootNode = loader.load(in);
        } finally {
            in.close();
        }//end try

        //TODO
        //FIND TIME TO TEST THE CODE BELOW
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("MyGui.fxml"));
        //Parent root = (Parent) loader.load();
        //MyController controller = (MyController) loader.getController();
        //controller.setStageAndSetupListeners(stage); // or what you want to do
    }

    public Parent getRootNode() throws IOException {
        //build the form if root node is null.
        //one can just getRootNode without having previously called the buildForm 
        if (rootNode == null) {
            buildForm();
        }
        return rootNode;
    }

    public void setRootNode(Parent rootNode) {
        this.rootNode = rootNode;
    }

    public VBox getRootNodeAsVBox() throws IOException {
        //Only cast the root VBox node once.
        //Because when called severally we want the same copy
        //I did this because I'm not sure if it will give a different copy each time casting is done
        //Investitigate the casting aspect
        if (rootNodeAsVBox == null) {
            rootNodeAsVBox = (VBox) getRootNode();
        }
        return rootNodeAsVBox;
    }

    public FXMLLoader getLoader() throws IOException {
        //build the if root node is null. Null root node means form not yet built
        //requested loader should always have a built form associated with it
        if (rootNode == null) {
            buildForm();
        }
        return loader;
    }

    public Initializable getController() throws IOException {
        return (Initializable) getLoader().getController();
    }

}//end class
