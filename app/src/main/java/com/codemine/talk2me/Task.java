package com.codemine.talk2me;

import java.util.Map;

public class Task {
	private int taskID;//
	@SuppressWarnings("rawtypes")
	private Map taskParam;//
	public static final int TASK_CONECT_DEVICE = 3;      //连接蓝牙设备
	public static final int TASK_SEND_MESSAGE = 9;  //发送信息

	public Task(int id, @SuppressWarnings("rawtypes") Map param) {
		this.taskID = id;
		this.taskParam = param;
	}
	public int getTaskID() {
		return taskID;
	}
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	@SuppressWarnings("rawtypes")
	public Map getTaskParam() {
		return taskParam;
	}
	public void setTaskParam(@SuppressWarnings("rawtypes") Map taskParam) {
		this.taskParam = taskParam;
	}
	
}
