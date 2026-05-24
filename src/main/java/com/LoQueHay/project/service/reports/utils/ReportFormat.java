package com.LoQueHay.project.service.reports.utils;

public enum ReportFormat {
    PDF, XLSX, CSV;

    public static ReportFormat fromString(String format) {
        try {
            return ReportFormat.valueOf(format.toUpperCase());
        } catch (Exception e) {
            return PDF; // Default
        }
    }
}