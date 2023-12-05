/**
 * 
 */
package org.jvoicexml.interpreter.datamodel;

/**
 * Base class for data models.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public abstract class AbstractDataModel implements DataModel {
    /**
     * {@inheritDoc}
     */
    @Override
    public String errorCodeToString(final int errorCode) {
        switch (errorCode) {
        case NO_ERROR:
            return "NO_ERROR";
        case ERROR_SCOPE_NOT_FOUND:
            return "ERROR_SCOPE_NOT_FOUND";
        case ERROR_VARIABLE_NOT_FOUND:
            return "ERROR_VARIABLE_NOT_FOUND";
        case ERROR_VARIABLE_ALREADY_DEFINED:
            return "ERROR_VARIABLE_ALREADY_DEFINED";
        default:
            return Integer.toString(errorCode);
        }
    }

}
