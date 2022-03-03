/*
 * ============================================================================
 * Copyright © 2002-2022 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
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

package org.tquadrat.foundation.sql.internal;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  <p>{@summary A wrapper for a result set that restricts the call to some
 *  methods.}</p>
 *  <p>The operations that would move the cursor will throw an
 *  {@link UnsupportedOperationException}:</p>
 *  <ul>
 *      <li>{@link #absolute(int)}</li>
 *      <li>{@link #afterLast()}</li>
 *      <li>{@link #beforeFirst()}</li>
 *      <li>{@link #first()}</li>
 *      <li>{@link #last()}</li>
 *      <li>{@link #moveToCurrentRow()}</li>
 *      <li>{@link #moveToInsertRow()}</li>
 *      <li>{@link #next()}</li>
 *      <li>{@link #previous()}</li>
 *      <li>{@link #relative(int)}</li>
 *  </ul>
 *  <p>as well as the following methods:</p>
 *  <ul>
 *      <li>{@link #close()}</li>
 *      <li>{@link #deleteRow()}</li>
 *      <li>{@link #insertRow()}</li>
 *      <li>{@link #setFetchDirection(int)}</li>
 *      <li>{@link #setFetchSize(int)}</li>
 *  </ul>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ResultSetWrapper.java 1020 2022-02-27 21:26:03Z tquadrat $
 *  @since 0.0.1
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "OverlyComplexClass" )
@ClassVersion( sourceVersion = "$Id: ResultSetWrapper.java 1020 2022-02-27 21:26:03Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.1" )
public abstract class ResultSetWrapper implements ResultSet
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The wrapped
     *  {@link ResultSet}
     *  instance.
     */
    private final ResultSet m_Instance;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ResultSetWrapper} instance.
     *
     *  @param  instance    The wrapped instance.
     */
    protected ResultSetWrapper( final ResultSet instance )
    {
        m_Instance = requireNonNullArgument( instance, "instance" );
    }   //  ResultSetWrapper()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean absolute( final int row ) throws SQLException
    {
        throw new UnsupportedOperationException( "absolute()" );
    }   //  absolute()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void afterLast() throws SQLException
    {
        throw new UnsupportedOperationException( "afterLast()" );
    }   //  afterLast()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void beforeFirst() throws SQLException
    {
        throw new UnsupportedOperationException( "beforeFirst()" );
    }   //  beforeFirst()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void cancelRowUpdates() throws SQLException { m_Instance.cancelRowUpdates(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void clearWarnings() throws SQLException { m_Instance.clearWarnings(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void close() throws SQLException
    {
        throw new UnsupportedOperationException( "close()" );
    }   //  close()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void deleteRow() throws SQLException
    {
        throw new UnsupportedOperationException( "deleteRow()" );
    }   //  deleteRow()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int findColumn( final String columnLabel ) throws SQLException { return m_Instance.findColumn( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean first() throws SQLException
    {
        throw new UnsupportedOperationException( "first()" );
    }   //  first()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Array getArray( final int columnIndex ) throws SQLException { return m_Instance.getArray( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Array getArray( final String columnLabel ) throws SQLException { return m_Instance.getArray( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final InputStream getAsciiStream( final int columnIndex ) throws SQLException { return m_Instance.getAsciiStream( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final InputStream getAsciiStream( final String columnLabel ) throws SQLException { return m_Instance.getAsciiStream( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final BigDecimal getBigDecimal( final int columnIndex ) throws SQLException { return m_Instance.getBigDecimal( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Deprecated
    @Override
    public final BigDecimal getBigDecimal( final int columnIndex, final int scale ) throws SQLException { return m_Instance.getBigDecimal( columnIndex, scale ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final BigDecimal getBigDecimal( final String columnLabel ) throws SQLException { return m_Instance.getBigDecimal( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Deprecated
    @Override
    public final BigDecimal getBigDecimal( final String columnLabel, final int scale ) throws SQLException { return m_Instance.getBigDecimal( columnLabel, scale ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final InputStream getBinaryStream( final int columnIndex ) throws SQLException { return m_Instance.getBinaryStream( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final InputStream getBinaryStream( final String columnLabel ) throws SQLException { return m_Instance.getBinaryStream( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Blob getBlob( final int columnIndex ) throws SQLException { return m_Instance.getBlob( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Blob getBlob( final String columnLabel ) throws SQLException { return m_Instance.getBlob( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean getBoolean( final int columnIndex ) throws SQLException { return m_Instance.getBoolean( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean getBoolean( final String columnLabel ) throws SQLException { return m_Instance.getBoolean( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final byte getByte( final int columnIndex ) throws SQLException { return m_Instance.getByte( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final byte getByte( final String columnLabel ) throws SQLException { return m_Instance.getByte( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final byte [] getBytes( final int columnIndex ) throws SQLException { return m_Instance.getBytes( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final byte [] getBytes( final String columnLabel ) throws SQLException { return m_Instance.getBytes( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Reader getCharacterStream( final int columnIndex ) throws SQLException { return m_Instance.getCharacterStream( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Reader getCharacterStream( final String columnLabel ) throws SQLException { return m_Instance.getCharacterStream( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Clob getClob( final int columnIndex ) throws SQLException { return m_Instance.getClob( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Clob getClob( final String columnLabel ) throws SQLException { return m_Instance.getClob( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getConcurrency() throws SQLException { return m_Instance.getConcurrency(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getCursorName() throws SQLException { return m_Instance.getCursorName(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Date getDate( final int columnIndex ) throws SQLException { return m_Instance.getDate( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Date getDate( final int columnIndex, final Calendar cal ) throws SQLException { return m_Instance.getDate( columnIndex, cal ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Date getDate( final String columnLabel ) throws SQLException { return m_Instance.getDate( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Date getDate( final String columnLabel, final Calendar cal ) throws SQLException { return m_Instance.getDate( columnLabel, cal ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final double getDouble( final int columnIndex ) throws SQLException { return m_Instance.getDouble( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final double getDouble( final String columnLabel ) throws SQLException { return m_Instance.getDouble( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getFetchDirection() throws SQLException { return m_Instance.getFetchDirection(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getFetchSize() throws SQLException { return m_Instance.getFetchSize(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final float getFloat( final int columnIndex ) throws SQLException { return m_Instance.getFloat( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final float getFloat( final String columnLabel ) throws SQLException { return m_Instance.getFloat( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getHoldability() throws SQLException { return m_Instance.getHoldability(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getInt( final int columnIndex ) throws SQLException { return m_Instance.getInt( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getInt( final String columnLabel ) throws SQLException { return m_Instance.getInt( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final long getLong( final int columnIndex ) throws SQLException { return m_Instance.getLong( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final long getLong( final String columnLabel ) throws SQLException { return m_Instance.getLong( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ResultSetMetaData getMetaData() throws SQLException { return m_Instance.getMetaData(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Reader getNCharacterStream( final int columnIndex ) throws SQLException { return m_Instance.getNCharacterStream( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Reader getNCharacterStream( final String columnLabel ) throws SQLException { return m_Instance.getNCharacterStream( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final NClob getNClob( final int columnIndex ) throws SQLException { return m_Instance.getNClob( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final NClob getNClob( final String columnLabel ) throws SQLException { return m_Instance.getNClob( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getNString( final int columnIndex ) throws SQLException { return m_Instance.getNString( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getNString( final String columnLabel ) throws SQLException { return m_Instance.getNString( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Object getObject( final int columnIndex ) throws SQLException { return m_Instance.getObject( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final <T> T getObject( final int columnIndex, final Class<T> type ) throws SQLException { return m_Instance.getObject( columnIndex, type ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Object getObject( final int columnIndex, final Map<String,Class<?>> map ) throws SQLException { return m_Instance.getObject( columnIndex, map ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Object getObject( final String columnLabel ) throws SQLException { return m_Instance.getObject( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final <T> T getObject( final String columnLabel, final Class<T> type ) throws SQLException { return m_Instance.getObject( columnLabel, type ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Object getObject( final String columnLabel, final Map<String,Class<?>> map ) throws SQLException { return m_Instance.getObject( columnLabel, map ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Ref getRef( final int columnIndex ) throws SQLException { return m_Instance.getRef( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Ref getRef( final String columnLabel ) throws SQLException { return m_Instance.getRef( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getRow() throws SQLException { return m_Instance.getRow(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final RowId getRowId( final int columnIndex ) throws SQLException { return m_Instance.getRowId( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final RowId getRowId( final String columnLabel ) throws SQLException { return m_Instance.getRowId( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final short getShort( final int columnIndex ) throws SQLException { return m_Instance.getShort( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final short getShort( final String columnLabel ) throws SQLException { return m_Instance.getShort( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final SQLXML getSQLXML( final int columnIndex ) throws SQLException { return m_Instance.getSQLXML( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final SQLXML getSQLXML( final String columnLabel ) throws SQLException { return m_Instance.getSQLXML( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Statement getStatement() throws SQLException { return m_Instance.getStatement(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getString( final int columnIndex ) throws SQLException { return m_Instance.getString( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getString( final String columnLabel ) throws SQLException { return m_Instance.getString( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Time getTime( final int columnIndex ) throws SQLException { return m_Instance.getTime( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Time getTime( final int columnIndex, final Calendar cal ) throws SQLException { return m_Instance.getTime( columnIndex, cal ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Time getTime( final String columnLabel ) throws SQLException { return m_Instance.getTime( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Time getTime( final String columnLabel, final Calendar cal ) throws SQLException { return m_Instance.getTime( columnLabel, cal ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Timestamp getTimestamp( final int columnIndex ) throws SQLException { return m_Instance.getTimestamp( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Timestamp getTimestamp( final int columnIndex, final Calendar cal ) throws SQLException { return m_Instance.getTimestamp( columnIndex, cal ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Timestamp getTimestamp( final String columnLabel ) throws SQLException { return m_Instance.getTimestamp( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Timestamp getTimestamp( final String columnLabel, final Calendar cal ) throws SQLException { return m_Instance.getTimestamp( columnLabel, cal ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getType() throws SQLException { return m_Instance.getType(); }

    /**
     *  {@inheritDoc}
     */
    @Deprecated
    @Override
    public final InputStream getUnicodeStream( final int columnIndex ) throws SQLException { return m_Instance.getUnicodeStream( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Deprecated
    @Override
    public final InputStream getUnicodeStream( final String columnLabel ) throws SQLException { return m_Instance.getUnicodeStream( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final URL getURL( final int columnIndex ) throws SQLException { return m_Instance.getURL( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final URL getURL( final String columnLabel ) throws SQLException { return m_Instance.getURL( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final SQLWarning getWarnings() throws SQLException { return m_Instance.getWarnings(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void insertRow() throws SQLException
    {
        throw new UnsupportedOperationException( "insertRow()" );
    }   //  insertRow()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isAfterLast() throws SQLException { return m_Instance.isAfterLast(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isBeforeFirst() throws SQLException { return m_Instance.isBeforeFirst(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isClosed() throws SQLException { return m_Instance.isClosed(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isFirst() throws SQLException { return m_Instance.isFirst(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isLast() throws SQLException { return m_Instance.isLast(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isWrapperFor( final Class<?> iface ) throws SQLException { return m_Instance.isWrapperFor( iface ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean last() throws SQLException
    {
        throw new UnsupportedOperationException( "last()" );
    }   //  last()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void moveToCurrentRow() throws SQLException
    {
        throw new UnsupportedOperationException( "moveToCurrentRow()" );
    }   //  moveToCurrentRow()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void moveToInsertRow() throws SQLException
    {
        throw new UnsupportedOperationException( "moveToInsertRow()" );
    }   //  moveToInsertRow()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean next() throws SQLException
    {
        throw new UnsupportedOperationException( "next()" );
    }   //  next()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean previous() throws SQLException
    {
        throw new UnsupportedOperationException( "previous()" );
    }   //  previous()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void refreshRow() throws SQLException { m_Instance.refreshRow(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean relative( final int rows ) throws SQLException
    {
        throw new UnsupportedOperationException( "relative()" );
    }   //  relative()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean rowDeleted() throws SQLException { return m_Instance.rowDeleted(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean rowInserted() throws SQLException { return m_Instance.rowInserted(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean rowUpdated() throws SQLException { return m_Instance.rowUpdated(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setFetchDirection( final int direction ) throws SQLException
    {
        throw new UnsupportedOperationException( "setFetchDirection()" );
    }   //  setFetchDirection()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setFetchSize( final int rows ) throws SQLException
    {
        throw new UnsupportedOperationException( "setFetchSize" );
    }   //  setFetchSize()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final <T> T unwrap( final Class<T> iface ) throws SQLException { return m_Instance.unwrap( iface ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateArray( final int columnIndex, final Array x ) throws SQLException { m_Instance.updateArray( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateArray( final String columnLabel, final Array x ) throws SQLException { m_Instance.updateArray( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateAsciiStream( final int columnIndex, final InputStream x ) throws SQLException { m_Instance.updateAsciiStream( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateAsciiStream( final int columnIndex, final InputStream x, final int length ) throws SQLException { m_Instance.updateAsciiStream( columnIndex, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateAsciiStream( final int columnIndex, final InputStream x, final long length ) throws SQLException { m_Instance.updateAsciiStream( columnIndex, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateAsciiStream( final String columnLabel, final InputStream x ) throws SQLException { m_Instance.updateAsciiStream( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateAsciiStream( final String columnLabel, final InputStream x, final int length ) throws SQLException { m_Instance.updateAsciiStream( columnLabel, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateAsciiStream( final String columnLabel, final InputStream x, final long length ) throws SQLException { m_Instance.updateAsciiStream( columnLabel, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBigDecimal( final int columnIndex, final BigDecimal x ) throws SQLException { m_Instance.updateBigDecimal( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBigDecimal( final String columnLabel, final BigDecimal x ) throws SQLException { m_Instance.updateBigDecimal( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBinaryStream( final int columnIndex, final InputStream x ) throws SQLException { m_Instance.updateBinaryStream( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBinaryStream( final int columnIndex, final InputStream x, final int length ) throws SQLException { m_Instance.updateBinaryStream( columnIndex, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBinaryStream( final int columnIndex, final InputStream x, final long length ) throws SQLException { m_Instance.updateBinaryStream( columnIndex, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBinaryStream( final String columnLabel, final InputStream x ) throws SQLException { m_Instance.updateBinaryStream( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBinaryStream( final String columnLabel, final InputStream x, final int length ) throws SQLException { m_Instance.updateBinaryStream( columnLabel, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBinaryStream( final String columnLabel, final InputStream x, final long length ) throws SQLException { m_Instance.updateBinaryStream( columnLabel, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBlob( final int columnIndex, final Blob x ) throws SQLException { m_Instance.updateBlob( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBlob( final int columnIndex, final InputStream inputStream ) throws SQLException { m_Instance.updateBlob( columnIndex, inputStream ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBlob( final int columnIndex, final InputStream inputStream, final long length ) throws SQLException { m_Instance.updateBlob( columnIndex, inputStream, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBlob( final String columnLabel, final Blob x ) throws SQLException { m_Instance.updateBlob( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBlob( final String columnLabel, final InputStream inputStream ) throws SQLException { m_Instance.updateBlob( columnLabel, inputStream ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBlob( final String columnLabel, final InputStream inputStream, final long length ) throws SQLException { m_Instance.updateBlob( columnLabel, inputStream, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBoolean( final int columnIndex, final boolean x ) throws SQLException { m_Instance.updateBoolean( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBoolean( final String columnLabel, final boolean x ) throws SQLException { m_Instance.updateBoolean( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateByte( final int columnIndex, final byte x ) throws SQLException { m_Instance.updateByte( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateByte( final String columnLabel, final byte x ) throws SQLException { m_Instance.updateByte( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBytes( final int columnIndex, final byte [] x ) throws SQLException { m_Instance.updateBytes( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateBytes( final String columnLabel, final byte [] x ) throws SQLException { m_Instance.updateBytes( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateCharacterStream( final int columnIndex, final Reader x ) throws SQLException { m_Instance.updateCharacterStream( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateCharacterStream( final int columnIndex, final Reader x, final int length ) throws SQLException { m_Instance.updateCharacterStream( columnIndex, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateCharacterStream( final int columnIndex, final Reader x, final long length ) throws SQLException { m_Instance.updateCharacterStream( columnIndex, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateCharacterStream( final String columnLabel, final Reader reader ) throws SQLException { m_Instance.updateCharacterStream( columnLabel, reader ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateCharacterStream( final String columnLabel, final Reader reader, final int length ) throws SQLException { m_Instance.updateCharacterStream( columnLabel, reader, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateCharacterStream( final String columnLabel, final Reader reader, final long length ) throws SQLException { m_Instance.updateCharacterStream( columnLabel, reader, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateClob( final int columnIndex, final Clob x ) throws SQLException { m_Instance.updateClob( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateClob( final int columnIndex, final Reader reader ) throws SQLException { m_Instance.updateClob( columnIndex, reader ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateClob( final int columnIndex, final Reader reader, final long length ) throws SQLException { m_Instance.updateClob( columnIndex, reader, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateClob( final String columnLabel, final Clob x ) throws SQLException { m_Instance.updateClob( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateClob( final String columnLabel, final Reader reader ) throws SQLException { m_Instance.updateClob( columnLabel, reader ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateClob( final String columnLabel, final Reader reader, final long length ) throws SQLException { m_Instance.updateClob( columnLabel, reader, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateDate( final int columnIndex, final Date x ) throws SQLException { m_Instance.updateDate( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateDate( final String columnLabel, final Date x ) throws SQLException { m_Instance.updateDate( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateDouble( final int columnIndex, final double x ) throws SQLException { m_Instance.updateDouble( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateDouble( final String columnLabel, final double x ) throws SQLException { m_Instance.updateDouble( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateFloat( final int columnIndex, final float x ) throws SQLException { m_Instance.updateFloat( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateFloat( final String columnLabel, final float x ) throws SQLException { m_Instance.updateFloat( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateInt( final int columnIndex, final int x ) throws SQLException { m_Instance.updateInt( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateInt( final String columnLabel, final int x ) throws SQLException { m_Instance.updateInt( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateLong( final int columnIndex, final long x ) throws SQLException { m_Instance.updateLong( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateLong( final String columnLabel, final long x ) throws SQLException { m_Instance.updateLong( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNCharacterStream( final int columnIndex, final Reader x ) throws SQLException { m_Instance.updateNCharacterStream( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNCharacterStream( final int columnIndex, final Reader x, final long length ) throws SQLException { m_Instance.updateNCharacterStream( columnIndex, x, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNCharacterStream( final String columnLabel, final Reader reader ) throws SQLException { m_Instance.updateNCharacterStream( columnLabel, reader ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNCharacterStream( final String columnLabel, final Reader reader, final long length ) throws SQLException { m_Instance.updateNCharacterStream( columnLabel, reader, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNClob( final int columnIndex, final NClob nClob ) throws SQLException { m_Instance.updateNClob( columnIndex, nClob ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNClob( final int columnIndex, final Reader reader ) throws SQLException { m_Instance.updateNClob( columnIndex, reader ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNClob( final int columnIndex, final Reader reader, final long length ) throws SQLException { m_Instance.updateNClob( columnIndex, reader, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNClob( final String columnLabel, final NClob nClob ) throws SQLException { m_Instance.updateNClob( columnLabel, nClob ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNClob( final String columnLabel, final Reader reader ) throws SQLException { m_Instance.updateNClob( columnLabel, reader ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNClob( final String columnLabel, final Reader reader, final long length ) throws SQLException { m_Instance.updateNClob( columnLabel, reader, length ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNString( final int columnIndex, final String nString ) throws SQLException { m_Instance.updateNString( columnIndex, nString ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNString( final String columnLabel, final String nString ) throws SQLException { m_Instance.updateNString( columnLabel, nString ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNull( final int columnIndex ) throws SQLException { m_Instance.updateNull( columnIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateNull( final String columnLabel ) throws SQLException { m_Instance.updateNull( columnLabel ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateObject( final int columnIndex, final Object x ) throws SQLException { m_Instance.updateObject( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateObject( final int columnIndex, final Object x, final int scaleOrLength ) throws SQLException { m_Instance.updateObject( columnIndex, x, scaleOrLength ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateObject( final int columnIndex, final Object x, final SQLType targetSqlType ) throws SQLException { m_Instance.updateObject( columnIndex, x, targetSqlType ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateObject( final int columnIndex, final Object x, final SQLType targetSqlType, final int scaleOrLength ) throws SQLException { m_Instance.updateObject( columnIndex, x, targetSqlType, scaleOrLength ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateObject( final String columnLabel, final Object x ) throws SQLException { m_Instance.updateObject( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateObject( final String columnLabel, final Object x, final int scaleOrLength ) throws SQLException { m_Instance.updateObject( columnLabel, x, scaleOrLength ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateObject( final String columnLabel, final Object x, final SQLType targetSqlType ) throws SQLException { m_Instance.updateObject( columnLabel, x, targetSqlType ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateObject( final String columnLabel, final Object x, final SQLType targetSqlType, final int scaleOrLength ) throws SQLException { m_Instance.updateObject( columnLabel, x, targetSqlType, scaleOrLength ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateRef( final int columnIndex, final Ref x ) throws SQLException { m_Instance.updateRef( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateRef( final String columnLabel, final Ref x ) throws SQLException { m_Instance.updateRef( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateRow() throws SQLException { m_Instance.updateRow(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateRowId( final int columnIndex, final RowId x ) throws SQLException { m_Instance.updateRowId( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateRowId( final String columnLabel, final RowId x ) throws SQLException { m_Instance.updateRowId( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateShort( final int columnIndex, final short x ) throws SQLException { m_Instance.updateShort( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateShort( final String columnLabel, final short x ) throws SQLException { m_Instance.updateShort( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateSQLXML( final int columnIndex, final SQLXML xmlObject ) throws SQLException { m_Instance.updateSQLXML( columnIndex, xmlObject ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateSQLXML( final String columnLabel, final SQLXML xmlObject ) throws SQLException { m_Instance.updateSQLXML( columnLabel, xmlObject ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateString( final int columnIndex, final String x ) throws SQLException { m_Instance.updateString( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateString( final String columnLabel, final String x ) throws SQLException { m_Instance.updateString( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateTime( final int columnIndex, final Time x ) throws SQLException { m_Instance.updateTime( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateTime( final String columnLabel, final Time x ) throws SQLException { m_Instance.updateTime( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateTimestamp( final int columnIndex, final Timestamp x ) throws SQLException { m_Instance.updateTimestamp( columnIndex, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void updateTimestamp( final String columnLabel, final Timestamp x ) throws SQLException { m_Instance.updateTimestamp( columnLabel, x ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean wasNull() throws SQLException { return m_Instance.wasNull(); }
}
//  class ResultSetWrapper

/*
 *  End of File
 */