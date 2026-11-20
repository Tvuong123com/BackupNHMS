"use client";

import { useState } from "react";

import {
    IncidentDetailsSection,
    type IncidentFormValues,
} from "./incident-details-section";

import { ReportFooter } from "./report-footer";

import { incidentsApi } from "@/services/incidents/incidents-api";

export function IncidentReport() {

    const [form, setForm] = useState<IncidentFormValues>({
        residentLabel: "",
        incidentType: "FALL",
        severityId: undefined,
        residentId: undefined,
        dateTime: "",
        location: "",
        description: "",
        witnesses: "",
    });

    // Không dùng alert nữa. Nếu có lỗi (validate hoặc gọi API thất bại),
    // ném Error ra để ReportFooter bắt và hiển thị bằng dialog.
    const handleSubmit = async () => {
        if (!form.residentId) {
            throw new Error("Please select a Resident from the dropdown list before reporting.");
        }

        if (!form.description || form.description.trim().length < 5) {
            throw new Error("Please enter an incident description (at least 5 characters).");
        }

        const occurredAtStr = form.dateTime && form.dateTime.includes("T")
            ? form.dateTime.replace("T", " ") + ":00"
            : new Date().toISOString().slice(0, 19).replace("T", " ");

        await incidentsApi.create({
            residentID: form.residentId,
            incidentType: form.incidentType as any,
            severityID: form.severityId || 2,
            occurredAt: occurredAtStr,
            location: form.location || "Resident Room",
            description: form.description,
            witnesses: form.witnesses || "Attending Staff",
        });
    };

    return (

        <>
            <IncidentDetailsSection
                onChange={setForm}
            />

            <ReportFooter
                onSubmit={handleSubmit}
            />
        </>

    );

}