package org.iish.acquisition.export

import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.WorkbookUtil
import org.iish.acquisition.domain.AnalogMaterial
import org.iish.acquisition.domain.AnalogMaterialCollection
import org.iish.acquisition.domain.AnalogUnit
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.DigitalMaterialCollection
import org.iish.acquisition.domain.Location
import org.iish.acquisition.domain.MaterialType
import org.iish.acquisition.domain.MiscMaterial
import org.iish.acquisition.domain.MiscMaterialCollection
import org.iish.acquisition.domain.MiscMaterialType
import org.iish.acquisition.search.CollectionSearch
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder as LCH

import java.text.SimpleDateFormat

/**
 * Builds an Excel (xls) export of all collections for a search request.
 */
class CollectionXlsExport {
	private static final int NUMBER_OF_HEADERS = 2
	private static final int HEADER_ROW_GROUPING = 0
	private static final int HEADER_ROW_LABELS = 1
	private static final int COLUMN_MAX_WIDTH = 10000

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat('yyyy-MM-dd')

	private HSSFWorkbook workbook
	private HSSFSheet sheet

	private List<Collection> collections
	private List<MaterialType> materials
	private List<MiscMaterialType> miscMaterials

	private Integer maxNumberOfLocations = 0
	private MessageSource messageSource

	private CellStyle headerGroupingStyle
	private CellStyle headerLabelsStyle
	private CellStyle textStyle
	private CellStyle dateStyle

	/**
	 * Constructs a new CollectionXlsExport for the given collection search.
	 * @param collectionSearch The collection search in question.
	 * @param messageSource Required for translated headers.
	 */
	CollectionXlsExport(CollectionSearch collectionSearch, MessageSource messageSource) {
		this.workbook = new HSSFWorkbook()
		this.sheet = workbook.createSheet(getSheetName())

		this.collections = collectionSearch.getResults() as List<Collection>
		this.materials = MaterialType.list()
		this.miscMaterials = MiscMaterialType.list()

		this.messageSource = messageSource

		determineMaxNumberOfLocations()
		createHeaderStyle()
		createTextStyle()
		createDateStyle()
	}

	/**
	 * Starts the building process of the Excel sheet/workbook.
	 */
	void build() {
		int noOfColumns = createHeader()

		collections.eachWithIndex { Collection collection, int i ->
			createRow(collection, (NUMBER_OF_HEADERS + i))
		}

		autoSizeColumns(noOfColumns)
	}

	/**
	 * Writes the Excel file to the given output stream.
	 * @param outputStream The output stream in question.
	 */
	void writeToStream(OutputStream outputStream) {
		workbook.write(outputStream)
		outputStream.flush()
	}

	/**
	 * Returns a possible filename for the Excel file.
	 * @return A possible filename for the Excel file.
	 */
	static String getFileName() {
		return "collections-export-${DATE_FORMAT.format(new Date())}.xls"
	}

	/**
	 * Returns the content type of the generated Excel file.
	 * @return The content type of the generated Excel file.
	 */
	static String getContentType() {
		return 'application/vnd.ms-excel'
	}

	/**
	 * Searches the collections to export for the collection with the most locations.
	 * This number indicates the number of columns we need for exporting the locations.
	 */
	private void determineMaxNumberOfLocations() {
		Collection collectionWithMostLocations = collections.max { it.locations?.size() }
		maxNumberOfLocations = collectionWithMostLocations?.locations?.size()
		if (!maxNumberOfLocations) {
			maxNumberOfLocations = 0
		}
	}

	/**
	 * Creates the style necessary for the header.
	 */
	private void createHeaderStyle() {
		headerGroupingStyle = workbook.createCellStyle()
		headerLabelsStyle = workbook.createCellStyle()

		[headerGroupingStyle, headerLabelsStyle].each { CellStyle cellStyle ->
			HSSFFont headerFont = workbook.createFont()
			headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD)

			cellStyle.setFont(headerFont)
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION)
		}

		headerGroupingStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN)
		headerGroupingStyle.setBorderRight(HSSFCellStyle.BORDER_THIN)
	}

	/**
	 * Creates the style necessary for text cells (wrapping of text).
	 */
	private void createTextStyle() {
		textStyle = workbook.createCellStyle()
		textStyle.setWrapText(true)
	}

	/**
	 * Creates the style necessary for date cells.
	 */
	private void createDateStyle() {
		CreationHelper createHelper = workbook.getCreationHelper()
		short dataFormat = createHelper.createDataFormat().getFormat('dd/mm/yyyy')

		dateStyle = workbook.createCellStyle()
		dateStyle.setDataFormat(dataFormat)
	}

	/**
	 * Creates the header row of the Excel export.
	 * @return The number of columns necessary.
	 */
	private int createHeader() {
		Row groupingRow = sheet.createRow(HEADER_ROW_GROUPING)
		Row labelsRow = sheet.createRow(HEADER_ROW_LABELS)
		sheet.createFreezePane(0, NUMBER_OF_HEADERS)

		createHeaderWithGrouping(groupingRow)
		int nrColumns = createHeaderWithLabels(labelsRow)

		return nrColumns
	}

	/**
	 * Creates the header row of the Excel export with the grouping.
	 * @param row The header row.
	 */
	private void createHeaderWithGrouping(Row row) {
		int totalMaterialTypes = MaterialType.getTotalNumberOfUniqueTypes()

		int indexStart = 0
		int indexAnalog = indexStart + 5 + maxNumberOfLocations
		int indexDigital = indexAnalog + totalMaterialTypes
		int indexMisc = indexDigital + materials.size() + 2
		int indexRemaining = indexMisc + miscMaterials.size()
		int indexEnd = indexRemaining + 15

		setHeaderCell(row, indexStart, '')
		setHeaderCell(row, indexAnalog, 'collection.analogMaterialCollection.extended.label')
		setHeaderCell(row, indexDigital, 'collection.digitalMaterialCollection.extended.label')
		setHeaderCell(row, indexMisc, 'collection.miscMaterialCollection.extended.label')
		setHeaderCell(row, indexRemaining, '')

		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), indexStart, indexAnalog - 1))
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), indexAnalog, indexDigital - 1))
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), indexDigital, indexMisc - 1))
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), indexMisc, indexRemaining - 1))
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), indexRemaining, indexEnd - 1))
	}

	/**
	 * Creates the header row of the Excel export with the labels.
	 * @param row The header row.
	 * @return The number of columns necessary.
	 */
	private int createHeaderWithLabels(Row row) {
		int i = 0
		setHeaderCell(row, i++, '#')
		setHeaderCell(row, i++, 'collection.name.label')
		setHeaderCell(row, i++, 'collection.acquisitionId.label')
		setHeaderCell(row, i++, 'collection.addedBy.label')
		setHeaderCell(row, i++, 'collection.objectRepositoryPID.label')

		if (maxNumberOfLocations > 0) {
			(1..maxNumberOfLocations).each { int locationNumber ->
				setHeaderCell(row, i++, 'collection.location.extended.label', locationNumber.toString())
			}
		}

		materials.each { MaterialType materialType ->
			if (materialType.inMeters && materialType.inNumbers) {
				setHeaderCell(row, i++, "${materialType.getNameAnalog()} (${AnalogUnit.METER})")
				setHeaderCell(row, i++, "${materialType.getNameAnalog()} (${AnalogUnit.NUMBER})")
			}
			else {
				setHeaderCell(row, i++, materialType.getNameAnalog())
			}
		}

		materials.each { MaterialType materialType ->
			setHeaderCell(row, i++, materialType.getNameDigital())
		}

		setHeaderCell(row, i++, 'digitalMaterialCollection.numberOfFiles.export.label')
		setHeaderCell(row, i++, 'digitalMaterialCollection.totalSize.label')

		miscMaterials.each { MiscMaterialType materialType ->
			setHeaderCell(row, i++, materialType.name)
		}

		setHeaderCell(row, i++, 'collection.content.label')
		setHeaderCell(row, i++, 'collection.listsAvailable.label')
		setHeaderCell(row, i++, 'collection.toBeDone.label')
		setHeaderCell(row, i++, 'collection.priority.label')
		setHeaderCell(row, i++, 'collection.level.label')
		setHeaderCell(row, i++, 'collection.owner.label')
		setHeaderCell(row, i++, 'collection.contract.label')
		setHeaderCell(row, i++, 'collection.accrual.label')
		setHeaderCell(row, i++, 'collection.appraisal.label')
		setHeaderCell(row, i++, 'collection.dateOfArrival.label')
		setHeaderCell(row, i++, 'collection.contactPerson.label')
		setHeaderCell(row, i++, 'collection.remarks.label')
		setHeaderCell(row, i++, 'collection.originalPackageTransport.label')
		setHeaderCell(row, i++, 'collection.status.label')
		setHeaderCell(row, i, 'collection.collectionLevelReady.label')

		return i
	}

	/**
	 * Creates a cell in the header with the specified message code or text value.
	 * @param row The header row.
	 * @param columnNumber The column number to enter the value.
	 * @param code The message code (or simply a text value) to enter in the specified column.
	 */
	private void setHeaderCell(Row row, int columnNumber, String code, String... messageArgs) {
		Cell cell = row.createCell(columnNumber)
		cell.setCellValue(messageSource.getMessage(code, messageArgs, code, LCH.getLocale()))

		switch (row.getRowNum()) {
			case HEADER_ROW_GROUPING:
				cell.setCellStyle(headerGroupingStyle)
				break
			case HEADER_ROW_LABELS:
				cell.setCellStyle(headerLabelsStyle)
				break
		}
	}

	/**
	 * Creates a row of the Excel export.
	 * @param collection The collection to export to this row.
	 * @param rowNumber The number of the row in question.
	 */
	private void createRow(Collection collection, int rowNumber) {
		Row row = sheet.createRow(rowNumber)
		int i = 0

		Set<Location> locations = collection.locations
		int noOfEmptyCells = maxNumberOfLocations - locations.size()
		DigitalMaterialCollection digitalMaterialCollection = collection.digitalMaterialCollection
		AnalogMaterialCollection analogMaterialCollection = collection.analogMaterialCollection
		MiscMaterialCollection miscMaterialCollection = collection.miscMaterialCollection

		setDataCellWithNumber(row, i++, collection.id)
		setDataCellWithText(row, i++, collection.name)
		setDataCellWithText(row, i++,
				collection.acquisitionId ? "$collection.acquisitionTypeId $collection.acquisitionId" : '')
		setDataCellWithText(row, i++, collection.addedBy?.toString())
		setDataCellWithText(row, i++, collection.objectRepositoryPID)

		locations.each { Location location ->
			setDataCellWithText(row, i++, location.toDetailedString())
		}

		if (noOfEmptyCells > 0) {
			(1..noOfEmptyCells).each {
				setDataCellWithText(row, i++, '')
			}
		}

		materials.each { MaterialType materialType ->
			if (materialType.inMeters && materialType.inNumbers) {
				AnalogMaterial analogMeter = analogMaterialCollection?.
						getMaterialByTypeAndUnit(materialType, AnalogUnit.METER)
				AnalogMaterial analogNumber = analogMaterialCollection?.
						getMaterialByTypeAndUnit(materialType, AnalogUnit.NUMBER)

				setDataCellWithNumber(row, i++, analogMeter?.size)
				setDataCellWithNumber(row, i++, analogNumber?.size)
			}
			else {
				Set<AnalogMaterial> analogMaterials = analogMaterialCollection?.getMaterialsByType(materialType)
				AnalogMaterial analogMaterial = (analogMaterials && !analogMaterials.isEmpty()) ?
						analogMaterials.first() : null

				setDataCellWithNumber(row, i++, analogMaterial?.size)
			}
		}

		materials.each { MaterialType materialType ->
			String hasMaterial = digitalMaterialCollection?.getMaterialByType(materialType) ? 'YES' : ''
			setDataCellWithText(row, i++, hasMaterial)
		}

		setDataCellWithNumber(row, i++, digitalMaterialCollection?.numberOfFiles)
		setDataCellWithText(row, i++,
				digitalMaterialCollection ? digitalMaterialCollection.totalSizeToStringWithUnit() : '')

		miscMaterials.each { MiscMaterialType materialType ->
			MiscMaterial material = miscMaterialCollection?.getMaterialByType(materialType)
			setDataCellWithNumber(row, i++, material?.size)
		}

		setDataCellWithText(row, i++, collection.content)
		setDataCellWithText(row, i++, collection.listsAvailable)
		setDataCellWithText(row, i++, collection.toBeDone)
		setDataCellWithText(row, i++, collection.priority?.toString())
		setDataCellWithText(row, i++, collection.level?.toString())
		setDataCellWithText(row, i++, collection.owner)
		setDataCellWithText(row, i++, collection.contract?.toString())
		setDataCellWithText(row, i++, collection.accrual?.toString())
		setDataCellWithText(row, i++, collection.appraisal?.toString())
		setDataCellWithDate(row, i++, collection.dateOfArrival)
		setDataCellWithText(row, i++, collection.contactPerson)
		setDataCellWithText(row, i++, collection.remarks)
		setDataCellWithText(row, i++, collection.originalPackageTransport)
		setDataCellWithText(row, i++, collection.status?.toString())
		setDataCellWithText(row, i, collection.collectionLevelReady ? 'YES' : '')
	}

	/**
	 * Creates a cell with the specified text value.
	 * @param row The header row.
	 * @param columnNumber The column number to enter the value.
	 * @param value The text value to enter in the specified column.
	 */
	private void setDataCellWithText(Row row, int columnNumber, String value) {
		Cell cell = row.createCell(columnNumber)
		if (!checkEmpty(cell, value)) {
			cell.setCellValue(value)
			cell.setCellStyle(textStyle)
		}
	}

	/**
	 * Creates a cell with the specified number.
	 * @param row The header row.
	 * @param columnNumber The column number to enter the number.
	 * @param value The number to enter in the specified column.
	 */
	private void setDataCellWithNumber(Row row, int columnNumber, Number value) {
		Cell cell = row.createCell(columnNumber)
		if (!checkEmpty(cell, value)) {
			cell.setCellValue(value.doubleValue())
		}
	}

	/**
	 * Creates a cell with the specified date.
	 * @param row The header row.
	 * @param columnNumber The column number to enter the date.
	 * @param value The date to enter in the specified column.
	 */
	private void setDataCellWithDate(Row row, int columnNumber, Date value) {
		Cell cell = row.createCell(columnNumber)
		if (!checkEmpty(cell, value)) {
			cell.setCellValue(value)
			cell.setCellStyle(dateStyle)
		}
	}

	/**
	 * Automatically resize all columns, based on the length of the entered data.
	 * @param noOfColumns The number of columns to resize, starting from the left-most cell.
	 */
	private void autoSizeColumns(int noOfColumns) {
		(0..noOfColumns).each { int column ->
			sheet.autoSizeColumn(column)

			int width = sheet.getColumnWidth(column)
			if (width > COLUMN_MAX_WIDTH) {
				sheet.setColumnWidth(column, COLUMN_MAX_WIDTH)
			}
		}
	}

	/**
	 * Returns the name of the sheet.
	 * @return The name of the sheet.
	 */
	private static String getSheetName() {
		return WorkbookUtil.createSafeSheetName("Collections export ${DATE_FORMAT.format(new Date())}")
	}

	/**
	 * Checks if the given value is empty, if so, make sure the cell is blank.
	 * @param cell The cell in question.
	 * @param value The value to check in question.
	 * @return Whether the given value is empty.
	 */
	private static boolean checkEmpty(Cell cell, Object value) {
		if ((value == null) || value.toString().isAllWhitespace()) {
			cell.setCellType(Cell.CELL_TYPE_BLANK)
			return true
		}
		return false
	}
}
