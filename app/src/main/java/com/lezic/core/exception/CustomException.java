package com.lezic.core.exception;

public class CustomException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	private String msg;
	
	public CustomException(String msg){
		super(msg);
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
