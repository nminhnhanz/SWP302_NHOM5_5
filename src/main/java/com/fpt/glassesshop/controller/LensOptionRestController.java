package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.dto.LensOptionDTO;
import com.fpt.glassesshop.entity.dto.PrescriptionDTO;
import com.fpt.glassesshop.service.LensOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lens-options")
public class LensOptionRestController {

    @Autowired
    private LensOptionService lensOptionService;

    @GetMapping
    public ResponseEntity<List<LensOptionDTO>> getAllLensOptions() {
        return ResponseEntity.ok(lensOptionService.getAllLensOptions());
    }

    @PostMapping("/compatible")
    public ResponseEntity<List<LensOptionDTO>> getCompatibleLensOptions(@RequestBody PrescriptionDTO prescription) {
        return ResponseEntity.ok(lensOptionService.getCompatibleLensOptions(prescription));
    }
}
