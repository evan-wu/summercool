package com.taobao.selleradminlog.agent.tool;

import java.io.File;

public class MyTest {

	private static String TMP_DIR = "d:/temp/";
	
	public static void main(String[] args) {
		MyThread t = new MyThread();
//		MyThread t1 = new MyThread();
		t.start();
		System.out.println("t started.");
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("t join finished.");
	}
	
	static class MyThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
