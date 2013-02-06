/*
    This file is part of  EasyTest CodeGen, a project to generate 
    JUnit test cases  from source code in EasyTest Template format and  helping to keep them in sync
    during refactoring.
 	EasyTest CodeGen, a tool provided by
	EaseTech Organization Under Apache License 2.0 
	http://www.apache.org/licenses/LICENSE-2.0.txt
*/

package org.easetech.easytest.codegen;

import com.sun.javadoc.DocErrorReporter;

import java.util.Properties;

/**
 * An interface for configurations 
 * 
 * @author Ravi Polampelli
 *
 */

public interface IConfigurableStrategy {
    public void init();

    public String getPropertyFileName();

    public void setPropertyFileName(String propertyFileName);    

    public void setProperties(Properties properties);
    
    public Properties getProperties();
    
    public String getFilterPropertyFileName();
    
    public void setFilterPropertyFileName(String filterPropertyFileName);
        
    public void setFilterProperties(Properties filterProperties);

    public Properties getFilterProperties();
    
    public String getSeedDataFileName();
    
    public void setSeedDataFileName(String seedDataFileName);
    
    public Properties getSeedData();
    
    public void setSeedData(Properties seedData);    

    public void setDocErrorReporter(DocErrorReporter reporter);

    public void printNotice(String msg);

    public void printWarning(String msg);

    public void printError(String msg);
}
