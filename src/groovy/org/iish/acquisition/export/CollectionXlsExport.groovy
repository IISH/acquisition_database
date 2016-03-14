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
	 * @param columns The column names to export.
	 */
	void build(List<String> columns) {
		int noOfColumns = createHeader(columns)

		collections.eachWithIndex { Collection collection, int i ->
			createRow(collection, (NUMBER_OF_HEADERS + i), columns)
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
	 * @param columns The column names to export.
	 * @return The number of columns necessary.
	 */
	private int createHeader(List<String> columns) {
		Row groupingRow = sheet.createRow(HEADER_ROW_GROUPING)
		Row labelsRow = sheet.createRow(HEADER_ROW_LABELS)
		sheet.createFreezePane(0, NUMBER_OF_HEADERS)

		createHeaderWithGrouping(groupingRow, columns)
		int nrColumns = createHeaderWithLabels(labelsRow, columns)

		return nrColumns
	}

	/**
	 * Creates the header row of the Excel export with the grouping.
	 * @param columns The column names to export.
	 * @param row The header row.
	 */
	private void createHeaderWithGrouping(Row row, List<String> columns) {
		int i = 0
		int prev = 0
		boolean noGrouping = false
		CollectionXlsColumn.ALL_COLUMNS.each { String name, CollectionXlsColumn column ->
			if (columns.contains(name)) {
				switch (column) {
					case CollectionXlsColumn.LOCATION:
						i += maxNumberOfLocations
						break;
					case CollectionXlsColumn.ANALOG_MATERIAL:
						noGrouping = true
						createNewHeaderGrouping(row, i, prev, column)
						prev = i
						i += MaterialType.getTotalNumberOfUniqueTypes()
						break;
					case CollectionXlsColumn.DIGITAL_MATERIAL:
						noGrouping = true
						createNewHeaderGrouping(row, i, prev, column)
						prev = i
						i += (materials.size() + 2)
						break;
					case CollectionXlsColumn.MISC_MATERIAL:
						noGrouping = true
						createNewHeaderGrouping(row, i, prev, column)
						prev = i
						i += miscMaterials.size()
						break;
					default:
						if (noGrouping) {
							noGrouping = false
							createNewHeaderGrouping(row, i, prev)
							prev = i
						}
						i++
				}
			}
		}
		createNewHeaderGrouping(row, i, prev)
	}

	/**
	 * Creates a new header grouping cell.
	 * @param row The header row.
	 * @param index The column index.
	 * @param prevIndex The previous column index. (For merging columns)
	 * @param column The column.
	 */
	private void createNewHeaderGrouping(Row row, int index, int prevIndex, CollectionXlsColumn column = null) {
		if (column) {
			setHeaderCell(row, index, column.languageCode)
		}

		if ((index - prevIndex) > 1) {
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), prevIndex, index - 1))
		}
	}

	/**
	 * Creates the header row of the Excel export with the labels.
	 * @param row The header row.
	 * @param columns The column names to export.
	 * @return The number of columns necessary.
	 */
	private int createHeaderWithLabels(Row row, List<String> columns) {
		int i = 0
		if (columns.contains(CollectionXlsColumn.ID.name)) setHeaderCell(row, i++, '#')
		if (columns.contains(CollectionXlsColumn.NAME.name)) setHeaderCell(row, i++, 'collection.name.label')
		if (columns.contains(CollectionXlsColumn.ACQUISITION_ID.name)) setHeaderCell(row, i++, 'collection.acquisitionId.label')
		if (columns.contains(CollectionXlsColumn.ADDED_BY.name)) setHeaderCell(row, i++, 'collection.addedBy.label')
		if (columns.contains(CollectionXlsColumn.PID.name)) setHeaderCell(row, i++, 'collection.objectRepositoryPID.label')

		if ((maxNumberOfLocations > 0) && columns.contains(CollectionXlsColumn.LOCATION.name)) {
			(1..maxNumberOfLocations).each { int locationNumber ->
				setHeaderCell(row, i++, 'collection.location.extended.label', locationNumber.toString())
			}
		}

		if (columns.contains(CollectionXlsColumn.ANALOG_MATERIAL.name)) {
			materials.each { MaterialType materialType ->
				if (materialType.inMeters && materialType.inNumbers) {
					setHeaderCell(row, i++, "${materialType.getNameAnalog()} (${AnalogUnit.METER})")
					setHeaderCell(row, i++, "${materialType.getNameAnalog()} (${AnalogUnit.NUMBER})")
				} else {
					setHeaderCell(row, i++, materialType.getNameAnalog())
				}
			}
		}

		if (columns.contains(CollectionXlsColumn.DIGITAL_MATERIAL.name)) {
			materials.each { MaterialType materialType ->
				setHeaderCell(row, i++, materialType.getNameDigital())
			}

			setHeaderCell(row, i++, 'digitalMaterialCollection.numberOfFiles.export.label')
			setHeaderCell(row, i++, 'digitalMaterialCollection.totalSize.label')
		}

		if (columns.contains(CollectionXlsColumn.MISC_MATERIAL.name)) {
			miscMaterials.each { MiscMaterialType materialType ->
				setHeaderCell(row, i++, materialType.name)
			}
		}

		if (columns.contains(CollectionXlsColumn.CONTENT.name)) setHeaderCell(row, i++, 'collection.content.label')
		if (columns.contains(CollectionXlsColumn.LISTS_AVAILABLE.name)) setHeaderCell(row, i++, 'collection.listsAvailable.label')
		if (columns.contains(CollectionXlsColumn.TO_BE_DONE.name)) setHeaderCell(row, i++, 'collection.toBeDone.label')
		if (columns.contains(CollectionXlsColumn.PRIORITY.name)) setHeaderCell(row, i++, 'collection.priority.label')
		if (columns.contains(CollectionXlsColumn.LEVEL.name)) setHeaderCell(row, i++, 'collection.level.label')
		if (columns.contains(CollectionXlsColumn.OWNER.name)) setHeaderCell(row, i++, 'collection.owner.label')
		if (columns.contains(CollectionXlsColumn.CONTRACT.name)) setHeaderCell(row, i++, 'collection.contract.label')
		if (columns.contains(CollectionXlsColumn.ACCRUAL.name)) setHeaderCell(row, i++, 'collection.accrual.label')
		if (columns.contains(CollectionXlsColumn.APPRAISAL.name)) setHeaderCell(row, i++, 'collection.appraisal.label')
		if (columns.contains(CollectionXlsColumn.DATE_OF_ARRIVAL.name)) setHeaderCell(row, i++, 'collection.dateOfArrival.label')
		if (columns.contains(CollectionXlsColumn.CONTACT_PERSON.name)) setHeaderCell(row, i++, 'collection.contactPerson.label')
		if (columns.contains(CollectionXlsColumn.REMARKS.name)) setHeaderCell(row, i++, 'collection.remarks.label')
		if (columns.contains(CollectionXlsColumn.ORIGINAL_PACKAGE_TRANSPORT.name)) setHeaderCell(row, i++, 'collection.originalPackageTransport.label')
		if (columns.contains(CollectionXlsColumn.STATUS.name)) setHeaderCell(row, i++, 'collection.status.label')
		if (columns.contains(CollectionXlsColumn.COLLECTION_LEVEL_READY.name)) setHeaderCell(row, i++, 'collection.collectionLevelReady.label')

		return i - 1
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
	 * @param columns The column names to export.
	 */
	private void createRow(Collection collection, int rowNumber, List<String> columns) {
		Row row = sheet.createRow(rowNumber)
		int i = 0

		if (columns.contains(CollectionXlsColumn.ID.name)) setDataCellWithNumber(row, i++, collection.id)
		if (columns.contains(CollectionXlsColumn.NAME.name)) setDataCellWithText(row, i++, collection.name)
		if (columns.contains(CollectionXlsColumn.ACQUISITION_ID.name)) setDataCellWithText(row, i++,
				collection.acquisitionId ? "$collection.acquisitionTypeId $collection.acquisitionId" : '')
		if (columns.contains(CollectionXlsColumn.ADDED_BY.name)) setDataCellWithText(row, i++, collection.addedBy?.toString())
		if (columns.contains(CollectionXlsColumn.PID.name)) setDataCellWithText(row, i++, collection.objectRepositoryPID)

		if (columns.contains(CollectionXlsColumn.LOCATION.name)) {
			Set<Location> locations = collection.locations

			locations.each { Location location ->
				setDataCellWithText(row, i++, location.toDetailedString())
			}

			if ((maxNumberOfLocations - locations.size()) > 0) {
				(1..noOfEmptyCells).each {
					setDataCellWithText(row, i++, '')
				}
			}
		}

		if (columns.contains(CollectionXlsColumn.ANALOG_MATERIAL.name)) {
			AnalogMaterialCollection analogMaterialCollection = collection.analogMaterialCollection

			materials.each { MaterialType materialType ->
				if (materialType.inMeters && materialType.inNumbers) {
					AnalogMaterial analogMeter = analogMaterialCollection?.
							getMaterialByTypeAndUnit(materialType, AnalogUnit.METER)
					AnalogMaterial analogNumber = analogMaterialCollection?.
							getMaterialByTypeAndUnit(materialType, AnalogUnit.NUMBER)

					setDataCellWithNumber(row, i++, analogMeter?.size)
					setDataCellWithNumber(row, i++, analogNumber?.size)
				} else {
					Set<AnalogMaterial> analogMaterials = analogMaterialCollection?.getMaterialsByType(materialType)
					AnalogMaterial analogMaterial = (analogMaterials && !analogMaterials.isEmpty()) ?
							analogMaterials.first() : null

					setDataCellWithNumber(row, i++, analogMaterial?.size)
				}
			}
		}

		if (columns.contains(CollectionXlsColumn.DIGITAL_MATERIAL.name)) {
			DigitalMaterialCollection digitalMaterialCollection = collection.digitalMaterialCollection

			materials.each { MaterialType materialType ->
				String hasMaterial = digitalMaterialCollection?.getMaterialByType(materialType) ? 'YES' : ''
				setDataCellWithText(row, i++, hasMaterial)
			}

			setDataCellWithNumber(row, i++, digitalMaterialCollection?.numberOfFiles)
			setDataCellWithText(row, i++,
					digitalMaterialCollection ? digitalMaterialCollection.totalSizeToStringWithUnit() : '')
		}

		if (columns.contains(CollectionXlsColumn.MISC_MATERIAL.name)) {
			MiscMaterialCollection miscMaterialCollection = collection.miscMaterialCollection

			miscMaterials.each { MiscMaterialType materialType ->
				MiscMaterial material = miscMaterialCollection?.getMaterialByType(materialType)
				setDataCellWithNumber(row, i++, material?.size)
			}
		}

		if (columns.contains(CollectionXlsColumn.CONTENT.name)) setDataCellWithText(row, i++, collection.content)
		if (columns.contains(CollectionXlsColumn.LISTS_AVAILABLE.name)) setDataCellWithText(row, i++, collection.listsAvailable)
		if (columns.contains(CollectionXlsColumn.TO_BE_DONE.name)) setDataCellWithText(row, i++, collection.toBeDone)
		if (columns.contains(CollectionXlsColumn.PRIORITY.name)) setDataCellWithText(row, i++, collection.priority?.toString())
		if (columns.contains(CollectionXlsColumn.LEVEL.name)) setDataCellWithText(row, i++, collection.level?.toString())
		if (columns.contains(CollectionXlsColumn.OWNER.name)) setDataCellWithText(row, i++, collection.owner)
		if (columns.contains(CollectionXlsColumn.CONTRACT.name)) setDataCellWithText(row, i++, collection.contract?.toString())
		if (columns.contains(CollectionXlsColumn.ACCRUAL.name)) setDataCellWithText(row, i++, collection.accrual?.toString())
		if (columns.contains(CollectionXlsColumn.APPRAISAL.name)) setDataCellWithText(row, i++, collection.appraisal?.toString())
		if (columns.contains(CollectionXlsColumn.DATE_OF_ARRIVAL.name)) setDataCellWithDate(row, i++, collection.dateOfArrival)
		if (columns.contains(CollectionXlsColumn.CONTACT_PERSON.name)) setDataCellWithText(row, i++, collection.contactPerson)
		if (columns.contains(CollectionXlsColumn.REMARKS.name)) setDataCellWithText(row, i++, collection.remarks)
		if (columns.contains(CollectionXlsColumn.ORIGINAL_PACKAGE_TRANSPORT.name)) setDataCellWithText(row, i++, collection.originalPackageTransport)
		if (columns.contains(CollectionXlsColumn.STATUS.name)) setDataCellWithText(row, i++, collection.status?.toString())
		if (columns.contains(CollectionXlsColumn.COLLECTION_LEVEL_READY.name)) setDataCellWithText(row, i, collection.collectionLevelReady ? 'YES' : '')
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
