package org.easetech.easytest.codegen;

import java.util.Collection;

/**
 * This class is a utility to check whether object is null, not null, empty etc..
 */
public class BaseObject {

    public final boolean isNull(Object o) {
        boolean result;

        result = (o == null);
        return result;
    }

    public final boolean isNotNull(Object o) {
        boolean result;

        result = (o != null);
        return result;
    }

    public final boolean areNotNull(Object ... objects) {
        boolean result = true;

        if (isNotNull(objects)) {
            for (Object each: objects) {
                if (each == null) {
                    result = false;
                    break;
                }
            }
        } else {
            result = false;
        }
        return result;
    }
    
    public final boolean isEmpty(Collection collection) {
        boolean result;

        result = (isNull(collection) || collection.isEmpty());
        return result;
    }

    public final boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public final boolean isEmpty(String string) {
        boolean result;

        result = (isNull(string) || (string.length()==0));
        return result;
    }

    public final boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }
    
    public final boolean isEmpty(Object[] array) {
        boolean result;

        result = (isNull(array) || (array.length == 0));
        return result;
    }

    public final boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }
}

