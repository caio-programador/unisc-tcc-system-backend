package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.Deliverables;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliverablesRepository extends JpaRepository<Deliverables, Long> {
    List<Deliverables> findAllByTcc_StudentId_OrderByDeliveryDateDesc(Long id);
    List<Deliverables> findAllByTcc_Id_OrderByDeliveryDateDesc(Long id);
}
