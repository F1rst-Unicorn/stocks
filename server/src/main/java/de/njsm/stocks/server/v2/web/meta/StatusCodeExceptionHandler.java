package de.njsm.stocks.server.v2.web.meta;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StatusCodeExceptionHandler {

	private static final Logger LOG = LogManager.getLogger(StatusCodeExceptionHandler.class);

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<Response> handleException(Throwable throwable) {
		if (inputInstantiationFailed(throwable)) {
			LOG.debug("Caught exception leaving web app", throwable);
			LOG.info("invalid input: " + throwable.getCause().getMessage());
			return setErrorStatus(StatusCode.INVALID_ARGUMENT);
		} else {
			LOG.error("Caught exception leaving web app", throwable);
			return setErrorStatus(StatusCode.GENERAL_ERROR);
		}
	}

	private boolean inputInstantiationFailed(Throwable throwable) {
		return throwable.getCause() instanceof IllegalStateException ||
			throwable.getCause() instanceof JsonProcessingException;
	}

	private ResponseEntity<Response> setErrorStatus(StatusCode invalidArgument) {
		return ResponseEntity.status(invalidArgument.toHttpStatus().getStatusCode())
			.body(new Response(invalidArgument));
	}
}
