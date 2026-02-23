package com.fpt.glassesshop.service;

import com.fpt.glassesshop.entity.LensOption;
import com.fpt.glassesshop.entity.dto.LensOptionDTO;
import com.fpt.glassesshop.entity.dto.PrescriptionDTO;
import com.fpt.glassesshop.repository.LensOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LensOptionService {

    @Autowired
    private LensOptionRepository lensOptionRepository;

    public List<LensOptionDTO> getAllLensOptions() {
        return lensOptionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<LensOptionDTO> getCompatibleLensOptions(PrescriptionDTO prescription) {
        List<LensOption> allLenses = lensOptionRepository.findAll();

        // Placeholder logic for compatibility based on prescription
        return allLenses.stream().map(lens -> {
            LensOptionDTO dto = mapToDTO(lens);

            boolean isHighPower = isHighPowerPrescription(prescription);

            if (isHighPower && !isHighIndexLens(lens)) {
                dto.setCompatibilityNote("Not recommended for high prescriptions. Consider high-index lenses (1.60+).");
            } else if (!isHighPower && isHighIndexLens(lens)) {
                dto.setCompatibilityNote("Compatible, but high-index may be unnecessary and more expensive.");
            } else {
                dto.setCompatibilityNote("Highly recommended for this prescription.");
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private boolean isHighPowerPrescription(PrescriptionDTO prescription) {
        if (prescription == null)
            return false;

        BigDecimal sphLeft = prescription.getSphLeft() != null ? prescription.getSphLeft() : BigDecimal.ZERO;
        BigDecimal sphRight = prescription.getSphRight() != null ? prescription.getSphRight() : BigDecimal.ZERO;
        BigDecimal cylLeft = prescription.getCylLeft() != null ? prescription.getCylLeft() : BigDecimal.ZERO;
        BigDecimal cylRight = prescription.getCylRight() != null ? prescription.getCylRight() : BigDecimal.ZERO;

        // Placeholder rule: if absolute SPH > 3.00 or CYL > 2.00, it's considered "high
        // power"
        return sphLeft.abs().compareTo(new BigDecimal("3.00")) > 0 ||
                sphRight.abs().compareTo(new BigDecimal("3.00")) > 0 ||
                cylLeft.abs().compareTo(new BigDecimal("2.00")) > 0 ||
                cylRight.abs().compareTo(new BigDecimal("2.00")) > 0;
    }

    private boolean isHighIndexLens(LensOption lens) {
        if (lens.getThickness() == null)
            return false;
        try {
            // Assume thickness is recorded as "1.56", "1.60", "1.67", etc.
            double thickness = Double.parseDouble(lens.getThickness());
            return thickness >= 1.60;
        } catch (NumberFormatException e) {
            // Fallback if thickness is a string like "High Index"
            String t = lens.getThickness().toLowerCase();
            return t.contains("1.6") || t.contains("1.7") || t.contains("high");
        }
    }

    private LensOptionDTO mapToDTO(LensOption lensOption) {
        return LensOptionDTO.builder()
                .lensOptionId(lensOption.getLensOptionId())
                .type(lensOption.getType())
                .thickness(lensOption.getThickness())
                .coating(lensOption.getCoating())
                .color(lensOption.getColor())
                .price(lensOption.getPrice())
                .build();
    }
}
