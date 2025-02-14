package model;

import java.util.Date;

public class Comment {
	private String commentId;
	private String cPostId;
	private String cUserId;
	private String comment;
	private Date writeDate;

	public Comment() {
	}

	public Comment(String commentId, String cPostId, String cUserId, String comment, Date writeDate) {
		super();
		this.commentId = commentId;
		this.cPostId = cPostId;
		this.cUserId = cUserId;
		this.comment = comment;
		this.writeDate = writeDate;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getcPostId() {
		return cPostId;
	}

	public void setcPostId(String cPostId) {
		this.cPostId = cPostId;
	}

	public String getcUserId() {
		return cUserId;
	}

	public void setcUserId(String cUserId) {
		this.cUserId = cUserId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
	}

	@Override
	public String toString() {
		return "Comment [commentId=" + commentId + ", cPostId=" + cPostId + ", cUserId=" + cUserId + ", comment="
				+ comment + ", writeDate=" + writeDate + "]";
	}

}
