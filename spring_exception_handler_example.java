@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class RestExceptionHandler {

	@Value("${exceptionHandler.showStackTrace}")
	private boolean showStackTrace;

	@ExceptionHandler(Exception.class)
	private ResponseEntity<ErrorResponse> handler(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse("commonError", "commonMessageError");
		if (showStackTrace) {
			errorResponse.setCause(Arrays.toString(ex.getStackTrace()));
		}

		HttpStatus status = INTERNAL_SERVER_ERROR;
		if (ex instanceof MethodArgumentNotValidException) {
			errorResponse.setError("Validation error");
			errorResponse.setMessage(parseFieldErrors((MethodArgumentNotValidException) ex));
			logErrorResponse(errorResponse);
		} else if (ex instanceof ConstraintViolationException) {
			errorResponse.setError("Validation error");
			errorResponse.setMessage(parseConstraintViolations((ConstraintViolationException) ex));
			logErrorResponse(errorResponse);
		} else if (ex instanceof MissingServletRequestParameterException
				|| ex instanceof HttpMessageNotReadableException
				|| ex instanceof MissingPathVariableException) {
			status = BAD_REQUEST;
			errorResponse.setError("Bad request");
			errorResponse.setMessage(ex.getMessage());
			logErrorResponse(errorResponse);
		} else if (ex instanceof ServiceException) {
			ServiceException serviceException = (ServiceException) ex;
			errorResponse = serviceException.getErrorResponse();
			status = serviceException.getStatus();
			logErrorResponse(errorResponse);
		} else if (ex instanceof AccessDeniedException
				|| ex instanceof HttpRequestMethodNotSupportedException
				|| ex instanceof AuthenticationException) {
			status = UNAUTHORIZED;
			errorResponse.setError("Access denied");
			errorResponse.setMessage(ex.getMessage());
			logErrorResponse(errorResponse);
		} else if (ex instanceof EntityNotFoundException) {
			status = NOT_FOUND;
			errorResponse.setError("Not found");
			errorResponse.setMessage("Entity not found");
		} else {
			log.error(ex.getClass().getName().concat(": ").concat(ex.getMessage() == null ? "" : ex.getMessage()));
			ex.printStackTrace();
		}
		return new ResponseEntity<>(errorResponse, status);
	}

	private void logErrorResponse(ErrorResponse errorResponse) {
		log.warn(errorResponse.getError().concat(": ").concat(errorResponse.getMessage()));
	}

	private String parseFieldErrors(MethodArgumentNotValidException ex) {
		StringBuilder errors = new StringBuilder();
		ex.getBindingResult().getAllErrors()
				.stream()
				.filter(er -> er instanceof FieldError)
				.map(er -> (FieldError) er)
				.forEach(er -> errors
						.append(er.getField())
						.append(": ")
						.append(er.getDefaultMessage())
						.append(". "));
		return errors.toString();
	}

	private String parseConstraintViolations(ConstraintViolationException ex) {
		StringBuilder errors = new StringBuilder();
		ex.getConstraintViolations()
				.forEach(constraintViolation -> errors
						.append(constraintViolation.getPropertyPath().toString())
						.append(": ")
						.append(constraintViolation.getMessage())
						.append(". "));
		return errors.toString();
	}

}