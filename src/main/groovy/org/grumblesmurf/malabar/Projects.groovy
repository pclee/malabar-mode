/**
 * Copyright (c) 2009 Espen Wiborg <espenhw@grumblesmurf.org>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */ 
package org.grumblesmurf.malabar;

import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.execution.*;

class Projects
{
    static def projects = [:];

    static Project getAt(pom) {
        Project p = projects[pom]
        File pomFile = pom as File
        if (p && p.modStamp >= pomFile.lastModified()) {
            return p
        }

        MvnServer mvnServer = GroovyServer.mvnServer;
        MavenEmbedder embedder = mvnServer.embedder
        MavenExecutionRequest req = mvnServer.newRequest()
        req.baseDirectory = pomFile.parentFile
        MavenExecutionResult result = embedder.readProjectWithDependencies(req)
        if (result.hasExceptions()) {
            // handle exceptions
            println '(error "%s" "' + result.exceptions + '")'
            return null;
        } else if (result.artifactResolutionResult.missingArtifacts) {
            println '(error "Missing artifacts: %s" "' + result.artifactResolutionResult.missingArtifacts + '")'
            return null;
        } else {
            Project me = new Project(pom, result, mvnServer);
            projects[pom] = me
            return me;
        }
    }
}
