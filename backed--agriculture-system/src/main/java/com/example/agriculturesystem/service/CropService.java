package com.example.agriculturesystem.service;

import com.example.agriculturesystem.exception.ResourceNotFoundException;
import com.example.agriculturesystem.model.Crop;
import com.example.agriculturesystem.repository.CropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropService {

    @Autowired
    private CropRepository cropRepository;

    // 获取作物信息
    public Crop getCropById(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found"));
    }

    // 获取所有作物信息
    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    // 添加新作物
    public Crop addCrop(Crop crop) {
        return cropRepository.save(crop);
    }

    // 更新作物
    public Crop updateCrop(Long cropId, Crop crop) {
        Crop existingCrop = cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found"));
        existingCrop.setName(crop.getName());
        existingCrop.setGrowthCycle(crop.getGrowthCycle());
        return cropRepository.save(existingCrop);
    }

    // 删除作物
    public void deleteCrop(Long cropId) {
        Crop existingCrop = cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found"));
        cropRepository.delete(existingCrop);
    }
}
