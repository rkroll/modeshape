/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.modeshape.jcr;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.graph.connector.inmemory.InMemoryRepositorySource;
import org.modeshape.jcr.JcrRepository.Option;

/**
 * A simple test that shows how to configure ModeShape to use anonymous access.
 */
public class AnonymousAccessTest {

    private JcrEngine engine;
    private Repository repository;
    private Session session;

    @Before
    public void beforeEach() throws Exception {
        JcrConfiguration config = new JcrConfiguration();
        config.repositorySource("source").usingClass(InMemoryRepositorySource.class);
        config.repository("repo")
              .setSource("source")
              .setOption(Option.ANONYMOUS_USER_ROLES, ModeShapeRoles.ADMIN)
              .setOption(Option.JAAS_LOGIN_CONFIG_NAME, "modeshape-jcr-non-existant"); // ensure no use of JAAS
        engine = config.build();
        engine.start();

        repository = engine.getRepository("repo");
    }

    @After
    public void afterEach() throws Exception {
        if (session != null) {
            try {
                session.logout();
            } finally {
                session = null;
            }
        }
        repository = null;
        try {
            engine.shutdown();
        } finally {
            engine = null;
        }
    }

    @Test
    public void shouldLogInAsAnonymousUsingNoCredentials() throws RepositoryException {
        session = repository.login();
        session.getRootNode().getPath();
        session.getRootNode().addNode("someNewNode");
    }
}
