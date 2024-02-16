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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.sql.internal.EnhancedPreparedStatementImpl.convertIndexBufferToParameterIndex;
import static org.tquadrat.foundation.sql.internal.EnhancedPreparedStatementImpl.parseSQL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.BlankArgumentException;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for the methods in
 *  {@link EnhancedPreparedStatementImpl}.
 *
 *  @version $Id: TestEnhancedPreparedStatementImpl.java 1030 2022-04-06 13:42:02Z tquadrat $
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: TestEnhancedPreparedStatementImpl.java 1030 2022-04-06 13:42:02Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.sql.internal.TestEnhancedPreparedStatementImpl" )
public class TestEnhancedPreparedStatementImpl extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/

    /**
     * Asserts that given arguments are equals.
     *
     * @param expected The expected value.
     * @param actual The actual value.
     * @throws AssertionFailedError The arguments are not equals.
     */
    private final void assertMapEquals( final Map<String, int[]> expected, final Map<String, int[]> actual )
    {
        assertEquals( expected.size(), actual.size() );
        assertEquals( expected.keySet(), actual.keySet() );
        for( final var key : expected.keySet() )
        {
            assertTrue( Objects.deepEquals( expected.get( key ), actual.get( key ) ) );
        }
    }   //  assertMapEquals()

    /**
     * Does the same as
     * {@link EnhancedPreparedStatementImpl#convertIndexBufferToParameterIndex(Map)}
     * but using a different approach.
     *
     * @param indexBuffer The index buffer.
     * @return The parameter index.
     */
    private final Map<String, int[]> convertIndexBufferToParameterIndexSimple( final Map<String, ? extends List<Integer>> indexBuffer )
    {
        final Map<String, int[]> retValue = new HashMap<>();
        for( final var entry : indexBuffer.entrySet() )
        {
            retValue.put( entry.getKey(), entry.getValue().stream().mapToInt( Integer::intValue ).toArray() );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  convertIndexBufferToParameterIndexSimple()

    /**
     * Some tests for
     * {@link EnhancedPreparedStatementImpl#convertIndexBufferToParameterIndex(Map)}.
     *
     * @throws Exception Something went unexpectedly wrong.
     */
    @Test
    final void testConvertIndexBufferToParameterIndex() throws Exception
    {
        skipThreadTest();

        assertThrows( NullArgumentException.class, () -> convertIndexBufferToParameterIndex( null ) );

        Map<String, int[]> parameterIndex;
        Map<String, List<Integer>> indexBuffer;

        indexBuffer = Map.of();
        parameterIndex = convertIndexBufferToParameterIndex( indexBuffer );
        assertNotNull( parameterIndex );
        assertTrue( parameterIndex.isEmpty() );

        parameterIndex = convertIndexBufferToParameterIndexSimple( indexBuffer );
        assertNotNull( parameterIndex );
        assertTrue( parameterIndex.isEmpty() );

        indexBuffer = Map.of( "p1", List.of( 1 ) );
        parameterIndex = convertIndexBufferToParameterIndex( indexBuffer );
        assertNotNull( parameterIndex );
        assertFalse( parameterIndex.isEmpty() );

        parameterIndex = convertIndexBufferToParameterIndexSimple( indexBuffer );
        assertNotNull( parameterIndex );
        assertFalse( parameterIndex.isEmpty() );

        assertMapEquals( convertIndexBufferToParameterIndexSimple( indexBuffer ), convertIndexBufferToParameterIndex( indexBuffer ) );

        indexBuffer = Map.of( "p1", List.of( 1, 2, 3 ), "p2", List.of( 4 ), "p3", List.of( 5, 9 ), "p4", List.of( 6, 7, 8 ) );
        assertMapEquals( convertIndexBufferToParameterIndexSimple( indexBuffer ), convertIndexBufferToParameterIndex( indexBuffer ) );
    }   //  testConvertIndexBufferToParameterIndex()

    /**
     * Some tests for
     * {@link EnhancedPreparedStatementImpl#parseSQL(String, Map)}.
     *
     * @throws Exception Something went unexpectedly wrong.
     */
    @Test
    final void testParseSQL() throws Exception
    {
        skipThreadTest();

        final Map<String, List<Integer>> indexBuffer;

        String statement, expected, actual;

        assertThrows( NullArgumentException.class, () -> parseSQL( null, Map.of() ) );
        assertThrows( EmptyArgumentException.class, () -> parseSQL( EMPTY_STRING, Map.of() ) );
        assertThrows( BlankArgumentException.class, () -> parseSQL( " ", Map.of() ) );

        assertThrows( NullArgumentException.class, () -> parseSQL( "SELECT * FROM table", null ) );

        indexBuffer = new HashMap<>();
        statement = "SELECT * FROM table";
        expected = statement;
        actual = parseSQL( statement, indexBuffer );
        assertNotNull( actual );
        assertFalse( actual.isEmpty() );
        assertFalse( actual.isBlank() );
        assertEquals( expected, actual );
        assertTrue( indexBuffer.isEmpty() );

        //noinspection RedundantOperationOnEmptyContainer
        indexBuffer.clear();
        statement = "SELECT * FROM table WHERE key = :key";
        expected = "SELECT * FROM table WHERE key = ?";
        actual = parseSQL( statement, indexBuffer );
        assertNotNull( actual );
        assertFalse( actual.isEmpty() );
        assertFalse( actual.isBlank() );
        assertEquals( expected, actual );
        assertFalse( indexBuffer.isEmpty() );
        assertMapEquals( convertIndexBufferToParameterIndex( Map.of( ":key", List.of( 1) ) ), convertIndexBufferToParameterIndex( indexBuffer ) );

        indexBuffer.clear();
        statement = "SELECT * FROM table WHERE key = :key AND age > :lowerBound AND age < :upperBound";
        expected = "SELECT * FROM table WHERE key = ? AND age > ? AND age < ?";
        actual = parseSQL( statement, indexBuffer );
        assertNotNull( actual );
        assertFalse( actual.isEmpty() );
        assertFalse( actual.isBlank() );
        assertEquals( expected, actual );
        assertFalse( indexBuffer.isEmpty() );
        assertMapEquals( convertIndexBufferToParameterIndex( Map.of( ":key", List.of( 1 ), ":lowerBound", List.of( 2 ), ":upperBound", List.of( 3 ) ) ), convertIndexBufferToParameterIndex( indexBuffer ) );

        indexBuffer.clear();
        statement = "SELECT * FROM table1, table2 WHERE table1.key = :key AND table2.key = :key";
        expected = "SELECT * FROM table1, table2 WHERE table1.key = ? AND table2.key = ?";
        actual = parseSQL( statement, indexBuffer );
        assertNotNull( actual );
        assertFalse( actual.isEmpty() );
        assertFalse( actual.isBlank() );
        assertEquals( expected, actual );
        assertFalse( indexBuffer.isEmpty() );
        assertMapEquals( convertIndexBufferToParameterIndex( Map.of( ":key", List.of( 1, 2 ) ) ), convertIndexBufferToParameterIndex( indexBuffer ) );
    }   //  testParseSQL()
}
//  class TestEnhancedPreparedStatementImpl

/*
 *  End of File
 */