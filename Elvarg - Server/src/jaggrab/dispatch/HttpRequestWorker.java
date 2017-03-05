package jaggrab.dispatch;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * A worker which services HTTP requests.
 * 
 * @author Graham
 */
public final class HttpRequestWorker extends RequestWorker<HttpRequest> {

	/**
	 * The value of the server header.
	 */
	private static final String SERVER_IDENTIFIER = "JAGeX/3.1";

	/**
	 * The directory with web files.
	 */
	private static final File WWW_DIRECTORY = new File("./data/www/");

	/**
	 * The default character set.
	 */
	private static final Charset CHARACTER_SET = Charset.forName("ISO-8859-1");

	@Override
	protected ChannelRequest<HttpRequest> nextRequest() throws InterruptedException {
		return RequestDispatcher.nextHttpRequest();
	}

	@Override
	protected void service(Channel channel, HttpRequest request) throws IOException {
		String path = request.uri();
		ByteBuf buf = ResourceRequester.request(path);

		ByteBuf wrappedBuf;
		HttpResponseStatus status = HttpResponseStatus.OK;

		String mimeType = "application/octet-stream";

		if (buf == null) {
			File f = new File(WWW_DIRECTORY, path);
			URI target = f.toURI().normalize();
			URI base = WWW_DIRECTORY.toURI().normalize();
			if (target.toASCIIString().startsWith(base.toASCIIString())) {
				if (f.exists()) {
					if (f.isDirectory()) {
						File tmp = new File(f, "index.html");
						if (tmp.exists()) {
							f = tmp;
						}
					}
					if (f.isDirectory()) {
						status = HttpResponseStatus.FORBIDDEN;
						wrappedBuf = createErrorPage(status, "Directory listings cannot be viewed.");
						mimeType = "text/html";
					} else {
						status = HttpResponseStatus.OK;
						wrappedBuf = readFile(f);
						mimeType = getMimeType(f.getName());
					}
				} else {
					status = HttpResponseStatus.NOT_FOUND;
					wrappedBuf = createErrorPage(status, "The page you requested could not be found.");
					mimeType = "text/html";
				}
			} else {
				status = HttpResponseStatus.FORBIDDEN;
				wrappedBuf = createErrorPage(status, "You are not authorized to access that page.");
				mimeType = "text/html";
			}
		} else {
			wrappedBuf = Unpooled.wrappedBuffer(buf);
		}

		HttpResponse resp = new DefaultHttpResponse(request.protocolVersion(), status);

		/*
		 * resp.headers().setHeader("Date", new Date());
		 * resp.headers().setHeader("Server", SERVER_IDENTIFIER);
		 * resp.headers().setHeader("Content-type", mimeType + ", charset=" +
		 * CHARACTER_SET.name()); resp.headers().setHeader("Cache-control",
		 * "no-cache"); resp.headers().setHeader("Pragma", "no-cache");
		 * resp.headers().setHeader("Expires", new Date(0));
		 * resp.headers().setHeader("Connection", "close");
		 * resp.headers().setHeader("Content-length",
		 * wrappedBuf.readableBytes()); resp.headers().setChunked(false);
		 * resp.headers().setContent(wrappedBuf);
		 */

		channel.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Reads a file.
	 * 
	 * @param f
	 *            The file.
	 * @return The channel buffer.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private ByteBuf readFile(File f) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		ByteBuffer buf;
		try {
			buf = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length());
		} finally {
			raf.close();
		}
		return Unpooled.wrappedBuffer(buf);
	}

	/**
	 * Gets the MIME type of a file by its name.
	 * 
	 * @param name
	 *            The file name.
	 * @return The MIME type.
	 */
	private String getMimeType(String name) {
		if (name.endsWith(".htm") || name.endsWith(".html")) {
			return "text/html";
		} else if (name.endsWith(".css")) {
			return "text/css";
		} else if (name.endsWith(".js")) {
			return "text/javascript";
		} else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (name.endsWith(".gif")) {
			return "image/gif";
		} else if (name.endsWith(".png")) {
			return "image/png";
		} else if (name.endsWith(".txt")) {
			return "text/plain";
		}
		return "application/octect-stream";
	}

	/**
	 * Creates an error page.
	 * 
	 * @param status
	 *            The HTTP status.
	 * @param description
	 *            The error description.
	 * @return The error page as a buffer.
	 */
	private ByteBuf createErrorPage(HttpResponseStatus status, String description) {
		String title = status.code() + " " + status.reasonPhrase();

		StringBuilder bldr = new StringBuilder();

		bldr.append("<!DOCTYPE html><html><head><title>");
		bldr.append(title);
		bldr.append("</title></head><body><h1>");
		bldr.append(title);
		bldr.append("</h1><p>");
		bldr.append(description);
		bldr.append("</p><hr /><address>");
		bldr.append(SERVER_IDENTIFIER);
		bldr.append(" Server</address></body></html>");

		return Unpooled.copiedBuffer(bldr.toString(), Charset.defaultCharset());
	}

}
