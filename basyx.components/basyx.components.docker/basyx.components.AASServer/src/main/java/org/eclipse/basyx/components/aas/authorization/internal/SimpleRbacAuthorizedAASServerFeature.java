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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.authorization.AuthorizedAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.components.security.authorization.internal.AuthorizationDynamicClassLoader;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.internal.SimpleRbacAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.aas.api.authorization.internal.SimpleRbacAASAPIAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSetDeserializer;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.SimpleRbacSubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.internal.SimpleRbacSubmodelAPIAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialization of {@link AuthorizedAASServerFeature} for the SimpleRbac
 * authorization scheme.
 *
 * @author wege
 */
public class SimpleRbacAuthorizedAASServerFeature<SubjectInformationType> extends AuthorizedAASServerFeature<SubjectInformationType> {
	private static Logger logger = LoggerFactory.getLogger(SimpleRbacAuthorizedAASServerFeature.class);

	public SimpleRbacAuthorizedAASServerFeature(final BaSyxSecurityConfiguration securityConfig) {
		super(securityConfig);
	}

	@Override
	public IAASServerDecorator getDecorator() {
		logger.info("use SimpleRbac authorization strategy");
		final RbacRuleSet rbacRuleSet = getRbacRuleSet();
		final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(rbacRuleSet);
		final IRoleAuthenticator<SubjectInformationType> roleAuthenticator = getSimpleRbacRoleAuthenticator();
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSimpleRbacSubjectInformationProvider();

		return new AuthorizedAASServerDecorator<>(new SimpleRbacSubmodelAPIAuthorizer<>(rbacRuleChecker, roleAuthenticator), new SimpleRbacSubmodelAggregatorAuthorizer<>(rbacRuleChecker, roleAuthenticator),
				new SimpleRbacAASAPIAuthorizer<>(rbacRuleChecker, roleAuthenticator), new SimpleRbacAASAggregatorAuthorizer<>(rbacRuleChecker, roleAuthenticator), subjectInformationProvider);
	}

	@Override
	public AuthorizedDefaultServletParams<SubjectInformationType> getFilesAuthorizerParams() {
		logger.info("use SimpleRbac authorization strategy");
		final RbacRuleSet rbacRuleSet = getRbacRuleSet();
		final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(rbacRuleSet);
		final IRoleAuthenticator<SubjectInformationType> roleAuthenticator = getSimpleRbacRoleAuthenticator();
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSimpleRbacSubjectInformationProvider();

		return new AuthorizedDefaultServletParams<>(new SimpleRbacFilesAuthorizer<>(rbacRuleChecker, roleAuthenticator), subjectInformationProvider);
	}

	public RbacRuleSet getRbacRuleSet() {
		try {
			final Consumer<ObjectMapper> mapperAddIn = mapper -> mapper.registerSubtypes(new NamedType(PathTargetInformation.class, "path"));
			return new RbacRuleSetDeserializer(mapperAddIn).fromFile(securityConfig.getAuthorizationStrategySimpleRbacRulesFilePath());
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private IRoleAuthenticator<SubjectInformationType> getSimpleRbacRoleAuthenticator() {
		return AuthorizationDynamicClassLoader.loadInstanceDynamically(securityConfig, BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_ROLE_AUTHENTICATOR, IRoleAuthenticator.class);
	}

	@SuppressWarnings("unchecked")
	private ISubjectInformationProvider<SubjectInformationType> getSimpleRbacSubjectInformationProvider() {
		return AuthorizationDynamicClassLoader.loadInstanceDynamically(securityConfig, BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER, ISubjectInformationProvider.class);
	}
}
