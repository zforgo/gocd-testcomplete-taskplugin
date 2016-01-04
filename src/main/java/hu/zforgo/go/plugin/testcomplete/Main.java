package hu.zforgo.go.plugin.testcomplete;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		{
			String appPath = "c:\\Program Files (x86)\\SmartBear\\TestComplete 11\\Bin\\";
			String app = "TestComplete";
			File f = new File(appPath, app + ".exe");
			String s = FilenameUtils.normalize(f.getAbsolutePath());
			System.out.println(s);
		}
		{
			String appPath = "/work/target/valami//";
			String app = "/TestComplete";
			File f = new File(appPath, app + ".exe");
			System.out.println(f.getAbsoluteFile());
			String s = FilenameUtils.normalize(f.getAbsolutePath());
			System.out.println(s);
		}
	}
}
