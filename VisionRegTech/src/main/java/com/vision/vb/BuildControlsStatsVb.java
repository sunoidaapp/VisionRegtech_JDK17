package com.vision.vb;

public class BuildControlsStatsVb extends CommonVb{
		private String	year = "";
		private int	dataSourceAt = 10;
		private String	dataSource = "";
		private String	buildModule = "";
		private String	lastBuildDate = "";
		private int	bcStatsStatusNt = 0;
		private int	bcStatsStatus = 0;
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public int getDataSourceAt() {
			return dataSourceAt;
		}
		public void setDataSourceAt(int dataSourceAt) {
			this.dataSourceAt = dataSourceAt;
		}
		public String getDataSource() {
			return dataSource;
		}
		public void setDataSource(String dataSource) {
			this.dataSource = dataSource;
		}
		public String getBuildModule() {
			return buildModule;
		}
		public void setBuildModule(String buildModule) {
			this.buildModule = buildModule;
		}
		public String getLastBuildDate() {
			return lastBuildDate;
		}
		public void setLastBuildDate(String lastBuildDate) {
			this.lastBuildDate = lastBuildDate;
		}
		public int getBcStatsStatusNt() {
			return bcStatsStatusNt;
		}
		public void setBcStatsStatusNt(int bcStatsStatusNt) {
			this.bcStatsStatusNt = bcStatsStatusNt;
		}
		public int getBcStatsStatus() {
			return bcStatsStatus;
		}
		public void setBcStatsStatus(int bcStatsStatus) {
			this.bcStatsStatus = bcStatsStatus;
		}
}
