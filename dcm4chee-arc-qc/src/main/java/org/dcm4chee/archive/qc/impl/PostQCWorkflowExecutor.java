//
/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4chee.archive.qc.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.dcm4chee.archive.dto.QCEventInstance;
import org.dcm4chee.archive.qc.QCEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Considers an on-going JTA transaction to be a QC workflow and binds arbitrary logic to it that
 * is executed before the transaction is committed.
 * 
 * @author Alexander Hoermandinger <alexander.hoermandinger@agfa.com>
 *
 */
@ApplicationScoped
public class PostQCWorkflowExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostQCWorkflowExecutor.class);
    
    private static final ThreadLocal<QCWorkflow> qcWorkflows = new ThreadLocal<QCWorkflow>();
    
    @Resource(lookup="java:jboss/TransactionManager")
    private TransactionManager tmManager; 
    
    @Resource(lookup="java:jboss/TransactionSynchronizationRegistry")
    private TransactionSynchronizationRegistry tsRegistry;
    
    /**
     * Aggregates the result state of a QC operation to the current QC workflow.
     * @param qcEvent Result state of QC operation
     */
    public void aggregateQCWorkflowState(QCEvent qcEvent) {
        QCWorkflow wf = initOrGetWorkflow();
        if (wf != null) {
            // add affected instance UIDs to workflow state
            Collection<QCEventInstance> srcInsts = qcEvent.getSource();
            if(srcInsts != null) {
                wf.addAffectedInstances(srcInsts);
            }
            Collection<QCEventInstance> remoteInsts = qcEvent.getTarget();
            if(remoteInsts != null) {
                wf.addAffectedInstances(remoteInsts);
            }
        }
    }
    
    private QCWorkflow initOrGetWorkflow() {
        try {
            Transaction tx = tmManager.getTransaction();
            if (tx == null) {
                return null;
            }

            QCWorkflow wf = qcWorkflows.get();
            if (wf == null) {
                tsRegistry.registerInterposedSynchronization(new OnWorkflowCommitRunner());
                wf = new QCWorkflow();
                qcWorkflows.set(wf);
            }
            return wf;
        } catch (SystemException e) {
            throw new RuntimeException("Could not register transaction synchronizer for QC workflow");
        } 
    }
    
    /**
     * Contains arbitrary logic to be executed at the end of a QC workflow.
     * @param wf
     */
    protected void runPostQCLogic(QCWorkflow wf) {
        System.out.println("Instances affected by QC workflow: " + wf.getAffectedInstances());
//        throw new RuntimeException("Fail the transaction");
    }
    
    private static class QCWorkflow {
        private final Set<QCEventInstance> affectedInstances = new HashSet<>();
        
        private void addAffectedInstances(Collection<QCEventInstance> affectedInstances) {
            this.affectedInstances.addAll(affectedInstances);
        }
        
        private Set<QCEventInstance> getAffectedInstances() {
            return affectedInstances;
        }
    }
    
    /*
     * Transaction hook that executes logic at the end of a QC 
     * workflow (=transaction commit time)
     */
    private class OnWorkflowCommitRunner implements Synchronization {

        // only called for active (not-rolled-back) transactions
        @Override
        public void beforeCompletion() {
            QCWorkflow wf = qcWorkflows.get();
            if (wf != null) {
                try {
                    runPostQCLogic(wf);
                } catch(Exception e) {
                    LOGGER.error("Error while executing after QC workflows", e);
                    markTransactionAsRollback(getTransaction());
                }
            }
        }
        
        @Override
        public void afterCompletion(int status) {
            // guaranteed clean-up of thread local state
            qcWorkflows.remove();
        }
        
        private Transaction getTransaction() {
            try {
                return tmManager.getTransaction();
            } catch (SystemException e) {
                throw new RuntimeException("Error while accessing transaction", e);
            }
        }
        
        private void markTransactionAsRollback(Transaction tx) {
            try {
                tx.setRollbackOnly();
            } catch (Exception e) {
                throw new RuntimeException("Error while flagging transaction", e);
            }
        }
       
    }

}
