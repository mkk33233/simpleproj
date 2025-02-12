package com.example.agriculturesystem.controller;

import com.example.agriculturesystem.model.Crop;
import com.example.agriculturesystem.service.CropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/crops")
public class CropController {

    @Autowired
    private CropService cropService;

    // 获取单个作物信息
    @GetMapping("/{cropId}")
    public ResponseEntity<Crop> getCrop(@PathVariable Long cropId) {
        Crop crop = cropService.getCropById(cropId);
        return ResponseEntity.ok(crop);
    }

    // 获取所有作物信息
    @GetMapping
    public ResponseEntity<List<Crop>> getAllCrops() {
        List<Crop> crops = cropService.getAllCrops();
        return ResponseEntity.ok(crops);
    }

    // 添加新作物
    @PostMapping
    public ResponseEntity<Crop> addCrop(@RequestBody Crop crop) {
        Crop savedCrop = cropService.addCrop(crop);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCrop);
    }

    // 更新作物信息
    @PutMapping("/{cropId}")
    public ResponseEntity<Crop> updateCrop(@PathVariable Long cropId, @RequestBody Crop crop) {
        Crop updatedCrop = cropService.updateCrop(cropId, crop);
        return ResponseEntity.ok(updatedCrop);
    }

    // 删除作物
    @DeleteMapping("/{cropId}")
    public ResponseEntity<?> deleteCrop(@PathVariable Long cropId) {
        cropService.deleteCrop(cropId);
        return ResponseEntity.noContent().build();
    }
}
