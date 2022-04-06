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

import java.io.Serial;
import java.io.Serializable;
import java.sql.SQLType;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  <p>{@summary A value for an
 *  {@link org.tquadrat.foundation.sql.EnhancedPreparedStatement }.}
 *
 *  @param  parameterName   The name of the parameter.
 *  @param  type    The SQL type.
 *  @param  value   A String representation of the value; it can be
 *      {@code null} if the value is {@code null}, but it could be also the
 *      value of
 *      {@link org.tquadrat.foundation.lang.CommonConstants#NULL_STRING}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: StatementValue.java 1030 2022-04-06 13:42:02Z tquadrat $
 *  @since 0.0.1
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: StatementValue.java 1030 2022-04-06 13:42:02Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.1" )
public record StatementValue( String parameterName, SQLType type, String value ) implements Serializable
{
        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The serial version UID for objects of this class: {@value}.
     *
     *  @hidden
     */
    @Serial
    private static final long serialVersionUID = 1L;

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        final var retValue = getClass().getName() +
            " [Parameter: " + parameterName +
            ", Type: " + type.getName() +
            ", Value: " + value +
            "]";

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()
}
//  record StatementValue

/*
 *  End of File
 */