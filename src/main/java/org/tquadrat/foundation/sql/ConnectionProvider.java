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

import static org.apiguardian.api.API.Status.STABLE;

import java.sql.Connection;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.lang.Status;

/**
 *  <p>{@summary Returns a database connection.}</p>
 *  <p>Implementations of this interface can be used when different sources for
 *  a database connection are possible for an instance that needs one.</p>
 *  <p>This is a functional interface whose functional method is
 *  {@link #getConnection()}</p>
 *
 *  @version $Id: ConnectionProvider.java 1024 2022-03-10 09:57:43Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@FunctionalInterface
@ClassVersion( sourceVersion = "$Id: ConnectionProvider.java 1024 2022-03-10 09:57:43Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public interface ConnectionProvider
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary Returns a valid database connection.}</p>
     *  <p>In case of an error, the method will set the
     *  {@link Status#errorCode() errorCode}
     *  attribute of the returned
     *  {@link Status}
     *  object.</p>
     *
     *  @note   The implementations of this method will never throw an
     *      exception.
     *
     *  @return An instance of
     *      {@link org.tquadrat.foundation.lang.Status}
     *      that holds the retrieved connection.
     */
    public Status<Connection,Throwable> getConnection();
}
//  interface ConnectionProvider

/*
 *  End of File
 */