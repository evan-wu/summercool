package org.summercool.util;

import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelUtil {

	/**
	 * 
	 * @Title: createWorkbook
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return Workbook 返回类型
	 * @param output
	 * @throws Exception
	 */
	public static Workbook createWorkbook(OutputStream output) throws Exception {
		Workbook wb = new HSSFWorkbook();
		wb.write(output);
		return wb;
	}

	/**
	 * 
	 * @Title: createSheet
	 * @Description: 创建Sheet
	 * @return Sheet 返回类型
	 * @param wb
	 * @param sheetName
	 */
	public static Sheet createSheet(Workbook wb, String sheetName) {
		Sheet sheet = wb.createSheet(sheetName);
		return sheet;
	}

	/**
	 * 
	 * @Title: createRow
	 * @Description: 创建Row
	 * @return Row 返回类型
	 * @param sheet
	 * @param row
	 */
	public static Row createRow(Sheet sheet, int row) {
		return sheet.createRow(row);
	}

	public static void createCell(Workbook wb, Row row, short column, XCell xCell) {
		CreationHelper createHelper = wb.getCreationHelper();
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yy-m-d"));
		//
		Cell cell = row.createCell(column);
		cellStyle.setAlignment(CellStyle.ALIGN_RIGHT); // 左右左对齐
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER); // 上下居中
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
		if (xCell.isColor()) {
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		}
		cell.setCellStyle(cellStyle);
		//
		if (xCell.getType() == 1) {
			cell.setCellValue(xCell.getValue());
		} else if (xCell.getType() == 2) {
			cell.setCellValue(Double.valueOf(xCell.getValue()));
		} else if (xCell.getType() == 3) {
			cell.setCellValue(xCell.getValue());
		}
	}

	public static class XCell {
		private int type = 1; // 1. String, 2. Number 3. Date
		private boolean color; // 是否有背景色
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public boolean isColor() {
			return color;
		}

		public void setColor(boolean color) {
			this.color = color;
		}

	}

}
