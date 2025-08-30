package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsCreateDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsUpdateDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.UserResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.TCCRelationships;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.entities.UserRole;
import br.com.unisc.unisctccsystembackend.repositories.TCCRelationshipsRepository;
import br.com.unisc.unisctccsystembackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TCCRelationshipsService {
    @Autowired
    private TCCRelationshipsRepository repository;

    @Autowired
    private UserRepository userRepository;

    public Page<TCCRelationshipsResponseDTO> getAllTCCs(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TCCRelationships> tccs = repository.findByStudent_NameContainingIgnoreCase(name, pageable);


        List<TCCRelationshipsResponseDTO> formattedTccList = tccs.stream().map(this::getTCCDTO).toList();
        return new PageImpl<>(formattedTccList, pageable, tccs.getTotalElements());
    }

    public void save(TCCRelationshipsCreateDTO tcc) throws Exception {
        User student = userRepository.findById(tcc.studentId()).orElseThrow(() -> new EntityNotFoundException("Student not found"));
        if (student.getRole() != UserRole.ALUNO) {
            throw new BadRequestException("The studentId provider is not Aluno");
        }
        User professor = userRepository.findById(tcc.professorId()).orElseThrow(() -> new EntityNotFoundException("Professor not found"));
        if(professor.getRole() != UserRole.PROFESSOR) {
            throw new BadRequestException("The professorId provider is not Professor");
        }
        TCCRelationships tccRelationships = getTccRelationships(tcc, student, professor);
        repository.save(tccRelationships);
    }

    public TCCRelationshipsResponseDTO getOneTCCById(Long id) {
        TCCRelationships tccRelationships =  repository.findById(id).orElseThrow(() -> new EntityNotFoundException("TCC not found"));
        return getTCCDTO(tccRelationships);
    }

    public void updateOneTCCById(TCCRelationshipsUpdateDTO tcc, Long tccId) throws Exception {
        TCCRelationships tccEntity = repository.findById(tccId).orElseThrow(() -> new EntityNotFoundException("TCC not found"));
        if(tcc.professorId() != null) {
            User professor = userRepository.findById(tcc.professorId()).orElseThrow(() -> new EntityNotFoundException("Professor not found"));
            if(professor.getRole() != UserRole.PROFESSOR) {
                throw new BadRequestException("The professorId provider is not Professor");
            }
            tccEntity.setProfessor(professor);
        }
        if(tcc.tccTitle() != null && !tcc.tccTitle().isEmpty()) {
            tccEntity.setTccTitle(tcc.tccTitle());
        }
        if (tcc.tccDeliveryDate() != null && !tcc.tccDeliveryDate().isEmpty()) {
            LocalDateTime tccDeliveryDate = LocalDateTime.parse(tcc.tccDeliveryDate());
            tccEntity.setTccDeliveryDate(tccDeliveryDate);
            tccEntity.setTccAssessmentDate(tccDeliveryDate.plusDays(7));
        }
        if(tcc.proposalDeliveryDate() != null && !tcc.proposalDeliveryDate().isEmpty()) {
            LocalDateTime proposalDeliveryDate = LocalDateTime.parse(tcc.proposalDeliveryDate());
            tccEntity.setProposalDeliveryDate(proposalDeliveryDate);
            tccEntity.setProposalAssessmentDate(proposalDeliveryDate.plusDays(7));
        }
        repository.save(tccEntity);
    }

    public void deleteOneTCCById(Long tccId) {
        TCCRelationships tccEntity = repository.findById(tccId).orElseThrow(() -> new EntityNotFoundException("TCC not found"));
        repository.delete(tccEntity);
    }

    private TCCRelationships getTccRelationships(TCCRelationshipsCreateDTO tcc, User student, User professor) {
        LocalDateTime proposalDeliveryDate = LocalDateTime.parse(tcc.proposalDeliveryDate());
        LocalDateTime tccDeliveryDate = LocalDateTime.parse(tcc.tccDeliveryDate());
        LocalDateTime proposalAssessmentDate = proposalDeliveryDate.plusDays(7);
        LocalDateTime tccAssessmentDate = tccDeliveryDate.plusDays(7);
        TCCRelationships tccRelationships = new TCCRelationships();
        tccRelationships.setProposalDeliveryDate(proposalDeliveryDate);
        tccRelationships.setTccDeliveryDate(tccDeliveryDate);
        tccRelationships.setProposalAssessmentDate(proposalAssessmentDate);
        tccRelationships.setTccAssessmentDate(tccAssessmentDate);
        tccRelationships.setStudent(student);
        tccRelationships.setProfessor(professor);
        return tccRelationships;
    }


    private TCCRelationshipsResponseDTO getTCCDTO(TCCRelationships tcc) {
        return new TCCRelationshipsResponseDTO(
                        tcc.getId(),
                        tcc.getTccTitle(),
                        tcc.getProposalDeliveryDate(),
                        tcc.getTccDeliveryDate(),
                        tcc.getProposalAssessmentDate(),
                        tcc.getTccAssessmentDate(),
                        new UserResponseDTO(
                                tcc.getStudent().getId(),
                                tcc.getStudent().getName(),
                                tcc.getStudent().getEmail(),
                                tcc.getStudent().getRole().name()
                        ),
                        new UserResponseDTO(
                                tcc.getProfessor().getId(),
                                tcc.getProfessor().getName(),
                                tcc.getProfessor().getEmail(),
                                tcc.getProfessor().getRole().name()
                        )
                );
    }
}
