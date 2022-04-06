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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  An implementation of
 *  {@link Spliterator}
 *  for instances of
 *  {@link ResultSet}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ResultSetSpliterator.java 1030 2022-04-06 13:42:02Z tquadrat $
 *  @since 0.0.1
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ResultSetSpliterator.java 1030 2022-04-06 13:42:02Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.1" )
public final class ResultSetSpliterator implements Spliterator<ResultSet>
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The wrapper for the result set that disallows several methods to be
     *  called.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: ResultSetSpliterator.java 1030 2022-04-06 13:42:02Z tquadrat $
     *  @since 0.0.1
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: ResultSetSpliterator.java 1030 2022-04-06 13:42:02Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.1" )
    private class InternalResultSet extends ResultSetWrapper
    {
            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code InternalResultSet} instance.
         */
        public InternalResultSet() { super( m_ResultSet ); }
    }
    //  class InternalResultSet

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The last exception that was caught on a call to
     *  {@link ResultSet#next()}.
     */
    private SQLException m_LastError = null;

    /**
     *  The result set.
     */
    private final ResultSet m_ResultSet;

    /**
     *  The wrapped result set.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final InternalResultSet m_WrappedResultSet;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ResultSetSpliterator} instance.
     *
     *  @param  resultSet   The result set.
     */
    public ResultSetSpliterator( final ResultSet resultSet )
    {
        m_ResultSet = requireNonNullArgument( resultSet, "resultSet" );
        m_WrappedResultSet = new InternalResultSet();
    }   //  ResultSetSpliterator()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "ConstantExpression" )
    @Override
    public final int characteristics() { return IMMUTABLE | NONNULL; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final long estimateSize() { return Long.MAX_VALUE; }

    /**
     *  Returns the last error that was caught on a call to
     *  {@link ResultSet#next()}.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the exception; will be
     *      {@linkplain Optional#empty() empty}
     *      if there had not been an error so far.
     */
    public final Optional<SQLException> getLastError() { return Optional.ofNullable( m_LastError ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean tryAdvance( final Consumer<? super ResultSet> action )
    {
        boolean retValue;
        try
        {
            retValue = m_ResultSet.next();
        }
        catch( final SQLException e )
        {
            m_LastError = e;
            retValue = false;
        }
        if( retValue ) action.accept( m_WrappedResultSet );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  tryAdvance()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Spliterator<ResultSet> trySplit() { return null; }
}
//  class ResultSetSpliterator

/*
 *  End of File
 */