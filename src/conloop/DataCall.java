package conloop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This gets data from the data source. Its the class through which all data is
 * retrieved Abstracts away how data is retrieved
 *
 * @author PatoWhiz 09/04/2018
 */
public class DataCall {

    protected Connection conn;
    //can be used to detect if maybe connection was lost and reconnect
    protected boolean LAST_RESULT_SET_NULL = false;

    public DataCall(Connection conn) {
        setConnection(conn);
    }

    private Connection getConn() {
        return conn;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public boolean isReturnedLastResultSetNull() {
        return LAST_RESULT_SET_NULL;
    }

    public ResultSet getResultSet(String sqlQuery) {

        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = getConn().createStatement();
            rs = stmt.executeQuery(sqlQuery);
        } catch (SQLException ex) {
            System.out.println("SQLException. Failed to get statement resultset: " + ex);
            ErrorHandler.printSQLException(ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.closeOnCompletion();
                } catch (SQLException ex) {
                    System.out.println("SQLException. Failed to close the statement");
                    ErrorHandler.printSQLException(ex);
                }
            }//end if
        }

        //detect if maybe connection was lost and reconnect
        LAST_RESULT_SET_NULL = rs == null;

        return rs;
    }

    public int getCount(String strTableName, String strColNameToCount, String condition) {
        int totalNum = 0;
        ResultSet rs = null;
        try {
            rs = getResultSet("SELECT COUNT(" + strColNameToCount + ") AS S FROM " + strTableName + " " + condition);
            while (rs.next()) {
                totalNum += rs.getInt("S");
            }

        } catch (SQLException ex) {
            System.err.println("SQLException in UniversalFunctiionClass : " + ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println("SQLException in UniversalFunctiionClass closing of resultset : " + ex);
                }
            }
        }

        return totalNum;
    }

    public int getCount(String strTableName, String condition) {
        return getCount(strTableName, "*", condition);
    }

    /**
     * checks if the wholly passed string value exists in the provided column of
     * the table. If found returns true. This function is case insensitive
     *
     * @param tableName
     * @param strColumnName
     * @param strValue
     * @return
     */
    public boolean checkIfStringValueExists(String tableName, String strColumnName, String strValue) {

        boolean ans = false;
        ResultSet rs = null;
        try {

            rs = getResultSet("SELECT " + strColumnName + " FROM " + tableName + " WHERE UPPER(" + strColumnName + ") LIKE UPPER('" + strValue + "') FETCH FIRST 1 ROWS ONLY");
            if (rs.next()) {
                //ans = !rs.getString(strColumnName).isEmpty();
                ans = true;
            }
        } catch (SQLException ex) {
            ErrorHandler.printSQLException(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ErrorHandler.printSQLException(ex);
                }
            }
        }

        return ans;
    }

    public boolean checkIfIntegerValueExists(String tableName, String strColumnName, int iValue) {

        boolean ans = false;
        ResultSet rs = null;
        try {

            rs = getResultSet("SELECT " + strColumnName + " FROM " + tableName + " WHERE  " + strColumnName + " = " + iValue + " FETCH FIRST 1 ROWS ONLY");
            if (rs.next()) {
                ans = true;
            }
        } catch (SQLException ex) {
            ErrorHandler.printSQLException(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ErrorHandler.printSQLException(ex);
                }
            }
        }

        return ans;
    }

    public boolean getValueAsBoolean(String tableName, String strColToGetTheValueFrom, String condition) {

        boolean ans = false;
        ResultSet rs = null;
        try {
            rs = getResultSet("SELECT " + strColToGetTheValueFrom + " AS S FROM " + tableName + " " + condition);
            while (rs.next()) {
                ans = rs.getBoolean("S");
            }
        } catch (SQLException ex) {
            ErrorHandler.printSQLException(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ErrorHandler.printSQLException(ex);
                }
            }
        }

        return ans;
    }

    /**
     *
     * This method gets the maximum value for any table column It constructs the
     * query SELECT MAX(columnNameToGetTheValueFrom) FROM tableName
     *
     * @param tableName
     * @param strColToGetTheValueFrom
     * @return maxValue if a record is found or noresult if no record found
     */
    public double getAnyMaxValue(String tableName, String strColToGetTheValueFrom) {
        double maxValue = 0;
        ResultSet rs = null;
        try {
            rs = getResultSet("SELECT MAX(" + strColToGetTheValueFrom + ") AS S FROM " + tableName);
            while (rs.next()) {
                maxValue = rs.getDouble("S");
            }
        } catch (SQLException ex) {
            ErrorHandler.printSQLException(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ErrorHandler.printSQLException(ex);
                }
            }
        }
        return maxValue;
    }

    public String getLastValAsString(String strTableName, String strColToGetTheValueFrom, String strKeyColName, String strCondition) {
        String strVal = "";
        ResultSet rs = null;
        try {

            if (strCondition == null) {
                strCondition = "";
            }
            rs = getResultSet("SELECT " + strColToGetTheValueFrom + " AS S FROM " + strTableName + " " + strCondition + " ORDER BY " + strKeyColName + " DESC FETCH FIRST 1 ROWS ONLY");
            if (rs.next()) {
                strVal = rs.getString("S");
            }

        } catch (SQLException ex) {
            System.err.println("SQLException in UniversalFunctiionClass : " + ex);
        } finally {
            tryCloseResultset(rs);
        }

        return strVal;
    }

    public int getLastValAsInteger(String strTableName, String strColName, String strKeyColName, String whereClause) {
        String strValue = getLastValAsString(strTableName, strColName, strKeyColName, whereClause);
        return StringsUtil.isNumeric(strValue) ? Integer.parseInt(strValue) : 0;
    }

    public long getLastValAsLong(String strTableName, String strColName, String strKeyColName, String whereClause) {
        String strValue = getLastValAsString(strTableName, strColName, strKeyColName, whereClause);
        return StringsUtil.isNumeric(strValue) ? Long.parseLong(strValue) : 0;
    }

    /**
     * gets the last value stored in the database. Suitable for unique columns.
     *
     * @param strTableName
     * @param strKeyColName
     * @return
     */
    public int getLastKeyValue(String strTableName, String strKeyColName) {
        String strValue = getLastValAsString(strTableName, strKeyColName, strKeyColName, "");
        return StringsUtil.isNumeric(strValue) ? Integer.parseInt(strValue) : 0;
    }

    public long getLastKeyValueAsLong(String strTableName, String strKeyColName) {
        String strValue = getLastValAsString(strTableName, strKeyColName, strKeyColName, "");
        return StringsUtil.isNumeric(strValue) ? Long.parseLong(strValue) : 0;
    }

    public double getSumTotal(String tableName, String columnNameToGetTheValueFrom, String condition) {

        double totals = 0;
        ResultSet rs = null;
        try {

            rs = getResultSet("SELECT SUM(" + columnNameToGetTheValueFrom + ") AS S FROM "
                    + tableName + " " + condition);
            while (rs.next()) {
                totals += rs.getDouble("S");
            }

        } catch (SQLException ex) {
            ErrorHandler.printSQLException(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ErrorHandler.printSQLException(ex);
                }
            }
        }

        return totals;
    }

    public int computePriKeyInt(String tableName, String columnName) {
        double result = getAnyMaxValue(tableName, columnName);
        //add 1 to the value returned
        return ((int) result + 1);
    }//end method

    public long computePriKeyLong(String tableName, String columnName) {
        double result = getAnyMaxValue(tableName, columnName);
        //add 1 to the value returned
        return ((long) result + 1);
    }//end method

    public void tryCloseResultset(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                System.err.println("Error in closing result set : " + ex);
            }
        }
    }

    public void tryClosePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                System.err.println("Error in closing prepared statement : " + ex);
            }
        }
    }

    public int saveNewSingleRecord(String tableName, LinkedHashMap<String, Object> lhsmColumnsAndValues) throws SQLException {
        int h = 0;
        String strColumnNames = "";
        String strColumnParams = "";
        String sql;
        PreparedStatement ps = null;
        try {

            //Get the column names
            int iFirst = 0;
            for (String strCol : lhsmColumnsAndValues.keySet()) {
                if (iFirst == 0) {
                    strColumnNames = strCol;
                    strColumnParams = "?";
                    iFirst = 1;
                } else {
                    strColumnNames = strColumnNames + "," + strCol;
                    strColumnParams += ",?";
                }
            } //end for loop

            sql = "INSERT INTO " + tableName + " (" + strColumnNames + ") VALUES " + "(" + strColumnParams + ")";
            ps = getConn().prepareStatement(sql);

            //Set the column values
            setPreparedStatementValues(ps, lhsmColumnsAndValues.values());

            h = ps.executeUpdate();

        } finally {
            tryClosePreparedStatement(ps);
        }//end finally

        if (h > 0) {
            return 1;
        } else {
            return -1;
        }
    }//end method

    /**
     * updates a record that has single or multiple columns as the condition
     *
     * @param tableName
     * @param lhsmColumnsAndValues
     * @param clsFilterCondtion
     * @return
     * @throws SQLException
     */
    public int updateSingleRecord(String tableName, LinkedHashMap<String, Object> lhsmColumnsAndValues, TableFilter clsFilterCondtion) throws SQLException {

        int h = 0;
        String sql;
        String strColumnNames = "";
        PreparedStatement ps = null;
        try {

            //Get the column names
            for (String strCol : lhsmColumnsAndValues.keySet()) {
                if (strColumnNames.isEmpty()) {
                    strColumnNames = strCol + "=?";
                } else {
                    strColumnNames = strColumnNames + "," + strCol + "=?";
                }
            } //end for loop

            sql = "UPDATE " + tableName + " SET " + strColumnNames;
            if (clsFilterCondtion != null) {
                sql = sql + " WHERE " + clsFilterCondtion.getParameterisedSqlCondition();
            }
            ps = getConn().prepareStatement(sql);
            //Set the column values
            List<Object> lstValues = new ArrayList<>();
            lstValues.addAll(lhsmColumnsAndValues.values());
            if (clsFilterCondtion != null) {
                lstValues.addAll(clsFilterCondtion.columnValues);
            }

            setPreparedStatementValues(ps, lstValues);
            h = ps.executeUpdate();

        } finally {
            tryClosePreparedStatement(ps);
        }//end finally

        if (h > 0) {
            return 1;
        } else {
            return -1;
        }

    }//end method

    /**
     * Deletes the record based on the passed condition. Returns 0 if successful
     * else -1
     *
     * @param tableName
     * @param clsFilterCondtion
     * @return
     * @throws SQLException
     */
    public int deleteRecord(String tableName, TableFilter clsFilterCondtion) throws SQLException {
        PreparedStatement ps = null;
        String sql;
        int h;
        try {
            //condition column names will have their boleans and logics attached to them for simplicty
            sql = "DELETE FROM " + tableName + " WHERE " + clsFilterCondtion.getParameterisedSqlCondition();
            ps = getConn().prepareStatement(sql);
            //Set the column values 
            setPreparedStatementValues(ps, clsFilterCondtion.columnValues);
            h = ps.executeUpdate();
        } finally {
            tryClosePreparedStatement(ps);
        }//end finally

        if (h > 0) {
            return 1;
        } else {
            return -1;
        }

    } //end method

    /**
     *
     * @param dbOperation. 1 = "insert", 2 = "update", 3 = "delete"
     * @param strTableName
     * @param keyColumnName.If it's an insert, it will be added in the list of
     * values to be inserted. Should never be null.
     * @param keyValue
     * @param lhsmColumnsAndValues
     * @return
     * @throws SQLException
     */
    public int saveRecord(int dbOperation, String strTableName, String keyColumnName, long keyValue, LinkedHashMap<String, Object> lhsmColumnsAndValues) throws SQLException {
        switch (dbOperation) {
            case 1:
                lhsmColumnsAndValues.put(keyColumnName, keyValue);
                return saveNewSingleRecord(strTableName, lhsmColumnsAndValues);
            case 2:
                return updateSingleRecord(strTableName, lhsmColumnsAndValues, new DataCall.TableFilter(keyColumnName, keyValue));
            case 3:
                return deleteRecord(strTableName, new DataCall.TableFilter(keyColumnName, keyValue));
            default:
                return -1;
        }
    }

    /*
     * @param dbOperation. 1 = "insert", 2 = "update", 3 = "delete"
     * @param strTableName
     * @param keyColumnName. If it's an insert, it will be added in the list of values to be inserted.Should never be null 
     * @param keyValue. 
     * @param lhsmColumnsAndValues. other column values to be added
     * @return
     * @throws SQLException
     */
    public int saveRecord(int dbOperation, String strTableName, String keyColumnName, int keyValue, LinkedHashMap<String, Object> lhsmColumnsAndValues) throws SQLException {
        //this method may look like a duplicate, but its necessary because the int and long may be handled differently when saving
        //investigate more on whether this duplicate int method is needed
        switch (dbOperation) {
            case 1:
                lhsmColumnsAndValues.put(keyColumnName, keyValue);
                return saveNewSingleRecord(strTableName, lhsmColumnsAndValues);
            case 2:
                return updateSingleRecord(strTableName, lhsmColumnsAndValues, new DataCall.TableFilter(keyColumnName, keyValue));
            case 3:
                return deleteRecord(strTableName, new DataCall.TableFilter(keyColumnName, keyValue));
            default:
                return -1;
        }
    }

    public boolean deleteRecordsAll(String tableName) throws SQLException {
        boolean bSuccess = false;
        PreparedStatement ps = null;
        String sql;
        int h;
        try {
            //condition column names will have their boleans and logics attached to them for simplicty
            sql = "DELETE FROM " + tableName;
            ps = getConn().prepareStatement(sql);
            ps.executeUpdate();
            bSuccess = true;
        } finally {
            tryClosePreparedStatement(ps);
        }//end finally
        return bSuccess;
    } //end method

    private void setPreparedStatementValues(PreparedStatement ps, Collection<Object> collectionValues) throws SQLException {
        //Get the column values
        int parameterIndex = 1;//position starts at 1
        for (Object objValues : collectionValues) {
            if (objValues instanceof Integer) {
                ps.setInt(parameterIndex, (Integer) objValues);
            } else if (objValues instanceof Double) {
                ps.setDouble(parameterIndex, (Double) objValues);
            } else if (objValues instanceof String) {
                ps.setString(parameterIndex, (String) objValues);
            } else if (objValues instanceof Boolean) {
                ps.setBoolean(parameterIndex, (Boolean) objValues);
            } else if (objValues instanceof java.sql.Date) {
                ps.setDate(parameterIndex, (java.sql.Date) objValues);
            } else if (objValues instanceof Long) {
                ps.setLong(parameterIndex, (Long) objValues);
            } else if (objValues instanceof DataCall.TypeValue) {
                //for our generic types
                DataCall.TypeValue clstype = (DataCall.TypeValue) objValues;

                int iSqlType = java.sql.Types.VARCHAR;//default

                //get the corresponding SQLTYPE
                if (clstype.classType == Integer.class) {
                    iSqlType = java.sql.Types.INTEGER;
                } else if (clstype.classType == String.class) {
                    iSqlType = java.sql.Types.VARCHAR;
                } else if (clstype.classType == Boolean.class) {
                    iSqlType = java.sql.Types.BOOLEAN;
                } else if (clstype.classType == Double.class) {
                    iSqlType = java.sql.Types.DOUBLE;
                } else if (clstype.classType == java.sql.Date.class) {
                    iSqlType = java.sql.Types.DATE;
                } else if (clstype.classType == Long.class) {
                    iSqlType = java.sql.Types.BIGINT;
                } else if (clstype.classType == Float.class) {
                    iSqlType = java.sql.Types.FLOAT;
                }
                //if null just set null
                if (clstype.value == null) {
                    ps.setNull(parameterIndex, iSqlType);
                } else {
                    ps.setObject(parameterIndex, clstype.value, iSqlType);
                }//end if

            } else if (objValues == null) {
                ps.setNull(parameterIndex, java.sql.Types.VARCHAR);
            }//end if

            parameterIndex++;
        }
    }

    /**
     * used to handle our generic object types like nulls in saving records to
     * database
     */
    public static class TypeValue {

        public Class<?> classType;
        public Object value;

        /**
         * used to handle nulls onlyF
         *
         * @param type
         * @param value
         */
        public TypeValue(Class<?> type, Object value) {
            this.classType = type;
            this.value = value;
        }

    }

    /**
     * used to handle our filtering of records during saving or (selecting in
     * future when we use parameterised queries) TODO. PUSH THIS CLASS TO IT'S
     * OWN FILE. VERY IMPORTANT CLASS
     */
    public static class TableFilter {

        private String parameterisedSqlCondition;
        private String sqlCondition;
        public final List<String> columnNames = new ArrayList<>();
        public final List<Object> columnValues = new ArrayList<>();

        public TableFilter() {
            resetFilter();
        }

        public TableFilter(String strColumnName, Object objColumnValue) {
            this();
            addValueCondition(strColumnName, objColumnValue);
        }

        public TableFilter(String strColumnName, Object objColumnValue, String strComparisonOperator) {
            this();
            addValueCondition(strColumnName, objColumnValue, strComparisonOperator);
        }

        public TableFilter(String strSqlColCondition) {
            this();
            addColumnCondition(strSqlColCondition);
        }

        //This constructor can be used for compicated queries that addCondition method can't
        //condition column names will have their boleans and logics attached to them for simplicty
//        public TableFilter(String strPreparedSqlCondition, List<Object> columnValues) {
//            this.parameterisedSqlCondition = strPreparedSqlCondition;
//            this.columnValues = columnValues;
//        }
        public void addValueCondition(String strColumnName, Object objColumnValue, String strComparisonOperator, String strlogicalOperator, boolean bIsSqlArrayString) {
            String strSqlColumnValue;

            if (bIsSqlArrayString) {
                //for instance, WHERE IN (1,2,3)
                strSqlColumnValue = String.valueOf(objColumnValue);
            } else if (objColumnValue instanceof String || objColumnValue instanceof java.sql.Date) {
                //strings,dates have single quotes around their values for normal sql queries
                strSqlColumnValue = "'" + String.valueOf(objColumnValue) + "'";
            } else {
                strSqlColumnValue = String.valueOf(objColumnValue);
            }

            if (isEmpty()) {
                parameterisedSqlCondition = strColumnName + strComparisonOperator + "?";
                this.sqlCondition = "(" + strColumnName + " " + strComparisonOperator + " " + strSqlColumnValue + ")";
            } else {
                parameterisedSqlCondition = parameterisedSqlCondition + " " + strlogicalOperator + " " + strColumnName + strComparisonOperator + "?";
                this.sqlCondition = this.sqlCondition + " " + strlogicalOperator + " (" + strColumnName + " " + strComparisonOperator + " " + strSqlColumnValue + ")";
            }
            columnValues.add(objColumnValue);
            columnNames.add(strColumnName);
        }

        public void addValueCondition(String strColumnName, Object objColumnValue, String strComparisonOperator, String strlogicalOperator) {
            addValueCondition(strColumnName, objColumnValue, strComparisonOperator, strlogicalOperator, false);
        }

        public void addValueCondition(String strColumnName, Object objColumnValue, String strComparisonOperator) {
            addValueCondition(strColumnName, objColumnValue, strComparisonOperator, "AND", false);
        }

        public void addValueCondition(String strColumnName, Object objColumnValue) {
            addValueCondition(strColumnName, objColumnValue, "=");
        }

        /**
         * will concatenate the list of integers to a sql string for instance,
         * List of (1,2,3) will be WHERE IN (1,2,3)
         *
         * @param strColumnName
         * @param lstColumnValues
         */
        public void addValueCondition(String strColumnName, List<Integer> lstColumnValues) {
            //concantenate the student ids into a sql like string for getting the transactions
            String strSqlStudentIds = "";
            for (int id : lstColumnValues) {
                if (strSqlStudentIds.isEmpty()) {
                    strSqlStudentIds = id + "";
                } else {
                    strSqlStudentIds = strSqlStudentIds + "," + id;
                }
            }//end for loop

            addValueCondition(strColumnName, "(" + strSqlStudentIds + ")", "IN", "AND", true);
        }

        public void addColumnCondition(String strSqlColCondition, String strlogicalOperator, List<String> columnNames) {
            //just check if sql condition is null. cause parametrised query is never used here
            if (isEmpty()) {
                this.sqlCondition = "(" + strSqlColCondition + ")";
            } else {
                this.sqlCondition = this.sqlCondition + " " + strlogicalOperator + " (" + strSqlColCondition + ")";
            }
            this.columnNames.addAll(columnNames);
        }

        public void addColumnCondition(String strSqlColCondition, String... columnNames) {
            addColumnCondition(strSqlColCondition, "AND", Arrays.asList(columnNames));
        }

        /**
         * @param tblFilter Nulls are allowed
         * @param strlogicalOperator
         */
        public void addCondition(TableFilter tblFilter, String strlogicalOperator) {
            //it's important theck if the passed tblFilter is null before checking it's contents. 
            if (tblFilter != null && !tblFilter.isEmpty()) {
                //TODO. Is this possible for paremetarised queries??
                if (isEmpty()) {
                    this.sqlCondition = "(" + tblFilter.getSqlCondition() + ")";
                } else {
                    this.sqlCondition = this.sqlCondition + " " + strlogicalOperator + " (" + tblFilter.getSqlCondition() + ")";
                }

                this.columnValues.addAll(tblFilter.columnValues);
                this.columnNames.addAll(tblFilter.columnNames);
            }
        }

        public void addCondition(TableFilter tblFilter) {
            addCondition(tblFilter, "AND");
        }

        public String getSqlCondition() {
            return sqlCondition;
        }

        public void setSqlCondition(String sqlCondition) {
            this.sqlCondition = parameterisedSqlCondition;
        }

        //used for complicated queries
        public void setParameterisedSqlCondition(String parameterisedSqlCondition) {
            this.parameterisedSqlCondition = parameterisedSqlCondition;
        }

        public String getParameterisedSqlCondition() {
            return parameterisedSqlCondition;
        }

        //for complicated queries
        public void addColumnNameAndValue(String strColumnName, Object objColumnValue) {
            this.columnNames.add(strColumnName);
            this.columnValues.add(objColumnValue);
        }

        public void resetFilter() {
            this.sqlCondition = "";
            this.parameterisedSqlCondition = "";
            this.columnValues.clear();
            this.columnNames.clear();
        }

        public boolean isEmpty() {
            return StringsUtil.isNullOrEmpty(getSqlCondition()); //Parametarised queries could be empty for columns conditions //&& StringsUtil.isNullOrEmpty(getParameterisedSqlCondition());
        }

        @Override
        public String toString() {
            return getSqlCondition();
        }

    }//end inner class

}//end class
