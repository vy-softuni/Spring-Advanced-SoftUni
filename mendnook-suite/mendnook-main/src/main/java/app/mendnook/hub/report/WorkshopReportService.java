package app.mendnook.hub.report;

import app.mendnook.hub.mend.MendRequest;
import app.mendnook.hub.mend.MendRequestService;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class WorkshopReportService {

    private final MendRequestService mendRequestService;

    public WorkshopReportService(MendRequestService mendRequestService) {
        this.mendRequestService = mendRequestService;
    }

    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public byte[] createMendWorkbook() {
        List<MendRequest> requests = mendRequestService.findAllForReport();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Mend requests");
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            createHeader(sheet, headerStyle);
            for (int index = 0; index < requests.size(); index++) {
                createDataRow(sheet, index + 1, requests.get(index));
            }
            for (int column = 0; column < 7; column++) {
                sheet.autoSizeColumn(column);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("The workbook could not be generated", exception);
        }
    }

    private void createHeader(Sheet sheet, CellStyle style) {
        String[] columns = {"Identifier", "Item", "Kind", "Urgency", "State", "Owner", "Submitted"};
        Row row = sheet.createRow(0);
        for (int index = 0; index < columns.length; index++) {
            row.createCell(index).setCellValue(columns[index]);
            row.getCell(index).setCellStyle(style);
        }
    }

    private void createDataRow(Sheet sheet, int rowIndex, MendRequest request) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(request.getId().toString());
        row.createCell(1).setCellValue(request.getItemLabel());
        row.createCell(2).setCellValue(request.getItemKind().name());
        row.createCell(3).setCellValue(request.getUrgency());
        row.createCell(4).setCellValue(request.getState().name());
        row.createCell(5).setCellValue(request.getOwner().getDisplayName());
        row.createCell(6).setCellValue(request.getSubmittedAt().toString());
    }
}
