/*
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.web.server;

import java.security.Principal;
import java.util.Optional;

import reactor.core.publisher.Mono;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;

/**
 * Default implementation of
 * {@link org.springframework.web.server.ServerWebExchange.MutativeBuilder}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
class DefaultServerWebExchangeMutativeBuilder implements ServerWebExchange.MutativeBuilder {

	private final ServerWebExchange delegate;

	private ServerHttpRequest request;

	private ServerHttpResponse response;

	private Principal user;

	private Mono<WebSession> session;


	public DefaultServerWebExchangeMutativeBuilder(ServerWebExchange delegate) {
		Assert.notNull(delegate, "'delegate' is required.");
		this.delegate = delegate;
	}


	@Override
	public ServerWebExchange.MutativeBuilder setRequest(ServerHttpRequest request) {
		this.request = request;
		return this;
	}

	@Override
	public ServerWebExchange.MutativeBuilder setResponse(ServerHttpResponse response) {
		this.response = response;
		return this;
	}

	@Override
	public ServerWebExchange.MutativeBuilder setPrincipal(Principal user) {
		this.user = user;
		return this;
	}

	@Override
	public ServerWebExchange.MutativeBuilder setSession(Mono<WebSession> session) {
		this.session = session;
		return this;
	}

	@Override
	public ServerWebExchange build() {
		return new MutativeDecorator(this.delegate,
				this.request, this.response, this.user, this.session);
	}


	/**
	 * An immutable wrapper of an exchange returning property overrides -- given
	 * to the constructor -- or original values otherwise.
	 */
	private static class MutativeDecorator extends ServerWebExchangeDecorator {

		private final ServerHttpRequest request;

		private final ServerHttpResponse response;

		private final Principal user;

		private final Mono<WebSession> session;


		public MutativeDecorator(ServerWebExchange delegate,
				ServerHttpRequest request, ServerHttpResponse response, Principal user,
				Mono<WebSession> session) {

			super(delegate);
			this.request = request;
			this.response = response;
			this.user = user;
			this.session = session;
		}


		@Override
		public ServerHttpRequest getRequest() {
			return (this.request != null ? this.request : getDelegate().getRequest());
		}

		@Override
		public ServerHttpResponse getResponse() {
			return (this.response != null ? this.response : getDelegate().getResponse());
		}

		@Override
		public Mono<WebSession> getSession() {
			return (this.session != null ? this.session : getDelegate().getSession());
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Principal> Optional<T> getPrincipal() {
			return (this.user != null ? Optional.of((T) this.user) : getDelegate().getPrincipal());
		}
	}

}

