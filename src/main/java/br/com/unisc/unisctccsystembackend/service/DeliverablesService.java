package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.DTO.DeliveryCreateDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.DeliveryResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.UserResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.Deliverables;
import br.com.unisc.unisctccsystembackend.entities.DeliveryStatus;
import br.com.unisc.unisctccsystembackend.entities.TCCRelationships;
import br.com.unisc.unisctccsystembackend.repositories.DeliverablesRepository;
import br.com.unisc.unisctccsystembackend.repositories.TCCRelationshipsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliverablesService {
    @Autowired
    private DeliverablesRepository deliverablesRepository;
    @Autowired
    private TCCRelationshipsRepository tccRelationshipsRepository;
    @Autowired
    private S3Service s3Service;

    public List<DeliveryResponseDTO> getAllDeliverablesByStudentId(Long studentId) {
        List<Deliverables> deliverables = deliverablesRepository.findAllByTcc_StudentId_OrderByDeliveryDateDesc(studentId);

        return deliverables.stream().map(this::mapToDTO).toList();
    }

    public List<DeliveryResponseDTO> getAllDeliverablesByTccId(Long tccId) {
        List<Deliverables> deliverables = deliverablesRepository.findAllByTcc_Id_OrderByDeliveryDateDesc(tccId);
        return deliverables.stream().map(this::mapToDTO).toList();
    }

    public void saveDeliverable(DeliveryCreateDTO deliverableDTO) throws BadRequestException {
        TCCRelationships tcc = tccRelationshipsRepository.findById(deliverableDTO.tccId()).orElseThrow(() ->
                new EntityNotFoundException("TCC not found"));
        tcc.setTccTitle(deliverableDTO.tccTitle());
        try {
            tccRelationshipsRepository.save(tcc);
        }catch (Exception e) {
            throw new BadRequestException("Erro ao salvar o título do TCC: ");
        }
        String bucketFileKey;
        try {
            bucketFileKey = s3Service.uploadFile(deliverableDTO.file());
        } catch (Exception e) {
            throw new BadRequestException("Erro ao fazer upload do arquivo: " + e.getMessage());
        }
        Deliverables deliverable = mapToEntity(deliverableDTO, tcc, bucketFileKey);

        deliverablesRepository.save(deliverable);
    }

    public void deleteDeliverable(Long deliverableId) throws BadRequestException {
        Deliverables deliverable = deliverablesRepository.findById(deliverableId).orElseThrow(() ->
                new EntityNotFoundException("Deliverable not found"));
        try {
            s3Service.deleteFile(deliverable.getBucketFileKey());
        } catch (Exception e) {
            throw new BadRequestException("Erro ao deletar o arquivo do S3: " + e.getMessage());
        }
        try {
            deliverablesRepository.deleteById(deliverableId);
        } catch (Exception e) {
            throw new BadRequestException("Erro ao deletar o deliverable: " + e.getMessage());
        }
    }

    public void updateDeliverableStatus(Long deliverableId, DeliveryStatus newStatus) throws BadRequestException {
        Deliverables deliverable = deliverablesRepository.findById(deliverableId).orElseThrow(() ->
                new EntityNotFoundException("Deliverable not found"));
        deliverable.setDeliveryStatus(newStatus);
        try {
            deliverablesRepository.save(deliverable);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Erro ao atualizar o status do deliverable: " + e.getMessage());
        }
    }

    public void updateDeliverableById(Long deliverableId, DeliveryCreateDTO deliverableDTO) throws BadRequestException {
        Deliverables existingDeliverable = deliverablesRepository.findById(deliverableId).orElseThrow(() ->
                new EntityNotFoundException("Deliverable not found"));

        existingDeliverable.setDeliveryDate(LocalDateTime.now());
        if (deliverableDTO.tccId() != null) {
            throw new BadRequestException("TCC Id cannot be changed.");
        }

        if(deliverableDTO.tccTitle() != null){
            TCCRelationships tcc = existingDeliverable.getTcc();
            tcc.setTccTitle(deliverableDTO.tccTitle());
            try {
                tccRelationshipsRepository.save(tcc);
            }catch (Exception e) {
                throw new BadRequestException("Erro ao salvar o título do TCC: ");
            }
        }
        if (deliverableDTO.file() != null && !deliverableDTO.file().isEmpty()) {
            try {
                s3Service.deleteFile(existingDeliverable.getBucketFileKey());
                String newBucketFileKey = s3Service.uploadFile(deliverableDTO.file());
                existingDeliverable.setBucketFileKey(newBucketFileKey);
            } catch (Exception e) {
                throw new BadRequestException("Erro ao atualizar o arquivo no S3: " + e.getMessage());
            }
        }
        if (deliverableDTO.deliveryType() != null) {
            existingDeliverable.setDeliveryType(deliverableDTO.deliveryType());
        }
        try {
            deliverablesRepository.save(existingDeliverable);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Erro ao atualizar o deliverable: " + e.getMessage());
        }
    }


    public byte[] downloadFile(String key) throws IOException {
        return s3Service.downloadFile(key);
    }

    private Deliverables mapToEntity(DeliveryCreateDTO deliverableDTO, TCCRelationships tcc, String bucketFileKey) {
        Deliverables deliverables = new Deliverables();
        deliverables.setTcc(tcc);
        deliverables.setBucketFileKey(bucketFileKey);
        LocalDateTime deliveryDate = LocalDateTime.now();
        deliverables.setDeliveryDate(deliveryDate);
        deliverables.setDeliveryType(deliverableDTO.deliveryType());
        deliverables.setDeliveryStatus(DeliveryStatus.AGUARDANDO_AVALIACAO);
        return deliverables;
    }

    private DeliveryResponseDTO mapToDTO(Deliverables deliverable) {
        return new DeliveryResponseDTO(
                deliverable.getId(),
                new TCCRelationshipsResponseDTO(
                        deliverable.getTcc().getId(),
                        deliverable.getTcc().getTccTitle(),
                        deliverable.getTcc().getProposalDeliveryDate(),
                        deliverable.getTcc().getTccDeliveryDate(),
                        deliverable.getTcc().getProposalAssessmentDate(),
                        deliverable.getTcc().getTccAssessmentDate(),
                        new UserResponseDTO(
                                deliverable.getTcc().getStudent().getId(),
                                deliverable.getTcc().getStudent().getName(),
                                deliverable.getTcc().getStudent().getEmail(),
                                deliverable.getTcc().getStudent().getRole().name()
                        ),
                        new UserResponseDTO(
                                deliverable.getTcc().getProfessor().getId(),
                                deliverable.getTcc().getProfessor().getName(),
                                deliverable.getTcc().getProfessor().getEmail(),
                                deliverable.getTcc().getProfessor().getRole().name()
                        )
                ),
                deliverable.getDeliveryType(),
                deliverable.getDeliveryStatus(),
                deliverable.getBucketFileKey(),
                deliverable.getDeliveryDate()
        );
    }

}
