package com.springboot.inbox.filter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.springboot.inbox.domain.AuthClaims;
import com.springboot.inbox.domain.ErrorResponse;
import com.springboot.inbox.helper.InboxAppHelper;
import com.springboot.inbox.utils.InboxAppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private InboxAppHelper inboxAppHelper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		logger.info("Inside JwtAuthenticationFilter doFilterInternal method.");
		String requestUri = request.getRequestURI();
		String requestMethod = request.getMethod();
		String authToken = request.getHeader(InboxAppConstants.AUTHORIZATION);
		logger.info("Request URI : [{}]", requestUri);

		try {

			if(InboxAppConstants.OPTIONS.equalsIgnoreCase(requestMethod)){
				logger.info("Allowing for options method.");
				filterChain.doFilter(request, response);
				return;
			}
			if (requestUri.contains(InboxAppConstants.ACTUATOR_URI)) {
				logger.info("Allowing for actuator endpoints.");
				filterChain.doFilter(request, response);
			} else {
				logger.info("Validating token for other endpoints.");

				if (!StringUtils.hasLength(authToken)) {
					logger.error("Authorization token not present in header.");
					this.sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}

				if (!authToken.startsWith(InboxAppConstants.BEARER)) {
					logger.error("Invalid authorization token.");
					this.sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}

				String tokenValue = authToken.split(InboxAppConstants.SPACE_DELIMITER)[1];
				if (!StringUtils.hasLength(tokenValue)) {
					logger.error("Authorization token not present.");
					this.sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
				
				AuthClaims authClaims = inboxAppHelper.validateTokenAndReturnClaims(authToken);
				List<String> authList = authClaims.getAuthorities();
				if (!CollectionUtils.isEmpty(authList) && authList.contains("TodoAdmin")) {
					filterChain.doFilter(request, response);
					return;
				}
				this.sendErrorResponse(request, response, HttpServletResponse.SC_FORBIDDEN);
			}
		} catch (Exception e) {
			this.sendErrorResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, int statusCode)
			throws IOException {
		logger.info("Inside JwtAuthenticationFilter sendErrorResponse method.");
		response.setContentType(InboxAppConstants.CONTENT_TYPE_JSON);
		response.setStatus(statusCode);
		response.getWriter().write(this.getErrorResponse(request, statusCode));
	}

	private String getErrorResponse(HttpServletRequest request, int statusCode) {
		logger.info("Inside JwtAuthenticationFilter getErrorResponse method.");
		try {
			String errorMessage = InboxAppConstants.JWT_TOKEN_VALIDATION_FAILURE_MSG;
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorMessage(errorMessage);
			errorResponse.setErrorCode(String.valueOf(statusCode));
			errorResponse.setDebugMessage(errorMessage);
			errorResponse.setPath(request.getRequestURI());
			errorResponse.setTimestamp(Instant.now().toEpochMilli());

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

			return objectMapper.writeValueAsString(errorResponse);
		} catch (JsonProcessingException e) {
			logger.error("Error occurred in JwtAuthenticationFilter getErrorResponse method.", e);
		}
		return null;
	}
}
