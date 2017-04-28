package com.hl;

import lotus.domino.*;


public class InitConfigInfo {
	
	//统计数据库
	private Database tjDatabase;
	private Database bjDatabase;
	private Database ptDatabase;
	private Database ptnewDatabase;
	private Database pjDatabase;
	private Session session;
	private Database hrDatabase;
	private Database ptDfDatabase;

	private String stStartDate;
	private String stEndDate;
	private String stWorkPlace;
	private String stWorkPlaceNum;
	private String stDeptName;
	private String stDeptID;
	public InitConfigInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InitConfigInfo(Database hrDatabase, Session session) {
		super();
		this.hrDatabase = hrDatabase;
		this.session = session;
	}

	public Database getTjDatabase() {
		Database tjDatabase = null;
		try {
			tjDatabase = session.getDatabase("", "hr/TJAttendanceManage/AttendanceStatistics.nsf");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return tjDatabase;
	}

	public void setTjDatabse(Database tjDatabase) {
		
		
		this.tjDatabase = tjDatabase;
	}
	public Database getBjDatabase() {
		Database bjDatabase = null;
		try {
			bjDatabase = session.getDatabase("", "hr/BJAttendanceManage/AttendanceStatistics.nsf");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return bjDatabase;
	}

	public void setBjDatabase(Database bjDatabase) {
		this.bjDatabase = bjDatabase;
	}

	public Database getPtDatabase() {
		Database ptDatabase = null;
		try {
			ptDatabase = session.getDatabase("", "hr/PTAttendanceManage/AttendanceStatistics.nsf");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return ptDatabase;
	}

	public void setPtDatabase(Database ptDatabase) {
		this.ptDatabase = ptDatabase;
	}

	public Database getPjDatabase() {
		Database pjDatabase = null;
		try {
			pjDatabase = session.getDatabase("", "hr/PJAttendanceManage/AttendanceStatistics.nsf");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return pjDatabase;
	}
	public void setPjDatabase(Database pjDatabase) {
		this.pjDatabase = pjDatabase;
	}
	
	public Database getPtnewDatabase() {
		Database ptnewDatabase = null;
		try {
			ptnewDatabase = session.getDatabase("", "hr/PTNEWAttendanceManage/AttendanceStatistics.nsf");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return ptnewDatabase;
	}

	public void setPtnewDatabase(Database ptnewDatabase) {
		this.ptnewDatabase = ptnewDatabase;
	}
	public Database getPtDfDatabase() {
		Database ptDfDatabase = null;
		try {
			ptDfDatabase = session.getDatabase("", "hr/PTAttendanceManage/DFAttendanceTolDateApp.nsf");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return ptDfDatabase;
	}

	public void setPtDfDatabase(Database ptDfDatabase) {
		this.ptDfDatabase = ptDfDatabase;
	}
	public String getStStartDate() {
		return stStartDate;
	}

	public void setStStartDate(String stStartDate) {
		this.stStartDate = stStartDate;
	}

	public String getStEndDate() {
		return stEndDate;
	}

	public void setStEndDate(String stEndDate) {
		this.stEndDate = stEndDate;
	}

	public String getStWorkPlace() {
		return stWorkPlace;
	}

	public void setStWorkPlace(String stWorkPlace) {
		this.stWorkPlace = stWorkPlace;
	}

	public String getStWorkPlaceNum() {
		return stWorkPlaceNum;
	}

	public void setStWorkPlaceNum(String stWorkPlaceNum) {
		this.stWorkPlaceNum = stWorkPlaceNum;
	}

	public String getStDeptName() {
		return stDeptName;
	}

	public void setStDeptName(String stDeptName) {
		this.stDeptName = stDeptName;
	}

	public String getStDeptID() {
		return stDeptID;
	}

	public void setStDeptID(String stDeptID) {
		this.stDeptID = stDeptID;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		
		this.session = session;
	}

	
}
