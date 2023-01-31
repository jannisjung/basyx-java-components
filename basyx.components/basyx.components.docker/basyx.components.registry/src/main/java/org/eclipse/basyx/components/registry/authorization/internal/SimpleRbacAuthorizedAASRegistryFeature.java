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
package org.eclipse.basyx.components.registry.authorization.internal;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.components.security.authorization.internal.AuthorizationDynamicClassLoader;
import org.eclipse.basyx.extensions.aas.directory.tagged.authorized.internal.SimpleRbacTaggedDirectoryAuthorizer;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.SimpleRbacAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSetDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialization of {@link AuthorizedAASRegistryFeature} for the SimpleRbac
 * authorization scheme.
 *
 * @author wege
 */
public class SimpleRbacAuthorizedAASRegistryFeature<SubjectInformationType> extends AuthorizedAASRegistryFeature {
	private static Logger logger = LoggerFactory.getLogger(SimpleRbacAuthorizedAASRegistryFeature.class);

	public SimpleRbacAuthorizedAASRegistryFeature(final BaSyxSecurityConfiguration securityConfig) {
		super(securityConfig);
	}

	@Override
	public IAASRegistryDecorator getAASRegistryDecorator() {
		logger.info("use SimpleRbac authorization strategy");
		final RbacRuleSet rbacRuleSet = getRbacRuleSet();
		final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(rbacRuleSet);
		final IRoleAuthenticator<SubjectInformationType> roleAuthenticator = getRoleAuthenticator();
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();

		return new AuthorizedAASRegistryDecorator<>(new SimpleRbacAASRegistryAuthorizer<>(rbacRuleChecker, roleAuthenticator), subjectInformationProvider);
	}

	@Override
	public ITaggedDirectoryDecorator getTaggedDirectoryDecorator() {
		logger.info("use SimpleRbac authorization strategy");
		final RbacRuleSet rbacRuleSet = getRbacRuleSet();
		final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(rbacRuleSet);
		final IRoleAuthenticator<SubjectInformationType> roleAuthenticator = getRoleAuthenticator();
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();

		return new AuthorizedTaggedDirectoryDecorator<>(new SimpleRbacTaggedDirectoryAuthorizer<>(rbacRuleChecker, roleAuthenticator), subjectInformationProvider);
	}

	public RbacRuleSet getRbacRuleSet() {
		try {
			return new RbacRuleSetDeserializer().fromFile(securityConfig.getAuthorizationStrategySimpleRbacRulesFilePath());
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private IRoleAuthenticator<SubjectInformationType> getRoleAuthenticator() {
		return AuthorizationDynamicClassLoader.loadInstanceDynamically(securityConfig, BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_ROLE_AUTHENTICATOR, IRoleAuthenticator.class);
	}

	@SuppressWarnings("unchecked")
	private ISubjectInformationProvider<SubjectInformationType> getSubjectInformationProvider() {
		return AuthorizationDynamicClassLoader.loadInstanceDynamically(securityConfig, BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER, ISubjectInformationProvider.class);
	}
}
