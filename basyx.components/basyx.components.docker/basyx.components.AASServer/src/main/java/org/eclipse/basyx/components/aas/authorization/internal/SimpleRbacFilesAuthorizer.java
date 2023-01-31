/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.aas.authorization.internal;

import java.nio.file.Path;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.SimpleRbacHelper;

/**
 * Simple role based implementation for {@link IFilesAuthorizer}.
 *
 * @author wege
 */
public class SimpleRbacFilesAuthorizer<SubjectInformationType> implements IFilesAuthorizer<SubjectInformationType> {
	protected IRbacRuleChecker rbacRuleChecker;
	protected IRoleAuthenticator<SubjectInformationType> roleAuthenticator;

	public SimpleRbacFilesAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
		this.rbacRuleChecker = rbacRuleChecker;
		this.roleAuthenticator = roleAuthenticator;
	}

	@Override
	public void authorizeDownloadFile(SubjectInformationType subjectInformation, Path path) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, FilesAuthorizerScopes.READ_SCOPE, new PathTargetInformation(path));
	}
}
