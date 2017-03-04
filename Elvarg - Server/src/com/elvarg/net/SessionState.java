package com.elvarg.net;

/**
 * The enumerated type whose elements represent the IO session states.
 *
 * @author lare96 <http://github.com/lare96>
 */
public enum SessionState {

	/**
	 * The client is currently decoding the login protocol.
	 */
	LOGGING_IN,

	/**
	 * The client is now a player, and is logged in.
	 */
	LOGGED_IN,

	/**
	 * The player requested a logout using the logout button.
	 */
	REQUESTED_LOG_OUT,

	/**
	 * A request has been sent to disconnect the client.
	 */
	LOGGING_OUT,

	/**
	 * The client has disconnected from the server.
	 */
	LOGGED_OUT
}