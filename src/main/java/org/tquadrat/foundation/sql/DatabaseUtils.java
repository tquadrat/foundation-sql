/*
 * ============================================================================
 *  Copyright © 2002-2022 by Thomas Thrien.
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

import static java.lang.Character.isWhitespace;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.StringUtils.isEmptyOrBlank;
import static org.tquadrat.foundation.util.StringUtils.splitString;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.sql.internal.ResultSetSpliterator;

/**
 *  <p>{@summary Several utilities for the work with databases that will be
 *  accessed through plain JDBC.}
 *
 *  @version $Id: DatabaseUtils.java 1030 2022-04-06 13:42:02Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: DatabaseUtils.java 1030 2022-04-06 13:42:02Z tquadrat $" )
@UtilityClass
@API( status = STABLE, since = "0.1.0" )
public final class DatabaseUtils
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  <p>{@summary Instances of this class are used to return the status of a call to
     *  {@link DatabaseUtils#execute(Connection,String...)}.}</p>
     *
     *  @param  command The failed command.
     *  @param  error   The exception that was thrown to indicate the failure.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: DatabaseUtils.java 1030 2022-04-06 13:42:02Z tquadrat $
     *  @since 0.0.1
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: DatabaseUtils.java 1030 2022-04-06 13:42:02Z tquadrat $" )
    @API( status = STABLE, since = "0.0.1" )
    public record ExecStatus( String command, SQLException error ) implements Serializable
    {
            /*------------------------*\
        ====** Static Initialisations **=======================================
            \*------------------------*/
        /**
         *  The serial version UID for objects of this class: {@value}.
         *
         *  @hidden
         */
        @Serial
        private static final long serialVersionUID = 1L;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code ExecStatus} instance.
         *
         *  @param  command The failed command.
         *  @param  error   The exception for the failure.
         */
        public ExecStatus( final String command, final SQLException error )
        {
            this.error = requireNonNullArgument( error, "error" );
            this.command = requireNotEmptyArgument( command, "command" );
        }   //  ExecStatus()
    }
    //  class ExecStatus

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  No instance allowed for this class.
     */
    private DatabaseUtils() { throw new PrivateConstructorForStaticClassCalledError( DatabaseUtils.class ); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary Checks whether the table, that is specified by its
     *  {@code catalog}, schema (through {@code schemaPattern}) and
     *  {@code tableNamePattern}, exists.}</p>
     *  <p>Where a pattern is allowed, the wildcards {@literal "%"} and
     *  {@literal "_"} can be used.</p>
     *  <p>This method works for any RDBMS.</p>
     *
     *  @param  connection  The connection to the database.
     *  @param  catalog A catalog name; it must match the catalog name as it is
     *      stored in the database. The empty String retrieves those tables
     *      without a catalog, and {@code null} means that the catalog name
     *      should not be used to narrow the search.
     *  @param  schemaPattern   A schema name pattern; it must match the schema
     *      name as it is stored in the database. The empty String retrieves
     *      those tables without a schema, and {@code null} means that the
     *      schema name should not be used to narrow the search.
     *  @param  tableNamePattern    A table name pattern; it must match the
     *      table name as it is stored in the database.
     *  @param  tableTypes  A list of table types, which must be from the list
     *      of table types returned from
     *      {@link DatabaseMetaData#getTableTypes()},
     *      to include. It can be omitted to return all types.
     *  @return {@code true} if the specified table exists, {@code false}
     *      otherwise.
     *  @throws SQLException    A database access error occurred.
     */
    public static final boolean checkIfTableExists( final Connection connection, final String catalog, final String schemaPattern, final String tableNamePattern, final String... tableTypes ) throws SQLException
    {
        final var metaData = connection.getMetaData();
        var retValue = false;
        final var effectiveTypes = nonNull( tableTypes ) && tableTypes.length == 0 ? null : tableTypes;
        try( final var resultSet = metaData.getTables( catalog, schemaPattern, tableNamePattern, effectiveTypes ) )
        {
            retValue = resultSet.next();
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  checkIfTableExists()

    /**
     *  <p>{@summary Executes the given list of commands on the given database
     *  connection.}</p>
     *  <p>The commands should be DDL or DML commands, not queries. If all
     *  commands were executed successfully, the method calls
     *  {@link Connection#commit() commit()}
     *  on the provided connection, otherwise a call to
     *  {@link Connection#rollback() rollback()}
     *  is issued. In case, the connection is configured for
     *  {@linkplain Connection#getAutoCommit() AutoCommit},
     *  neither call will be made.</p>
     *  <p>In case of an error, the return value is not
     *  {@linkplain Optional#empty() empty}.</p>
     *  <p>Empty commands and commands that will start with a hash
     *  (&quot;{@code #}&quot;) will be ignored; this allows to process
     *  script files without extensive reformatting.</p>
     *
     *  @param  connection  The connection to the database.
     *  @param  commands    A list of SQL (DDL or DML) commands that will be
     *      executed against the provided connection.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the execution status; will be.
     *      {@linkplain Optional#empty() empty}
     *      if all commands were successfully executed.
     *
     *  @see Connection#getAutoCommit()
     *  @see Connection#setAutoCommit(boolean)
     *  @see #parseSQLScript(CharSequence)
     */
    @API( status = STABLE, since = "0.0.1" )
    public static final Optional<ExecStatus> execute( final Connection connection, final String... commands )
    {
        final var retValue = execute( connection, List.of( requireNonNullArgument( commands, "commands" ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  execute()

    /**
     *  <p>{@summary Executes the given list of commands on the given database
     *  connection.}</p>
     *  <p>The commands should be DDL or DML commands, not queries. If all
     *  commands were executed successfully, the method calls
     *  {@link Connection#commit() commit()}
     *  on the provided connection, otherwise a call to
     *  {@link Connection#rollback() rollback()}
     *  is issued. In case, the connection is configured for
     *  {@linkplain Connection#getAutoCommit() AutoCommit},
     *  neither call will be made.</p>
     *  <p>In case of an error, the return value is not
     *  {@linkplain Optional#empty() empty}.</p>
     *  <p>Empty commands will be silently ignored.</p>
     *
     *  @param  connection  The connection to the database.
     *  @param  commands    A list of SQL (DDL or DML) commands that will be
     *      executed against the provided connection.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the execution status; will be.
     *      {@linkplain Optional#empty() empty}
     *      if all commands were successfully executed.
     *
     *  @see Connection#getAutoCommit()
     *  @see Connection#setAutoCommit(boolean)
     *  @see #parseSQLScript(CharSequence)
     */
    @API( status = STABLE, since = "0.1.0" )
    public static final Optional<ExecStatus> execute( final Connection connection, final List<String> commands )
    {
        Optional<ExecStatus> retValue = Optional.empty();
        var currentCommand = EMPTY_STRING;
        try( final var statement = requireNonNullArgument( connection, "connection" ).createStatement() )
        {
            ExecLoop: for( final var command : requireNonNullArgument( commands, "commands" ) )
            {
                if( isEmptyOrBlank( command ) ) continue ExecLoop;
                currentCommand = command;
                statement.execute( command );
            }   //  ExecLoop:

            if( !connection.getAutoCommit() )
            {
                currentCommand = "commit;";
                connection.commit();
            }
        }
        catch( final SQLException e )
        {
            try
            {
                if( !connection.getAutoCommit() ) { connection.rollback(); }
            }
            catch( final SQLException eOnRollback )
            {
                e.addSuppressed( eOnRollback );
            }
            retValue = Optional.of( new ExecStatus( currentCommand, e ) );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  execute()

    /**
     *  <p>{@summary Parses the given SQL script.}</p>
     *  <p>Basically, the method splits the provided String into the single
     *  commands (they are separated by semicolon &quot;;&quot;), and returns
     *  them as a
     *  {@link List}
     *  that can be used with
     *  {@link #execute(Connection, List)}.</p>
     *  <p>Text between &quot;--&quot; (two hyphens, &amp;#x002D) and the end
     *  of the line will be seen as a comment and is ignored; same for comments
     *  between &quot;&#x002F;*&quot; and &quot;*&#x002F;&quot;.
     *
     *  @param  script  The script.
     *  @return The separated commands.
     */
    @SuppressWarnings( {"OverlyLongMethod", "OverlyComplexMethod"} )
    @API( status = STABLE, since = "0.0.1" )
    public static final List<String> parseSQLScript( final CharSequence script )
    {
        //---* Get the single commands *---------------------------------------
        final var chars = requireNonNullArgument( script, "script" ).toString().toCharArray();
        final var sourceLen = chars.length;
        final var buffer = new char [sourceLen];
        var targetLen = 0;
        var isSingleQuoteString = false;
        var isDoubleQuoteString = false;
        var isBlockComment = false;
        var isLineComment = false;
        var lastChar = ' ';

        ScanLoop: for( final var currentChar : chars )
        {
            if( (targetLen == 0) && isWhitespace( currentChar ) ) continue ScanLoop;

            AnalyzeSwitch:
            //noinspection EnhancedSwitchMigration
            switch( currentChar )
            {
                case '-':
                {
                    if( isBlockComment || isLineComment ) break AnalyzeSwitch;
                    if( isSingleQuoteString || isDoubleQuoteString )
                    {
                        buffer [targetLen++] = lastChar;
                        break AnalyzeSwitch;
                    }
                    if( lastChar == currentChar )
                    {
                        isLineComment = true;
                        lastChar = ' ';
                        continue ScanLoop;
                    }
                    buffer [targetLen++] = lastChar;
                    break AnalyzeSwitch;
                }

                case '\n':
                {
                    if( isBlockComment ) continue ScanLoop;
                    if( isLineComment )
                    {
                        isLineComment = false;
                        lastChar = ' ';
                        continue ScanLoop;
                    }
                    if( isSingleQuoteString || isDoubleQuoteString )
                    {
                        buffer [targetLen++] = lastChar;
                        lastChar = ' ';
                        continue ScanLoop;
                    }
                    if( isWhitespace( lastChar ) )
                    {
                        continue ScanLoop;
                    }
                    if( lastChar == ';' )
                    {
                        buffer [targetLen++] = ';';
                        break AnalyzeSwitch;
                    }
                    buffer [targetLen++] = lastChar;
                    lastChar = ' ';
                    continue ScanLoop;
                }

                case '\'':
                {
                    if( isBlockComment || isLineComment ) break AnalyzeSwitch;
                    if( !isDoubleQuoteString )
                    {
                        if( !isSingleQuoteString )
                        {
                            isSingleQuoteString = true;
                        }
                        else if( lastChar != '\\' )
                        {
                            isSingleQuoteString = false;
                        }
                    }
                    buffer [targetLen++] = lastChar;
                    break AnalyzeSwitch;
                }

                case '"':
                {
                    if( isBlockComment || isLineComment ) break AnalyzeSwitch;
                    if( !isSingleQuoteString )
                    {
                        if( !isDoubleQuoteString )
                        {
                            isDoubleQuoteString = true;
                        }
                        else if( lastChar != '\\' )
                        {
                            isDoubleQuoteString = false;
                        }
                    }
                    buffer [targetLen++] = lastChar;
                    break AnalyzeSwitch;
                }

                case '/':
                {
                    if( isBlockComment )
                    {
                        if( lastChar == '*' )
                        {
                            isBlockComment = false;
                            lastChar = ' ';
                            continue ScanLoop;
                        }
                        break AnalyzeSwitch;
                    }
                    if( isLineComment ) break AnalyzeSwitch;
                    buffer [targetLen++] = lastChar;
                    break AnalyzeSwitch;
                }

                case '*':
                {
                    if( isBlockComment || isLineComment ) break AnalyzeSwitch;
                    if( isSingleQuoteString || isDoubleQuoteString )
                    {
                        buffer [targetLen++] = lastChar;
                        break AnalyzeSwitch;
                    }
                    if( lastChar == '/' )
                    {
                        isBlockComment = true;
                        lastChar = ' ';
                        continue ScanLoop;
                    }
                    buffer [targetLen++] = lastChar;
                    break AnalyzeSwitch;
                }

                default:
                {
                    if( isBlockComment || isLineComment ) break AnalyzeSwitch;
                    if( isWhitespace( currentChar ) )
                    {
                        if( !isWhitespace( lastChar ) )
                        {
                            buffer [targetLen++] = lastChar;
                        }
                        break AnalyzeSwitch;
                    }
                    if( isSingleQuoteString || isDoubleQuoteString )
                    {
                        buffer [targetLen++] = lastChar;
                        break AnalyzeSwitch;
                    }
                    if( currentChar == ';' )
                    {
                        buffer [targetLen++] = lastChar;
                        buffer [targetLen++] = ';';
                        lastChar = '\n';
                        continue ScanLoop;
                    }
                    buffer [targetLen++] = lastChar;
                    break AnalyzeSwitch;
                }
            }   //  AnalyzeSwitch:

            lastChar = currentChar;
        }   //  ScanLoop:

        //---* Compose the return value *--------------------------------------
        final List<String> retValue = targetLen > 1 ? List.of( splitString( new String( buffer, 1, targetLen ).trim(), '\n' ) ) : List.of();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //   parseSQLScript()

    /**
     *  <p>{@summary Returns a
     *  {@link Stream}
     *  implementation for the given
     *  {@link ResultSet}.}</p>
     *  <p>Several operations on a {@code ResultSet} instance will not work
     *  when called on the streamed instance.</p>
     *  <p>When one of the operations that would move the cursor is called on
     *  the {@code ResultSet} instance that is pushed into an operation on the
     *  stream, an
     *  {@link UnsupportedOperationException}
     *  is thrown; this affects the methods</p>
     *  <ul>
     *      <li>{@link ResultSet#absolute(int)}</li>
     *      <li>{@link ResultSet#afterLast()}</li>
     *      <li>{@link ResultSet#beforeFirst()}</li>
     *      <li>{@link ResultSet#first()}</li>
     *      <li>{@link ResultSet#last()}</li>
     *      <li>{@link ResultSet#moveToCurrentRow()}</li>
     *      <li>{@link ResultSet#moveToInsertRow()}</li>
     *      <li>{@link ResultSet#next()}</li>
     *      <li>{@link ResultSet#previous()}</li>
     *      <li>{@link ResultSet#relative(int)}</li>
     *  </ul>
     *  <p>as well as the following methods:</p>
     *  <ul>
     *      <li>{@link ResultSet#close()}</li>
     *      <li>{@link ResultSet#deleteRow()}</li>
     *      <li>{@link ResultSet#insertRow()}</li>
     *      <li>{@link ResultSet#setFetchDirection(int)}</li>
     *      <li>{@link ResultSet#setFetchSize(int)}</li>
     *  </ul>
     *  <p>A call to
     *  {@link Stream#close()}
     *  does not close the
     *  {@link ResultSet}!</p>
     *
     *  @param  resultSet   The result set to stream on.
     *  @return The stream.
     */
    @API( status = STABLE, since = "0.0.1" )
    public static final Stream<ResultSet> stream( final ResultSet resultSet )
    {
        final var spliterator = new ResultSetSpliterator( resultSet );
        final var retValue = StreamSupport.stream( spliterator, false );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  stream()
}
//  class DatabaseUtils

/*
 *  End of File
 */