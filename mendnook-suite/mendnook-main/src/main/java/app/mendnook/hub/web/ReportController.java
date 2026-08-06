package app.mendnook.hub.web;

import app.mendnook.hub.mend.MendRequestService;
import app.mendnook.hub.report.WorkshopReportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final MendRequestService mendRequestService;
    private final WorkshopReportService reportService;

    public ReportController(MendRequestService mendRequestService, WorkshopReportService reportService) {
        this.mendRequestService = mendRequestService;
        this.reportService = reportService;
    }

    @GetMapping
    String reports(Model model) {
        model.addAttribute("mends", mendRequestService.findAllForReport());
        return "reports";
    }

    @GetMapping("/mends.xlsx")
    ResponseEntity<byte[]> exportMends() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("mendnook-mends.xlsx").build());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportService.createMendWorkbook());
    }
}
