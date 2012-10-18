package util;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.summercool.util.ExcelUtil;
import org.summercool.util.ExcelUtil.XCell;

public class ExcelUtilTest {

	/**
	 * @Title: main
	 * @Description: ExcelUtil测试类
	 * @return void 返回类型
	 * @param args
	 */
	private static String FILE = "D:/projects-dest/excel/poi-dest.xls";

	public static void main(String[] args) {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(FILE);
			Workbook wb = ExcelUtil.createWorkbook();
			Sheet sheet = ExcelUtil.createSheet(wb, "report");
			//
			{
				Row row = ExcelUtil.createRow(sheet, 0);
				//
				XCell xCell1 = new XCell();
				xCell1.setColor(true);
				xCell1.setType(1);
				xCell1.setValue("列一");
				XCell xCell2 = new XCell();
				xCell2.setColor(true);
				xCell2.setType(2);
				xCell2.setValue("10000");
				XCell xCell3 = new XCell();
				xCell3.setColor(true);
				xCell3.setType(3);
				xCell3.setValue("2012-10-18");
				//
				ExcelUtil.createCell(wb, row, (short) 0, xCell1);
				ExcelUtil.createCell(wb, row, (short) 1, xCell2);
				ExcelUtil.createCell(wb, row, (short) 2, xCell3);
			}
			//
			{
				Row row = ExcelUtil.createRow(sheet, 1);
				//
				XCell xCell1 = new XCell();
				xCell1.setType(1);
				xCell1.setValue("列一");
				XCell xCell2 = new XCell();
				xCell2.setType(2);
				xCell2.setValue("10000");
				XCell xCell3 = new XCell();
				xCell3.setType(3);
				xCell3.setValue("2012-10-18");
				//
				ExcelUtil.createCell(wb, row, (short) 0, xCell1);
				ExcelUtil.createCell(wb, row, (short) 1, xCell2);
				ExcelUtil.createCell(wb, row, (short) 2, xCell3);
			}
			//
			ExcelUtil.writeWorkbook(wb, output);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
