package camchua.taixiu.v2;

import camchua.taixiu.v2.FileManager.Files;

public enum BetType {
	
	TAI("Tai"), XIU("Xiu");
	
	
	private String key;
	
	BetType(String k) {
		this.key = k;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public static String getFormat(BetType bt) {
		return FileManager.getFileConfig(Files.CONFIG).getString("Settings.Format." + bt.getKey());
	}

}
