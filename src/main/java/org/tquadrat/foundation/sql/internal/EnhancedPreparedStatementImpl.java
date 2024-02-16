/*
 * ============================================================================
 *  Copyright © 2002-2023 by Thomas Thrien.
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

package org.tquadrat.foundation.sql.internal;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotBlankArgument;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.stream.MapStream;

/**
 *  The implementation for
 *  {@link org.tquadrat.foundation.sql.EnhancedPreparedStatement}.
 *
 *  @version $Id: EnhancedPreparedStatementImpl.java 1075 2023-10-02 12:37:07Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: EnhancedPreparedStatementImpl.java 1075 2023-10-02 12:37:07Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public final class EnhancedPreparedStatementImpl extends EnhancedPreparedStatementBase
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  Flag that controls whether a stack trace should be added to the log
     *  output.
     */
    private static boolean m_AddStacktrace = false;

    /**
     *  The log check method.
     */
    private static BooleanSupplier m_LogCheck = () -> false;

    /**
     *  The logger method.
     */
    private static StatementLogger m_Logger = null;

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The pattern that is used to identify a variable in a SQL statement
     *  text.
     */
    private static final Pattern m_VariablePattern;

    static
    {
        try
        {
            m_VariablePattern = compile( VARIABLE_PATTERN );
        }
        catch( final PatternSyntaxException e )
        {
            /*
             * The provided pattern is a constant and should be properly
             * tested. Therefore, a PatternSyntaxException is unlikely.
             */
            throw new UnexpectedExceptionError( e );
        }
    }

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code EnhancedPreparedStatementImpl}.
     *
     *  @param  sourceStatement The original SQL statement with the
     *      placeholders; mainly used for logging purposes.
     *  @param  preparedStatement   The wrapped instance of
     *      {@link PreparedStatement}.
     *  @param  parameterIndex  The mapping for the named placeholders to the
     *      position based placeholders.
     */
    public EnhancedPreparedStatementImpl( final String sourceStatement, final PreparedStatement preparedStatement, final Map<String,int[]> parameterIndex )
    {
        super( sourceStatement, preparedStatement, parameterIndex );
    }   //  EnhancedPreparedStatementImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    protected final boolean addStacktrace() { return m_AddStacktrace; }

    /**
     *  Converts the index buffer to a parameter index.
     *
     *  @param  indexBuffer The index buffer.
     *  @return The parameter index.
     *
     *  @note   The method is public to allow simpler Unit tests.
     */
    @API( status = INTERNAL, since = "0.1.0" )
    public static final Map<String,int []> convertIndexBufferToParameterIndex( final Map<String, ? extends List<Integer>> indexBuffer )
    {
        final var retValue = MapStream.of( requireNonNullArgument( indexBuffer, "indexBuffer" ) )
            .mapValues( v -> v.stream().mapToInt( Integer::intValue ).toArray() )
            .collect();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  convertIndexBufferToParameterIndex()

    /**
     *  Creates a new instance of {@code EnhancedPreparedStatementImpl}.
     *
     *  @param  connection  The connection to the database.
     *  @param  sql The text of the SQL statement with the placeholders.
     *  @return The new statement.
     *  @throws SQLException    Unable to create an instance of an
     *      {@link org.tquadrat.foundation.sql.EnhancedPreparedStatement}.
     */
    @API( status = MAINTAINED, since = "0.1.0" )
    public static final EnhancedPreparedStatementImpl create( final Connection connection, final String sql ) throws SQLException
    {
        final Map<String, List<Integer>> indexBuffer = new HashMap<>();
        final var preparedStatement = requireNonNullArgument( connection, "connection" ).prepareStatement( parseSQL( sql, indexBuffer ) );
        final var retValue = new EnhancedPreparedStatementImpl( sql, preparedStatement, convertIndexBufferToParameterIndex( indexBuffer ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  create()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final void doLogging( final String operation, final StackTraceElement [] stackTrace )
    {
        final var values = getCurrentValues()
            .stream()
            .map( v -> format( "%1$s [%2$s]: %3$s", v.parameterName(), (isNull( v.type() ) ? NULL_STRING : v.type().getName()), v.value() ) )
            .sorted()
            .toList();
        m_Logger.log( operation, getSourceStatement(), values, stackTrace );
    }   //  doLogging()

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
    @SuppressWarnings( "MethodOverridesStaticMethodOfSuperclass" )
    public static final void enableLogging( final StatementLogger logger, final BooleanSupplier logCheck, final boolean addStacktrace )
    {
        m_Logger = requireNonNullArgument( logger, "logger" );
        m_LogCheck = requireNonNullArgument( logCheck, "logCheck" );
        m_AddStacktrace = addStacktrace;
    }   //  enableLogging()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isLoggingEnabled() { return m_LogCheck.getAsBoolean(); }

    /**
     *  Parses the given SQL statement with the named placeholders and returns
     *  the text for a call to
     *  {@link java.sql.Connection#prepareStatement(String)}.
     *
     *  @param  sql The source text for the SQL statement.
     *  @param  indexBuffer The mapping from the names to the indexes.
     *  @return The target SQL text.
     *
     *  @note   The method is public to allow simpler Unit tests.
     */
    @API( status = INTERNAL, since = "0.1.0" )
    public static final String parseSQL( final String sql, final Map<? super String, List<Integer>> indexBuffer )
    {
        requireNonNullArgument( indexBuffer, "indexBuffer" );

        //---* Parse the statement text *--------------------------------------
        var index = 0;
        final var buffer = new StringBuilder();
        final var matcher = m_VariablePattern.matcher( requireNotBlankArgument( sql, "sql" ) );
        while( matcher.find() )
        {
            final var variableName = matcher.group( 1 );
            indexBuffer.computeIfAbsent( variableName, $ -> new ArrayList<>() ).add( Integer.valueOf( ++index ) );
            matcher.appendReplacement( buffer, " ?" );
        }
        matcher.appendTail( buffer );

        //---* Save the text of the prepared statement *-----------------------
        final var retValue = buffer.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parseSQL()
}
//  class EnhancedPreparedStatementImpl

/*
 *  End of File
 */