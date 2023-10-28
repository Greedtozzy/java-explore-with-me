package ru.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.location.model.AdminLocation;

public interface AdminLocationRepository extends JpaRepository<AdminLocation, Long> {
}
