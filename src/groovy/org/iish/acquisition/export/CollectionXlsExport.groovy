package org.iish.acquisition.export

import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.WorkbookUtil
import org.iish.acquisition.domain.*
import org.iish.acquisition.search.CollectionSearch
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder as LCH

import java.text.SimpleDateFormat

/**
 * Builds an Excel (xls) export of all collections for a search request.
 */
class CollectionXlsExport {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat('yyyy-MM-dd')

	private HSSFWorkbook workbook
	private HSSFSheet sheet

	private List<Collection> collections
	private List<MaterialType> materials

	private Integer maxNumberOfLocations = 0
	private MessageSource messageSource

	private CellStyle headerStyle
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

		this.messageSource = messageSource

		determineMaxNumberOfLocations()
		createHeaderStyle()
		createDateStyle()
	}

	/**
	 * Starts the building process of the Excel sheet/workbook.
	 */
	void build() {
		int noOfColumns = createHeader()

		collections.eachWithIndex { Collection collection, int i ->
			createRow(collection, ++i)
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
		HSSFFont headerFont = workbook.createFont()
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD)

		headerStyle = workbook.createCellStyle()
		headerStyle.setFont(headerFont)
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
		Row row = sheet.createRow(0)
		sheet.createFreezePane(0, 1)

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
			setHeaderCell(row, i++, 'collection.analogMaterialCollection.extended.label',
					materialType.getNameAnalog())
		}

		materials.each { MaterialType materialType ->
			setHeaderCell(row, i++, 'collection.digitalMaterialCollection.extended.label',
					materialType.getNameDigital())
		}

		setHeaderCell(row, i++, 'digitalMaterialCollection.numberOfFiles.extended.label')
		setHeaderCell(row, i++, 'digitalMaterialCollection.totalSize.extended.label')
		setHeaderCell(row, i++, 'digitalMaterialCollection.numberOfDiskettes.label')
		setHeaderCell(row, i++, 'digitalMaterialCollection.numberOfOpticalDisks.label')
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
		cell.setCellStyle(headerStyle)
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
			Set<AnalogMaterial> analogMaterials = analogMaterialCollection?.getMaterialsByType(materialType)
			if (!analogMaterials) {
				analogMaterials = []
			}

			setDataCellWithText(row, i++, analogMaterials.collect { "${it.sizeToString()} $it.unit" }.join(', '))
		}

		materials.each { MaterialType materialType ->
			String hasMaterial = digitalMaterialCollection?.getMaterialByType(materialType) ? 'YES' : ''
			setDataCellWithText(row, i++, hasMaterial)
		}

		setDataCellWithNumber(row, i++, digitalMaterialCollection?.numberOfFiles)
		setDataCellWithText(row, i++, digitalMaterialCollection ?
				"${digitalMaterialCollection.totalSizeToString()} ${digitalMaterialCollection.unit}" : '')
		setDataCellWithNumber(row, i++, digitalMaterialCollection?.numberOfDiskettes)
		setDataCellWithNumber(row, i++, digitalMaterialCollection?.numberOfOpticalDisks)
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
