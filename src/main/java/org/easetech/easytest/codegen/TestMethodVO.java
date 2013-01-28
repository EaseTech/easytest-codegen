package org.easetech.easytest.codegen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sun.javadoc.MethodDoc;

/**
* ValueObject to encapsulate Test Method information
*
* @author Ravi Polampelli
*
*/

public class TestMethodVO {

  /** Array of MethodDoc, contains 
	 * parameters, signature, return type of all the methods of source class
	 * */
	private MethodDoc[] methodDocs;
	
	/** template properties */
	private Properties properties;
	
	/** set of import classes to be added to test class */
	private Set<String> importsSet;
	
	/** list of parameters and default values */
	private List<Map<String,Object>> methodData;
	
	/** test method code */
	private StringBuffer newCode;
	
	/** Source code of the method from the java source class */
	private StringBuffer methodSourceCode;
	
	public TestMethodVO(){
		super();
	}
	
	public TestMethodVO(MethodDoc[] methodDocs, Properties properties,
			Set<String> importsSet, List<Map<String, Object>> methodData,
			StringBuffer newCode) {
		super();
		this.methodDocs = methodDocs;
		this.properties = properties;
		this.importsSet = importsSet;
		this.methodData = methodData;
		this.newCode = newCode;
	}
	public MethodDoc[] getMethodDocs() {
		return methodDocs;
	}
	public void setMethodDocs(MethodDoc[] methodDocs) {
		this.methodDocs = methodDocs;
	}
	public StringBuffer getMethodSourceCode() {
		return methodSourceCode;
	}

	public void setMethodSourceCode(StringBuffer methodSourceCode) {
		this.methodSourceCode = methodSourceCode;
	}

	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	public Set<String> getImportsSet() {
		return importsSet;
	}
	public void setImportsSet(Set<String> importsSet) {
		this.importsSet = importsSet;
	}
	public List<Map<String, Object>> getMethodData() {
		return methodData;
	}
	public void setMethodData(List<Map<String, Object>> methodData) {
		this.methodData = methodData;
	}
	public StringBuffer getNewCode() {
		return newCode;
	}
	public void setNewCode(StringBuffer newCode) {
		this.newCode = newCode;
	}
	@Override
	public String toString() {
		return "TestMethodVO [methodDocs=" + Arrays.toString(methodDocs)
				+ ", properties=" + properties + ", importsSet=" + importsSet
				+ ", methodData=" + methodData + ", newCode=" + newCode
				+ ", methodSourceCode=" + methodSourceCode + "]";
	}
	
	
}
