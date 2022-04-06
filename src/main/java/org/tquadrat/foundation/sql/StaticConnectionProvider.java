/*
 * ============================================================================
 * Copyright © 2002-2022 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.tquadrat.foundation.sql;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.ShardingKey;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.lang.Status;

/**
 *  <p>{@summary An implementation of
 *  {@link ConnectionProvider}
 *  that returns always the same
 *  {@link Connection}
 *  instance.}</p>
 *  <p>This connection provider should not be used for production purposes! It
 *  will not allow the connection to be closed, the database session remains
 *  open for its whole lifetime. In addition, it is by no means
 *  thread-safe!</p>
 *  <p>It can be used for testing purposes, or when the database access is
 *  needed for a very short period of.</p>
 *
 *  @version $Id: StaticConnectionProvider.java 1030 2022-04-06 13:42:02Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: StaticConnectionProvider.java 1030 2022-04-06 13:42:02Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class StaticConnectionProvider implements ConnectionProvider
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  An implementation of
     *  {@link Connection}
     *  that replaces the implementation of the method
     *  {@link #close()}
     *  by a dummy that does nothing.
     *
     *  @version $Id: StaticConnectionProvider.java 1030 2022-04-06 13:42:02Z tquadrat $
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @UMLGraph.link
     *  @since 0.0.1
     */
    @ClassVersion( sourceVersion = "$Id: StaticConnectionProvider.java 1030 2022-04-06 13:42:02Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.1" )
    private final class UncloseableConnection implements Connection
    {
            /*--------------*\
        ====** Constructors **=====================================================
            \*--------------*/
        /**
         *  Creates an instance of {@code UncloseableConnection}.
         */
        public UncloseableConnection() { /* Just exists */ }

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final void abort( final Executor executor ) throws SQLException {m_Connection.abort( executor );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void beginRequest() throws SQLException {m_Connection.beginRequest();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void clearWarnings() throws SQLException { m_Connection.clearWarnings(); }

        /**
         *  {@inheritDoc}
         *  <p>This implementation does nothing!</p>
         */
        @Override
        public final void close() throws SQLException { /* Does nothing! */ }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void commit() throws SQLException { m_Connection.commit(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Array createArrayOf( final String typeName, final Object[] elements ) throws SQLException {return m_Connection.createArrayOf( typeName, elements );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Blob createBlob() throws SQLException {return m_Connection.createBlob();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Clob createClob() throws SQLException {return m_Connection.createClob();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final NClob createNClob() throws SQLException {return m_Connection.createNClob();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final SQLXML createSQLXML() throws SQLException {return m_Connection.createSQLXML();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Statement createStatement() throws SQLException { return m_Connection.createStatement(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Statement createStatement( final int resultSetType, final int resultSetConcurrency ) throws SQLException { return m_Connection.createStatement( resultSetType, resultSetConcurrency ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Statement createStatement( final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {return m_Connection.createStatement( resultSetType, resultSetConcurrency, resultSetHoldability );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Struct createStruct( final String typeName, final Object[] attributes ) throws SQLException {return m_Connection.createStruct( typeName, attributes );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void endRequest() throws SQLException {m_Connection.endRequest();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean getAutoCommit() throws SQLException { return m_Connection.getAutoCommit(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final String getCatalog() throws SQLException { return m_Connection.getCatalog(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final String getClientInfo( final String name ) throws SQLException {return m_Connection.getClientInfo( name );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Properties getClientInfo() throws SQLException {return m_Connection.getClientInfo();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final int getHoldability() throws SQLException {return m_Connection.getHoldability();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final DatabaseMetaData getMetaData() throws SQLException { return m_Connection.getMetaData(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final int getNetworkTimeout() throws SQLException {return m_Connection.getNetworkTimeout();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final String getSchema() throws SQLException {return m_Connection.getSchema();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final int getTransactionIsolation() throws SQLException { return m_Connection.getTransactionIsolation(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Map<String, Class<?>> getTypeMap() throws SQLException {return m_Connection.getTypeMap();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final SQLWarning getWarnings() throws SQLException { return m_Connection.getWarnings(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean isClosed() throws SQLException { return m_Connection.isClosed(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean isReadOnly() throws SQLException { return m_Connection.isReadOnly(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean isValid( final int timeout ) throws SQLException {return m_Connection.isValid( timeout );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean isWrapperFor( final Class<?> iface ) throws SQLException {return m_Connection.isWrapperFor( iface );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final String nativeSQL( final String sql ) throws SQLException { return m_Connection.nativeSQL( sql ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final CallableStatement prepareCall( final String sql ) throws SQLException {return m_Connection.prepareCall( sql );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final CallableStatement prepareCall( final String sql, final int resultSetType, final int resultSetConcurrency ) throws SQLException {return m_Connection.prepareCall( sql, resultSetType, resultSetConcurrency );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final CallableStatement prepareCall( final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {return m_Connection.prepareCall( sql, resultSetType, resultSetConcurrency, resultSetHoldability );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final PreparedStatement prepareStatement( final String sql ) throws SQLException { return m_Connection.prepareStatement( sql ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final PreparedStatement prepareStatement( final String sql, final int resultSetType, final int resultSetConcurrency ) throws SQLException {return m_Connection.prepareStatement( sql, resultSetType, resultSetConcurrency );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final PreparedStatement prepareStatement( final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability ) throws SQLException {return m_Connection.prepareStatement( sql, resultSetType, resultSetConcurrency, resultSetHoldability );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final PreparedStatement prepareStatement( final String sql, final int autoGeneratedKeys ) throws SQLException {return m_Connection.prepareStatement( sql, autoGeneratedKeys );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final PreparedStatement prepareStatement( final String sql, final int[] columnIndexes ) throws SQLException {return m_Connection.prepareStatement( sql, columnIndexes );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final PreparedStatement prepareStatement( final String sql, final String[] columnNames ) throws SQLException {return m_Connection.prepareStatement( sql, columnNames );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void releaseSavepoint( final Savepoint savepoint ) throws SQLException {m_Connection.releaseSavepoint( savepoint );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void rollback() throws SQLException {m_Connection.rollback();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void rollback( final Savepoint savepoint ) throws SQLException {m_Connection.rollback( savepoint );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setAutoCommit( final boolean autoCommit ) throws SQLException { m_Connection.setAutoCommit( autoCommit ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setCatalog( final String catalog ) throws SQLException { m_Connection.setCatalog( catalog ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setClientInfo( final String name, final String value ) throws SQLClientInfoException {m_Connection.setClientInfo( name, value );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setClientInfo( final Properties properties ) throws SQLClientInfoException {m_Connection.setClientInfo( properties );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setHoldability( final int holdability ) throws SQLException {m_Connection.setHoldability( holdability );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setNetworkTimeout( final Executor executor, final int milliseconds ) throws SQLException {m_Connection.setNetworkTimeout( executor, milliseconds );}

        /**
         *  {@inheritDoc}
         */
        public final void setReadOnly( final boolean readOnly ) throws SQLException { m_Connection.setReadOnly( readOnly ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Savepoint setSavepoint() throws SQLException {return m_Connection.setSavepoint();}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Savepoint setSavepoint( final String name ) throws SQLException {return m_Connection.setSavepoint( name );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setSchema( final String schema ) throws SQLException {m_Connection.setSchema( schema );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setShardingKey( final ShardingKey shardingKey, final ShardingKey superShardingKey ) throws SQLException {m_Connection.setShardingKey( shardingKey, superShardingKey );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setShardingKey( final ShardingKey shardingKey ) throws SQLException {m_Connection.setShardingKey( shardingKey );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean setShardingKeyIfValid( final ShardingKey shardingKey, final ShardingKey superShardingKey, final int timeout ) throws SQLException {return m_Connection.setShardingKeyIfValid( shardingKey, superShardingKey, timeout );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean setShardingKeyIfValid( final ShardingKey shardingKey, final int timeout ) throws SQLException {return m_Connection.setShardingKeyIfValid( shardingKey, timeout );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setTransactionIsolation( final int level ) throws SQLException { m_Connection.setTransactionIsolation( level ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final void setTypeMap( final Map<String, Class<?>> map ) throws SQLException {m_Connection.setTypeMap( map );}

        /**
         *  {@inheritDoc}
         */
        @Override
        public final <T> T unwrap( final Class<T> iface ) throws SQLException {return m_Connection.unwrap( iface );}
    }
    //  class UncloseableConnection

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The connection.
     */
    private final Connection m_Connection;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  <p>{@summary Creates a new instance of
     *  {@code StaticConnectionProvider}.}</p>
     *  <p>The provided connection must be open and valid, although this will
     *  not be checked here.</p>
     *
     *  @param  connection  The connection that is returned by each call to
     */
    public StaticConnectionProvider( final Connection connection )
    {
        m_Connection = requireNonNullArgument( connection, "connection" );
    }   //  StaticConnectionProvider()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final Status<Connection,Throwable> getConnection()
    {
        Status<Connection,Throwable> retValue;
        try
        {
            //---* Check if connection is (still) open *-----------------------
            @SuppressWarnings( "unused" )
            final var metaData = m_Connection.getMetaData();
            retValue = new Status<>( new UncloseableConnection(), null );
        }
        catch( final SQLException e )
        {
            retValue = new Status<>( null, e );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getConnection()
}
//  class StaticConnectionProvider

/*
 *  End of File
 */