package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.Properties;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.shared.utils.Os;

/**
 * This is a test set for <a href="https://issues.apache.org/jira/browse/MNG-6386">MNG-6386</a>.
 */
public class MavenITmng6386BaseUriPropertyTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng6386BaseUriPropertyTest()
    {
        super( "[3.5.4,)" );
    }

    public void testitMNG6386()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-6386" ).getCanonicalFile();

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.setLogFileName( "log-basic.txt" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        Properties props = verifier.loadProperties( "target/profile.properties" );
        String pomProperty = props.getProperty( "project.properties.pomProperty" );
        // set via project
        assertEquals( testDir.toPath().toUri().toASCIIString(), pomProperty );
        // check that baseUri begins with file:///
        assertTrue( pomProperty.startsWith( "file:///" ) );
    }

     public void testitMNG6386UnicodeChars()
        throws Exception
    {
        String fileEncoding = System.getProperty( "file.encoding" );
        /*
         * Unfortunately, AbstractMavenIntegrationTestCase still uses JUnit 3.8 which does not have
         * Assume, so we cannot make assumptions and skip the test on non-compatible systems.
         */
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) ||
             "UTF-8".equalsIgnoreCase( fileEncoding ) || "UTF8".equalsIgnoreCase( fileEncoding ) )
        {
            File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-6386-это по-русский" ).getCanonicalFile();

            Verifier verifier = newVerifier( testDir.getAbsolutePath() );
            verifier.setAutoclean( false );
            verifier.deleteDirectory( "target" );
            verifier.setLogFileName( "log-basic.txt" );
            verifier.executeGoal( "validate" );
            verifier.verifyErrorFreeLog();
            verifier.resetStreams();

            Properties props = verifier.loadProperties( "target/profile.properties" );
            String pomProperty = props.getProperty( "project.properties.pomProperty" );
            // set via project
            assertEquals( testDir.toPath().toUri().toASCIIString(), pomProperty );
            // check that baseUri begins with file:///
            assertTrue( pomProperty.startsWith( "file:///" ) );
            // todo - check why we have different encoding on MacOS of й - %D0%B8%CC%86 vs %D0%B9
            String msg = "Check that baseUri '" + pomProperty + "' ends with 'это по-русский/'";

            if ( Os.isFamily( Os.FAMILY_MAC ))
            {
                assertTrue( msg, pomProperty.endsWith( "%D1%8D%D1%82%D0%BE%20%D0%BF%D0%BE-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B8%CC%86/" ) );
            }
            else
            {
                assertTrue( msg, pomProperty.endsWith( "%D1%8D%D1%82%D0%BE%20%D0%BF%D0%BE-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9/" ) );
            }
        }
        else
        {
            System.out.println();
            System.out.println( "[WARNING] Skipping MNG-6386 Unicode Chars Test on incompatible encoding: " + fileEncoding );
            System.out.println();
        }
    }

}
