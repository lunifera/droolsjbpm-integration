/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.spring.persistence;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.jpa.JpaPersistenceContext;
import org.jbpm.persistence.JpaProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContextManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class KieSpringJpaManager extends AbstractKieSpringJpaManager
        implements
        ProcessPersistenceContextManager {


    public KieSpringJpaManager(Environment env) {
        super(env);

        getApplicationScopedPersistenceContext(); // we create this on initialisation so that we own the EMF reference
        // otherwise Spring will close it after the transaction finishes
    }

    public PersistenceContext getApplicationScopedPersistenceContext() {

        return new JpaPersistenceContext(getApplicationScopedEntityManager(), isJTA, (TransactionManager)this.env.get(EnvironmentName.TRANSACTION_MANAGER));
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return new JpaPersistenceContext((EntityManager) this.env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER), isJTA, (TransactionManager)this.env.get(EnvironmentName.TRANSACTION_MANAGER));
    }

    public void beginCommandScopedEntityManager() {
        EntityManager cmdScopedEntityManager = getCommandScopedEntityManager();

        if (isJTA) {
            this.getCommandScopedPersistenceContext().joinTransaction();
            this.appScopedEntityManager.joinTransaction();
        }
    }

    public ProcessPersistenceContext getProcessPersistenceContext() {
        return new JpaProcessPersistenceContext((EntityManager) this.env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER), (TransactionManager)this.env.get(EnvironmentName.TRANSACTION_MANAGER));
    }

    public void endCommandScopedEntityManager() {
        if (TransactionSynchronizationManager.hasResource("cmdEM")) {
            // Code formerly in the clearPersistenceContext method.
            EntityManager cmdScopedEntityManager = (EntityManager) this.env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
            if (cmdScopedEntityManager != null) {
                cmdScopedEntityManager.clear();
            }

            TransactionSynchronizationManager.unbindResource("cmdEM");
            if (this.env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER) != null) {
                getCommandScopedPersistenceContext().close();
            }
        }

        if (TransactionSynchronizationManager.hasResource(KieSpringTransactionManager.RESOURCE_CONTAINER)) {
            TransactionSynchronizationManager.unbindResource(KieSpringTransactionManager.RESOURCE_CONTAINER);
        }
    }

}
