package org.easetech.easytest.codegen;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.SourcePosition;

/**
* An implementation of javadoc DocErrorREporter
*
* @author Ravi Polampelli
*
*/

public class StrictDocErrorReporter extends BaseObject implements DocErrorReporter {
    private DocErrorReporter delegate = null;
    private boolean          strict   = false;

    public StrictDocErrorReporter(boolean strict) {
        this.strict = strict;
    }

    public void printError(String message) {
        printError(null, message);
    }

    public void printWarning(String message) {
        printWarning(null, message);
    }

    public void printNotice(String message) {
        printNotice(null, message);
    }

    public void printError(SourcePosition sourcePosition, String message) {
        if (isNotNull(delegate)) {
            if (isNull(sourcePosition)) {
                delegate.printError(message);
            } else {
                delegate.printError(sourcePosition, message);
            }
        } else {
            System.err.println(message);
        }
    }

    public void printWarning(SourcePosition sourcePosition, String message) {
        if (isNotNull(delegate)) {
            if (isStrict()) {
                if (isNull(sourcePosition)) {
                    delegate.printError(message);
                } else {
                    delegate.printError(null, message);
                }
            } else {
                if (isNull(sourcePosition)) {
                    delegate.printWarning(message);
                } else {
                    delegate.printWarning(null, message);
                }
            }
        } else {
            System.err.println(message);
        }
    }

    public void printNotice(SourcePosition sourcePosition, String message) {
        if (isNotNull(delegate)) {
            if (isNull(sourcePosition)) {
                delegate.printNotice(message);
            } else {
                delegate.printNotice(null, message);
            }
        } else {
            System.out.println(message);
        }
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public DocErrorReporter getDelegate() {
        return delegate;
    }

    public void setDelegate(DocErrorReporter delegate) {
        this.delegate = delegate;
    }
}

