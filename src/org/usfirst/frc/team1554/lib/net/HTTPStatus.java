package org.usfirst.frc.team1554.lib.net;

public class HTTPStatus {

    int code;

    public HTTPStatus(int code) {
        this.code = code;
    }

    public int getStatusCode() {
        return this.code;
    }

    // RFC1945 (HTTP/1.0), RFC2616 (HTTP/1.1) and RFC2518 (WebDAV)

    // --- 1XX : Informational Codes --- //

    /**
     * <tt>100 Continue</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_CONTINUE = 100;
    /**
     * <tt>101 Switching Protocols</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_SWITCHING_PROTOCOLS = 101;
    /**
     * <tt>102 Processing</tt> (WebDAV - RFC2518)
     */
    public static final int STATUS_PROCESSING = 102;

    // --- 2XX : Success Codes --- //

    /**
     * <tt>200 OK</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_OK = 200;
    /**
     * <tt>201 Created</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_CREATED = 201;
    /**
     * <tt>202 Accepted</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_ACCEPTED = 202;
    /**
     * <tt>203 Non Authoritative Information</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_NON_AUTHORITATIVE_INFORMATION = 203;
    /**
     * <tt>204 No Content</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_NO_CONTENT = 204;
    /**
     * <tt>205 Reset Content</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_RESET_CONTENT = 205;
    /**
     * <tt>206 Partial Content</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_PARTIAL_CONTENT = 206;
    /**
     * <tt>207 Multi-Status</tt> (WebDAV - RFC 2518)<br />
     * <tt>207 Partial Update OK</tt> (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
     */
    public static final int STATUS_MULTI_STATUS = 207;

    // --- 3XX : Redirection Codes --- //

    /**
     * <tt>300 Multiple Choices</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_MULTIPLE_CHOICES = 300;
    /**
     * <tt>301 Moved Permanently</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_MOVED_PERM = 301;
    /**
     * <tt>302 Moved Temporarily or Found</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_MOVED_TEMP = 302;
    /**
     * <tt>303 See Other</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_SEE_OTHER = 303;
    /**
     * <tt>304 Not ModifiedK</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_NOT_MODIFIED = 304;
    /**
     * <tt>305 Use Proxy</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_USE_PROXY = 305;
    /**
     * <tt>307 Temporary Redirect</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_TEMP_REDIRECT = 307;

    // --- 4XX : Client Error Codes --- //

    /**
     * <tt>400 Bad Request</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_BAD_REQUEST = 400;
    /**
     * <tt>401 Unauthorized</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_UNAUTHORIZED = 401;
    /**
     * <tt>402 Payment Required</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_PAY_REQUIRED = 402;
    /**
     * <tt>403 Forbidden</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_FORBIDDEN = 403;
    /**
     * <tt>404 Not Found</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_NOT_FOUND = 404;
    /**
     * <tt>405 Method Not Allowed</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_METHOD_NOT_ALLOWED = 405;
    /**
     * <tt>406 Not Acceptable</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_NOT_ACCEPTABLE = 406;
    /**
     * <tt>407 Proxy Authentication Required</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_PROXY_AUTH_REQUIRED = 407;
    /**
     * <tt>408 Request Timeout</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_REQUREST_TIMEOUT = 408;
    /**
     * <tt>409 Conflict</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_CONFLICT = 409;
    /**
     * <tt>410 Gone</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_GONE = 410;
    /**
     * <tt>411 Length Required/tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_LEN_REQUIRED = 411;
    /**
     * <tt>412 Precondition Failed</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_PRECONDITION_FAILED = 412;
    /**
     * <tt>413 Request Too Long</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_REQUEST_TOO_LONG = 413;
    /**
     * <tt>414 Request URI Too Long</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_REQUEST_URI_TOO_LONG = 414;
    /**
     * <tt>415 Unsupported Media Type</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_UNSUPPORTED_MEDIA_TYPE = 415;
    /**
     * <tt>416 Requested Range Not Satisfiable</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    /**
     * <tt>417 Expectation Failed</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_EXPECTATION_FAILED = 417;
    /**
     * <tt>419 Insufficient Space On Resource</tt> (HTTP/1.1 - Drafts)
     */
    public static final int STATUS_INSUFFICIENT_SPACE_ON_RESOURCE = 420;
    /**
     * <tt>420 Method Failure</tt> (WebDAV - draft-ietf-webdav-protocol-05?)
     */
    public static final int STATUS_METHOD_FAILURE = 421;
    /**
     * <tt>422 Unprocessable Entity</tt> (WebDAV - RFC 2518)
     */
    public static final int STATUS_UNPROCESSABLE_ENTITY = 422;
    /**
     * <tt>423 Locked</tt> (WebDAV - RFC 2518)
     */
    public static final int STATUS_LOCKED = 423;
    /**
     * <tt>424 Failed Dependency</tt> (WebDAV - RFC 2518)
     */
    public static final int STATUS_FAILED_DEPENDENCY = 424;

    // --- 5XX : Server Error Codes --- //

    /**
     * <tt>500 Server Error</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    /**
     * <tt>501 Not Implemented</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_NOT_IMPLEMENTED = 501;
    /**
     * <tt>502 Bad Gateway</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_BAD_GATEWAY = 502;
    /**
     * <tt>503 Service Unavailable</tt> (HTTP/1.0 - RFC 1945)
     */
    public static final int STATUS_SERVICE_UNAVAILABLE = 503;
    /**
     * <tt>504 Gateway Timeout</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_GATEWAY_TIMEOUT = 504;
    /**
     * <tt>504 HTTP Version Not Supported</tt> (HTTP/1.1 - RFC 2616)
     */
    public static final int STATUS_HTTP_VERSION_NOT_SUPPORTED = 505;
    /**
     * <tt>505 Insufficient Storage</tt> (WebDAV - RFC 2518)
     */
    public static final int STATUS_INSUFFICIENT_STORAGE = 507;
}
