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

package org.tquadrat.foundation.sql.internal;

import static java.sql.ParameterMetaData.parameterNullableUnknown;
import static org.apiguardian.api.API.Status.INTERNAL;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Collection;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.sql.ParameterMetaData;

/**
 *  The base class for a variant of
 *  {@link java.sql.ParameterMetaData}
 *  that is used by
 *  {@link org.tquadrat.foundation.sql.EnhancedPreparedStatement}.
 *
 *  @version $Id: ParameterMetaDataBase.java 1020 2022-02-27 21:26:03Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: ParameterMetaDataBase.java 1020 2022-02-27 21:26:03Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public abstract sealed class ParameterMetaDataBase implements ParameterMetaData
    permits EnhancedPreparedStatementBase.ParameterMetaDataImpl
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The wrapped metadata instance.
     */
    private final java.sql.ParameterMetaData m_MetaData;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code ParameterMetaDataBase}.
     *
     *  @param metaData The wrapped metadata instance.
     */
    protected ParameterMetaDataBase( final java.sql.ParameterMetaData metaData )
    {
        m_MetaData = metaData;
    }   //  ParameterMetaDataBase()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getParameterClassName( final String parameterName ) throws SQLException
    {
        final var indexes = getParameterIndexes( parameterName );
        final var retValue = m_MetaData.getParameterClassName( indexes [0] );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getParameterClassName()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getParameterCount() throws SQLException { return getParameterNames().size(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public abstract int[] getParameterIndexes( final String parameterName ) throws SQLException;

    /**
     *  {@inheritDoc}
     */
    @Override
    public abstract Collection<String> getParameterNames();

    /**
     *  {@inheritDoc}
     */
    @Override
    public final SQLType getParameterType( final String parameterName ) throws SQLException
    {
        final var indexes = getParameterIndexes( parameterName );
        final var retValue = JDBCType.valueOf( m_MetaData.getParameterType( indexes [0] ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getParameterType()

    /**
     *  {@inheritDoc}
     */
    @Override
    public String getParameterTypeName( final String parameterName ) throws SQLException
    {
        final var indexes = getParameterIndexes( parameterName );
        final var retValue = m_MetaData.getParameterTypeName( indexes [0] );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getParameterTypeName()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getPrecision( final String parameterName ) throws SQLException
    {
        final var indexes = getParameterIndexes( parameterName );
        final var retValue = m_MetaData.getPrecision( indexes [0] );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getPrecision()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int getScale( final String parameterName ) throws SQLException
    {
        final var indexes = getParameterIndexes( parameterName );
        final var retValue = m_MetaData.getScale( indexes [0] );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getScale()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int isNullable( final String parameterName ) throws SQLException
    {
        final var indexes = getParameterIndexes( parameterName );
        var retValue = m_MetaData.isNullable( indexes [0] );
        for( var i = 1; (retValue != parameterNullableUnknown) && (i < indexes.length); ++i )
        {
            if( retValue != m_MetaData.isNullable( indexes [i] ) ) retValue = parameterNullableUnknown;
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isNullable()

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean isSigned( final String parameterName ) throws SQLException
    {
        final var indexes = getParameterIndexes( parameterName );
        final var retValue = m_MetaData.isSigned( indexes [0] );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isSigned()
}
//  class ParameterMetaDataBase

/*
 *  End of File
 */