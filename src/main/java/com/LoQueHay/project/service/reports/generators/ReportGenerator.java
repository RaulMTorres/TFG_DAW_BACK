package com.LoQueHay.project.service.reports.generators;

import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;

public interface ReportGenerator {
    byte[] generate(ReportRequestDTO request);
}