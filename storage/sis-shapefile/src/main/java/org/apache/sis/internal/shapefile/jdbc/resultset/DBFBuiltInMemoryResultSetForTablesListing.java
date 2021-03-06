/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sis.internal.shapefile.jdbc.resultset;

import java.util.logging.Level;

import org.apache.sis.internal.shapefile.jdbc.statement.DBFStatement;

/**
 * Special ResultSet listing tables contained in this DBase 3 (a single table).
 * @author Marc LE BIHAN
 */
public class DBFBuiltInMemoryResultSetForTablesListing extends BuiltInMemoryResultSet {
    /** There's only one result in this ResultSet. */
    private int index = 0;

    /**
     * Construct a ResultSet.
     * @param stmt Statement.
     */
    public DBFBuiltInMemoryResultSetForTablesListing(DBFStatement stmt) {
        super(stmt, "driver list tables in this DBase 3 file");
    }

    /**
     * @see java.sql.ResultSet#getString(java.lang.String)
     */
    @Override public String getString(String columnLabel) {
        logStep("getString", columnLabel);

        {   // On the JDK7 branch, this is a switch on strings.
            if (columnLabel.equals("TABLE_NAME"))                // String => table name.
            {
                String tableName = getTableName();
                this.wasNull = (tableName == null);
                return tableName;
            }

            else if (columnLabel.equals("TABLE_TYPE")) {             // String => table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
                this.wasNull = false;
                return "TABLE";
            }

            else if (columnLabel.equals("TYPE_NAME")                 // String => type name (may be null)
                  || columnLabel.equals("TABLE_CAT")                 // String => table catalog (may be null)
                  || columnLabel.equals("TABLE_SCHEM")               // String => table schema (may be null)
                  || columnLabel.equals("REMARKS")                   // String => explanatory comment on the table
                  || columnLabel.equals("TYPE_CAT")                  // String => the types catalog (may be null)
                  || columnLabel.equals("TYPE_SCHEM")                // String => the types schema (may be null)
                  || columnLabel.equals("SELF_REFERENCING_COL_NAME") // String => name of the designated "identifier" column of a typed table (may be null)
                  || columnLabel.equals("REF_GENERATION")) {         // String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null)
                this.wasNull = true;
                return null;
            }

            else {
                this.wasNull = true;
                return null;
            }
        }
    }

    /**
     * @see java.sql.ResultSet#next()
     */
    @Override public boolean next() throws SQLNoResultException
    {
        logStep("next");

        if (this.index > 1) {
            throw new SQLNoResultException(format(Level.WARNING, "excp.only_one_table_per_dbf"), "Driver manager asks for table listing", getFile());
        }

        this.index ++;
        return (this.index == 1) ? true : false;
    }
}
