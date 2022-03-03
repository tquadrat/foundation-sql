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

package org.tquadrat.foundation.sql;

import static java.lang.String.join;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_CHARSEQUENCE;
import static org.tquadrat.foundation.sql.DatabaseUtils.parseSQLScript;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for
 *  {@link DatabaseUtils}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 */
@SuppressWarnings( {"GrazieInspection", "SpellCheckingInspection"} )
@ClassVersion( sourceVersion = "$Id: TestDatabaseUtils.java 1020 2022-02-27 21:26:03Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.sql.TestDatabaseUtils" )
public class TestDatabaseUtils extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Some test for
     *  {@link DatabaseUtils#parseSQLScript(CharSequence)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testParseScriptWithNullArgument() throws Exception
    {
        skipThreadTest();

        assertThrows( NullArgumentException.class, () -> parseSQLScript( null ) );
    }   //  testParseScriptWithNullArgument()

    /**
     *  Some test for
     *  {@link DatabaseUtils#parseSQLScript(CharSequence)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testParseScript() throws Exception
    {
        skipThreadTest();

        CharSequence candidate;
        String expected;
        List<String> result;

        candidate = EMPTY_CHARSEQUENCE;
        result = parseSQLScript( candidate );
        assertNotNull( result );
        assertTrue( result.isEmpty() );

        candidate =
            """
            /*
             * ============================================================================
             * Copyright © 2002-2020 by Thomas Thrien.
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
             *
             * Version $Id: TestDatabaseUtils.java 1020 2022-02-27 21:26:03Z tquadrat $
             */
            \s
            -- Table: public."Country_Names"

            -- DROP TABLE public."Country_Names";

            CREATE TABLE public."Country_Names"
            (
                "countrycode" character(2) COLLATE pg_catalog."default" NOT NULL,
                "locale" character varying COLLATE pg_catalog."default" NOT NULL,
                "publicname" character varying COLLATE pg_catalog."default" NOT NULL,
                "officialname" character varying COLLATE pg_catalog."default",
                CONSTRAINT "Country_Names_pkey" PRIMARY KEY ("countrycode", locale)
                    USING INDEX TABLESPACE data
            )

            TABLESPACE data;
            """;
        expected =
            """
            CREATE TABLE public."Country_Names" \
            ( \
            "countrycode" character(2) COLLATE pg_catalog."default" NOT NULL, \
            "locale" character varying COLLATE pg_catalog."default" NOT NULL, \
            "publicname" character varying COLLATE pg_catalog."default" NOT NULL, \
            "officialname" character varying COLLATE pg_catalog."default", \
            CONSTRAINT "Country_Names_pkey" PRIMARY KEY ("countrycode", locale) \
            USING INDEX TABLESPACE data \
            ) \
            TABLESPACE data;\
            """;
        result = parseSQLScript( candidate );
        assertNotNull( result );
        assertEquals( expected, join( "\n", result.toArray( String []::new ) ) );

        candidate =
            """
            /*
             * ============================================================================
             * Copyright © 2002-2020 by Thomas Thrien.
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
             *
             * Version $Id: TestDatabaseUtils.java 1020 2022-02-27 21:26:03Z tquadrat $
             */
            
            -- Table: public."Country_Names"

            -- DROP TABLE public."Country_Names";

            CREATE TABLE public."Country_Names"
            (
                "countrycode" character(2) COLLATE pg_catalog."default" NOT NULL,
                "locale" character varying COLLATE pg_catalog."default" NOT NULL,
                "publicname" character varying COLLATE pg_catalog."default" NOT NULL,
                "officialname" character varying COLLATE pg_catalog."default",
                CONSTRAINT "Country_Names_pkey" PRIMARY KEY ("countrycode", locale)
                    USING INDEX TABLESPACE data
            )

            TABLESPACE data;

            ALTER TABLE public."Country_Names"
                OWNER to tquadrat;
            COMMENT ON TABLE public."Country_Names"
                IS 'The names of countries in different languages';

            COMMENT ON COLUMN public."Country_Names"."countryCode"
                IS 'This ISO 2-letter code that identifies the country.';

            COMMENT ON COLUMN public."Country_Names".locale
                IS 'The language code for the name of the country; usually, this is the two letter code according to ISO 639-1. But in cases where there are different names for a country in different countries (that affects mainly the official names), the country code for that country will be appended with an underscore: de for "Deutsch", de_DE for "Deutsch in Deutschland", en for English, en_UK for British English.';

            COMMENT ON COLUMN public."Country_Names".publicname
                IS 'The public name for the country in the given language.';

            COMMENT ON COLUMN public."Country_Names".officialname
                IS 'The official name for the country according to the given locale code. May be the same as the public name.
            Will be NULL if there are variants for different locales of the same language.';
            """;
        expected =
            """
            CREATE TABLE public."Country_Names" \
            ( \
            "countrycode" character(2) COLLATE pg_catalog."default" NOT NULL, \
            "locale" character varying COLLATE pg_catalog."default" NOT NULL, \
            "publicname" character varying COLLATE pg_catalog."default" NOT NULL, \
            "officialname" character varying COLLATE pg_catalog."default", \
            CONSTRAINT "Country_Names_pkey" PRIMARY KEY ("countrycode", locale) \
            USING INDEX TABLESPACE data \
            ) \
            TABLESPACE data;
            ALTER TABLE public."Country_Names" OWNER to tquadrat;
            COMMENT ON TABLE public."Country_Names" IS 'The names of countries in different languages';
            COMMENT ON COLUMN public."Country_Names"."countryCode" IS 'This ISO 2-letter code that identifies the country.';
            COMMENT ON COLUMN public."Country_Names".locale IS 'The language code for the name of the country; usually, this is the two letter code according to ISO 639-1. But in cases where there are different names for a country in different countries (that affects mainly the official names), the country code for that country will be appended with an underscore: de for "Deutsch", de_DE for "Deutsch in Deutschland", en for English, en_UK for British English.';
            COMMENT ON COLUMN public."Country_Names".publicname IS 'The public name for the country in the given language.';
            COMMENT ON COLUMN public."Country_Names".officialname IS 'The official name for the country according to the given locale code. May be the same as the public name. Will be NULL if there are variants for different locales of the same language.';""";
        result = parseSQLScript( candidate );
        assertNotNull( result );
        assertEquals( expected, join( "\n", result ) );
    }   //  testParseScript()

    /**
     *  Validates whether the class is static.
     */
    @Test
    final void validateClass()
    {
        assertTrue( validateAsStaticClass( DatabaseUtils.class ) );
    }   //  validateClass()
}
//  class TestDatabaseUtils

/*
 *  End of File
 */