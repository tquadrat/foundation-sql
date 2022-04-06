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

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.lang.Status;

/**
 *  An implementation of
 *  {@link ConnectionProvider}
 *  that will just call
 *  {@link DriverManager#getConnection(String,Properties)}
 *  to obtain a
 *  {@link Connection}.
 *
 *  @version $Id: SimpleConnectionProvider.java 1030 2022-04-06 13:42:02Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: SimpleConnectionProvider.java 1030 2022-04-06 13:42:02Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class SimpleConnectionProvider implements ConnectionProvider
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The JDBC URL.
     */
    private final String m_URL;

    /**
     *  The properties.
     */
    private final Properties m_Properties = new Properties();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code SimpleConnectionProvider}.
     *
     *  @param  url The JDBC URL for the database with the calendar.
     *  @param  properties  The properties.
     */
    @SuppressWarnings( "CollectionDeclaredAsConcreteClass" )
    public SimpleConnectionProvider( final String url, final Properties properties )
    {
        m_URL = requireNotEmptyArgument( url, "url" );
        if( nonNull( properties ) && !properties.isEmpty() ) m_Properties.putAll( properties );
    }   //  SimpleConnectionProvider()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/

    /**
     *  {@inheritDoc}
     */
    @Override
    public Status<Connection,Throwable> getConnection()
    {
        Status<Connection,Throwable> retValue;
        try
        {
            @SuppressWarnings( "CallToDriverManagerGetConnection" )
            final var connection = DriverManager.getConnection( m_URL, m_Properties );
            retValue = new Status<>( connection, null );
        }
        catch( final SQLException e )
        {
            retValue = new Status<>( null, e );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getConnection()
}
//  class SimpleConnectionProvider

/*
 *  End of File
 */