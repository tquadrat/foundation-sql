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
import static java.lang.Thread.currentThread;
import static java.sql.JDBCType.ARRAY;
import static java.sql.JDBCType.BIGINT;
import static java.sql.JDBCType.BLOB;
import static java.sql.JDBCType.BOOLEAN;
import static java.sql.JDBCType.CLOB;
import static java.sql.JDBCType.DATALINK;
import static java.sql.JDBCType.DATE;
import static java.sql.JDBCType.DOUBLE;
import static java.sql.JDBCType.INTEGER;
import static java.sql.JDBCType.LONGNVARCHAR;
import static java.sql.JDBCType.LONGVARBINARY;
import static java.sql.JDBCType.LONGVARCHAR;
import static java.sql.JDBCType.NCHAR;
import static java.sql.JDBCType.NCLOB;
import static java.sql.JDBCType.NUMERIC;
import static java.sql.JDBCType.REAL;
import static java.sql.JDBCType.REF;
import static java.sql.JDBCType.ROWID;
import static java.sql.JDBCType.SMALLINT;
import static java.sql.JDBCType.SQLXML;
import static java.sql.JDBCType.TIME;
import static java.sql.JDBCType.TIMESTAMP;
import static java.sql.JDBCType.TINYINT;
import static java.sql.JDBCType.VARBINARY;
import static java.sql.JDBCType.VARCHAR;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotBlankArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.lang.Objects;
import org.tquadrat.foundation.sql.EnhancedPreparedStatement;
import org.tquadrat.foundation.sql.ParameterMetaData;
import org.tquadrat.foundation.util.LazyMap;

/**
 *  The base class for implementations of
 *  {@link EnhancedPreparedStatement}.
 *
 *  @version $Id: EnhancedPreparedStatementBase.java 1100 2024-02-16 23:33:45Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "OverlyComplexClass" )
@ClassVersion( sourceVersion = "$Id: EnhancedPreparedStatementBase.java 1100 2024-02-16 23:33:45Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public abstract sealed class EnhancedPreparedStatementBase implements EnhancedPreparedStatement
    permits EnhancedPreparedStatementImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The base class for a variant of
     *  {@link java.sql.ParameterMetaData}
     *  that is used by
     *  {@link org.tquadrat.foundation.sql.EnhancedPreparedStatement}.
     *
     *  @version $Id: EnhancedPreparedStatementBase.java 1100 2024-02-16 23:33:45Z tquadrat $
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @UMLGraph.link
     *  @since 0.1.0
     */
    @SuppressWarnings( "ProtectedInnerClass" )
    @ClassVersion( sourceVersion = "$Id: EnhancedPreparedStatementBase.java 1100 2024-02-16 23:33:45Z tquadrat $" )
    @API( status = INTERNAL, since = "0.1.0" )
    protected final class ParameterMetaDataImpl extends ParameterMetaDataBase
    {
            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new instance of {@code ParameterMetaDataImpl}.
         *
         *  @param metaData The wrapped metadata instance.
         */
        public ParameterMetaDataImpl( final java.sql.ParameterMetaData metaData )
        {
            super( metaData );
        }   //  ParameterMetaDataImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final int[] getParameterIndexes( final String parameterName ) throws SQLException
        {
            final var retValue = EnhancedPreparedStatementBase.this.getParameterIndexes( parameterName );

            //---* Done *----------------------------------------------------------
            return retValue;
        }   //  getParameterIndexes()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Collection<String> getParameterNames()
        {
            final var retValue = EnhancedPreparedStatementBase.this.getParameterNames();

            //---* Done *----------------------------------------------------------
            return retValue;
        }   //  getParameterNames()
    }
    //  class ParameterMetaDataImpl

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The counter for the added batch commands.
     */
    private int m_BatchCounter = 0;

    /**
     *  <p>{@summary The parameter index.}</p>
     *  <p>The parameter names are the keys for the map, the values are the
     *  indexes.</p>
     */
    private final Map<String,int []> m_ParameterIndex = new HashMap<>();

    /**
     *  The wrapped
     *  {@link java.sql.PreparedStatement}.
     */
    private final PreparedStatement m_PreparedStatement;

    /**
     *  The statement source.
     */
    private final String m_SourceStatement;

    /**
     *  The values.
     */
    private final LazyMap<String,StatementValue> m_Values = LazyMap.use( HashMap::new );

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code EnhancedPreparedStatementBase}.
     *
     *  @param  sourceStatement The original SQL statement with the
     *      placeholders; mainly used for logging purposes.
     *  @param  preparedStatement   The wrapped instance of
     *      {@link PreparedStatement}.
     *  @param  parameterIndex  The mapping for the named placeholders to the
     *      position based placeholders.
     */
    protected EnhancedPreparedStatementBase( final String sourceStatement, final PreparedStatement preparedStatement, final Map<String,int[]> parameterIndex )
    {
        m_SourceStatement = requireNotEmptyArgument( sourceStatement, "sourceStatement" );
        m_PreparedStatement = requireNonNullArgument( preparedStatement, "preparedStatement" );
        m_ParameterIndex.putAll( requireNonNullArgument( parameterIndex, "parameterIndex" ) );
    }   //  EnhancedPreparedStatementBase()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final void addBatch() throws SQLException
    {
        if( isLoggingEnabled() ) doLogging( format( "addBatch() #%d", m_BatchCounter ), addStacktrace() ? currentThread().getStackTrace() : null );
        m_PreparedStatement.addBatch();
        ++m_BatchCounter;
    }   //  addBatch()

    /**
     *  Returns a flag that indicates whether a stacktrace should be added to
     *  the logging.
     *
     *  @return {@code true} if the stacktrace should be created and added,
     *      {@code false} otherwise.
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    protected abstract boolean addStacktrace();

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void cancel() throws SQLException
    {
        m_PreparedStatement.cancel();
        if( isLoggingEnabled() ) doLogging( "cancel()", addStacktrace() ? currentThread().getStackTrace() : null );
    }   //  cancel()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void clearBatch() throws SQLException
    {
        m_PreparedStatement.clearBatch();
        m_BatchCounter = 0;
    }   //  clearBatch()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void clearParameters() throws SQLException
    {
        m_PreparedStatement.clearParameters();
        m_Values.clear();
    }   //  clearParameters()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void clearWarnings() throws SQLException { m_PreparedStatement.clearWarnings(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void close() throws SQLException { m_PreparedStatement.close(); }

    @Override
    public final void closeOnCompletion() throws SQLException { m_PreparedStatement.closeOnCompletion(); }

    /**
     *  Composes the log information and sends it.
     *
     *  @param  operation   The operation that logs.
     *  @param  stackTrace  The stack trace; can be {@code null}.
     */
    @SuppressWarnings( "MethodCanBeVariableArityMethod" )
    protected abstract void doLogging( final String operation, final StackTraceElement [] stackTrace );

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String enquoteIdentifier( final String identifier, final boolean alwaysQuote ) throws SQLException
    {
        final var retValue = m_PreparedStatement.enquoteIdentifier( identifier, alwaysQuote );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enquoteIdentifier()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String enquoteLiteral( final String value ) throws SQLException { return m_PreparedStatement.enquoteLiteral( value ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String enquoteNCharLiteral( final String s ) throws SQLException
    {
        final var retValue = m_PreparedStatement.enquoteNCharLiteral( s );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enquoteNCharLiteral()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean execute() throws SQLException
    {
        if( isLoggingEnabled() ) doLogging( "execute()", addStacktrace() ? currentThread().getStackTrace() : null );
        final var retValue = m_PreparedStatement.execute();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  execute()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int[] executeBatch() throws SQLException
    {
        final var retValue = m_PreparedStatement.executeBatch();
        m_BatchCounter = 0;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  executeBatch()

    @Override
    public long[] executeLargeBatch() throws SQLException
    {
        final var retValue = m_PreparedStatement.executeLargeBatch();
        m_BatchCounter = 0;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  executeLargeBatch()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final long executeLargeUpdate() throws SQLException
    {
        if( isLoggingEnabled() ) doLogging( "executeLargeUpdate()", addStacktrace() ? currentThread().getStackTrace() : null );
        final var retValue = m_PreparedStatement.executeLargeUpdate();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  executeLargeUpdate()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ResultSet executeQuery() throws SQLException
    {
        if( isLoggingEnabled() ) doLogging( "executeQuery()", addStacktrace() ? currentThread().getStackTrace() : null );
        final var retValue = m_PreparedStatement.executeQuery();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  executeQuery()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int executeUpdate() throws SQLException
    {
        if( isLoggingEnabled() ) doLogging( "executeUpdate()", addStacktrace() ? currentThread().getStackTrace() : null );
        final var retValue = m_PreparedStatement.executeUpdate();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  executeUpdate()

    /**
     *  Provides the current values for logging purposes.
     *
     *  @return The current values.
     */
    protected final Collection<StatementValue> getCurrentValues() { return m_Values.isPresent() ? List.of() : m_Values.values(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Connection getConnection() throws SQLException { return m_PreparedStatement.getConnection(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getFetchDirection() throws SQLException { return m_PreparedStatement.getFetchDirection(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getFetchSize() throws SQLException { return m_PreparedStatement.getFetchSize(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ResultSet getGeneratedKeys() throws SQLException { return m_PreparedStatement.getGeneratedKeys(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final long getLargeMaxRows() throws SQLException { return m_PreparedStatement.getLargeMaxRows(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final long getLargeUpdateCount() throws SQLException { return m_PreparedStatement.getLargeUpdateCount(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getMaxFieldSize() throws SQLException { return m_PreparedStatement.getMaxFieldSize(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getMaxRows() throws SQLException { return m_PreparedStatement.getMaxRows(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ResultSetMetaData getMetaData() throws SQLException { return m_PreparedStatement.getMetaData(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean getMoreResults() throws SQLException { return m_PreparedStatement.getMoreResults(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean getMoreResults( final int current ) throws SQLException { return m_PreparedStatement.getMoreResults( current ); }

    /**
     *  Returns the parameter indexes for the given parameter name.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return The parameter indexes for this parameter name.
     *  @throws SQLException    The given parameter name is not defined.
     */
    protected final int [] getParameterIndexes( final String parameterName ) throws SQLException
    {
        final var retValue = m_ParameterIndex.get( requireNotBlankArgument( parameterName, "parameterName" ) );
        if( isNull( retValue ) ) throw new SQLException( "Parameter name '%1$s' unknown".formatted( parameterName ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getParameterIndexes()

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        final ParameterMetaData retValue = new ParameterMetaDataImpl( m_PreparedStatement.getParameterMetaData() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getParameterMetaData()

    /**
     *  Returns the parameter names for this {@code EnhancedPreparedStatement}.
     *
     *  @return The parameter names.
     */
    protected final Collection<String> getParameterNames()
    {
        final var retValue = Set.copyOf( m_ParameterIndex.keySet() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getParameterNames()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getQueryTimeout() throws SQLException { return m_PreparedStatement.getQueryTimeout(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ResultSet getResultSet() throws SQLException { return m_PreparedStatement.getResultSet(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getResultSetConcurrency() throws SQLException { return m_PreparedStatement.getResultSetConcurrency(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getResultSetHoldability() throws SQLException { return m_PreparedStatement.getResultSetHoldability(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getResultSetType() throws SQLException { return m_PreparedStatement.getResultSetType(); }

    /**
     *  Provides the source statement for logging purposes.
     *
     *  @return The source statement.
     */
    protected final String getSourceStatement() { return m_SourceStatement; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getUpdateCount() throws SQLException { return m_PreparedStatement.getUpdateCount(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final SQLWarning getWarnings() throws SQLException { return m_PreparedStatement.getWarnings(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isClosed() throws SQLException { return m_PreparedStatement.isClosed(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isCloseOnCompletion() throws SQLException { return m_PreparedStatement.isCloseOnCompletion(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public abstract boolean isLoggingEnabled();

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isPoolable() throws SQLException { return m_PreparedStatement.isPoolable(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isSimpleIdentifier( final String identifier ) throws SQLException
    {
        final var retValue = m_PreparedStatement.isSimpleIdentifier( requireNotEmptyArgument( identifier, "identifier" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isSimpleIdentifier()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setArray( final String parameterName, final Array value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setArray( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, ARRAY, Objects.toString( value ) ) );
        }
    }   //  setArray()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void setAsciiStream( final String parameterName, final InputStream value, final int length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setAsciiStream( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARCHAR, valueText ) );
        }
    }   //  setAsciiStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void setAsciiStream( final String parameterName, final InputStream value, final long length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setAsciiStream( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARCHAR, valueText ) );
        }
    }   //  setAsciiStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void setAsciiStream( final String parameterName, final InputStream value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setAsciiStream( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARCHAR, valueText ) );
        }
    }   //  setAsciiStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void setBinaryStream( final String parameterName, final InputStream value, final int length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBinaryStream( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARBINARY, valueText ) );
        }
    }   //  setBinaryStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void setBinaryStream( final String parameterName, final InputStream value, final long length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBinaryStream( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARBINARY, valueText ) );
        }
    }   //  setBinaryStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void setBinaryStream( final String parameterName, final InputStream value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBinaryStream( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARBINARY, valueText ) );
        }
    }   //  setBinaryStream()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "CallToNumericToString" )
    @Override
    public final void setBigDecimal( final String parameterName, final BigDecimal value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBigDecimal( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, NUMERIC, value.toString() ) );
        }
    }   //  setBigDecimal()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setBlob( final String parameterName, final Blob value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBlob( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, BLOB, valueText ) );
        }
    }   //  setBlob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setBlob( final String parameterName, final InputStream value, final long length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBlob( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, BLOB, valueText ) );
        }
    }   //  setBlob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setBlob( final String parameterName, final InputStream value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBlob( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, BLOB, valueText ) );
        }
    }   //  setBlob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setBoolean( final String parameterName, final boolean value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBoolean( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, BOOLEAN, value ? "true" : "false" ) );
        }
    }   //  setBoolean()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setByte( final String parameterName, final byte value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setByte( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, TINYINT, Byte.toString( value ) ) );
        }
    }   //  setByte()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setBytes( final String parameterName, final byte[] value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setBytes( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), value.length );
            m_Values.put( parameterName, new StatementValue( parameterName, VARBINARY, valueText ) );
        }
    }   //  setBytes()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setCharacterStream( final String parameterName, final Reader value, final int length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setCharacterStream( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARCHAR, valueText ) );
        }
    }   //  setCharacterStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setCharacterStream( final String parameterName, final Reader value, final long length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setCharacterStream( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARCHAR, valueText ) );
        }
    }   //  setCharacterStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setCharacterStream( final String parameterName, final Reader value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setCharacterStream( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, LONGVARCHAR, valueText ) );
        }
    }   //  setCharacterStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setClob( final String parameterName, final Clob value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setClob( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, CLOB, valueText ) );
        }
    }   //  setClob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setClob( final String parameterName, final Reader value, final long length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setClob( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, CLOB, valueText ) );
        }
    }   //  setClob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setCursorName( final String name ) throws SQLException { m_PreparedStatement.setCursorName( name ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setClob( final String parameterName, final Reader value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setClob( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, CLOB, valueText ) );
        }
    }   //  setClob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setDate( final String parameterName, final Date value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setDate( index, value );
        if( isLoggingEnabled() )
        {
            var valueText = NULL_STRING;
            if( nonNull( value ) )
            {
                final var instant = value.toInstant();
                valueText = format( "%2$s (%1$s)", instant, instant.atZone( ZoneId.systemDefault() ).toLocalDate() );
            }
            m_Values.put( parameterName, new StatementValue( parameterName, DATE, valueText ) );
        }
    }   //  setDate()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfObsoleteDateTimeApi" )
    @Override
    public void setDate( final String parameterName, final Date value, final Calendar calendar ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setDate( index, value, calendar );
        if( isLoggingEnabled() )
        {
            var valueText = NULL_STRING;
            if( nonNull( value ) )
            {
                final var instant = value.toInstant();
                final var timezone = isNull( calendar ) ? ZoneId.systemDefault() : calendar.getTimeZone().toZoneId();
                valueText = format( "%2$s (%1$s)", instant, instant.atZone( timezone ).toLocalDate() );
            }
            m_Values.put( parameterName, new StatementValue( parameterName, DATE, valueText ) );
        }
    }   //  setDate()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setDouble( final String parameterName, final double value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setDouble( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, DOUBLE, Double.toString( value ) ) );
        }
    }   //  setDouble()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setFetchDirection( final int direction ) throws SQLException { m_PreparedStatement.setFetchDirection( direction ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setFetchSize( final int rows ) throws SQLException { m_PreparedStatement.setFetchSize( rows ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setFloat( final String parameterName, final float value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setFloat( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, REAL, Float.toString( value ) ) );
        }
    }   //  setFloat()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setInt( final String parameterName, final int value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setInt( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, INTEGER, Integer.toString( value ) ) );
        }
    }   //  setInt()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setLargeMaxRows( final long max ) throws SQLException { m_PreparedStatement.setLargeMaxRows( max ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setLong( final String parameterName, final long value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setLong( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, BIGINT, Long.toString( value ) ) );
        }
    }   //  setLong()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setMaxFieldSize( final int max ) throws SQLException { m_PreparedStatement.setMaxFieldSize( max ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setMaxRows( final int max ) throws SQLException { m_PreparedStatement.setMaxRows( max ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setNCharacterStream( final String parameterName, final Reader value, final long length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setNCharacterStream( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, LONGNVARCHAR, valueText ) );
        }
    }   //  setNCharacterStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setNCharacterStream( final String parameterName, final Reader value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setNCharacterStream( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, LONGNVARCHAR, valueText ) );
        }
    }   //  setNCharacterStream()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setNClob( final String parameterName, final NClob value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setNClob( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, NCLOB, valueText ) );
        }
    }   //  setNClob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setNClob( final String parameterName, final Reader value, final long length ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setNClob( index, value, length );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : format( "%1$s (%2$d)", value.getClass().getName(), length );
            m_Values.put( parameterName, new StatementValue( parameterName, NCLOB, valueText ) );
        }
    }   //  setNClob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setNClob( final String parameterName, final Reader value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setNClob( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, NCLOB, valueText ) );
        }
    }   //  setNClob()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setNString( final String parameterName, final String value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setNString( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, NCHAR, value ) );
        }
    }   //  setNString()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setNull( final String parameterName, final SQLType sqlType ) throws SQLException
    {
        final var type = requireNonNullArgument( sqlType, "sqlType" ).getVendorTypeNumber();
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setNull( index, type );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, sqlType, NULL_STRING ) );
        }
    }   //  setNull()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void setNull( final String parameterName, final SQLType sqlType, final String typeName ) throws SQLException
    {
        final var type = requireNonNullArgument( sqlType, "sqlType" ).getVendorTypeNumber();
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setNull( index, type, typeName );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, sqlType, format( "%1$s (%2$s)", NULL_STRING, typeName ) ) );
        }
    }   //  setNull()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setObject( final String parameterName, final Object value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setObject( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, null, Objects.toString( value ) ) );
        }
    }   //  setObject()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setObject( final String parameterName, final Object value, final SQLType targetSqlType, final int scaleOrLength ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setObject( index, value, scaleOrLength );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, targetSqlType, format( "%1$s (%2$d)", Objects.toString( value ), scaleOrLength ) ) );
        }
    }   //  setObject()

    /**
     *  {@inheritDoc}
     */
    @Override
    public void setObject( final String parameterName, final Object value, final SQLType targetSqlType ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setObject( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, targetSqlType, Objects.toString( value ) ) );
        }
    }   //  setObject()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setQueryTimeout( final int timeout ) throws SQLException { m_PreparedStatement.setQueryTimeout( timeout ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setRef( final String parameterName, final Ref value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setRef( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, REF, Objects.toString( value ) ) );
        }
    }   //  setRef()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setRowId( final String parameterName, final RowId value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setRowId( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, ROWID, Objects.toString( value ) ) );
        }
    }   //  setRowId()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setShort( final String parameterName, final short value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setShort( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, SMALLINT, Short.toString( value ) ) );
        }
    }   //  setShort()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setSQLXML( final String parameterName, final SQLXML value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setSQLXML( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.getClass().getName();
            m_Values.put( parameterName, new StatementValue( parameterName, SQLXML, valueText ) );
        }
    }   //  setSQLXML

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setString( final String parameterName, final String value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setString( index, value );
        if( isLoggingEnabled() )
        {
            m_Values.put( parameterName, new StatementValue( parameterName, VARCHAR, value ) );
        }
    }   //  setString()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setTime( final String parameterName, final Time value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setTime( index, value );
        if( isLoggingEnabled() )
        {
            var valueText = NULL_STRING;
            if( nonNull( value ) )
            {
                final var instant = value.toInstant();
                valueText = format( "%2$s (%1$s)", instant, instant.atZone( ZoneId.systemDefault() ).toLocalTime() );
            }
            m_Values.put( parameterName, new StatementValue( parameterName, TIME, valueText ) );
        }
    }   //  setTime()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfObsoleteDateTimeApi" )
    @Override
    public final void setTime( final String parameterName, final Time value, final Calendar calendar ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setTime( index, value, calendar );
        if( isLoggingEnabled() )
        {
            var valueText = NULL_STRING;
            if( nonNull( value ) )
            {
                final var instant = value.toInstant();
                final var timezone = isNull( calendar ) ? ZoneId.systemDefault() : calendar.getTimeZone().toZoneId();
                valueText = format( "%2$s (%1$s)", instant, instant.atZone( timezone ).toLocalTime() );
            }
            m_Values.put( parameterName, new StatementValue( parameterName, TIME, valueText ) );
        }
    }   //  setTime()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setTimestamp( final String parameterName, final Timestamp value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setTimestamp( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.toInstant().atZone( ZoneId.systemDefault() ).toString();
            m_Values.put( parameterName, new StatementValue( parameterName, TIMESTAMP, valueText ) );
        }
    }   //  setTimestamp()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfObsoleteDateTimeApi" )
    @Override
    public final void setTimestamp( final String parameterName, final Timestamp value, final Calendar calendar ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setTimestamp( index, value, calendar );
        if( isLoggingEnabled() )
        {
            final var timezone = isNull( calendar ) ? ZoneId.systemDefault() : calendar.getTimeZone().toZoneId();
            final var valueText = isNull( value ) ? NULL_STRING : value.toInstant().atZone( timezone ).toString();
            m_Values.put( parameterName, new StatementValue( parameterName, TIMESTAMP, valueText ) );
        }
    }   //  setTimestamp()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setURL( final String parameterName, final URL value ) throws SQLException
    {
        for( final var index : getParameterIndexes( parameterName ) ) m_PreparedStatement.setURL( index, value );
        if( isLoggingEnabled() )
        {
            final var valueText = isNull( value ) ? NULL_STRING : value.toExternalForm();
            m_Values.put( parameterName, new StatementValue( parameterName, DATALINK, valueText ) );
        }
    }   //  setURL()
}
//  class EnhancedPreparedStatementBase

/*
 *  End of File
 */