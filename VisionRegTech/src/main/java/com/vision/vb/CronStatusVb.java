package com.vision.vb;

public class CronStatusVb extends CommonVb{

	private static final long serialVersionUID = -7135158174947618107L;
	private String serverName = "";
	private String serverEnvironment = "";
	private String nodeIp = "";
	private String nodeName =  "";
	private String nodeUser =  "";
	private String nodePwd =  "";
	private String cronName = "";
	private int	cronStatusAt  =  0;
	private String cronStatus = "";
	/*
	 * private List<CronStatusVb> children = new ArrayList<CronStatusVb>(0); private
	 * CronStatusVb parent = null;
	 */
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerEnvironment() {
		return serverEnvironment;
	}
	public void setServerEnvironment(String serverEnvironment) {
		this.serverEnvironment = serverEnvironment;
	}
	public String getNodeIp() {
		return nodeIp;
	}
	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeUser() {
		return nodeUser;
	}
	public void setNodeUser(String nodeUser) {
		this.nodeUser = nodeUser;
	}
	public String getNodePwd() {
		return nodePwd;
	}
	public void setNodePwd(String nodePwd) {
		this.nodePwd = nodePwd;
	}
	public String getCronName() {
		return cronName;
	}
	public void setCronName(String cronName) {
		this.cronName = cronName;
	}
	public int getCronStatusAt() {
		return cronStatusAt;
	}
	public void setCronStatusAt(int cronStatusAt) {
		this.cronStatusAt = cronStatusAt;
	}
	public String getCronStatus() {
		return cronStatus;
	}
	public void setCronStatus(String cronStatus) {
		this.cronStatus = cronStatus;
	}
	/*
	 * public List<CronStatusVb> getChildren() { return children; } public void
	 * setChildren(List<CronStatusVb> children) { this.children = children; } public
	 * CronStatusVb getParent() { return parent; } public void
	 * setParent(CronStatusVb parent) { this.parent = parent; }
	 */
}
