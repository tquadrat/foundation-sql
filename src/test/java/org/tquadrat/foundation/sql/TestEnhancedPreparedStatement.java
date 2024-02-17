/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
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

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.sql.EnhancedPreparedStatement.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.exception.BlankArgumentException;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for
 *  {@link EnhancedPreparedStatement}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@DisplayName( "org.tquadrat.foundation.sql.TestEnhancedPreparedStatement" )
public class TestEnhancedPreparedStatement extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Mocks a JDBC connection.
     *
     *  @return The connection.
     *
     *  @throws SQLException    Something went wrong.
     */
    private final Connection createConnection() throws SQLException
    {
        final Connection retValue = createMock( Connection.class );

        expect( retValue.prepareStatement( anyString() ) )
            .andReturn( createPreparedStatement() )
            .anyTimes();

        retValue.close();
        expectLastCall().anyTimes();

        //---* Replay *--------------------------------------------------------
        replay( retValue );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConnection()

    /**
     *  Mocks a prepared statement.
     *
     *  @return The prepared statement.
     *
     *  @throws SQLException    Something went wrong.
     */
    private final PreparedStatement createPreparedStatement() throws SQLException
    {
        final PreparedStatement retValue = createMock( PreparedStatement.class );

        retValue.setString( anyInt(), anyString() );
        expectLastCall().anyTimes();

        retValue.close();
        expectLastCall().anyTimes();

        //---* Replay *--------------------------------------------------------
        replay( retValue );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createPreparedStatement()

    /**
     *  Test the arguments for the factory method
     *  {@link EnhancedPreparedStatement#prepareStatement(Connection, String)}.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testPrepareStatement_Arguments() throws Exception
    {
        skipThreadTest();
        @SuppressWarnings( "resource" )
        final var connection = createConnection();
        final var selectSQL = """
            SELECT *
                FROM table
                WHERE table.key = :key
            """;

        //noinspection resource
        assertThrows( NullArgumentException.class, () -> prepareStatement( null, selectSQL ) );
        assertThrows( NullArgumentException.class, () -> prepareStatement( connection, null ) );
        assertThrows( EmptyArgumentException.class, () -> prepareStatement( connection, EMPTY_STRING ) );
        assertThrows( BlankArgumentException.class, () -> prepareStatement( connection, " " ) );
    }   //  testPrepareStatement_Arguments()

    /**
     *  Test with a SELECT statement that has no argument.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testSELECTNoArg() throws Exception
    {
        skipThreadTest();

        final var key = "key";
        final var selectSQL = """
            SELECT *
                FROM table
            """;

        try( final var connection = createConnection() )
        {
            //noinspection NestedTryStatement
            try( final var candidate = prepareStatement( connection, selectSQL ) )
            {
                final var e = assertThrows( SQLException.class, () -> candidate.setString( ":key", key ) );
                assertEquals( "Parameter name ':key' unknown", e.getMessage() );
            }
        }
    }   //  testSELECTNoArg()

    /**
     *  Test with a SELECT statement that has one argument.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testSELECTOneArg() throws Exception
    {
        skipThreadTest();

        final var key = "key";
        final var selectSQL = """
            SELECT *
                FROM table
                WHERE table.key = :key
            """;

        try( final var connection = createConnection() )
        {
            //noinspection NestedTryStatement
            try( final var candidate = prepareStatement( connection, selectSQL ) )
            {
                assertThrows( NullArgumentException.class, () -> candidate.setString( null, key ) );
                assertThrows( EmptyArgumentException.class, () -> candidate.setString( EMPTY_STRING, key ) );
                assertThrows( BlankArgumentException.class, () -> candidate.setString( " ", key ) );

                final var e = assertThrows( SQLException.class, () -> candidate.setString( ":value", key ) );
                assertEquals( "Parameter name ':value' unknown", e.getMessage() );
                candidate.setString( ":key", key );
            }
        }
    }   //  testSELECTOneArg()
}
//  class TestEnhancedPreparedStatement

/*
 *  End of File
 */