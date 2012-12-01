package name.nanek.andutil.imageupload;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class SentCountingMultiPartEntity extends MultipartEntity {

	public static interface SentCountListener {
		void onSentCountUpdated(long aBytesSend);
	}

	private final SentCountListener mListener;

	public SentCountingMultiPartEntity(final SentCountListener listener) {
		this.mListener = listener;
	}

	public SentCountingMultiPartEntity(final HttpMultipartMode mode, final SentCountListener listener) {
		super(mode);
		this.mListener = listener;
	}

	public SentCountingMultiPartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final SentCountListener listener) {
		super(mode, boundary, charset);
		this.mListener = listener;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new WriteCountingOutputStream(outstream, mListener));
	}

	public static class WriteCountingOutputStream extends FilterOutputStream {

		private final SentCountListener mListener;
		
		private long mBytesTransferred;

		public WriteCountingOutputStream(final OutputStream out, final SentCountListener listener) {
			super(out);
			mListener = listener;
			mBytesTransferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			mBytesTransferred += len;
			mListener.onSentCountUpdated(mBytesTransferred);
		}

		public void write(int b) throws IOException {
			out.write(b);
			mBytesTransferred++;
			mListener.onSentCountUpdated(mBytesTransferred);
		}
	}
}