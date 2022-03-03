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

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  A variable for an
 *  {@link org.tquadrat.foundation.sql.EnhancedPreparedStatement EnhancedPreparedStatement}.
 *
 *  @param  name    The name of the variable.
 *  @param  position    The position for this variable in the
 *      {@link java.sql.PreparedStatement}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: StatementVariable.java 1020 2022-02-27 21:26:03Z tquadrat $
 *  @since 0.0.1
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: StatementVariable.java 1020 2022-02-27 21:26:03Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.1" )
public record StatementVariable( String name, int position) implements Serializable
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
            " [Name: " + name +
            ", Position: " + position +
            "]";

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()
}
//  record StatementVariable

/*
 *  End of File
 */