/*
    This file is part of  EasyTest CodeGen, a project to generate 
    JUnit test cases  from source code in EasyTest Template format and  helping to keep them in sync
    during refactoring.
 	EasyTest CodeGen, a tool provided by
	EaseTech Organization Under Apache License 2.0 
	http://www.apache.org/licenses/LICENSE-2.0.txt
*/

package org.easetech.easytest.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

/**
 * An extension of {@link Doclet} for the EasyTest code generation. 
 * This class's start() is first invoked when javadoc task is executed.
 * javadoc parses the source java classes from the input package directory and pass it to this customised Doclet.
 * This class is responsible for generating JUnit test cases in EasyTest/Customised template.
 * It invokes TestingStrategy classes for generating test cases and writing strategy classes for writing those to files 
 * 
 * @author Ravi Polampelli
 * 
 */

public class JUnitDoclet extends Doclet implements JUnitDocletProperties {

    
    /**
     * An instance of logger associated.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(JUnitDoclet.class);
	
	protected static final String OPTION_INPUT_DIR    = "-sourcepath";
    protected static final String OPTION_OUTPUT_DIR   = "-d";
    protected static final String OPTION_PROPERTIES   = "-properties";
    protected static final String OPTION_TESTING      = "-testing";
    protected static final String OPTION_WRITING      = "-writing";
    protected static final String OPTION_NAMING       = "-naming";
    protected static final String OPTION_BUILDALL     = "-buildall";
    protected static final String OPTION_HELP         = "-help";
    protected static final String OPTION_SUBPACKAGE   = "-subpackage";
    protected static final String OPTION_TEST_IN_TEST = "-testintest";
    protected static final String OPTION_STRICT       = "-strict";
    protected static final String OPTION_FILTER_PROPERTIES   = "-filterProperties";
    protected static final String OPTION_SEED_DATA   = "-seedData";
    /* JunitDoclet can't support looking for source files in class path. See javadoc tools documentation */
	// default source path must be "" to support filesets, since there no
    // source path is set. Taking the current path in this case is an error
    // since the real source path of the files could be something different.
    protected static final String DEFAULT_SOURCE_PATH = "";

    protected static final String USAGE_STRING      =
            "Parameters of EasyTest CodeGen  \n"+
            "-d <out_dir>                                      (required)\n"+
            "            Where to write the JUnit-Tests\n"+
            "\n"+
            "-subpackage <sub_package_name>                    (optional)\n"+
            "            Use a sub package to have the tests close to the\n"+
            "            application but separate. Usualy the sub-package is named \"test\".\n"+
            "\n"+
            "-buildall                                         (optional)\n"+
            "            All tests are rebuild, even if application is unchanged.\n"+
            "\n"+
            "-properties <property_file_name>                  (optional)\n"+
            "            Holding all templates and definitions\n"+
            "            (default is junit4.properties)\n"+
            "\n"+
            "-naming <naming_strategy_class_name>              (optional)\n"+
            "            Strategy class to define names\n"+
            "            (default is org.easetech.easytest.codegen.NamingStrategy)\n"+
            "\n"+
            "-writing <writing_strategy_class_name>            (optional)\n"+
            "            Strategy class handle file access\n"+
            "            default is org.easetech.easytest.codegen.WritingStrategy)\n"+
            "\n"+
            "-testing <testing_strategy_class_name>            (optional)\n"+
            "            Strategy class build the tests\n"+
            "            default is org.easetech.easytest.codegen.TestingStrategy)\n"+
            "\n"+
            "-testintest                                       (optional)\n"+
            "            Generate TestCase for all classes that can be found in source path.\n"+
            "            If set, tests will be generated for all classes that are not \n"+
            "            TestCases or TestSuites themselves, even if they are in a \n"+
            "            test subpackage. This option is NOT recommended.\n"+
            "\n"+
            "-strict                                           (optional)\n"+
            "            Promote every warning to an error. This option is helpfull to keep\n"+
            "            the regeneration process working during refactorings.\n"+
            "            Typical warnings would be:\n"+
            "              * any not empty testVault method\n"+
            "              * any missing marker (usually \"// JUnitDoclet begin import\")\n";

    private String           sourcePath;
    private String           outputRoot;
    private INamingStrategy  namingStrategy;
    private IWritingStrategy writingStrategy;
    private ITestingStrategy testingStrategy;
    private String           namingStrategyName;
    private String           writingStrategyName;
    private String           testingStrategyName;
    private String           propertyFileName;
    private String           filterPropertyFileName;
    private String           seedDataFileName;
    private String           subPackage;
    private boolean          buildAll;
    private boolean          testInTest;
    private boolean          strict;
    private DocErrorReporter docErrorReporter;

    public JUnitDoclet() {
        super();
        init();
    }

    public void init() {
    	LOG.info("init started");
        setBuildAll(false);
        setTestInTest(false);
        setStrict(false);
        setSourcePath(null);
        setOutputRoot(null);
        setNamingStrategy(null);
        setWritingStrategy(null);
        setTestingStrategy(null);
        setNamingStrategyName("org.easetech.easytest.codegen.NamingStrategy");
        setWritingStrategyName("org.easetech.easytest.codegen.WritingStrategy");
        setTestingStrategyName("org.easetech.easytest.codegen.TestingStrategy");
        setPropertyFileName(ConfigurableStrategy.DEFAULT_PROPERTY_FILE_NAME);
        setSeedDataFileName(null);
        setSubPackage(null);
        LOG.info("init finished");
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String theSourcePath) {
        sourcePath = theSourcePath;
    }

    public String getOutputRoot() {
        return outputRoot;
    }

    public void setOutputRoot(String outputRoot) {
        this.outputRoot = outputRoot;
    }

    public String getPropertyFileName() {
        return propertyFileName;
    }
    
    public String getSeedDataFileName() {
        return seedDataFileName;
    }
    
    public String getFilterPropertyFileName() {
        return filterPropertyFileName;
    }

    public void setPropertyFileName(String propertyFileName) {

        this.propertyFileName = propertyFileName;

        getNamingStrategy().setPropertyFileName(propertyFileName);
        getWritingStrategy().setPropertyFileName(propertyFileName);
        getTestingStrategy().setPropertyFileName(propertyFileName);
    }
    
    private void setFilterPropertyFileName(String propertyFileName) {
        this.filterPropertyFileName = propertyFileName;

        getNamingStrategy().setFilterPropertyFileName(propertyFileName);
        getWritingStrategy().setFilterPropertyFileName(propertyFileName);
        getTestingStrategy().setFilterPropertyFileName(propertyFileName);
		
	}
    
    public void setSeedDataFileName(String seedDataFileName) {

        this.seedDataFileName = seedDataFileName;

        getNamingStrategy().setSeedDataFileName(seedDataFileName);
        getWritingStrategy().setSeedDataFileName(seedDataFileName);
        getTestingStrategy().setSeedDataFileName(seedDataFileName);
    }

    public INamingStrategy getNamingStrategy() {

        if (namingStrategy == null) {
            setNamingStrategy((INamingStrategy) createByClassName(getNamingStrategyName()));
        }

        return namingStrategy;
    }

    public void setNamingStrategy(INamingStrategy namingStrategy) {

        this.namingStrategy = namingStrategy;

        if (namingStrategy != null) {
            namingStrategy.setPropertyFileName(getPropertyFileName());
            namingStrategy.setSubPackage(getSubPackage());
            namingStrategy.setTestInTest(isTestInTest());
        }
    }

    public IWritingStrategy getWritingStrategy() {

        if (writingStrategy == null) {
            setWritingStrategy((IWritingStrategy) createByClassName(getWritingStrategyName()));
        }

        return writingStrategy;
    }

    public void setWritingStrategy(IWritingStrategy writingStrategy) {

        this.writingStrategy = writingStrategy;

        if (writingStrategy != null) {
            writingStrategy.setPropertyFileName(getPropertyFileName());
        }
    }

    public ITestingStrategy getTestingStrategy() {

        if (testingStrategy == null) {
            setTestingStrategy((ITestingStrategy) createByClassName(getTestingStrategyName()));
        }

        return testingStrategy;
    }

    public void setTestingStrategy(ITestingStrategy testingStrategy) {

        this.testingStrategy = testingStrategy;

        if (testingStrategy != null) {
            testingStrategy.setPropertyFileName(getPropertyFileName());
        }
    }

    public String getNamingStrategyName() {
        return namingStrategyName;
    }

    public void setNamingStrategyName(String namingStrategyName) {
        this.namingStrategyName = namingStrategyName;
        setNamingStrategy(null);
    }

    public String getWritingStrategyName() {
        return writingStrategyName;
    }

    public void setWritingStrategyName(String writingStrategyName) {
        this.writingStrategyName = writingStrategyName;
        setWritingStrategy(null);
    }

    public String getTestingStrategyName() {
        return testingStrategyName;
    }

    public void setTestingStrategyName(String testingStrategyName) {
        this.testingStrategyName = testingStrategyName;
        setTestingStrategy(null);
    }

    public boolean isBuildAll() {
        return buildAll;
    }

    public void setBuildAll(boolean buildAll) {
        this.buildAll = buildAll;
    }

    public boolean isTestInTest() {
        return testInTest;
    }

    public void setTestInTest(boolean testInTest) {
        this.testInTest = testInTest;
    }

    public String getSubPackage() {
        return subPackage;
    }

    public void setSubPackage(String subPackage) {
        this.subPackage = subPackage;
        getNamingStrategy().setSubPackage(subPackage);
    }

    private Object createByClassName(String className) {

        Object returnValue = null;
        Class  clazz;

        try {
            clazz       = Class.forName(className);
            returnValue = clazz.newInstance();
        } catch (Exception e) {

            // not a valid class name
        }

        return returnValue;
    }

    public boolean processPackage( TestSuiteVO testSuiteVO, int index, DocErrorReporter reporter) {
    	
    	LOG.info("processPackage started, index:"+index);
    	
        boolean         returnValue = true;
        StringBuffer    newCode, oldCode;
        String          fullTestSuiteName;
        ITestingStrategy testing;
        IWritingStrategy writing;
        INamingStrategy naming;

        testing           = getTestingStrategy();
        writing           = getWritingStrategy();
        naming            = getNamingStrategy();
        
        String testSuiteExtention = testing.getProperties().getProperty(TESTSUITE_EXTENSION);
        if(testSuiteExtention != null && !"".equals(testSuiteExtention)){
        	naming.setTEST_SUITE_EXT(testSuiteExtention);
        }

        if (testing.isTestablePackage(testSuiteVO.getPackageDocs()[index], naming)) {

            fullTestSuiteName = naming.getFullTestSuiteName(testSuiteVO.getPackageDocs()[index].name());
            oldCode = writing.loadClassSource(getOutputRoot(), fullTestSuiteName);


            if ((oldCode == null) || testing.isValid(oldCode.toString())) {
                newCode     = new StringBuffer();
                testSuiteVO.setNaming(naming);
                testSuiteVO.setNewCode(newCode);
                testSuiteVO.setProperties(testing.getProperties());
                System.out.println("testSuiteVO.getTestClasses():"+testSuiteVO.getTestClasses());
                returnValue = testing.codeTestSuite(testSuiteVO, index);
                if (testing.isValid(newCode.toString())) {
                    writing.indent(newCode);

                    if (testing.merge(newCode, oldCode, fullTestSuiteName)) {
                        if (isWritingNeeded(newCode, oldCode)) {
                            reporter.printNotice("Writing TestSuite "+fullTestSuiteName+".");
                            writing.writeClassSource(getOutputRoot(), fullTestSuiteName, newCode);
                            testSuiteVO.getTestSuiteClasses().add(fullTestSuiteName);
                        } // no else
                    } // no else
                } else {
                    reporter.printError("Could not generate TestSuite "+fullTestSuiteName+ " (possible reason: missing or wrong properties).");
                }
            } else {
                if (oldCode != null) {
                    reporter.printWarning("TestSuite "+fullTestSuiteName+ " is invalid. It's not overwritten.");
                }
            }
        }
        LOG.info("processPackage finished, returnValue:"+returnValue);
        return returnValue;
    }

    public boolean processClass(ClassDoc doc, PackageDoc packageDoc, DocErrorReporter reporter, TestSuiteVO testSuiteVO) {
    	
    	LOG.info("processClass started, ClassDoc:"+doc);
    	
        boolean         returnValue = true;
        StringBuffer    oldCode, newCode;
        String          fullTestCaseName;
        String          fullClassName;
        ITestingStrategy testing;
        IWritingStrategy writing;
        INamingStrategy naming;

        testing = getTestingStrategy();
        writing = getWritingStrategy();
        naming  = getNamingStrategy();

        if (packageDoc == null) {
            packageDoc = doc.containingPackage();
        }

        fullClassName    = doc.qualifiedTypeName();
        String testCaseExtention = testing.getProperties().getProperty(TESTCASE_EXTENSION);
        if(testCaseExtention != null && !"".equals(testCaseExtention)){
        	naming.setTEST_CASE_EXT(testCaseExtention);
        }
        
        fullTestCaseName = naming.getFullTestCaseName(fullClassName);

        if (testing.isTestableClass(doc, naming)) {

            // generate TestCase only if it does not exist or is older than application class.
            if (isGenerationNeeded(fullClassName, fullTestCaseName)) {
            	LOG.debug("getOutputRoot():"+getOutputRoot());
            	LOG.debug("fullTestCaseName:"+fullTestCaseName);
                oldCode = writing.loadClassSource(getOutputRoot(), fullTestCaseName);
                LOG.debug("oldCode:"+oldCode);
                if ((oldCode == null) ||testing.isValid(oldCode.toString())) {
                    newCode     = new StringBuffer();
                    StringBuffer sourceCode = writing.loadSourceClassSource(fullClassName,getSourcePath());

                    Map<String, StringBuffer> convertersMap = new HashMap<String,StringBuffer>();
                    TestCaseVO testCaseVO = new TestCaseVO(packageDoc, doc,naming,testing.getProperties(),
                    								convertersMap,new HashMap<String, List<Map<String, Object>>>(),
                    								sourceCode,newCode,new HashMap<String, List<String>>());
                    
                    returnValue = testing.codeTestCase(testCaseVO);
                    if (testing.isValid(newCode.toString())) {
                        writing.indent(newCode);
                        if (isWritingNeeded(newCode, oldCode)) {
                            reporter.printNotice("Writing TestCase "+fullTestCaseName+".");
                            writing.writeClassSource(getOutputRoot(), fullTestCaseName, newCode);
                            LOG.debug("writeTestDataFile testData:"+testCaseVO.getTestData());
                            //creating test data xls
                            writing.writeTestDataFile(getOutputRoot(), fullTestCaseName, 
                            		testCaseVO,testing.getSeedData());
                            //generating converter classes 
                            LOG.debug("convertersMap:"+convertersMap);
                            String testPackageName = fullTestCaseName.substring(0, fullTestCaseName.lastIndexOf("."));
                            //check the property overwrite converters, if yes then only generate the converters code
                            String overwriteConverters = testCaseVO.getProperties().getProperty(OVERWRITE_EXISTING_CONVERTERS);
                            writing.writeConverterSources(getOutputRoot(),testPackageName,convertersMap,overwriteConverters);
                            testSuiteVO.getTestClasses().add(fullTestCaseName);
                        } else {
                            reporter.printNotice("TestCase "+fullTestCaseName+ " did not change but "+fullClassName+" did.");
                            
                        }
                    } else {
                        reporter.printError("Could not generate TestCase "+fullTestCaseName+ " (possible reason: missing or wrong properties).");
                    }
                } else {
                    if (oldCode != null) {
                        reporter.printWarning("TestCase "+fullTestCaseName+ " is invalid. It's not overwritten.");
                    }
                }
            } else {

                // Do not regenerate TestCase.
                returnValue = true;
            }
        } else {

            // Do not generate test case.
            returnValue = true;
        }
        LOG.info("processClass finished, returnValue:"+returnValue);
        return returnValue;
    }

    /**
     * Checks if file of application class is modified later than TestCase file.
     */
    public boolean isGenerationNeeded(String fullClassName, String fullTestCaseName) {
    	
    	LOG.info("isGenerationNeeded started, fullClassName:"+fullClassName+" ,fullTestCaseName"+fullTestCaseName);
    	
        boolean returnValue;

        returnValue = isBuildAll();
        returnValue = returnValue || !getWritingStrategy().isExistingAndNewer(getOutputRoot(),
                                                                              fullTestCaseName,
                                                                              getSourcePath(),
                                                                              fullClassName);
        LOG.info("isGenerationNeeded finished, returnValue:"+returnValue);
        return returnValue;
    }

    public boolean isWritingNeeded(StringBuffer newCode, StringBuffer oldCode) {
    	LOG.info("isWritingNeeded started, newCode:"+newCode);
        boolean returnValue;

        returnValue = isBuildAll();
        returnValue = returnValue || (oldCode == null);
        returnValue = returnValue || ((newCode != null) && (oldCode != null) && (!newCode.toString().equals(oldCode.toString())));
        LOG.info("isWritingNeeded finished, returnValue:"+returnValue);
        return returnValue;
    }
    
    /**
     * This class is invoked first by Doclet after parsing the source code files 
     * and encapsulate the meta-information in a RootDoc and pass it as parameter 
     * 
     * @param RootDoc doc encapsulated meta-information about source code
     */
    public static boolean start(RootDoc doc) {
    	LOG.info("start started, RootDoc:"+doc);
        JUnitDoclet instance;

        instance = new JUnitDoclet();
        instance.setOptions(doc.options());
        instance.setDocErrorReporter(doc);
        LOG.info("instanceExecute started, RootDoc:"+doc);
        //execute the instance
        boolean instanceExecute = instance.execute(doc);
        LOG.info("instanceExecute finished,"+instanceExecute);
        return instanceExecute;
    }

    public void setDocErrorReporter(DocErrorReporter doc) {
        docErrorReporter = doc;
        getNamingStrategy().setDocErrorReporter(doc);
        getWritingStrategy().setDocErrorReporter(doc);
        getTestingStrategy().setDocErrorReporter(doc);
    }

    /**
     * Execute the root doc
     * It fetches the package doc and class doc and invokes corresponding process methods.
     * 
     * @param RootDoc doc encapsulated meta-information about source code
     */
    public boolean execute(RootDoc doc) {
        boolean          returnValue = true;
        PackageDoc[]     packageDocs;
        ClassDoc[]       classDocs;
        DocErrorReporter reporter;
        
        reporter = createErrorReporter(isStrict());

        if (reporter != null) {
            reporter.printNotice("Generating TestSuites and TestCases.");
            packageDocs = doc.specifiedPackages();            
            
            classDocs = doc.specifiedClasses();
            TestSuiteVO testSuiteVO = new TestSuiteVO(packageDocs,new ArrayList<String>());
        	for (int j = 0; j < classDocs.length; j++) {
            	LOG.debug("specifiedClasses processing");
                returnValue = returnValue && processClass(classDocs[j], null, reporter,testSuiteVO);
            }  
            
        	testSuiteVO.setTestSuiteClasses(new ArrayList<String>());
        	
            LOG.debug("specifiedPackages processing");
            //traversing in reverse direction so that generation of test cases for sub packages(if specified) will happen first
            //and then goes up in hierarchy till base package.
            for (int i = packageDocs.length-1; i >= 0; i--) {
            	
            	//for sub packages no need to include upper level test cases, hence making them null.
            	// to create new test suite for sub packages.
            	//if( i > 0){
            		testSuiteVO.setTestClasses(new ArrayList<String>());
            	//}            	 

            	returnValue = returnValue && processPackageClasses(packageDocs[i],testSuiteVO,reporter);
                returnValue = returnValue && processPackage(testSuiteVO, i, reporter);
                
                /*
            	String packageDocSpecified = packageDocs[i].name().replace('.', '/');
                File file = new File(getSourcePath(),packageDocSpecified);
                LOG.debug("packageDocSpecified"+packageDocSpecified);            
                LOG.debug("specified package/file name:"+file.getAbsolutePath());
                
              
                if(file.isDirectory() && generateSubpackageTestCases ){
                	
                	File[] packagesAndClasses = file.listFiles();
                	LOG.debug("packagesAndClasses:"+packagesAndClasses.length);
                	for(File packagesOrClass:packagesAndClasses){
                		LOG.debug("packagesOrClass:"+packagesOrClass);
                		if(packagesOrClass.isDirectory()){
                			//System.out.println("processing this package through javadoc execute:"+packagesOrClass);
                			String subPackage = packageDocs[i].name()+"."+packagesOrClass.getName();
                			LOG.debug("processing this subPackage :"+subPackage);
                			/*PackageDoc subPackageDoc = doc.packageNamed(subPackage);
                			System.out.println("subPackageDoc:"+subPackageDoc.name());
                			ClassDoc[] allClasses = subPackageDoc.allClasses();
                			System.out.println("allClasses size"+allClasses.length);
                			System.out.println("subPackageDoc is packageDoc"+subPackageDoc.commentText());
                			PackageDoc[] subPackageDocs = new PackageDoc[1];
                			subPackageDocs[0] = subPackageDoc;
                			TestSuiteVO testSubPackageSuiteVO = new TestSuiteVO(subPackageDocs,new ArrayList<String>());
                			returnValue = returnValue && processPackageClasses(subPackageDoc, testSubPackageSuiteVO, reporter);
                			returnValue = returnValue && processPackage(testSubPackageSuiteVO, 0, reporter);
                			*//*
                			LOG.debug("processing this subPackage through javadoc execute:"+subPackage);
                			String[] javadocargs = {subPackage,"-d", getOutputRoot(),
                								  "-sourcepath", getSourcePath(),
                								  "-doclet", "org.easetech.easytest.codegen.JUnitDoclet",
                								  "-properties",getPropertyFileName(),
                								  "-classpath","maven.compile.classpath",
                								  "",""};
                			if(getSeedDataFileName() != null) {
                				int arrLen = javadocargs.length;      
                				Arrays.fill(javadocargs,arrLen-2,arrLen-1,"-seedData");
                				Arrays.fill(javadocargs,arrLen-1,arrLen,getSeedDataFileName());
                			}
                			int returnIntValue = com.sun.tools.javadoc.Main.execute(javadocargs);
                			LOG.debug("returnIntValue"+returnIntValue);
                			if(returnIntValue >= 0){
                				returnValue = returnValue && true;
                			} else {
                				returnValue = returnValue && false;
                			}

                		}
                		
                	}
                }*/

            }
            
            
            
        }
        return returnValue;
    }

    private boolean processPackageClasses(PackageDoc packageDoc, TestSuiteVO testSuiteVO,
			DocErrorReporter reporter) {
    	LOG.debug("processPackageClasses started");
    
    	ClassDoc[] classDocs;
    	boolean returnValue = true;
    	
    	classDocs = packageDoc.ordinaryClasses();
    	LOG.debug("No.of class docs"+classDocs.length);
        for (int j = 0; j < classDocs.length; j++) {
        	System.out.println("processClass started for"+classDocs[j].name());
            returnValue = returnValue && processClass(classDocs[j], packageDoc, reporter,testSuiteVO);
        }           
        
        LOG.debug("processPackageClasses finished with value"+returnValue);
        
       return returnValue;

	}

	protected DocErrorReporter createErrorReporter(boolean strict) {
        DocErrorReporter result;

        result = new StrictDocErrorReporter(strict);
        return result;
    }

    public void setOptions(String[][] options) {

        for (int i = 0; (i < options.length); i++) {

            if (options[i][0].equals(OPTION_BUILDALL)) {
                setBuildAll(true);
            }

            if (options[i][0].equals(OPTION_STRICT)) {
                setStrict(true);
            }

            if (options[i][0].equals(OPTION_TEST_IN_TEST)) {
                setTestInTest(true);
            }

            if (options[i][0].equals(OPTION_INPUT_DIR)) {
                setSourcePath(options[i][1]);
            }

            if (options[i][0].equals(OPTION_OUTPUT_DIR)) {
                setOutputRoot(options[i][1]);
            }

            if (options[i][0].equals(OPTION_SUBPACKAGE)) {
                setSubPackage(options[i][1]);
            }

            if (options[i][0].equals(OPTION_PROPERTIES)) {
                setPropertyFileName(options[i][1]);
            }
            
            if (options[i][0].equals(OPTION_FILTER_PROPERTIES)) {
                setFilterPropertyFileName(options[i][1]);
            }
            
            if (options[i][0].equals(OPTION_SEED_DATA)) {
                setSeedDataFileName(options[i][1]);
            }

            if (options[i][0].equals(OPTION_TESTING)) {
                setTestingStrategyName(options[i][1]);
            }

            if (options[i][0].equals(OPTION_WRITING)) {
                setWritingStrategyName(options[i][1]);
            }

            if (options[i][0].equals(OPTION_NAMING)) {
                setNamingStrategyName(options[i][1]);
            }
        }
    }



	public static int optionLength(String s) {

        int returnValue = 0;

        if (s.equals(OPTION_HELP)) {
            printUsage();
        } else if (s.equals(OPTION_BUILDALL)) {
            returnValue = 1;
        } else if (s.equals(OPTION_STRICT)) {
            returnValue = 1;
        } else if (s.equals(OPTION_TEST_IN_TEST)) {
            returnValue = 1;
        } else if (s.equals(OPTION_OUTPUT_DIR)) {
            returnValue = 2;
        } else if (s.equals(OPTION_PROPERTIES)) {
            returnValue = 2;
        } else if (s.equals(OPTION_SUBPACKAGE)) {
            returnValue = 2;
        } else if (s.equals(OPTION_TESTING)) {
            returnValue = 2;
        } else if (s.equals(OPTION_WRITING)) {
            returnValue = 2;
        } else if (s.equals(OPTION_NAMING)) {
            returnValue = 2;
        } else if (s.equals(OPTION_SEED_DATA)) {
            returnValue = 2;
        } // no else

        return returnValue;
    }

    public static boolean validOptions(String[][] parameters, DocErrorReporter reporter) {
    	
    	LOG.debug("validOptions started"+parameters);
    	
        boolean returnValue  = true;
        boolean foundOutput  = false;
        boolean isTestInTest = false;
        String  subPackage   = null;
        String  sourcePath   = DEFAULT_SOURCE_PATH;
        String  outputPath   = null;

        for (String[] parameter : parameters) {
        	System.out.println("parameter:"+parameter[0]);
            if (parameter[0].equals(OPTION_OUTPUT_DIR)) {
                if (!ValidationHelper.isDirectoryName(parameter[1])) {
                    returnValue = false;

                    if (reporter != null) {
                        reporter.printError("Error:" + parameter[1] + " is not a directory.");
                    }
                } else {
                    outputPath = parameter[1];
                }

                foundOutput = true;
            }

            if (parameter[0].equals(OPTION_INPUT_DIR)) {
                sourcePath = parameter[1];
            }

            if (parameter[0].equals(OPTION_TEST_IN_TEST)) {
                isTestInTest = true;
            }

            if (parameter[0].equals(OPTION_PROPERTIES)) {
                if (!ValidationHelper.isPropertyName(parameter[1])) {
                    returnValue = false;

                    if (reporter != null) {
                        reporter.printError("Error:" + parameter[1] + " is not a property file.");
                    }
                }
            }
            
            if (parameter[0].equals(OPTION_SEED_DATA)) {
                if (!ValidationHelper.isPropertyName(parameter[1])) {
                    returnValue = false;

                    if (reporter != null) {
                        reporter.printError("Error:" + parameter[1] + " is not a property file.");
                    }
                }
            }

            if (parameter[0].equals(OPTION_SUBPACKAGE)) {
                if (!ValidationHelper.isPackageName(parameter[1])) {
                    returnValue = false;

                    if (reporter != null) {
                        reporter.printError("Error:" + parameter[1] + " is not a valid package name.");
                    }
                } else {
                    subPackage = parameter[1];
                }
            }

            if (parameter[0].equals(OPTION_NAMING)) {
                if (!ValidationHelper.isClassName(parameter[1])) {
                    returnValue = false;

                    if (reporter != null) {
                        reporter.printError("Error:" + parameter[1] + " is not a class.");
                    }
                }
            }

            if (parameter[0].equals(OPTION_TESTING)) {
                if (!ValidationHelper.isClassName(parameter[1])) {
                    returnValue = false;

                    if (reporter != null) {
                        reporter.printError("Error:" + parameter[1] + " is not a class.");
                    }
                }
            }

            if (parameter[0].equals(OPTION_WRITING)) {
                if (!ValidationHelper.isClassName(parameter[1])) {
                    returnValue = false;

                    if (reporter != null) {
                        reporter.printError("Error:" + parameter[1] + " is not a class.");
                    }
                }
            }
        }

        returnValue = returnValue && foundOutput;

        // output path must not be in source path if no subpackage is specified
        if ((returnValue) && (subPackage == null) && !isTestInTest) {
            returnValue = !isOutputMatchingAnySource(sourcePath, outputPath, reporter);
        }

        return returnValue;
    }

    public static boolean isOutputMatchingAnySource(String sourcePath, String outputPath, DocErrorReporter reporter) {
        boolean returnValue = false;
        String canonicalSourcePath;
        String canonicalOutputPath = null;

        StringTokenizer sourcePathTokenizer = new StringTokenizer(sourcePath, File.pathSeparator);

        try {
            canonicalOutputPath = new File(outputPath).getCanonicalPath();
        } catch (IOException e) {
            if (reporter != null) {
                reporter.printError("Error: File '" + outputPath + "' not found (" + e.toString() + ").");
            }
            returnValue = true;
        }

        if (!returnValue && (canonicalOutputPath != null)) {
            while (sourcePathTokenizer.hasMoreTokens()) {
                try {
                    canonicalSourcePath = new File(sourcePathTokenizer.nextToken()).getCanonicalPath();
                    if (canonicalOutputPath.equals(canonicalSourcePath)) {
                        returnValue = true;
                    }
                } catch (IOException e) {
                    // ignore non existent elements in classpath
                }
            }
            if (returnValue) {
                if (reporter != null) {
                    reporter.printError("Error: value of " + OPTION_OUTPUT_DIR +
                            " must not be in value of " + OPTION_INPUT_DIR + ".\n" +
                            "You may override this restriction with " + OPTION_TEST_IN_TEST + ".");
                }
            }
        }
        return returnValue;
    }

    private static void printUsage() {
        System.out.println(USAGE_STRING);
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
