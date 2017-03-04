package com.elvarg.net;

import java.math.BigInteger;

import com.google.common.collect.ImmutableList;

import io.netty.util.AttributeKey;

/**
 * A class containing different attributes which affect networking in different
 * ways.
 * 
 * @author Swiffy
 */
public class NetworkConstants {

	/**
	 * The game port
	 */
	public static final int GAME_PORT = 43595;

	/**
	 * The opcode for requesting a login.
	 */
	public static final int LOGIN_REQUEST_OPCODE = 14;

	/**
	 * Signifies a new connection.
	 */
	public static final int NEW_CONNECTION_OPCODE = 16;

	/**
	 * Signifies the return of an existing connection.
	 */
	public static final int RECONNECTION_OPCODE = 18;

	/**
	 * The time required for the channel to time out
	 */
	public static final int SESSION_TIMEOUT = 15;

	/**
	 * The JAGGRAB port. for the fileserver
	 */
	public static final int JAGGRAB_PORT = 43596;

	/**
	 * The service port (which is also used for the 'on-demand' protocol). for
	 * the fileserver
	 */
	public static final int SERVICE_PORT = 43597;

	/**
	 * The HTTP port for the fileserver.
	 */
	public static final int HTTP_PORT = 80;

	/**
	 * The keys used for encryption on login
	 */
	public static final BigInteger RSA_MODULUS = new BigInteger(
			"104491769878744214552327916539299463496996457081116392641740420337580247359457531212713234798435269852381858199895582444770363103378419890508986198319599243102737368616946490728678876018327788000439596635223141886089230154991381365099178986572201859664528128354742213167942196819984139030533812106754541601427");
	public static final BigInteger RSA_EXPONENT = new BigInteger(
			"60599709927999604905700283074075621535832298407602909067050214324591452792136683802017251754259849192812146824871924219308664736457374966789226657090366163438668361486273661119889727044594679526697477360216141989053223520684400208813776138325305181882593010856241272041142533590779910844335986101437613222337");

	/**
	 * The list of exceptions that are ignored and discarded by the
	 * {@link UpstreamChannelHandler}.
	 */
	public static final ImmutableList<String> IGNORED_NETWORK_EXCEPTIONS = ImmutableList.of(
			"An existing connection was forcibly closed by the remote host",
			"An established connection was aborted by the software in your host machine",
			"En befintlig anslutning tvingades att stänga av fjärrvärddatorn"); // Swedish
																				// <3

	/**
	 * The amount of connections that are allowed from the same host.
	 */
	public static final int CONNECTION_LIMIT = 2;

	/**
	 * The attribute that contains the key for a players session.
	 */
	public static final AttributeKey<PlayerSession> SESSION_KEY = AttributeKey.valueOf("session.key");

	/**
	 * The maximum amount of messages that can be processed in one sequence.
	 */
	public static final int PACKET_PROCESS_LIMIT = 25;

}
