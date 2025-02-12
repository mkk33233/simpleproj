package com.example.agriculturesystem.repository;

import com.example.agriculturesystem.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {
    // 可以根据需要自定义查询方法，例如：
    List<Crop> findByName(String name); // 根据作物名称查找作物
}
