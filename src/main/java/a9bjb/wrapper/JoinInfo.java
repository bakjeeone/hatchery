package a9bjb.wrapper;

public class JoinInfo {
	public JoinInfo(String filename1, String filename2, String columnName) {
		super();
		this.filename1 = filename1;
		this.filename2 = filename2;
		this.columnName = columnName;
	}
	private String filename1;
	private String filename2;
	private String columnName;
	public String getFilename1() {
		return filename1;
	}
	public void setFilename1(String filename1) {
		this.filename1 = filename1;
	}
	public String getFilename2() {
		return filename2;
	}
	public void setFilename2(String filename2) {
		this.filename2 = filename2;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
}