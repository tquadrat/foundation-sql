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

import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Collection;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  <p>{@summary An instance of an implementation of this interface can be used
 *  to get information about the types and properties for each named parameter
 *  in a
 *  {@link EnhancedPreparedStatement}
 *  instance.} For some queries and driver implementations, the data that would
 *  be returned by a
 *  {@link ParameterMetaData}
 *  object may not be available until the {@code EnhancedPreparedStatement} has
 *  been executed.</p>
 *  <p>Each named parameter for an {@code EnhancedPreparedStatement} can be
 *  applied to the underlying
 *  {@link java.sql.PreparedStatement}
 *  multiple times, for multiple parameter markers. The assumption is that the
 *  features are the same for all occurrences. Only for the nullability, all
 *  occurrences
 *  {@linkplain #isNullable(String) will be checked}.</p>
 *
 *  @version $Id: ParameterMetaData.java 1020 2022-02-27 21:26:03Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 *
 *  @see java.sql.ParameterMetaData
 */
@ClassVersion( sourceVersion = "$Id: ParameterMetaData.java 1020 2022-02-27 21:26:03Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public sealed interface ParameterMetaData
    permits org.tquadrat.foundation.sql.internal.ParameterMetaDataBase
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Retrieves the fully-qualified name of the Java class whose instances
     *  should be passed to the method
     *  {@link EnhancedPreparedStatement#setObject(String, Object)}.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return The fully-qualified name of the class in the Java programming
     *      language that would be used by the method
     *      {@code EnhancedPreparedStatement.setObject()} to set the value in
     *      the specified parameter. This is the class name used for custom
     *      mapping.
     *  @throws SQLException    A database access error occurred.
     */
    public String getParameterClassName( final String parameterName ) throws SQLException;

    /**
     *  Retrieves the number of parameters in the
     *  {@link EnhancedPreparedStatement}
     *  object for which this {@code ParameterMetaData} object contains
     *  information.
     *
     *  @return The number of parameters.
     *  @throws SQLException    A database access error occurred.
     */
    public int getParameterCount() throws SQLException;

    /**
     *  Retrieves the parameter indexes for the given parameter name.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return The parameter indexes for this parameter name.
     *  @throws SQLException    The given parameter name is not defined.
     */
    public int [] getParameterIndexes( final String parameterName ) throws SQLException;

    /**
     *  Retrieves the names of the parameters in the
     *  {@link EnhancedPreparedStatement}
     *  object for which this {@code ParameterMetaData} object contains
     *  information.
     *
     *  @return The names of the parameters.
     */
    public Collection<String> getParameterNames();

    /**
     *  <p>{@summary Retrieves the designated parameter's SQL type.}</p>
     *  <p>Different from
     *  {@link java.sql.ParameterMetaData#getParameterType(int)}
     *  will this method return an instance of
     *  {@link java.sql.SQLType}
     *  for the type, and not an integer.</p>
     *  <p>If the numerical value (as defined in
     *  {@link java.sql.Types})
     *  is required, it can be obtained like this:</p>
     *  <pre><code>  …
     *  int sqlType = getParameterType( param ).getVendorTypeNumber().intValue();
     *  …</code></pre>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return The SQL type.
     *  @throws SQLException    A database access error occurred.
     *
     *  @see    java.sql.JDBCType
     */
    public SQLType getParameterType( final String parameterName ) throws SQLException;

    /**
     *  Retrieves the designated parameter's database-specific type name.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return The name of the type used by the database. If the parameter
     *      type is a user-defined type, then a fully-qualified type name is
     *      returned.
     *  @throws SQLException    A database access error occurred.
     */
    public String getParameterTypeName( final String parameterName ) throws SQLException;

    /**
     *  <p>{@summary Retrieves the designated parameter's specified column
     *  size.}</p>
     *  <p>The returned value represents the maximum column size for the given
     *  parameter. For numeric data, this is the maximum precision. For
     *  character data, this is the length in characters. For datetime
     *  datatypes, this is the length in characters of the String
     *  representation (assuming the maximum allowed precision of the
     *  fractional seconds component). For binary data, this is the length in
     *  bytes. For the ROWID datatype, this is the length in bytes. 0 is
     *  returned for data types where the column size is not applicable.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return The precision.
     *  @throws SQLException    A database access error occurred.
     */
    public int getPrecision( final String parameterName ) throws SQLException;

    /**
     *  <p>{@summary Retrieves the designated parameter's number of digits to
     *  right of the decimal point.}</p>
     *  <p>0 is returned for data types where the scale is not applicable.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return The scale.
     *  @throws SQLException    A database access error occurred.
     */
    public int getScale( final String parameterName ) throws SQLException;

    /**
     *  <p>{@summary Retrieves whether {@code NULL} values are allowed in the designated
     *  parameter.}</p>
     *  <p>If the parameter with the the given name is used multiple times in
     *  the underlying
     *  {@link java.sql.PreparedStatement},
     *  this method returns
     *  {@value java.sql.ParameterMetaData#parameterNullableUnknown}
     *  when the nullability status is not the same for all uses.</p>
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return The nullability status of the given parameter; one of
     *      {@link java.sql.ParameterMetaData#parameterNoNulls},
     *      {@link java.sql.ParameterMetaData#parameterNullable},
     *      or
     *      {@link java.sql.ParameterMetaData#parameterNullableUnknown}.
     *  @throws SQLException    A database access error occurs
     */
    @SuppressWarnings( "NonBooleanMethodNameMayNotStartWithQuestion" )
    public int isNullable( final String parameterName ) throws SQLException;

    /**
     *  Retrieves whether values for the designated parameter can be signed
     *  numbers.
     *
     *  @param  parameterName   The name of the parameter, prefixed by a colon.
     *  @return {@code true} if a value can be a signed number, {@code false}
     *      otherwise.
     *  @throws SQLException    A database access error occurred.
     */
    public boolean isSigned( final String parameterName ) throws SQLException;
}
//  interface ParameterMetaData

/*
 *  End of File
 */