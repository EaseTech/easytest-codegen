
package org.easetech.easytest.codegen.example.dto;



public final class ExampleConstants
{    
    
    public static enum ExampleEnumType
    {
        FILE("FILE"), DIRECTORY("DIRECTORY"),DEFAULT_STRING("defaultString");
        
        private String typeBatch;

        ExampleEnumType(String s)
        {
            typeBatch = s.trim();
        }

        public String toString()
        {
            return typeBatch;
        }

        
        public static ExampleEnumType getExampleEnumType(String passedString)
        {
          for(ExampleEnumType exampleEnumType: ExampleEnumType.values())
        	{
        		if(exampleEnumType.typeBatch.equals(passedString.trim()))
        		{
        			return exampleEnumType;
        		}
        	}
            throw new IllegalArgumentException("Invalid batch type: " + passedString);
        }
    }

}

