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

package org.dcm4chee.archive.query.decorators;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;

import org.dcm4chee.archive.conf.QueryParam;
import org.dcm4chee.archive.query.DerivedSeriesFields;
import org.dcm4chee.archive.query.DerivedStudyFields;
import org.dcm4chee.conf.decorators.DynamicDecoratorWrapper;
import org.dcm4chee.storage.conf.Availability;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import java.util.Date;
import java.util.Set;

@Decorator
public class DerivedSeriesFieldsDynamicDecorator extends DynamicDecoratorWrapper<DerivedSeriesFields> implements DerivedSeriesFields {
	@Inject
	@Delegate
	DerivedSeriesFields delegate;

	@Override
	public void addInstance(Tuple result, QueryParam param) {
		wrapWithDynamicDecorators(delegate).addInstance(result, param);
	}

	@Override
	public Expression<?>[] fields() {
		return wrapWithDynamicDecorators(delegate).fields();
	}

	@Override
	public int getNumberOfInstances() {
		return wrapWithDynamicDecorators(delegate).getNumberOfInstances();
	}

	@Override
	public String[] getRetrieveAETs() {
		return wrapWithDynamicDecorators(delegate).getRetrieveAETs();
	}

	@Override
	public Availability getAvailability() {
		return wrapWithDynamicDecorators(delegate).getAvailability();
	}

	@Override
	public Date getLastUpdateTime() {
		return wrapWithDynamicDecorators(delegate).getLastUpdateTime();
	}

	@Override
	public int getNumberOfVisibleImages() {
		return wrapWithDynamicDecorators(delegate).getNumberOfVisibleImages();
	}
	
	
	@Override
	public void reset() {
		wrapWithDynamicDecorators(delegate).reset();
	}
}
