/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.foundation.sql;

import static org.apiguardian.api.API.Status.STABLE;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.BatchUpdateException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLOutput;
import java.sql.SQLTimeoutException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.sql.internal.EnhancedPreparedStatementImpl;

/**
 *  <p>{@summary The definition of an enhanced
 *  {@link java.sql.PreparedStatement}.}</p>
 *  <p>The implementation of this interface wraps an instance of
 *  {@code PreparedStatement} and provides solutions for three issues that the
 *  original definition has:</p>
 *  <ol>
 *    <li>The placeholders in a prepared statement are only identified by their
 *    position, and it is quite easy to mix those positions up, especially when
 *    modifying the statement. This can cause errors during runtime that are
 *    very difficult to detect.</li>
 *    <li>The fact that the placeholders are only position based causes another
 *    inconvenience. Assume an SQL statement like that one below:
 *    <pre><code>    SELECT *
 *    FROM table1,table2
 *    WHERE table1.key = table2.key
 *          AND table1.value &lt; ?
 *          AND table2.value &gt; ?</code></pre>
 *    where the question mark stands for the same value. This would require to
 *    call
 *    {@link java.sql.PreparedStatement#setInt(int, int) setInt()}
 *    twice, with the same value, but a different position index.</li>
 *    <li>{@code PreparedStatement} does not allow to log the SQL statement
 *    together with the assigned values for the placeholders; at least this is
 *    not defined in the interface itself; some vendor specific implementations
 *    may provide such an additional API, but there is no generic one. An
 *    implementation of this interface takes a method that can write any
 *    executed SQL statement to a logger.</li>
 *  </ol>
 *
 *  @note The interface {@code EnhancedPreparedStatement} does not extends the
 *      interface {@code java.sql.PreparedStatement}!
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: EnhancedPreparedStatement.java 1091 2024-01-25 23:10:08Z tquadrat $
 *  @since 0.1.0
 *
 *  @UMLGraph.link
 *
 *  @see    java.sql.PreparedStatement
 */
@SuppressWarnings( {"ClassWithTooManyMethods", "OverlyComplexClass"} )
@ClassVersion( sourceVersion = "$Id: EnhancedPreparedStatement.java 1091 2024-01-25 23:10:08Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public sealed interface EnhancedPreparedStatement extends AutoCloseable
    permits org.tquadrat.foundation.sql.internal.EnhancedPreparedStatementBase
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  <p>{@summary The definition of logger method for a
     *  {@link java.sql.PreparedStatement}.}</p>
     *  <p>This is a functional interface whose functional method is
     *  {@link #log(String,String,List,StackTraceElement[])}.</p>
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: EnhancedPreparedStatement.java 1091 2024-01-25 23:10:08Z tquadrat $
     *  @since 0.1.0
     *
     *  @UMLGraph.link
     *
     *  @see    java.sql.PreparedStatement
     */
    @SuppressWarnings( "InnerClassOfInterface" )
    @ClassVersion( sourceVersion = "$Id: EnhancedPreparedStatement.java 1091 2024-01-25 23:10:08Z tquadrat $" )
    @API( status = STABLE, since = "0.1.0" )
    @FunctionalInterface
    public interface StatementLogger
    {
        /**
         *  Provides the logging information.
         *
         *  @param  operation   The name of the operation that logs.
         *  @param  statement   The source of the prepared statement.
         *  @param  values  The values.
         *  @param  stackTrace  The stacktrace; can be {@code null}.
         */
        @SuppressWarnings( "MethodCanBeVariableArityMethod" )
        public void log( final String operation, final String statement, final List<String> values, final StackTraceElement [] stackTrace );
    }
    //  interface StatementLogger

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The regular expression that is used to identify a named parameter in
     *  the SQL statement text: {@value}.
     *
     *  @see    #prepareStatement(Connection,String)
     */
    public static final String VARIABLE_PATTERN = "[^:](:[a-zA-Z][a-zA-Z0-9]*)";

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds a set of parameters to this {@code EnhancedPreparedStatement}
     *  instance's batch of commands.
     *
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    java.sql.Statement#addBatch(String)
     */
    public void addBatch() throws SQLException;

    /**
     *  <p>{@summary Cancels this {@code EnhancedPreparedStatement} object if
     *  both the DBMS and driver support aborting an SQL statement.}</p>
     *  <p>This method can be used by one thread to cancel a statement that
     *  is being executed by another thread.</p>
     *
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    void cancel() throws SQLException;

    /**
     *  Empties this {@code EnhancedPreparedStatement} instance's current list
     *  of SQL commands.
     *
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement}, or the
     *      driver does not support batch updates.
     *
     *  @see    #addBatch()
     *  @see    java.sql.DatabaseMetaData#supportsBatchUpdates()
     */
    public void clearBatch() throws SQLException;

    /**
     *  <p>{@summary Clears the current parameter values immediately.}</p>
     *  <p>In general, parameter values remain in force for repeated use of a
     *  statement. Setting a parameter value automatically clears its previous
     *  value. However, in some cases it is useful to immediately release the
     *  resources used by the current parameter values; this can be done by
     *  calling the method {@code clearParameters()}.
     *
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     */
    public void clearParameters() throws SQLException;

    /**
     *  <p>{@summary Clears all the warnings reported on this
     *  {@code EnhancedPreparedStatement} instance.} After a call to this
     *  method, the method
     *  {@link #getWarnings()}
     *  will return {@code null} until a new warning is reported for this
     *  {@code EnhancedPreparedStatement} instance.</p>
     *
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     */
    public void clearWarnings() throws SQLException;

    /**
     *  <p>{@summary Releases this instance's database and JDBC resources
     *  immediately instead of waiting for this to happen when it is
     *  automatically closed.} It is generally good practice to release
     *  resources as soon as you are finished with them to avoid tying up
     *  database resources.</p>
     *  <p>Calling the method {@code close} on a
     *  {@code EnhancedPreparedStatement} object that is already closed has no
     *  effect.</p>
     *
     *  @note   When a {@code EnhancedPreparedStatement} instance is closed,
     *      its current {@code ResultSet} instance, if one exists, is also
     *      closed.
     *
     *  @throws SQLException    A database access error occurred.
     */
    @Override
    public void close() throws SQLException;

    /**
     *  <p>{@summary Specifies that this {@code EnhancedPreparedStatement} will
     *  be closed when all its dependent result sets are closed.} If the
     *  execution of the {@code EnhancedPreparedStatement} does not produce
     *  any result sets, this method has no effect.</p>
     *
     *  @note   Multiple calls to {@code closeOnCompletion()} do not toggle the
     *      effect on this {@code EnhancedPreparedStatement}. However, a call
     *      to {@code closeOnCompletion()} does affect both the subsequent
     *      execution of statements, and statements that currently have open,
     *      dependent, result sets.
     *
     *  @throws SQLException    This method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void closeOnCompletion() throws SQLException;

    /**
     *  <p>{@summary Enables the logging output for the
     *  {@code EnhancedPreparedStatement} instances.}</p>
     *  <p>The {@code logger} method takes three arguments:</p>
     *  <ol>
     *      <li>{@code operation} – The name of the operation that logs.</li>
     *      <li>{@code statement} – The source of the prepared statement.</li>
     *      <li>{@code values} – A list of the values in the format
     *      <pre><code>&lt;<i>name</i>&gt;<b> [</b>&lt;<i>type</i>&gt;<b>]:&lt;<i>value</i>&gt;</b></code></pre>
     *      A type of {@code NULL} indicates an unknown type; for large values (like
     *      {@link java.sql.Blob}
     *      or
     *      {@link java.io.Reader})
     *      only the class is given instead of the real value.</li>
     *      <li>{@code stacktrace} – The stacktrace; will be {@code null} if
     *      {@code addStacktrace} is {@code false}.</li>
     *  </ol>
     *  <p>The {@code logCheck} method returns {@code true} only when logging
     *  should be done. No information is collected while it returns
     *  {@code false}. As the method is called for nearly any operation, its
     *  implementation should be as efficient as possible.</p>
     *
     *  @param  logger  The method that takes the logging information.
     *  @param  logCheck    The method that returns a flag whether log output
     *      is desired.
     *  @param  addStacktrace   {@code true} if the stacktrace should be added
     *      to the log output.
     */
    public static void enableLogging( final StatementLogger logger, final BooleanSupplier logCheck, final boolean addStacktrace )
    {
        EnhancedPreparedStatementImpl.enableLogging( logger, logCheck, addStacktrace );
    }   //  enableLogging()

    /**
     *  <p>{@summary Returns a SQL identifier.} If {@code identifier} is a
     *  simple SQL identifier:</p>
     *  <ul>
     *      <li>Return the original value if {@code alwaysQuote} is
     *      {@code false}</li>
     *      <li>Return a delimited identifier if {@code alwaysQuote} is
     *      {@code true}</li>
     *  </ul>
     *  <p>If {@code identifier} is not a simple SQL identifier,
     *  {@code identifier} will be enclosed in double quotes if not already
     *  present. If the datasource does not support double quotes for delimited
     *  identifiers, the identifier should be enclosed by the string returned
     *  from
     *  {@link java.sql.DatabaseMetaData#getIdentifierQuoteString}.
     *  If the datasource does not support delimited identifiers, a
     *  {@code SQLFeatureNotSupportedException} should be thrown.</p>
     *  <p>A {@code SQLException} will be thrown if {@code identifier} contains
     *  any characters invalid in a delimited identifier or the identifier
     *  length is invalid for the datasource.</p>
     *
     *  @param  identifier  An SQL identifier.
     *  @param  alwaysQuote Indicates if a simple SQL identifier should be
     *      returned as a quoted identifier.
     *  @return A simple SQL identifier or a delimited identifier
     *  @throws SQLException    The identifier was not a valid identifier.
     *  @throws SQLFeatureNotSupportedException The datasource does not support
     *      delimited identifiers.
     *  @throws NullPointerException    The identifier was {@code null}.
     */
    public String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException;


    /**
     *  <p>{@summary Returns a {@code String} enclosed in single quotes.} Any
     *  occurrence of a single quote within the string will be replaced by two
     *  single quotes.</p>
     *
     *  <blockquote>
     *      <table class="striped">
     *          <caption>Examples of the conversion:</caption>
     *          <thead>
     *              <tr>
     *                  <th scope="col">Value</th>
     *                  <th scope="col">Result</th>
     *              </tr>
     *          </thead>
     *          <tbody style="text-align:center">
     *              <tr>
     *                  <th scope="row">Hello</th>
     *                  <td>'Hello'</td>
     *              </tr>
     *              <tr>
     *                  <th scope="row">G'Day</th>
     *                  <td>'G''Day'</td>
     *              </tr>
     *              <tr>
     *                  <th scope="row">'G''Day'</th>
     *                  <td>'''G''''Day'''</td>
     *              </tr>
     *              <tr>
     *                  <th scope="row">I'''M</th>
     *                  <td>'I''''''M'</td>
     *              </tr>
     *          </tbody>
     *      </table>
     *  </blockquote>
     *
     *  @param  value A character String.
     *  @return A string enclosed by single quotes with every single quote
     *      converted to two single quotes
     *  @throws NullPointerException    The {@code value} argument was
     *      {@code null}.
     *  @throws SQLException    A database access error occurred.
     */
    public String enquoteLiteral( String value ) throws SQLException;

    /**
     *  <p>{@summary Returns a {@code String} representing a National Character
     *  Set Literal enclosed in single quotes and prefixed with a upper case
     *  letter {@code N}.} Any occurrence of a single quote within the string
     *  will be replaced by two single quotes.</p>
     *
     *  <blockquote>
     *    <table class="striped">
     *      <caption>Examples of the conversion:</caption>
     *      <thead>
     *        <tr>
     *          <th scope="col">Value</th>
     *          <th scope="col">Result</th>
     *        </tr>
     *      </thead>
     *      <tbody>
     *        <tr>
     *          <th scope="row">Hello</th>
     *          <td>N'Hello'</td>
     *        </tr>
     *        <tr>
     *          <th scope="row">G'Day</th>
     *          <td>N'G''Day'</td>
     *        </tr>
     *        <tr>
     *          <th scope="row">'G''Day'</th>
     *          <td>N'''G''''Day'''</td>
     *        </tr>
     *        <tr>
     *          <th scope="row">I'''M</th>
     *          <td>N'I''''''M'</td>
     *        </tr>
     *        <tr>
     *          <th scope="row">N'Hello'</th>
     *          <td>N'N''Hello'''</td>
     *        </tr>
     *      </tbody>
     *    </table>
     *  </blockquote>
     *
     *  @param  s   A character string
     *  @return The result of replacing every single quote character in the
     *      argument by two single quote characters where this entire result is
     *      then prefixed with 'N'.
     *  @throws SQLException    A database access error occurred.
     *
     *  @see java.sql.Statement#enquoteNCharLiteral(String)
     */
    public String enquoteNCharLiteral( String s ) throws SQLException;

    /**
     *  <p>{@summary Executes the SQL statement in this
     *  {@code PreparedStatement} object, which may be any kind of SQL
     *  statement.}</p>
     *  <p>Some prepared statements return multiple results; the
     *  {@code execute()} method handles these complex statements as well as
     *  the simpler form of statements handled by the methods
     *  {@link #executeQuery()}
     *  and
     *  {@link #executeUpdate()}.</p>
     *  <p>The {@code execute()} method returns a {@code boolean} to indicate
     *  the form of the first result. You must call either the method
     *  {@link #getResultSet()}
     *  or
     *  {@link #getUpdateCount()}
     *  to retrieve the result; you must call
     *  {@link #getMoreResults()}
     *  to move to any subsequent result(s).</p>
     *
     *  @return {@code true} if the first result is a
     *      {@link ResultSet}
     *      instance; {@code false} if the first result is an update count or
     *      there is no result.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or no
     *      argument was supplied for at least one parameter.
     *  @throws SQLTimeoutException The driver determined that the timeout
     *      value that was specified by the
     *      {@link #setQueryTimeout(int)}
     *      method has been exceeded and has at least attempted to cancel the
     *      currently running
     *      {@link java.sql.Statement}.
     *
     *  @see    java.sql.Statement#execute
     *  @see    #getResultSet()
     *  @see    #getUpdateCount()
     *  @see    #getMoreResults()
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    public boolean execute() throws SQLException;

    /**
     *  <p>{@summary Submits a batch of commands to the database for execution
     *  and if all commands execute successfully, returns an array of update
     *  counts.} The {@code int} elements of the array that is returned are
     *  ordered to correspond to the commands in the batch, which are ordered
     *  according to the order in which they were added to the batch.</p>
     *  <p>The elements in the array returned by the method
     *  {@code executeBatch()} may be one of the following:</p>
     *  <ol>
     *      <li>A number greater than or equal to zero – indicates that the
     *      command was processed successfully and is an update count giving
     *      the number of rows in the database that were affected by the
     *      command's execution.</li>
     *      <li>A value of
     *      {@link java.sql.Statement#SUCCESS_NO_INFO}
     *      ({@value java.sql.Statement#SUCCESS_NO_INFO} – indicates that the
     *      command was processed successfully but that the number of rows
     *      affected is unknown.</li>
     *  </ol>
     *  <p>If one of the commands in a batch update fails to execute properly,
     *  this method throws a
     *  {@link BatchUpdateException},
     *  and a JDBC driver may or may not continue to process the remaining
     *  commands in the batch. However, the driver's behaviour must be
     *  consistent with a particular DBMS, either always continuing to process
     *  commands or never continuing to process commands. If the driver
     *  continues processing after a failure, the array returned by the method
     *  {@link BatchUpdateException#getUpdateCounts()}
     *  will contain as many elements as there are commands in the batch, and
     *  at least one of the elements will be
     *  {@link java.sql.Statement#EXECUTE_FAILED}
     *  ({@value java.sql.Statement#EXECUTE_FAILED} – indicating that the
     *  command failed to execute successfully); it occurs only if a driver
     *  continues to process commands after a command failed.</p>
     *
     *  @note    The possible implementations and return values have been
     *  modified in the Java 2 SDK, Standard Edition, version 1.3 to
     *  accommodate the option of continuing to process commands in a batch
     *  update after a {@code BatchUpdateException} object has been thrown.
     *
     *  @return An array of update counts containing one element for each
     *      command in the batch. The elements of the array are ordered
     *      according to the order in which commands were added to the batch.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      driver does not support batch statements.
     *  @throws BatchUpdateException    One of the commands sent to the
     *      database failed to execute properly or attempts to return a result
     *      set.
     *  @throws SQLTimeoutException The driver determined that the timeout
     *      value that was specified by a call to
     *      {@link #setQueryTimeout(int)}
     *      was exceeded and at least attempted to cancel the currently running
     *      statement.
     *
     *  @see    #addBatch()
     *  @see    java.sql.DatabaseMetaData#supportsBatchUpdates
     */
    public int[] executeBatch() throws SQLException;

    /**
     *  <p>{@summary Submits a batch of commands to the database for execution
     *  and if all commands execute successfully, returns an array of update
     *  counts.} The {@code int} elements of the array that is returned are
     *  ordered to correspond to the commands in the batch, which are ordered
     *  according to the order in which they were added to the batch.</p>
     *  <p>The elements in the array returned by the method
     *  {@code executeBatch()} may be one of the following:</p>
     *  <ol>
     *      <li>A number greater than or equal to zero – indicates that the
     *      command was processed successfully and is an update count giving
     *      the number of rows in the database that were affected by the
     *      command's execution.</li>
     *      <li>A value of
     *      {@link java.sql.Statement#SUCCESS_NO_INFO}
     *      ({@value java.sql.Statement#SUCCESS_NO_INFO} – indicates that the
     *      command was processed successfully but that the number of rows
     *      affected is unknown.</li>
     *  </ol>
     *  <p>If one of the commands in a batch update fails to execute properly,
     *  this method throws a
     *  {@link BatchUpdateException},
     *  and a JDBC driver may or may not continue to process the remaining
     *  commands in the batch. However, the driver's behaviour must be
     *  consistent with a particular DBMS, either always continuing to process
     *  commands or never continuing to process commands. If the driver
     *  continues processing after a failure, the array returned by the method
     *  {@link BatchUpdateException#getLargeUpdateCounts()}
     *  will contain as many elements as there are commands in the batch, and
     *  at least one of the elements will be
     *  {@link java.sql.Statement#EXECUTE_FAILED}
     *  ({@value java.sql.Statement#EXECUTE_FAILED} – indicating that the
     *  command failed to execute successfully); it occurs only if a driver
     *  continues to process commands after a command failed.</p>
     *  <p>This method should be used when the returned row count may exceed
     *  {@link Integer#MAX_VALUE}.</p>
     *
     *  @return An array of update counts containing one element for each
     *      command in the batch. The elements of the array are ordered
     *      according to the order in which commands were added to the batch.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      driver does not support batch statements.
     *  @throws BatchUpdateException    One of the commands sent to the
     *      database failed to execute properly or attempts to return a result
     *      set.
     *  @throws SQLTimeoutException The driver determined that the timeout
     *      value that was specified by a call to
     *      {@link #setQueryTimeout(int)}
     *      was exceeded and at least attempted to cancel the currently running
     *      statement.
     *
     *  @see    #addBatch()
     *  @see    #executeBatch()
     *  @see    java.sql.Statement#executeLargeBatch()
     *  @see    java.sql.DatabaseMetaData#supportsBatchUpdates
     */
    public long[] executeLargeBatch() throws SQLException;

    /**
     *  <p>{@summary Executes the SQL statement in this
     *  {@code EnhancedPreparedStatement} instance.} It must be an SQL Data
     *  Manipulation Language (DML) statement, such as {@code INSERT},
     *  {@code UPDATE} or {@code DELETE}; or an SQL statement that returns
     *  nothing, such as a DDL statement.</p>
     *  <p>This method should be used when the returned row count may exceed
     *  {@link Integer#MAX_VALUE}.</p>
     *  <p>The default implementation will throw
     *  {@link UnsupportedOperationException}.</p>
     *
     *  @return Either the row count for SQL Data Manipulation Language (DML)
     *      statements or 0 for SQL statements that return nothing.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the SQL
     *      statement returns a
     *      {@link ResultSet}
     *      object.
     *  @throws SQLTimeoutException The driver determined that the timeout
     *      value that was specified by the
     *      {@link #setQueryTimeout(int)}
     *      method has been exceeded and has at least attempted to cancel the
     *      currently running
     *      {@link java.sql.Statement}.
     */
    public long executeLargeUpdate() throws SQLException;

    /**
     *  <p>{@summary Executes the SQL query in this {@code PreparedStatement}
     *  object and returns the
     *  {@link ResultSet}
     *  object generated by the query.}</p>
     *
     *  @return A
     *      {@link ResultSet}
     *      instance that contains the data produced by the query; never
     *      {@code null}.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the SQL
     *      statement did not return a {@code ResultSet} object.
     *  @throws SQLTimeoutException The driver determined that the timeout
     *      value that was specified by the
     *      {@link #setQueryTimeout(int)}
     *      method has been exceeded and has at least attempted to cancel the
     *      currently running
     *      {@link java.sql.Statement}.
     */
    public ResultSet executeQuery() throws SQLException;

    /**
     *  <p>{@summary Executes the SQL statement in this
     *  {@code EnhancedPreparedStatement} instance.} It must be an SQL Data
     *  Manipulation Language (DML) statement, such as {@code INSERT},
     *  {@code UPDATE} or {@code DELETE}; or an SQL statement that returns
     *  nothing, such as a DDL statement.</p>
     *
     *  @return Either the row count for SQL Data Manipulation Language (DML)
     *      statements or 0 for SQL statements that return nothing.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the SQL
     *      statement returns a
     *      {@link ResultSet}
     *      object.
     *  @throws SQLTimeoutException The driver determined that the timeout
     *      value that was specified by the
     *      {@link #setQueryTimeout(int)}
     *      method has been exceeded and has at least attempted to cancel the
     *      currently running
     *      {@link java.sql.Statement}.
     */
    public int executeUpdate() throws SQLException;

    /**
     *  <p>{@summary Retrieves the
     *  {@link Connection}
     *  instance that produced the
     *  {@link java.sql.PreparedStatement}
     *  instance wrapped by this {@code EnhancedPreparedStatement}.}</p>
     *
     *  @return The connection that produced the wrapped prepared statement.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     */
    public Connection getConnection()  throws SQLException;

    /**
     *  <p>{@summary Retrieves the direction for fetching rows from database
     *  tables that is the default for result sets generated from this
     *  {@code EnhancedPreparedStatement} instance.}</p>
     *  <p>If this {@code EnhancedPreparedStatement} instance has not set a
     *  fetch direction by calling the method
     *  {@link #setFetchDirection(int)},
     *  the return value is implementation-specific.</p>
     *
     *  @return The default fetch direction for result sets generated from this
     *      {@code EnhancedPreparedStatement} instance.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #setFetchDirection(int)
     */
    public int getFetchDirection() throws SQLException;

    /**
     *  <p>{@summary Retrieves the number of result set rows that is the
     *  default fetch size for
     *  {@link java.sql.ResultSet}
     *  instances generated from this {@code EnhancedPreparedStatement}
     *  instance.} If this statement instance has not set a fetch size by
     *  calling the method
     *  {@link #setFetchSize(int)},
     *  the return value is implementation-specific.</p>
     *
     *  @return The default fetch size for result sets generated from this
     *      {@code EnhancedPreparedStatement} instance.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #setFetchSize(int)
     */
    public int getFetchSize() throws SQLException;

    /**
     *  <p>{@summary Retrieves any auto-generated keys created as a result of
     *  executing this {@code EnhancedPreparedStatement} instance.} If this
     *  statement did not generate any keys, an empty
     *  {@link java.sql.ResultSet}
     *  object is returned.</p>
     *
     *  @note   If the columns which represent the auto-generated keys were not
     *      specified, the JDBC driver implementation will determine the
     *      columns which best represent the auto-generated keys.
     *
     *  @return A {@code ResultSet} instance containing the auto-generated
     *      key(s) generated by the execution of this
     *      {@code EnhancedPreparedStatement} instance.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code Statement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public ResultSet getGeneratedKeys() throws SQLException;

    /**
     *  <p>{@summary Retrieves the maximum number of rows that a
     *  {@link ResultSet}
     *  instance produced by this {@code EnhancedPreparedStatement} instance
     *  can contain.} If this limit is exceeded, the excess rows are silently
     *  dropped.</p>
     *  <p>This method should be used when the returned row limit may exceed
     *  {@link Integer#MAX_VALUE}.</p>
     *  <p>The default implementation will return {@code 0}.</p>
     *
     *  @return The current maximum number of rows for a {@code ResultSet}
     *      instance produced by this {@code EnhancedPreparedStatement}
     *      instance; zero means there is no limit, or that the method is not
     *      supported by the JDBC driver.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #getMaxRows()
     *  @see    #setMaxRows(int)
     *  @see    #setLargeMaxRows(long)
      */
    public long getLargeMaxRows() throws SQLException;

    /**
     *  <p>{@summary Retrieves the current result as an update count.} If the
     *  result is a
     *  {@link ResultSet}
     *  object or there are no more results, -1 is returned.</p>
     *  <p>This method should be used when the returned row count may exceed
     *  {@link Integer#MAX_VALUE}.</p>
     *  <p>The default implementation will throw
     *  {@link UnsupportedOperationException}</p>
     *
     *  @return The current result as an update count; -1 if the current result
     *      is a {@code ResultSet} object or there are no more results.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #execute()
     */
    public long getLargeUpdateCount() throws SQLException;

    /**
     *  <p>{@summary Retrieves the maximum number of bytes that can be
     *  returned for character and binary column values in a
     *  {@link ResultSet}
     *  object produced by this {@code EnhancedPreparedStatement} object.}</p>
     *  <p>This limit applies only to
     *  {@link java.sql.JDBCType#BINARY},
     *  {@link java.sql.JDBCType#VARBINARY},
     *  {@link java.sql.JDBCType#LONGVARBINARY},
     *  {@link java.sql.JDBCType#CHAR},
     *  {@link java.sql.JDBCType#VARCHAR},
     *  {@link java.sql.JDBCType#NCHAR},
     *  {@link java.sql.JDBCType#NVARCHAR},
     *  {@link java.sql.JDBCType#LONGNVARCHAR}
     *  and
     *  {@link java.sql.JDBCType#LONGVARCHAR}
     *  columns. If the limit is exceeded, the excess data is silently
     *  discarded.</p>
     *
     *  @return The current column size limit for columns storing character and
     *      binary values; zero means there is no limit.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}
     *
     *  @see    #setMaxFieldSize(int)
     */
    public int getMaxFieldSize() throws SQLException;

    /**
     *  <p>{@summary Retrieves the maximum number of rows that a
     *  {@link ResultSet}
     *  instance produced by this {@code EnhancedPreparedStatement} instance
     *  can contain.} If this limit is exceeded, the excess rows are silently
     *  dropped.</p>
     *
     *  @return The current maximum number of rows for a {@code ResultSet}
     *      instance produced by this {@code EnhancedPreparedStatement}
     *      instance; zero means there is no limit.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #getLargeMaxRows()
     *  @see    #setMaxRows(int)
     *  @see    #setLargeMaxRows(long)
     */
    public int getMaxRows() throws SQLException;

    /**
     *  <p>{@summary Retrieves a
     *  {@link ResultSetMetaData}
     *  object that contains information about the columns of the
     *  {@link ResultSet}
     *  instance that will be returned when this
     *  {@code EnhancedPreparedStatement}
     *  instance is executed.}</p>
     *  <p>Because the
     *  {@link java.sql.PreparedStatement}
     *  that is wrapped by this {@code EnhancedPreparedStatement} is
     *  precompiled, it is possible to know about the {@code ResultSet} object
     *  that it will return without having to execute it. Consequently, it is
     *  possible to invoke the method {@code getMetaData()} on a
     *  {@code EnhancedPreparedStatement} instance rather than waiting to
     *  execute it and then invoking the
     *  {@link java.sql.ResultSet#getMetaData()}
     *  method on the {@code ResultSet} instance that is returned.</p>
     *
     *  @note   Using this method may be expensive for some drivers due to the
     *      lack of underlying DBMS support.
     *
     *  @return The description of a {@code ResultSet} object's columns or
     *      {@code null} if the driver cannot return a
     *      {@code ResultSetMetaData} object.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code PreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public ResultSetMetaData getMetaData() throws SQLException;

    /**
     *  <p>{@summary Moves to this {@code EnhancedPreparedStatement} instance's
     *  next result, returns {@code true} if it is a
     *  {@link ResultSet}
     *  instance, and implicitly closes any current {@code ResultSet}
     *  instance(s) previously obtained with the method
     *  {@link #getResultSet()}.}</p>
     *  <p>There are no more results when the following is true:</p>
     *  <pre><code>
     *     // stmt is a EnhancedPreparedStatement object
     *     ((stmt.getMoreResults() == false) &amp;&amp; (stmt.getUpdateCount() == -1))
     *  </code></pre>
     *
     *  @return {@code true} if the next result is a {@code ResultSet}
     *      instance; {@code false} if it is an update count or there are no
     *      more results.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #execute()
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    public boolean getMoreResults() throws SQLException;

    /**
     *  <p>{@summary Moves to this {@code EnhancedPreparedStatement} instance's
     *  next result, deals with any current
     *  {@link java.sql.ResultSet}
     *  instance(s) according  to the instructions specified by the given flag,
     *  and returns {@code true} if the next result is a {@code ResultSet}
     *  object.}</p>
     *  <p>There are no more results when the following is true:</p>
     *  <pre><code>
     *     // stmt is a Statement object
     *     ((stmt.getMoreResults(&nbsp;current&nbsp;) == false) &amp;&amp; (stmt.getUpdateCount() == -1))
     *  </code></pre>
     *
     *  @param  current One of the following {@code Statement} constants
     *      indicating what should happen to current
     *      {@link java.sql.ResultSet}
     *      instances obtained using the method
     *      {@link #getResultSet()}:
     *      {@link java.sql.Statement#CLOSE_CURRENT_RESULT},
     *      {@link java.sql.Statement#KEEP_CURRENT_RESULT},
     *      or
     *      {@link java.sql.Statement#CLOSE_ALL_RESULTS}.
     *  @return {@code true} if the next result is a {@code ResultSet}
     *      instance, {@code false} if it is an update count or there are no
     *      more results
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      argument supplied is not one of the following:
     *      {@link java.sql.Statement#CLOSE_CURRENT_RESULT},
     *      {@link java.sql.Statement#KEEP_CURRENT_RESULT},
     *      or
     *      {@link java.sql.Statement#CLOSE_ALL_RESULTS}.
     *  @throws SQLFeatureNotSupportedException
     *      {@link java.sql.DatabaseMetaData#supportsMultipleOpenResults()}
     *      returned {@code false} and either
     *      {@link java.sql.Statement#KEEP_CURRENT_RESULT}
     *      or
     *      {@link java.sql.Statement#CLOSE_ALL_RESULTS}
     *      are supplied as the argument.
     *
     *  @see    #execute()
     *  @see    #getMoreResults()
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    public boolean getMoreResults( final int current ) throws SQLException;

    /**
     *  Retrieves the number, types and properties of this
     *  {@code EnhancedPreparedStatement}
     *  instance's parameters.
     *
     *  @return An instance of {@code ParameterMetaData} that contains
     *      information about the number, types and properties for each named
     *      parameter of this {@code EnhancedPreparedStatement} instance.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see java.sql.ParameterMetaData
     *  @see org.tquadrat.foundation.sql.ParameterMetaData
     */
    public ParameterMetaData getParameterMetaData() throws SQLException;

    /**
     *  <p>{@summary Retrieves the number of seconds the driver will wait for a
     *  {@link java.sql.Statement}
     *  instance to execute.} If the limit is exceeded, a
     *  {@link SQLTimeoutException}
     *  is thrown.</p>
     *
     *  @return The current query timeout limit in seconds; zero means there is
     *      no limit.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #setQueryTimeout(int)
     */
    public int getQueryTimeout() throws SQLException;

    /**
     *  <p>{@summary Retrieves the current result as a
     *  {@link ResultSet}
     *  instance.}</p>
     *
     *  @note   This method should be called only once per result.
     *
     *  @return The current result as a {@code ResultSet} instance or
     *      {@code null} if the result is an update count or there are no more
     *      results.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #execute()
     */
    public ResultSet getResultSet() throws SQLException;

    /**
     *  Retrieves the result set concurrency for
     *  {@link java.sql.ResultSet}
     *  instances generated by this {@code EnhancedPreparedStatement}
     *  instance.
     *
     *  @return Either
     *      {@link java.sql.ResultSet#CONCUR_READ_ONLY}
     *      or
     *      {@link java.sql.ResultSet#CONCUR_UPDATABLE}.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     */
    public int getResultSetConcurrency() throws SQLException;

    /**
     *  Retrieves the result set holdability for
     *  {@link java.sql.ResultSet}
     *  instances generated by this {@code EnhancedPreparedStatement} instance.
     *
     *  @return Either
     *      {@link java.sql.ResultSet#HOLD_CURSORS_OVER_COMMIT}
     *      ({@value java.sql.ResultSet#HOLD_CURSORS_OVER_COMMIT})
     *      or
     *      {@link java.sql.ResultSet#CLOSE_CURSORS_AT_COMMIT}
     *      ({@value java.sql.ResultSet#CLOSE_CURSORS_AT_COMMIT}).
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     */
    public int getResultSetHoldability() throws SQLException;

    /**
     *  Retrieves the result set type for
     *  {@link java.sql.ResultSet}
     *  instances generated by this {@code EnhancedPreparedStatement}
     *  instance.
     *
     *  @return One of
     *      {@link java.sql.ResultSet#TYPE_FORWARD_ONLY},
     *      {@link java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE},
     *      or
     *      {@link java.sql.ResultSet#TYPE_SCROLL_SENSITIVE}.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     */
    public int getResultSetType()  throws SQLException;

    /**
     *  <p>{@summary Retrieves the current result as an update count.} If the
     *  result is a
     *  {@link ResultSet}
     *  object or there are no more results, -1 is returned.</p>
     *
     *  @note   This method should be called only once per result.
     *
     *  @return The current result as an update count; -1 if the current result
     *      is a {@code ResultSet} object or there are no more results.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #execute()
     */
    public int getUpdateCount() throws SQLException;

    /**
     *  <p>{@summary Retrieves the first warning reported by calls on this
     *  {@code EnhancedPreparedStatement} instance.} Subsequent warnings
     *  regarding the execution of this instance will be chained to this
     *  {@link java.sql.SQLWarning}
     *  instance</p>.
     *  <p>The warning chain is automatically cleared each time a statement is
     *  (re)executed. This method may not be called on a closed
     *  {@code EnhancedPreparedStatement} instance; doing so will cause an
     *  {@link SQLException}
     *  to be thrown.</p>
     *
     *  @note   If you are processing a {@code ResultSet} instance, any
     *      warnings associated with reads on that {@code ResultSet} instance
     *      will be chained on it rather than on the statement instance that
     *      produced it.
     *
     *  @return The first {@code SQLWarning} object or {@code null} if there
     *      are no warnings.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *
     *  @see    #clearWarnings()
     */
    public SQLWarning getWarnings() throws SQLException;

    /**
     *  <p>{@summary Retrieves whether this {@code EnhancedPreparedStatement}
     *  object has been closed.} An {@code EnhancedPreparedStatement} is closed
     *  if the method
     *  {@link #close()} has been called on it, or if it is automatically
     *  closed.</p>
     *
     *  @return {@code true} if this {@code EnhancedPreparedStatement} object
     *      is closed; {@code false} if it is still open.
     *
     *  @throws SQLException    A database access error occurred.
     */
    public boolean isClosed() throws SQLException;

    /**
     *  <p>{@summary Returns a value indicating whether this
     *  {@code EnhancedPreparedStatement} will be closed when all its dependent
     *  result sets are closed.}</p>
     *
     *  @return {@code true} if this {@code EnhancedPreparedStatement} will be
     *      closed when all of its dependent result sets are closed,
     *      {@code false} otherwise.
     *
     *  @throws SQLException    This method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public boolean isCloseOnCompletion() throws SQLException;

    /**
     *  Checks whether logging is currently enabled.
     *
     *  @return {@code true} if logging is enabled and log information have to
     *      be collected, {@code false} otherwise.
     */
    public boolean isLoggingEnabled();

    /**
     *  Returns a  value indicating whether the wrapped
     *  {@link java.sql.PreparedStatement}
     *  is poolable or not.
     *
     *  @return {@code true} if the {@code PreparedStatement} is poolable,
     *      {@code false} otherwise
     *  @throws SQLException    This method was called on a closed
     *      {@code EnhancedPreparedStatement}
     *
     *  @see java.sql.Statement#setPoolable(boolean) setPoolable(boolean)
     */
    public boolean isPoolable() throws SQLException;

    /**
     *  Retrieves whether {@code identifier} is a simple SQL identifier.
     *
     *  @param  identifier  An SQL identifier
     *  @return {@code true} if the given value is a simple SQL identifier,
     *      {@code false} otherwise
     *  @throws SQLException    A database access error occurred.
     */
    public boolean isSimpleIdentifier( final String identifier) throws SQLException;

    /**
     *  <p>{@summary Creates a new {@code EnhancedPreparedStatement}
     *  instance.}</p>
     *  <p>The text for the SQL statement is different from the format that
     *  is used by
     *  {@link java.sql.Connection#prepareStatement(String)}
     *  to create an instance of
     *  {@link java.sql.PreparedStatement}:
     *  this format uses explicit names instead of simple question marks as
     *  placeholders. This means, instead of</p>
     *  <pre><code>SELECT value FROM table WHERE key = ?</code></pre>
     *  <p>one will write</p>
     *  <pre><code>SELECT value FROM table WHERE key = :key</code></pre>
     *  <p>This allows to address the variables by name, and it makes it
     *  possible that the same value is only provided once, despite it occurs
     *  multiple time.</p>
     *  <p>A parameter name is prefixed by a colon, followed by an ASCII
     *  letter, then by an arbitrary number of ASCII letters and digits. No
     *  special characters are allowed.</p>
     *
     *  @param  connection  The connection to the database.
     *  @param  sql The text of the SQL statement with the placeholders.
     *  @return The new statement.
     *  @throws SQLException    A database access error occurred, or the given
     *      statement could not be parsed properly.
     */
    @API( status = STABLE, since = "0.1.0" )
    public static EnhancedPreparedStatement prepareStatement( final Connection connection, final String sql ) throws SQLException
    {
        final EnhancedPreparedStatement retValue = EnhancedPreparedStatementImpl.create( connection, sql );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  prepareStatement()

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.Array}
     *  instance.}</p>
     *  <p>The driver converts this to an SQL {@code ARRAY} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method
     */
    public void setArray ( final String parameterName, final Array value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.io.InputStream}
     *  which will have the specified number of bytes.}</p>
     *  <p>When a very large ASCII value is input to a {@code LONGVARCHAR}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.InputStream}. The data will be read from the stream as
     *  needed until end-of-file is reached. The JDBC driver will do any
     *  necessary conversion from ASCII to the database char format.</p>
     *
     *  @note   This stream object can either be a standard Java stream object
     *      or your own subclass that implements the standard interface.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @param  length  The number of bytes in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setAsciiStream( final String parameterName, final InputStream value, final int length ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.io.InputStream}
     *  which will have the specified number of bytes.}</p>
     *  <p>When a very large ASCII value is input to a {@code LONGVARCHAR}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.InputStream}. The data will be read from the stream as
     *  needed until end-of-file is reached. The JDBC driver will do any
     *  necessary conversion from ASCII to the database char format.</p>
     *
     *  @note   This stream object can either be a standard Java stream object
     *      or your own subclass that implements the standard interface.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @param  length  The number of bytes in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setAsciiStream( final String parameterName, final InputStream value, final long length ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.io.InputStream}.}</p>
     *  <p>When a very large ASCII value is input to a {@code LONGVARCHAR}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.InputStream}. The data will be read from the stream as
     *  needed until end-of-file is reached. The JDBC driver will do any
     *  necessary conversion from ASCII to the database char format.</p>
     *
     *  @note   This stream object can either be a standard Java stream object
     *      or your own subclass that implements the standard interface.
     *  @note   Consult your JDBC driver documentation to determine if it might
     *      be more efficient to use a version of {@code setAsciiStream()}
     *      which takes a length parameter.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setAsciiStream( final String parameterName, final InputStream value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.io.InputStream}
     *  which will have the specified number of bytes.}</p>
     *  <p>When a very large binary value is input to a {@code LONGVARBINARY}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.InputStream}. The data will be read from the stream as
     *  needed until end-of-file is reached.</p>
     *
     *  @note   This stream object can either be a standard Java stream object
     *      or your own subclass that implements the standard interface.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @param  length  The number of bytes in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setBinaryStream( final String parameterName, final InputStream value, final int length) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.io.InputStream}
     *  which will have the specified number of bytes.}</p>
     *  <p>When a very large binary value is input to a {@code LONGVARBINARY}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.InputStream}. The data will be read from the stream as
     *  needed until end-of-file is reached.</p>
     *
     *  @note   This stream object can either be a standard Java stream object
     *      or your own subclass that implements the standard interface.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @param  length  The number of bytes in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setBinaryStream( final String parameterName, final InputStream value, final long length ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.io.InputStream}.}</p>
     *  <p>When a very large binary value is input to a {@code LONGVARBINARY}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.InputStream}. The data will be read from the stream as
     *  needed until end-of-file is reached.</p>
     *
     *  @note   This stream object can either be a standard Java stream object
     *      or your own subclass that implements the standard interface.
     *  @note   Consult your JDBC driver documentation to determine if it might
     *      be more efficient to use a version of {@code setBinaryStream()}
     *      which takes a length parameter.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setBinaryStream( final String parameterName, final InputStream value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@link BigDecimal}
     *  value.}</p>
     *  <p>The driver converts this to an SQL {@code NUMERIC} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    void setBigDecimal( final String parameterName, final BigDecimal value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@link java.sql.Blob}
     *  instance.}</p>
     *  <p>The driver converts this to an SQL {@code BLOB} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setBlob ( final String parameterName, final Blob value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to a
     *  {@link java.io.InputStream}
     *  object.} The {@code InputStream} must contain  the number of characters
     *  as specified by the {@code length} argument, otherwise a
     *  {@link SQLException}
     *  will be generated when the {@code EnhancedPreparedStatement} is
     *  executed.</p>
     *  <p>This method differs from the
     *  {@link #setBinaryStream (String,InputStream,int)}
     *  method because it informs the driver that the parameter value should be
     *  sent to the server as a {@code BLOB}. When the
     *  {@code setBinaryStream()} method is used, the driver may have to do
     *  extra work to determine whether the parameter data should be sent to
     *  the server as a {@code LONGVARBINARY} or a {@code BLOB}.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code InputStream} instance that contains the data
     *      to set the parameter value to.
     *  @param  length  The number of bytes in the parameter data.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred, this method was called on a closed
     *      {@code EnhancedPreparedStatement}, the specified length was less
     *      than zero or if the number of bytes in the {@code InputStream} did
     *      not match the specified length.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setBlob(  final String parameterName, final InputStream value, final long length ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to a
     *  {@link java.io.InputStream}
     *  object.}</p>
     *  <p>This method differs from the
     *  {@link #setBinaryStream (String,InputStream,int)}
     *  method because it informs the driver that the parameter value should be
     *  sent to the server as a {@code BLOB}. When the
     *  {@code setBinaryStream()} method is used, the driver may have to do
     *  extra work to determine whether the parameter data should be sent to
     *  the server as a {@code LONGVARBINARY} or a {@code BLOB}.</p>
     *
     *  @note   Consult your JDBC driver documentation to determine if it might
     *      be more efficient to use a version of {@code setBlob()} which takes
     *      a length parameter.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code InputStream} instance that contains the data
     *      to set the parameter value to.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred, or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setBlob( final String parameterName, final InputStream value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@code boolean} value.}</p>
     *  <p>The driver converts this to an SQL {@code BIT} or {@code BOOLEAN}
     *  value when it sends it to the database.</p>
     *  <p>For the logging, it is always {@code BOOLEAN}.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setBoolean( final String parameterName, final boolean value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@code byte} value.}</p>
     *  <p>The driver converts this to an SQL {@code TINYINT} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setByte( final String parameterName, final byte value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  array of bytes.}</p>
     *  <p>The driver converts this to an SQL {@code VARBINARY} or
     *  {@code LONGVARBINARY} (depending on the argument's size relative to the
     *  driver's limits on {@code VARBINARY} values) when it sends it to the
     *  database.</p>
     *  <p>For the logging, it is always {@code VARBINARY}.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setBytes( final String parameterName, final byte [] value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link Reader}
     *  instance, which provides the given number of characters.}</p>
     *  <p>When a very large UNICODE value is input to a {@code LONGVARCHAR}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.Reader} instance. The data will be read from the stream
     *  as needed until end-of-file is reached. The JDBC driver will do any
     *  necessary conversion from UNICODE to the database char format.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      Unicode data.
     *  @param  length The number of characters in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setCharacterStream( final String parameterName, final Reader value, final int length ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link Reader}
     *  instance, which provides the given number of characters.}</p>
     *  <p>When a very large UNICODE value is input to a {@code LONGVARCHAR}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.Reader} instance. The data will be read from the stream
     *  as needed until end-of-file is reached. The JDBC driver will do any
     *  necessary conversion from UNICODE to the database char format.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      Unicode data.
     *  @param  length The number of characters in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setCharacterStream( final String parameterName, final Reader value, final long length ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link Reader}
     *  instance.}</p>
     *  <p>When a very large UNICODE value is input to a {@code LONGVARCHAR}
     *  parameter, it may be more practical to send it via a
     *  {@code java.io.Reader} instance. The data will be read from the stream
     *  as needed until end-of-file is reached. The JDBC driver will do any
     *  necessary conversion from UNICODE to the database char format.</p>
     *
     *  @note   This reader object can either be a standard Java {@code Reader}
     *      object or your own subclass that implements the standard interface.
     *  @note   Consult your JDBC driver documentation to determine if it might
     *      be more efficient to use a version of {@code setCharacterStream()}
     *      which takes a length parameter.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      Unicode data.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setCharacterStream( final String parameterName, final Reader value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.Clob}
     *  instance.}</p>
     *  <p>The driver converts this to an SQL {@code CLOB} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method
     */
    public void setClob( final String parameterName, final Clob value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to a
     *  {@link java.io.Reader}
     *  instance.} The reader must contain the number of characters specified
     *  by the {@code length} argument, otherwise a
     *  {@link SQLException}
     *  will be generated when the {@code EnhancedPreparedStatement} is
     *  executed.</p>
     *  <p>This method differs from the
     *  {@link #setCharacterStream(String,Reader,int)}
     *  method because it informs the driver that the parameter value should be
     *  sent to the server as a {@code CLOB}. When the
     *  {@code setCharacterStream()} method is used, the driver may have to do
     *  extra work to determine whether the parameter data should be sent to
     *  the server as a {@code LONGVARCHAR} or a {@code CLOB}.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      data to set the parameter value to.
     *  @param  length The number of characters in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}, the {@code Reader} was shorter
     *      than specified, or the specified length was less than zero.
     *  @throws SQLFeatureNotSupportedException  The JDBC driver does not
     *      support this method.
     */
    public void setClob( final String parameterName, final Reader value, final long length ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to a
     *  {@link java.io.Reader}
     *  instance.}</p>
     *  <p>This method differs from the
     *  {@link #setCharacterStream(String,Reader,int)}
     *  method because it informs the driver that the parameter value should be
     *  sent to the server as a {@code CLOB}. When the
     *  {@code setCharacterStream()} method is used, the driver may have to do
     *  extra work to determine whether the parameter data should be sent to
     *  the server as a {@code LONGVARCHAR} or a {@code CLOB}.</p>
     *
     *  @note   Consult your JDBC driver documentation to determine if it might
     *      be more efficient to use a version of {@code setClob()} which takes
     *      a length parameter.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      data to set the parameter value to.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException  The JDBC driver does not
     *      support this method.
     */
    public void setClob( final String parameterName, final Reader value ) throws SQLException;

    /**
     *  <p>{@summary Sets the SQL cursor name to the given String, which will
     *  be used by subsequent statement {@code execute()} methods.} This name
     *  can then be used in SQL positioned update or delete statements to
     *  identify the current row in the
     *  {@link java.sql.ResultSet}
     *  instance generated by this statement.  If the database does not support
     *  positioned update/delete, this method is a noop.  To ensure that a
     *  cursor has the proper isolation level to support updates, the cursor's
     *  {@code SELECT} statement should have the form
     *  {@code SELECT FOR UPDATE}. If {@code FOR UPDATE} is not present,
     *  positioned updates may fail.</p>
     *
     *  @note   By definition, the execution of positioned updates and deletes
     *  must be done by a different statement instances than the one that
     *  generated the {@code ResultSet} object being used for positioning.
     *  Also, cursor names must be unique within a connection.
     *
     *  @param  name    The new cursor name, which must be unique within a
     *      connection.
     *  @throws SQLException    A database access error occurred or this method
     *      was called on a closed {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setCursorName( final String name ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.Date}
     *  value using the default time zone of the virtual machine that is
     *  running the application.}</p>
     *  <p>The driver converts this to an SQL {@code DATE} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setDate( final String parameterName, final Date value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.Date}
     *  value using the given
     *  {@link java.util.Calendar}
     *  instance.} The driver used the {@code Calendar} instance to construct
     *  an SQL {@code DATE} value, which the driver then sends to the database.
     *  With the {@code Calendar} instance, the driver can calculate the date
     *  taking into account a custom timezone. If no {@code Calendar} instance
     *  is specified, the driver uses the default timezone, which is that of
     *  the virtual machine running the application.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @param  calendar    The {@code Calendar} instance the driver will use
     *      to construct the date.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    @SuppressWarnings( "UseOfObsoleteDateTimeApi" )
    public void setDate( final String parameterName, final Date value, final Calendar calendar ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@code double} value.}</p>
     *  <p>The driver converts this to an SQL {@code DOUBLE} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setDouble( final String parameterName, final double value ) throws SQLException;

    /**
     *  <p>{@summary Gives the driver a hint as to the direction in which rows
     *  will be processed in
     *  {@link java.sql.ResultSet}
     *  instances created using this {@code EnhancedPreparedStatement} object.}
     *  The default value is
     *  {@link java.sql.ResultSet#FETCH_FORWARD}.</p>
     *  <p>Note that this method sets only the default fetch direction for
     *  result sets generated by this {@code EnhancedPreparedStatement}
     *  instance. Each result set has its own methods for getting and setting
     *  its own fetch direction.</p>
     *
     *  @param  direction   The initial direction for processing rows.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      given direction is not one of
     *      {@link java.sql.ResultSet#FETCH_FORWARD},
     *      {@link java.sql.ResultSet#FETCH_REVERSE},
     *      or
     *      {@link java.sql.ResultSet#FETCH_UNKNOWN}
     *
     *  @see    #getFetchDirection()
     *  @see    java.sql.ResultSet#setFetchDirection(int)
     *  @see    java.sql.ResultSet#getFetchDirection()
     */
    public void setFetchDirection( final int direction ) throws SQLException;

    /**
     *  <p>{@summary Gives the JDBC driver a hint as to the number of rows that
     *  should be fetched from the database when more rows are needed for
     *  {@link java.sql.ResultSet}
     *  instances generated by this {@code EnhancedPreparedStatement}
     *  instance.}
     *  If the specified value is zero, then the hint is ignored.</p>
     *  <p>The default value is zero.</p>
     *
     *  @param  rows    The number of rows to fetch.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      condition {@code rows >= 0} was not satisfied.
     */
    public void setFetchSize( final int rows ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@code float} value.}</p>
     *  <p>The driver converts this to an SQL {@code REAL} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setFloat( final String parameterName, final float value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@code int} value.}</p>
     *  <p>The driver converts this to an SQL {@code INTEGER} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setInt( final String parameterName, final int value ) throws SQLException;

    /**
     *  <p>{@summary Sets the limit for the maximum number of rows that any
     *  {@link ResultSet}
     *  instance generated by this {@code EnhancedPreparedStatement} instance
     *  can contain.} If the limit is exceeded, the excess rows are silently
     *  dropped.</p>
     *  <p>This method should be used when the row limit may exceed
     *  {@link Integer#MAX_VALUE}.</p>
     *  <p>The default implementation will throw
     *  {@link UnsupportedOperationException}.</p>
     *
     *  @param  max The new max rows limit; zero means there is no limit.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      condition {@code max >= 0} is not satisfied.
     *
     *  @see    #setMaxRows(int)
     *  @see    #getMaxRows()
     *  @see    #getLargeMaxRows()
     */
    public void setLargeMaxRows( final long max) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@code long} value.}</p>
     *  <p>The driver converts this to an SQL {@code BIGINT} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setLong( final String parameterName, final long value ) throws SQLException;

    /**
     *  <p>{@summary Sets the limit for the maximum number of bytes that can be
     *  returned for character and binary column values in a {@code ResultSet}
     *  object produced by this {@code Statement} object.}</p>
     *  <p>This limit applies only to
     *  {@link java.sql.JDBCType#BINARY},
     *  {@link java.sql.JDBCType#VARBINARY},
     *  {@link java.sql.JDBCType#LONGVARBINARY},
     *  {@link java.sql.JDBCType#CHAR},
     *  {@link java.sql.JDBCType#VARCHAR},
     *  {@link java.sql.JDBCType#NCHAR},
     *  {@link java.sql.JDBCType#NVARCHAR},
     *  {@link java.sql.JDBCType#LONGNVARCHAR}
     *  and
     *  {@link java.sql.JDBCType#LONGVARCHAR}
     *  columns. If the limit is exceeded, the excess data is silently
     *  discarded. For maximum portability, use values greater than 256.</p>
     *
     *  @param  max The new column size limit in bytes; zero means there is no
     *      limit.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      condition {@code max >= 0} is not satisfied.
     *
     *  @see    #getMaxFieldSize()
     */
    public void setMaxFieldSize( final int max ) throws SQLException;

    /**
     *  <p>{@summary Sets the limit for the maximum number of rows, that any
     *  {@link ResultSet}
     *  instance generated by this {@code EnhancedPreparedStatement} instance
     *  can contain, to the given number.} If the limit is exceeded, the excess
     *  rows are silently dropped.</p>
     *
     *  @param  max The new max rows limit; zero means there is no limit.
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      condition {@code max >= 0} was not satisfied.
     *
     *  @see    #getMaxRows()
     *  @see    #getLargeMaxRows()
     *  @see    #setLargeMaxRows(long)
     */
    public void setMaxRows( final int max ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link Reader}
     *  instance.}</p>
     *  <p>The {@code Reader} reads the data till end-of-file is reached. The
     *  driver does the necessary conversion from Java character format to the
     *  national character set in the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      data.
     *  @param  length The number of characters in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, the driver does not support
     *      national character sets, the driver detected that a data conversion
     *      error could occur, a database access error occurred or this method
     *      was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method,
     */
    void setNCharacterStream(  final String parameterName, final Reader value, long length) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link Reader}
     *  instance.}</p>
     *  <p>The {@code Reader} reads the data till end-of-file is reached. The
     *  driver does the necessary conversion from Java character format to the
     *  national character set in the database.</p>
     *
     *  @note   This reader object can either be a standard Java {@code Reader}
     *      object or your own subclass that implements the standard interface.
     *  @note   Consult your JDBC driver documentation to determine if it might
     *      be more efficient to use a version of {@code setNCharacterStream()}
     *      which takes a length parameter.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      data.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, the driver does not support
     *      national character sets, the driver detected that a data conversion
     *      error could occur, a database access error occurred or this method
     *      was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method,
     */
    public void setNCharacterStream( final String parameterName, final Reader value) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.NClob} value.}</p>
     *  <p>The driver converts this to an SQL {@code NCLOB} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, the driver does not support
     *      national character sets, the driver detected that a data conversion
     *      error could occur, a database access error occurred or this method
     *      was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method,
     */
    public void setNClob( final String parameterName, final NClob value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to a
     *  {@link java.io.Reader}
     *  instance.} The reader must contain the number of characters specified
     *  by the {@code length} argument, otherwise a
     *  {@link SQLException}
     *  will be generated when the {@code EnhancedPreparedStatement} is
     *  executed.</p>
     *  <p>This method differs from the
     *  {@link #setNCharacterStream(String,Reader,long)}
     *  method because it informs the driver that the parameter value should be
     *  sent to the server as a {@code NCLOB}. When the
     *  {@code setNCharacterStream()} method is used, the driver may have to do
     *  extra work to determine whether the parameter data should be sent to
     *  the server as a {@code LONGNVARCHAR} or a {@code NCLOB}.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      data to set the parameter value to.
     *  @param  length The number of characters in the stream.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}, the {@code Reader} was shorter
     *      than specified, or the specified length was less than zero.
     *  @throws SQLFeatureNotSupportedException  The JDBC driver does not
     *      support this method.
     */
    public void setNClob( final String parameterName, final Reader value, final long length ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to a
     *  {@link java.io.Reader}
     *  instance.}</p>
     *  <p>This method differs from the
     *  {@link #setNCharacterStream(String,Reader,long)}
     *  method because it informs the driver that the parameter value should be
     *  sent to the server as a {@code NCLOB}. When the
     *  {@code setNCharacterStream()} method is used, the driver may have to do
     *  extra work to determine whether the parameter data should be sent to
     *  the server as a {@code LONGNVARCHAR} or a {@code NCLOB}.</p>
     *
     *  @note   Consult your JDBC driver documentation to determine if it might
     *      be more efficient to use a version of {@code setNClob()} which
     *      takes a length parameter.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The {@code java.io.Reader} object that contains the
     *      data to set the parameter value to.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException  The JDBC driver does not
     *      support this method.
     */
    public void setNClob( final String parameterName, final Reader value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@link String} value.}</p>
     *  <p>The driver converts this to an SQL {@code NCHAR}, {@code NVARCHAR},
     *  or {@code LONGNVARCHAR} value (depending on the argument's size
     *  relative to the driver's limits on {@code NVARCHAR} values)when it
     *  sends it to the database.</p>
     *  <p>For the logging, it is always {@code NCHAR}.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, the driver does not
     *      support national character sets, the driver detected that a data
     *      conversion error could occur, a database access error occurred or
     *      this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setNString( final String parameterName, final String value ) throws SQLException;

    /**
     *  Sets the designated parameter to SQL {@code NULL}.
     *
     *  @note   You must specify the parameter's SQL type.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  sqlType The SQL type code.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The {@code sqlType} is a
     *      {@link java.sql.JDBCType#ARRAY},
     *      {@link java.sql.JDBCType#BLOB},
     *      {@link java.sql.JDBCType#CLOB},
     *      {@link java.sql.JDBCType#DATALINK},
     *      {@link java.sql.JDBCType#JAVA_OBJECT},
     *      {@link java.sql.JDBCType#NCHAR},
     *      {@link java.sql.JDBCType#NCLOB},
     *      {@link java.sql.JDBCType#NVARCHAR},
     *      {@link java.sql.JDBCType#LONGNVARCHAR},
     *      {@link java.sql.JDBCType#REF},
     *      {@link java.sql.JDBCType#ROWID},
     *      {@link java.sql.JDBCType#SQLXML}
     *      or
     *      {@link java.sql.JDBCType#STRUCT}
     *      data type and the JDBC driver does not support this data type.
     *
     *  @see    java.sql.JDBCType
     */
    public void setNull( final String parameterName, final SQLType sqlType ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to SQL {@code NULL}.}</p>
     *  <p>This version of the method {@code setNull()} should be used for
     *  user-defined types and REF type parameters. Examples of user-defined
     *  types include: {@code STRUCT}, {@code DISTINCT}, {@code JAVA_OBJECT},
     *  and named array types.</p>
     *  <p>Although this method is intended for user-defined and {@code REF}
     *  parameters, this method may be used to set a {@code null} parameter of
     *  any JDBC type. If the parameter does not have a user-defined or
     *  {@code REF} type, the given {@code typeName} is ignored.</p>
     *
     *  @note   To be portable, applications must give the SQL type code and
     *      the fully-qualified SQL type name when specifying a {@code NULL}
     *      user-defined or {@code REF} parameter. In the case of a
     *      user-defined type the name is the type name of the parameter
     *      itself. For a {@code REF} parameter, the name is the type name of
     *      the referenced type. If a JDBC driver does not need the type code
     *      or type name information, it may ignore it.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  sqlType The SQL type code.
     *  @param typeName The fully-qualified name of an SQL user-defined type;
     *      ignored if the parameter is not a user-defined type or {@code REF}.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The {@code sqlType} is a
     *      {@link java.sql.JDBCType#ARRAY},
     *      {@link java.sql.JDBCType#BLOB},
     *      {@link java.sql.JDBCType#CLOB},
     *      {@link java.sql.JDBCType#DATALINK},
     *      {@link java.sql.JDBCType#JAVA_OBJECT},
     *      {@link java.sql.JDBCType#NCHAR},
     *      {@link java.sql.JDBCType#NCLOB},
     *      {@link java.sql.JDBCType#NVARCHAR},
     *      {@link java.sql.JDBCType#LONGNVARCHAR},
     *      {@link java.sql.JDBCType#REF},
     *      {@link java.sql.JDBCType#ROWID},
     *      {@link java.sql.JDBCType#SQLXML}
     *      or
     *      {@link java.sql.JDBCType#STRUCT}
     *      data type and the JDBC driver does not support this data type.
     */
    public void setNull ( final String parameterName, final SQLType sqlType, final String typeName ) throws SQLException;

    /**
     *  <p>{@summary Sets the value of the designated parameter using the given
     *  object.}</p>
     *  <p>The JDBC specification specifies a standard mapping from Java
     *  {@code Object} types to SQL types.  The given argument will be
     *  converted to the corresponding SQL type before being sent to the
     *  database.</p>
     *  <p>Note that this method may be used to pass database- specific
     *  abstract data types, by using a driver-specific Java type.</p>
     *  <p>If the object is of a class implementing the interface
     *  {@link java.sql.SQLData},
     *  the JDBC driver should call the method {@code SQLData.writeSQL} to
     *  write it to the SQL data stream.</p>
     *  <p>If, on the other hand, the object is of a class implementing
     *  {@link java.sql.Ref},
     *  {@link java.sql.Blob},
     *  {@link java.sql.Clob},
     *  {@link java.sql.NClob},
     *  {@link java.sql.Struct},
     *  {@link java.net.URL},
     *  {@link java.sql.RowId},
     *  {@link java.sql.SQLXML}
     *  or
     *  {@link java.sql.Array},
     *  the driver should pass it to the database as a value of the
     *  corresponding SQL type.</p>
     *
     *  @note   Not all databases allow for a non-typed {@code NULL} to be sent
     *  to the backend. For maximum portability, the {@code setNull()} or the
     *  {@code setObject( final String parameterName, final Object x, int sqlType )}
     *  method should be used instead of {@code setObject( final String parameterName, final Object value )}
     *  with {@code value} set to {@code null}
     *
     *  @note   This method throws an exception if there is an ambiguity, for
     *      example, if the object is of a class implementing more than one of
     *      the interfaces named above.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The object containing the input parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred, this method was called on a closed
     *      {@code EnhancedPreparedStatement} or the type of the given object
     *      is ambiguous.
     */
    public void setObject( final String parameterName, final Object value ) throws SQLException;

    /**
     *  <p>{@summary Sets the value of the designated parameter using the given
     *  object.}</p>
     *  <p>If the second argument is an
     *  {@link InputStream}
     *  or a
     *  {@link Reader}
     *  then the stream must contain the number of bytes specified by
     *  the {@code scaleOrLength} argument</p>.
     *  <p>If these conditions are not true the driver will generate a
     *  {@link SQLException}
     *  when the prepared statement is executed.</p>
     *  <p>The given Java object will be converted to the given
     *  {@code targetSqlType} before being sent to the database.</p>
     *  <p>If the object has a custom mapping (is of a class implementing the
     *  interface
     *  {@link java.sql.SQLData}),
     *  the JDBC driver should call the method
     *  {@link java.sql.SQLData#writeSQL(SQLOutput)}
     *  to write it to the SQL data stream.</p>
     *  <p>If, on the other hand, the object is of a class implementing
     *  {@link java.sql.Ref},
     *  {@link java.sql.Blob},
     *  {@link java.sql.Clob},
     *  {@link java.sql.NClob},
     *  {@link java.sql.Struct},
     *  {@link java.net.URL},
     *  or
     *  {@link java.sql.Array},
     *  the driver should pass it to the database as a value of the
     *  corresponding SQL type.</p>
     *  <p>Note that this method may be used to pass database-specific abstract
     *  data types.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The object containing the input parameter value.
     *  @param  targetSqlType   The SQL type to be sent to the database. The
     *      scale argument may further qualify this type.
     *  @param  scaleOrLength   For
     *      {@link java.sql.JDBCType#DECIMAL}
     *      or
     *      {@link java.sql.JDBCType#NUMERIC}
     *      types, this is the number of digits after the decimal point. For
     *      Java Object types
     *      {@link InputStream}
     *      and
     *      {@link Reader},
     *      this is the length of the data in the stream or reader. For all
     *      other types, this value will be ignored.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred, this method was called on a closed
     *      {@code EnhancedPreparedStatement} or the Java Object specified by
     *      {@code value} is an {@code InputStream} or {@code Reader} instance
     *      and the value of the scale parameter is less than zero.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support the specified {@code targetSqlType}.
     *
     *  @see    JDBCType
     *  @see    SQLType
     */
    public void setObject( final String parameterName, final Object value, SQLType targetSqlType, int scaleOrLength) throws SQLException;

    /**
     *  <p>{@summary Sets the value of the designated parameter with the given
     *  object.}</p>
     *  <p>This method is similar to
     *  {@link #setObject(String,Object,SQLType,int)},
     *  except that it assumes a scale of zero.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The object containing the input parameter value.
     *  @param  targetSqlType   The SQL type to be sent to the database. The
     *      scale argument may further qualify this type.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred, or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support the specified {@code targetSqlType}.
     *
     *  @see    JDBCType
     *  @see    SQLType
     */
    public void setObject( final String parameterName, final Object value, SQLType targetSqlType ) throws SQLException;

    /**
     *  <p>{@summary Sets the number of seconds the driver will wait for a
     *  {@link java.sql.Statement}
     *  instance to execute to the given number of seconds.} By default there
     *  is no limit on the amount of time allowed for a running statement to
     *  complete.</p>
     *  <p>If the limit is exceeded, an
     *  {@link SQLTimeoutException}
     *  is thrown.</p>
     *  <p>A JDBC driver must apply this limit to the
     *  {@link java.sql.Statement#execute(String)},
     *  {@link java.sql.Statement#executeQuery(String)}
     *  and
     *  {@link java.sql.Statement#executeUpdate(String)}
     *  methods.</p>
     *
     *  @note   JDBC driver implementations may also apply this limit to
     *      {@code ResultSet} methods (consult your driver vendor documentation
     *      for details).
     *
     *  @note   In the case of {@code Statement} batching, it is implementation
     *      defined whether the time-out is applied to individual SQL commands
     *      added via the {@code #addBatch()} method or to the entire batch of
     *      SQL commands invoked by the {@code executeBatch()} method (consult
     *      your driver vendor documentation for details).
     *
     *  @param  timeout The new query timeout limit in seconds; zero means
     *      there is no limit
     *  @throws SQLException    A database access error occurred, this method
     *      was called on a closed {@code EnhancedPreparedStatement} or the
     *      condition {@code seconds >= 0} is not satisfied
     *
     *  @see    #getQueryTimeout()
     */
    public void setQueryTimeout( final int timeout ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.JDBCType#REF REF(&lt;structured-type&gt;)}
     *  value.}</p>
     *  <p>The driver converts this to an SQL {@code REF} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setRef ( final String parameterName, final Ref value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.RowId}
     *  instance.}</p>
     *  <p>The driver converts this to an SQL {@code ROWID} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setRowId( final String parameterName, final RowId value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@code short} value.}</p>
     *  <p>The driver converts this to an SQL {@code SMALLINT} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setShort( final String parameterName, final short value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.SQLXML} value.}</p>
     *  <p>The driver converts this to an SQL {@code SQLXML} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   An {@code SQLXML} instance that maps an SQL
     *      {@code SQLXML} value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred, this method was called on a closed
     *      {@code EnhancedPreparedStatement}, or the
     *      {@link javax.xml.transform.Result},
     *      {@link java.io.Writer}
     *      or
     *      {@link java.io.OutputStream}
     *      was closed for the {@code SQLXML} object.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    void setSQLXML( final String parameterName, final SQLXML value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given Java
     *  {@link java.lang.String}
     *  value.}</p>
     *  <p>The driver converts this to an SQL {@code VARCHAR} or
     *  {@code LONGVARCHAR} value (depending on the argument's size relative to the
     *  driver's limits on {@code VARCHAR} values) when it sends it to the
     *  database.</p>
     *  <p>For the logging, it is always {@code VARCHAR}.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setString( final String parameterName, final String value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.Time}
     *  value.}</p>
     *  <p>The driver converts this to an SQL {@code TIME} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setTime( final String parameterName, final Time value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.Time}
     *  value using the given
     *  {@link java.util.Calendar}
     *  instance.} The driver used the {@code Calendar} instance to construct
     *  an SQL {@code TIME} value, which the driver then sends to the database.
     *  With the {@code Calendar} instance, the driver can calculate the date
     *  taking into account a custom timezone. If no {@code Calendar} instance
     *  is specified, the driver uses the default timezone, which is that of
     *  the virtual machine running the application.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @param  calendar    The {@code Calendar} instance the driver will use
     *      to construct the date.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    @SuppressWarnings( "UseOfObsoleteDateTimeApi" )
    public void setTime( final String parameterName, final Time value, final Calendar calendar ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.Timestamp}
     *  value.}</p>
     *  <p>The driver converts this to an SQL {@code TIMESTAMP} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    public void setTimestamp( final String parameterName, final Timestamp value ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.sql.Timestamp}
     *  value using the given
     *  {@link java.util.Calendar}
     *  instance.} The driver used the {@code Calendar} instance to construct
     *  an SQL {@code TIMESTAMP} value, which the driver then sends to the
     *  database. With the {@code Calendar} instance, the driver can calculate
     *  the timestamp taking into account a custom timezone. If no
     *  {@code Calendar} instance is specified, the driver uses the default
     *  timezone, which is that of the virtual machine running the
     *  application.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @param  calendar    The {@code Calendar} instance the driver will use
     *      to construct the date.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     */
    @SuppressWarnings( "UseOfObsoleteDateTimeApi" )
    public void setTimestamp( final String parameterName, final Timestamp value, final Calendar calendar ) throws SQLException;

    /**
     *  <p>{@summary Sets the designated parameter to the given
     *  {@link java.net.URL}
     *  value.}</p>
     *  <p>The driver converts this to an SQL {@code DATALINK} value when it
     *  sends it to the database.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @param  value   The parameter value.
     *  @throws SQLException    The parameter name did not correspond to any
     *      defined parameter in the SQL statement, a database access error
     *      occurred or this method was called on a closed
     *      {@code EnhancedPreparedStatement}.
     *  @throws SQLFeatureNotSupportedException The JDBC driver does not
     *      support this method.
     */
    public void setURL( final String parameterName, final URL value ) throws SQLException;

























































































































































































    //--------------------------JDBC 4.1 -----------------------------


    //--------------------------JDBC 4.2 -----------------------------
}
//  interface EnhancedPreparedStatement

/*
 *  End of File
 */