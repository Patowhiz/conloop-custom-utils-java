package conloop;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

/**
 *
 * @author PatoWhiz 24/04/2018 04:11 AM
 * modified on 02/03/2022 02:41 PM at Machakos house
 *
 */
public class FXFormLoader {

    private FXMLLoader loader;
    private String filePathAndName;//fxml file path name
    private Class clazz; //would normally be the main class

    public FXFormLoader(Class clazz) {
        this.clazz = clazz;
    }

    public FXFormLoader(Class clazz, String strfilePathAndName) {
        this(clazz);
        this.filePathAndName = strfilePathAndName;
    }

    //changing the file path automatically invalidates the current root node
    public void setFilePathAndName(String filePathAndName) {
        this.filePathAndName = filePathAndName;
        this.loader = null;
    }

    public String getFilePathAndName() {
        return filePathAndName;
    }

    public void buildForm() throws IOException {
        // Parent root = FXMLLoader.load(  ParentForm.class.getResource(filePathAndName));
        //InputStream in;
        //loader = new FXMLLoader();
        //in = clazz.getResourceAsStream(filePathAndName);
        //loader.setBuilderFactory(new JavaFXBuilderFactory());
        //loader.setLocation(clazz.getResource(filePathAndName));
        //try {
        //    rootNode = loader.load(in);
        //} finally {
        //    in.close();
        //}//end try

        //initialise the loaader with the location of the fxml document
        loader = new FXMLLoader(clazz.getResource(filePathAndName));
        //load the fxml document and its resources
        loader.load();
    }

    public Parent getRootNode() throws IOException {
        //build the form if root node is null.
        //one can just getRootNode without having previously called the buildForm 
        if (loader == null) {
            buildForm();
        }
        return loader.getRoot();
    }

    public VBox getRootNodeAsVBox() throws IOException {
        return (VBox) getRootNode();
    }

    public FXMLLoader getLoader() throws IOException {
        //build the if root node is null. Null root node means form not yet built
        //requested loader should always have a built form associated with it
        if (loader == null) {
            buildForm();
        }
        return loader;
    }

    public Initializable getController() throws IOException {
        return (Initializable) getLoader().getController();
    }

    public void dispose() {
        loader = null;
        clazz = null;
        filePathAndName = null;
    }

}//end class
