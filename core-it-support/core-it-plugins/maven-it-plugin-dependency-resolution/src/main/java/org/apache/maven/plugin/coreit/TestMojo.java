package org.apache.maven.plugin.coreit;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Creates text files that list the dependencies with scope test in the order returned from the Maven core.
 * 
 * @goal test
 * @requiresDependencyResolution test
 * 
 * @author Benjamin Bentmann
 * @version $Id$
 */
public class TestMojo
    extends AbstractDependencyMojo
{

    /**
     * The path to the output file for the test artifacts, relative to the project base directory. Each line of this
     * UTF-8 encoded file specifies an artifact identifier. If not specified, the artifact list will not be written to
     * disk.
     * 
     * @parameter
     */
    private String testArtifacts;

    /**
     * The path to the output file for the test class path, relative to the project base directory. Each line of
     * this UTF-8 encoded file specifies the absolute path to a class path element. If not specified, the class path
     * will not be written to disk.
     * 
     * @parameter
     */
    private String testClassPath;

    /**
     * Runs this mojo.
     * 
     * @throws MojoExecutionException If the output file could not be created or any dependency could not be resolved.
     */
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            writeArtifacts( testArtifacts, project.getTestArtifacts() );
            writeClassPath( testClassPath, project.getTestClasspathElements() );
        }
        catch ( DependencyResolutionRequiredException e )
        {
            throw new MojoExecutionException( "Failed to resolve dependencies", e );
        }
    }

}
