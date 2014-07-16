package pt.iflow.api.documents;

import java.io.InputStream;
import java.util.Date;
/**
 * When a document has too large of a content, instead of storing it's content in a byte array
 * we keep a binary stream to the document content
 * 
 * @author pussman
 *
 */
public class DocumentDataStream extends DocumentData {
	private InputStream contentStream;

	public InputStream getContentStream() {
		return contentStream;
	}

	public void setContentStream(InputStream contentStream) {
		this.contentStream = contentStream;
	}

	public DocumentDataStream(int aDocId, String asFileName, byte[] aContent,
			Date aUpdated, int flowid, int pid, int subpid) {
		super(aDocId, asFileName, aContent, aUpdated, flowid, pid, subpid);		
	}		
}
