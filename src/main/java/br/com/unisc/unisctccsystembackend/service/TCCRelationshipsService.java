package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.*;
import br.com.unisc.unisctccsystembackend.entities.DTO.*;
import br.com.unisc.unisctccsystembackend.repositories.DefensePanelRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class TCCRelationshipsService {
    @Autowired
    private TCCRelationshipsRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DefensePanelRepository defensePanelRepository;

    @Autowired
    private AlertService alertService;

    public Page<TCCRelationshipsResponseDTO> getAllTCCs(String name, int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TCCRelationships> tccs = repository.findByStudent_NameContainingIgnoreCase(name, pageable);


        List<TCCRelationshipsResponseDTO> formattedTccList = tccs.stream().map(this::getTCCDTO).toList();

        if(currentUser.getRole().equals(UserRole.PROFESSOR)) {
            formattedTccList = formattedTccList.stream().filter(tcc -> tcc.defensePanel().professor1Id().equals(currentUser.getId())
                    || tcc.defensePanel().professor2Id().equals(currentUser.getId())
                    || tcc.defensePanel().professor3Id().equals(currentUser.getId())).toList();
        }


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

        verifyProfessors(professor.getId(), tcc.professor2Id(), tcc.professor3Id());

        User professor2 = userRepository.findById(tcc.professor2Id()).orElseThrow(() -> new EntityNotFoundException("Professor not found"));
        if(professor2.getRole() != UserRole.PROFESSOR) {
            throw new BadRequestException("The professor2Id provider is not Professor");
        }

        User professor3 = userRepository.findById(tcc.professor3Id()).orElseThrow(() -> new EntityNotFoundException("Professor not found"));
        if(professor3.getRole() != UserRole.PROFESSOR) {
            throw new BadRequestException("The professor3Id provider is not Professor");
        }


        TCCRelationships tccRelationships = getTccRelationships(tcc, student, professor, professor2, professor3);
        repository.save(tccRelationships);
    }

    public TCCRelationshipsResponseDTO getOneTCCById(Long id) {
        TCCRelationships tccRelationships = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("TCC not found"));
        return getTCCDTO(tccRelationships);
    }

    public TCCRelationshipsResponseDTO getOneTCCByStudentId(Long studentId) throws BadRequestException {
        User student = userRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student not found"));
        if (student.getRole() != UserRole.ALUNO) {
            throw new BadRequestException("The studentId provider is not Student");
        }
        TCCRelationships tccRelationships = repository.findByStudent_Id(studentId).orElseThrow(() ->
                new EntityNotFoundException("TCC not found for the given studentId"));
        return getTCCDTO(tccRelationships);
    }

    public void updateOneTCCById(TCCRelationshipsUpdateDTO tcc, Long tccId) throws Exception {
        TCCRelationships tccEntity = repository.findById(tccId).orElseThrow(() -> new EntityNotFoundException("TCC not found"));
        if(tcc.professorId() != null) {
            verifyProfessors(tcc.professorId(), tccEntity.getDefensePanel().getProfessor2().getId(),
                    tccEntity.getDefensePanel().getProfessor3().getId());
            User professor = userRepository.findById(tcc.professorId()).orElseThrow(() -> new EntityNotFoundException("Professor not found"));
            if(professor.getRole() != UserRole.PROFESSOR) {
                throw new BadRequestException("The professorId provider is not Professor");
            }
            tccEntity.setProfessor(professor);
            tccEntity.getDefensePanel().setProfessor1(professor);
            if(!tcc.professorId().equals(tccEntity.getProfessor().getId())) {
                alertService.createOrUpdateAlert(
                        professor,
                        "Você foi designado como membro da Banca Examinadora do TCC do(a) aluno(a) "
                                +tccEntity.getStudent().getName()+". Por favor, verifique os detalhes da Proposta e prepare-se para a avaliação",
                        tccEntity.getProposalAssessmentDate(),
                        AlertType.ATRASO_AVALIACAO,
                        null
                );
                alertService.createOrUpdateAlert(
                        professor,
                        "Você foi designado como membro da Banca Examinadora do TCC do(a) aluno(a) "
                                +tccEntity.getStudent().getName()+". Por favor, verifique os detalhes do TCC e prepare-se para a avaliação",
                        tccEntity.getTccAssessmentDate(),
                        AlertType.ATRASO_AVALIACAO,
                        null
                );
            }
        }
        if(tcc.professor2Id() != null) {
            verifyProfessors(tccEntity.getProfessor().getId(), tcc.professor2Id(),
                    tccEntity.getDefensePanel().getProfessor3().getId());
            User professor2 = userRepository.findById(tcc.professor2Id()).orElseThrow(() -> new EntityNotFoundException("Professor not found"));
            if(professor2.getRole() != UserRole.PROFESSOR) {
                throw new BadRequestException("The professor2Id provider is not Professor");
            }
            tccEntity.getDefensePanel().setProfessor2(professor2);
            if(!tcc.professor2Id().equals(tccEntity.getDefensePanel().getProfessor2().getId())) {
                alertService.createOrUpdateAlert(
                        professor2,
                        "Você foi designado como membro da Banca Examinadora do TCC do(a) aluno(a) "
                                +tccEntity.getStudent().getName()+". Por favor, verifique os detalhes da Proposta e prepare-se para a avaliação",
                        tccEntity.getProposalAssessmentDate(),
                        AlertType.ATRASO_AVALIACAO,
                        null
                );
                alertService.createOrUpdateAlert(
                        professor2,
                        "Você foi designado como membro da Banca Examinadora do TCC do(a) aluno(a) "
                                +tccEntity.getStudent().getName()+". Por favor, verifique os detalhes do TCC e prepare-se para a avaliação",
                        tccEntity.getTccAssessmentDate(),
                        AlertType.ATRASO_AVALIACAO,
                        null
                );
            }
        }

        if(tcc.professor3Id() != null) {
            verifyProfessors(tccEntity.getProfessor().getId(),
                    tccEntity.getDefensePanel().getProfessor2().getId(), tcc.professor3Id());
            User professor3 = userRepository.findById(tcc.professor3Id()).orElseThrow(() -> new EntityNotFoundException("Professor not found"));
            if(professor3.getRole() != UserRole.PROFESSOR) {
                throw new BadRequestException("The professor3Id provider is not Professor");
            }
            tccEntity.getDefensePanel().setProfessor3(professor3);
            if(!tcc.professor3Id().equals(tccEntity.getDefensePanel().getProfessor3().getId())) {
                alertService.createOrUpdateAlert(
                        professor3,
                        "Você foi designado como membro da Banca Examinadora do TCC do(a) aluno(a) "
                                +tccEntity.getStudent().getName()+". Por favor, verifique os detalhes da Proposta e prepare-se para a avaliação",
                        tccEntity.getProposalAssessmentDate(),
                        AlertType.ATRASO_AVALIACAO,
                        null
                );
                alertService.createOrUpdateAlert(
                        professor3,
                        "Você foi designado como membro da Banca Examinadora do TCC do(a) aluno(a) "
                                +tccEntity.getStudent().getName()+". Por favor, verifique os detalhes do TCC e prepare-se para a avaliação",
                        tccEntity.getTccAssessmentDate(),
                        AlertType.ATRASO_AVALIACAO,
                        null
                );
            }
        }

        if(tcc.tccTitle() != null && !tcc.tccTitle().isEmpty()) {
            tccEntity.setTccTitle(tcc.tccTitle());
        }
        if (tcc.tccDeliveryDate() != null && !tcc.tccDeliveryDate().isEmpty()) {
            LocalDateTime tccDeliveryDate = LocalDateTime.parse(tcc.tccDeliveryDate().split("\\.")[0]);
            tccEntity.setTccDeliveryDate(tccDeliveryDate);
            tccEntity.setTccAssessmentDate(tccDeliveryDate.plusDays(7));

            alertService.createOrUpdateAlert(
                    tccEntity.getStudent(),
                    "A data de entrega de seu TCC mudou.Você deve entregar o TCC até a data limite",
                    tccDeliveryDate,
                    AlertType.ATRASO_ENTREGA,
                    null
            );

        }
        if(tcc.proposalDeliveryDate() != null && !tcc.proposalDeliveryDate().isEmpty()) {
            LocalDateTime proposalDeliveryDate = LocalDateTime.parse(tcc.proposalDeliveryDate().split("\\.")[0]);
            tccEntity.setProposalDeliveryDate(proposalDeliveryDate);
            tccEntity.setProposalAssessmentDate(proposalDeliveryDate.plusDays(7));
            alertService.createOrUpdateAlert(
                    tccEntity.getStudent(),
                    "A data de entrega de sua Proposta de TCC mudou.Você deve entregar a Proposta de TCC até a data limite",
                    proposalDeliveryDate,
                    AlertType.ATRASO_ENTREGA,
                    null
            );
        }

        if(tcc.admissibility() != null && !tcc.admissibility().isEmpty()) {
            tccEntity.setAdmissibility(Admissibility.valueOf(tcc.admissibility()));
        }

        defensePanelRepository.save(tccEntity.getDefensePanel());

        repository.save(tccEntity);
    }

    public void deleteOneTCCById(Long tccId) {
        TCCRelationships tccEntity = repository.findById(tccId).orElseThrow(() -> new EntityNotFoundException("TCC not found"));
        repository.delete(tccEntity);
    }

    private TCCRelationships getTccRelationships(TCCRelationshipsCreateDTO tcc, User student, User professor,
                                                 User professor2, User professor3) {
        LocalDateTime proposalDeliveryDate = LocalDateTime.parse(tcc.proposalDeliveryDate().split("\\.")[0]);
        LocalDateTime tccDeliveryDate = LocalDateTime.parse(tcc.tccDeliveryDate().split("\\.")[0]);
        LocalDateTime proposalAssessmentDate = proposalDeliveryDate.plusDays(7);
        LocalDateTime tccAssessmentDate = tccDeliveryDate.plusDays(7);
        TCCRelationships tccRelationships = new TCCRelationships();
        tccRelationships.setProposalDeliveryDate(proposalDeliveryDate);
        tccRelationships.setTccDeliveryDate(tccDeliveryDate);
        tccRelationships.setProposalAssessmentDate(proposalAssessmentDate);
        tccRelationships.setTccAssessmentDate(tccAssessmentDate);
        tccRelationships.setStudent(student);
        alertService.createOrUpdateAlert(
                student,
                "Você deve entregar a Proposta de TCC até a data limite",
                proposalDeliveryDate,
                AlertType.ATRASO_ENTREGA,
                null
        );

        alertService.createOrUpdateAlert(
                student,
                "Você deve entregar o TCC até a data limite",
                tccDeliveryDate,
                AlertType.ATRASO_ENTREGA,
                null
                );
        tccRelationships.setProfessor(professor);
        DefensePanel defensePanel = new DefensePanel();
        defensePanel.setProfessor1(professor);
        defensePanel.setProfessor2(professor2);
        defensePanel.setProfessor3(professor3);
        User[] professors = {professor, professor2, professor3};
        Arrays.stream(professors).forEach(singleProfessor -> {
            alertService.createOrUpdateAlert(
                    singleProfessor,
                    "Você foi designado como membro da Banca Examinadora do TCC do(a) aluno(a) "
                            +student.getName()+". Por favor, verifique os detalhes da Proposta e prepare-se para a avaliação",
                    proposalAssessmentDate,
                    AlertType.ATRASO_AVALIACAO,
                    null
            );
            alertService.createOrUpdateAlert(
                    singleProfessor,
                    "Você foi designado como membro da Banca Examinadora do TCC do(a) aluno(a) "
                            +student.getName()+". Por favor, verifique os detalhes do TCC e prepare-se para a avaliação",
                    tccAssessmentDate,
                    AlertType.ATRASO_AVALIACAO,
                    null
            );
        });


        defensePanel = defensePanelRepository.save(defensePanel);
        tccRelationships.setDefensePanel(defensePanel);
        tccRelationships.setAdmissibility(Admissibility.PENDING);
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
                        tcc.getAdmissibility(),
                        new DefensePanelDTO(
                                tcc.getDefensePanel().getProfessor1().getId(),
                                tcc.getDefensePanel().getProfessor1().getName(),
                                tcc.getDefensePanel().getProfessor2().getId(),
                                tcc.getDefensePanel().getProfessor2().getName(),
                                tcc.getDefensePanel().getProfessor3().getId(),
                                tcc.getDefensePanel().getProfessor3().getName()
                        ),
                        new UserResponseDTO(
                                tcc.getProfessor().getId(),
                                tcc.getProfessor().getName(),
                                tcc.getProfessor().getEmail(),
                                tcc.getProfessor().getRole().name()
                        )
                );
    }
    private void verifyProfessors(Long professorId, Long professor2Id, Long professor3Id) throws BadRequestException {
        if(professorId.equals(professor2Id)) {
            throw new BadRequestException("The professor2Id provider cannot be the same as professorId");
        }
        if (professorId.equals(professor3Id)) {
            throw new BadRequestException("The professor3Id provider cannot be the same as professorId");
        }
        if(professor2Id.equals(professor3Id)) {
            throw new BadRequestException("The professor3Id provider cannot be the same as professor2Id");
        }
    }

    public void updateAdmissibility(Long id, Admissibility admissibility) {
        TCCRelationships tcc = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("TCC not found"));
        tcc.setAdmissibility(admissibility);
        repository.save(tcc);

        alertService.createOrUpdateAlert(tcc.getStudent(),
                "A admissibilidade do seu TCC foi atualizada para: " + admissibility.name(),
                LocalDateTime.now(),
                AlertType.NOVO_PARECER,
                null);
    }
}
