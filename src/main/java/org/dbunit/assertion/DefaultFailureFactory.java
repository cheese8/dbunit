package org.dbunit.assertion;

/**
 * Default failure factory which returns DBUnits own assertion error
 * instances.
 *
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 872 $ $Date: 2008-11-08 09:45:52 -0600 (Sat, 08 Nov
 * 2008) $
 * @since 2.4.0
 */
public class DefaultFailureFactory implements FailureFactory {
    public Error createFailure(final String message, final String expected, final String actual) {
        // Return dbunit's own comparison failure object
        return new DbComparisonFailure(message, expected, actual);
    }

    public Error createFailure(final String message) {
        // Return dbunit's own failure object
        return new DbAssertionFailedError(message);
    }
}