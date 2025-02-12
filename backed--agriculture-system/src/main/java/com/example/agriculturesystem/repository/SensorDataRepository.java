package com.example.agriculturesystem.repository;

import com.example.agriculturesystem.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    // 根据设备ID查找所有传感器数据
    List<SensorData> findByDeviceId(Long deviceId);

    // 根据时间范围查找传感器数据
    List<SensorData> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    // 根据设备ID查询历史数据


    // 根据设备ID和时间范围查询数据
    List<SensorData> findByDeviceIdAndTimestampBetween(Long deviceId, LocalDateTime start, LocalDateTime end);

    SensorData findTopByOrderByTimestampDesc();
}
