package conloop;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Pato
 * This class is responsible for handling all the errors for the project. It
 * handles notifications and takes the measurements required to ensure that all
 * errors are recorded properly
 */
public class ErrorHandler {

    /**
     * Outputs the SQLState, error code, error description, and cause (if there
     * is one) contained in the SQLException as well as any other exception
     * chained to it:
     *
     * @param ex takes the SQLException thrown
     */
    public static void printSQLException(SQLException ex) {

        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                if (ignoreSQLException(((SQLException) e).getSQLState()) == false) {

                    e.printStackTrace(System.err);

                    // Gives the SQLState code. This code consists of 5 Alphanumeric  characters
                    System.err.println("SQLState: " + ((SQLException) e).getSQLState());

                    // Gives the error code. An integer value identifying the error that 
                    //caused the SQLException instance to be thrown
                    System.err.println("Error Code: " + ((SQLException) e).getErrorCode());

                    // Gives a description of the error
                    System.err.println("Message: " + e.getMessage());

                    Throwable t = ex.getCause();
                    while (t != null) {
                        System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }
    }

    /**
     * This method ensures that information about the SQLException is ignored if
     * the sql state is null.That is, there is no information about the sql
     * state
     *
     * @param sqlState A string parameter that gives info about the Sql State
     * information
     * @return
     */
    private static boolean ignoreSQLException(String sqlState) {

        if (sqlState == null) {
            System.out.println("The SQL state is not defined!");
            return false;
        }

        /*
         // X0Y32: Jar file already exists in schema
         if (sqlState.equalsIgnoreCase("X0Y32")) {
         return true;
         }

         // 42Y55: Table already exists in schema
         if (sqlState.equalsIgnoreCase("42Y55")) {
         return true;
         }

         return false;
         */
        return true;
    }

    /**
     * Gets all the warnings from results set object for analysis
     * See printWarnings
     * @param rs gets the resultset object
     * @throws SQLException 
     */
    public static void getWarningsFromResultSet(ResultSet rs)
            throws SQLException {
        printWarnings(rs.getWarnings());
    }

    /**
     * Gets all the warnings from the statement object for analysis
     * See printWarnings
     * @param stmt gets the statement object
     * @throws SQLException 
     */
    public static void getWarningsFromStatement(Statement stmt)
            throws SQLException {
        printWarnings(stmt.getWarnings());
    }
    
/**
 * Prints all the warnings of the SQLWarning passed as a parameter
 * @param warning gets the SQLWarning 
 * @throws SQLException 
 */
    private static void printWarnings(SQLWarning warning)
            throws SQLException {

        if (warning != null) {
            System.out.println("\n---Warning---\n");

            while (warning != null) {
                System.out.println("Message: " + warning.getMessage());
                System.out.println("SQLState: " + warning.getSQLState());
                System.out.print("Vendor error code: ");
                System.out.println(warning.getErrorCode());
                System.out.println("");
                warning = warning.getNextWarning();
            }
        }
    }
    
/**
 * Prints all prints all of the SQLException information 
 * plus the update counts contained in a BatchUpdateException object 
 * @param b this parameter takes the BatchUpdateException
 */
    public static void printBatchUpdateException(BatchUpdateException b) {

    System.err.println("----BatchUpdateException----");
    System.err.println("SQLState:  " + b.getSQLState());
    System.err.println("Message:  " + b.getMessage());
    System.err.println("Vendor:  " + b.getErrorCode());
    System.err.print("Update counts:  ");
    int [] updateCounts = b.getUpdateCounts();

    for (int i = 0; i < updateCounts.length; i++) {
        System.err.print(updateCounts[i] + "   ");
    }
}
    

}
