package org.unamur.dto;

import lombok.Setter;

import java.util.List;

public record PrMetrics(
   int criticalAlerts,
   int totalImpactedFiles,
   double riskScore,
   List<ImpactedClass> impactedClasses
) {}
