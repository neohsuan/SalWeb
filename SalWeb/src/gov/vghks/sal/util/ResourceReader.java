/*
 * Created on May 7, 2007
 *
 */
package gov.vghks.sal.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;


public class ResourceReader {

    private static ResourceReader me;

    private ResourceBundle resourceBundle;

    private ResourceReader() {
        resourceBundle = ResourceBundle.getBundle("gov.vghks.sal.resources.ApplicationResources");
    }

    public static ResourceReader getInstance() {
        if (me == null) {
            me = new ResourceReader();
        }
        return me;
    }

    /** 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param key
	 * @return the Resource Value
	 * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
    public String getResource(String key) {
        return resourceBundle.getString(key);
    }
    
	/** 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param key
	 * @param objects
	 * @return the Resource Value
	 * @generated "UML to Java (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
	 */
    public String getResource(String key, Object... objects) {
    	return MessageFormat.format(resourceBundle.getString(key), objects);
    }
}