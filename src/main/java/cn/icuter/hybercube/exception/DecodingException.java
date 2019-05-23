package cn.icuter.hybercube.exception;

/**
 * @author edward
 * @since 2018-11-05
 */
public class DecodingException extends Exception {
    private String errorResponse;

    public String getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(String errorResponse) {
        this.errorResponse = errorResponse;
    }
}
