package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.request.AddTurmaRequestDTO;
import com.sistema_escolar.dtos.request.CodeRequestDTO;
import com.sistema_escolar.dtos.request.CreateTurmaRequestDTO;
import com.sistema_escolar.dtos.request.TurmaRequestDTO;
import com.sistema_escolar.dtos.response.CodeResponseDTO;
import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.entities.Turma;
import com.sistema_escolar.exceptions.*;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import com.sistema_escolar.services.TurmaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class TurmaServiceTest {

    @InjectMocks
    private TurmaService turmaService;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private EstudanteRepository estudanteRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @BeforeEach
    void setup(){
        Turma turma = criarTurma();
        turma.setTurmaCode("7%$@&57632");
        turma.setCodeExpirationTime(LocalDateTime.now().plusHours(2));
        when(disciplinaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarDisciplina()));
        when(turmaRepository.findByNameAndDisciplina(ArgumentMatchers.anyString(), ArgumentMatchers.any(Disciplina.class)))
                .thenReturn(Optional.empty());
        when(estudanteRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(criarEstudante()));
        when(turmaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarTurma()));
        when(turmaRepository.findByEstudantes(ArgumentMatchers.any(Estudante.class)))
                .thenReturn(Optional.empty());
        when(professorRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(criarProfessor()));
        when(turmaRepository.findByIdAndProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Professor.class)))
                .thenReturn(Optional.empty());
        when(turmaRepository.findByProfessorId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarTurma()));
        when(turmaRepository.findByTurmaCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(turma));
        when(professorRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarProfessor()));
        when(estudanteRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarEstudante()));
        when(turmaRepository.findByProfessorIdAndTurmaCode(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("criarTurma deve cadastrar uma turma quando bem sucedido")
    void criarTurma_CadastraUmaTurma_QuandoBemSucedido() {
        assertThatCode(() -> turmaService.criarTurma(new CreateTurmaRequestDTO("Turma A", 1L)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("criarTurma deve lançar uma EntityAlreadyExistsException quando o nome da turma passado já existir")
    void criarTurma_LancaEntityAlreadyExistsException_QuandoNomeDaTurmaJaExistir(){
        when(turmaRepository.findByNameAndDisciplina(ArgumentMatchers.anyString(), ArgumentMatchers.any(Disciplina.class)))
                .thenReturn(Optional.of(criarTurma()));
        assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> turmaService.criarTurma(new CreateTurmaRequestDTO("Turma B", 1L)))
                .withMessage("A turma que está sendo criada já existe");
    }

    @Test
    @DisplayName("criarTurma deve lançar uma EntityNotFoundException quando o id da disciplina passado não existir")
    void criarTurma_LancaEntityNotFoundException_QuandoDisciplinaIdNaoExistir(){
        when(disciplinaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> turmaService.criarTurma(new CreateTurmaRequestDTO("Turma A", 2L)))
                .withMessage("Disciplina passada não existe");
    }

    @Test
    @DisplayName("addEstudante deve adicionar um estudante a uma turma quando bem sucedido")
    void addEstudante_AdicionaUmEstudanteAUmaTurma_QuandoBemSucedido() {
        assertThatCode(() -> turmaService.addEstudante(new AddTurmaRequestDTO("ciclano@example.com", 1L)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("addEstudante deve cadastrar um Estudante em uma Turma percorrendo as diferentes condições da relações entre as classes")
    void addEstudante_CadastraEstudanteEmUmaTurma_PercorrendoAsDiferentesCondicoes(){
        Turma turma = criarTurma();
        turma.setEstudantes(null);
        turma.setId(2L);
        turma.setTurmaCode("7%$@&57682");
        turma.setCodeExpirationTime(LocalDateTime.now().plusHours(2));
        Estudante estudante = criarEstudante();
        estudante.setTurmas(List.of(criarTurma()));
        when(turmaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(turma));
        when(estudanteRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(estudante));
        assertThatCode(() -> turmaService.addEstudante(new AddTurmaRequestDTO("ciclano@example.com", 1L)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("addEstudante deve lançar uma UserNotFoundException quando o email do estudante não existir no banco de dados")
    void addEstudante_LancaUserNotFoundException_QuandoEmailDoEstudanteNaoExistir(){
        when(estudanteRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> turmaService.addEstudante(new AddTurmaRequestDTO("fulano@example.com", 1L)))
                .withMessage("Email do estudante que deseja adicionar não existe");
    }

    @Test
    @DisplayName("addEstudante deve lançar uma EntityNotFoundException quando o id da turma passado não existir")
    void addEstudante_LancaEntityNotFoundException_QuandoTurmaIdNaoExistir(){
        when(turmaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> turmaService.addEstudante(new AddTurmaRequestDTO("ciclano@example.com", 2L)))
                .withMessage("Turma selecionada não existe");
    }

    @Test
    @DisplayName("addEstudante deve lançar uma UserAlreadyBelongsToAnEntityException quando o estudante a ser adicionado já pertencer a turma")
    void addEstudante_LancaUserAlreadyBelongsToAnEntityException_QuandoEstudanteJaPertencerATurma(){
        when(turmaRepository.findByEstudantes(ArgumentMatchers.any(Estudante.class)))
                .thenReturn(Optional.of(criarTurma()));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaService.addEstudante(new AddTurmaRequestDTO("ciclano@example.com", 1L)))
                .withMessage("Estudante já está cadastrado nesta turma!");
    }

    @Test
    @DisplayName("addProfessor deve adicionar um professor a uma turma quando bem sucedido")
    void addProfessor_AdicionaUmProfessorAUmaTurma_QuandoBemSucedido() {
        Professor professor = criarProfessor();
        professor.setDisciplina(null);
        when(professorRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(professor));
        assertThatCode(() -> turmaService.addProfessor(new AddTurmaRequestDTO("professor@example.com", 1L)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("addProfessor deve lançar uma UserNotFoundException quando o email do professor não existir no banco de dados")
    void addProfessor_LancaUserNotFoundException_QuandoEmailDoProfessorNaoExistir(){
        when(professorRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> turmaService.addProfessor(new AddTurmaRequestDTO("ciclano@example.com", 1L)))
                .withMessage("Email do professor que deseja adicionar não existe");
    }

    @Test
    @DisplayName("addProfessor deve lançar uma EntityNotFoundException quando o id da turma selecionada não existir")
    void addProfessor_LancaEntityNotFoundException_QuandoTurmaIdNaoExistir(){
        when(turmaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> turmaService.addProfessor(new AddTurmaRequestDTO("professor@example.com", 2L)))
                .withMessage("Turma selecionada não existe");
    }

    @Test
    @DisplayName("addProfessor deve lançar uma UserAlreadyBelongsToAnEntityException quando professor a ser adicionado já pertencer a turma")
    void addProfessor_LancaUserAlreadyBelongsToAnEntityException_QuandoProfessorJaPertencerATurma(){
        when(turmaRepository.findByIdAndProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Professor.class)))
                .thenReturn(Optional.of(criarTurma()));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaService.addProfessor(new AddTurmaRequestDTO("professor@example.com", 1L)))
                .withMessage("Professor já está cadastrado nesta turma!");
    }

    @Test
    @DisplayName("addProfessor deve lançar uma UserAlreadyBelongsToAnEntityException quando o professor já pertencer a outra disciplina")
    void addProfessor_LancaUserAlreadyBelongsToAnEntityException_QuandoProfessorJaPertencerAOutraDisciplina(){
        Professor professor = criarProfessor();
        Disciplina disciplina = criarDisciplina();
        disciplina.setName("Matemática");
        professor.setDisciplina(disciplina);
        when(professorRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(professor));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaService.addProfessor(new AddTurmaRequestDTO("professor@example.com", 1L)))
                .withMessage("Professor já está cadastrado em outra disciplina");
    }

    @Test
    @DisplayName("gerarCodigo recebe uma TurmaRequestDTO e deve retornar um CodeResponseDTO quando o código da turma for gerado com sucesso")
    void gerarCodigo_RecebeTurmaRequestDTO_RetornaCodeResponseDTO_QuandoCodigoDaTurmaEGeradoComSucesso() {
        CodeResponseDTO codeResponseDTO = turmaService.gerarCodigo(new TurmaRequestDTO(1L));
        assertThat(codeResponseDTO).isNotNull();
    }

    @Test
    @DisplayName("gerarCodigo recebe uma TurmaRequestDTO e deve lançar uma EntityNotFoundException quando o id da turma passado não existir")
    void gerarCodigo_RecebeTurmaRequestDTO_LancaEntityNotFoundException_QuandoTurmaIdNaoExistir(){
        when(turmaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> turmaService.gerarCodigo(new TurmaRequestDTO(2L)))
                .withMessage("Turma selecionada não existe");
    }

    @Test
    @DisplayName("gerarCodigo recebe um Professor e deve retornar um CodeResponseDTO quando o código da turma for gerado com sucesso")
    void gerarCodigo_RecebeProfessor_RetornaCodeResponseDTO_QuandoCodigoDaTurmaEGeradoComSucesso(){
        CodeResponseDTO codeResponseDTO = turmaService.gerarCodigo(criarProfessor());
        assertThat(codeResponseDTO).isNotNull();
    }

    @Test
    @DisplayName("gerarCodigo recebe um Professor e deve lançar uma UserDoesntBelongException quando o professor não estiver vinculado a nenhuma turma")
    void gerarCodigo_RecebeProfessor_LancaUserDoesntBelongException_QuandoProfessorNaoEstiverVinculadoANenhumaTurma(){
        when(turmaRepository.findByProfessorId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> turmaService.gerarCodigo(criarProfessor()))
                .withMessage("Professor não esta vinculado a nenhuma turma");
    }

    @Test
    @DisplayName("entrarTurma cadastrar um Estudante na turma quando bem sucedido")
    void entrarTurma_CadastraUmEstudanteNaTurma_QuandoBemSucedido() {
        assertThatCode(() -> turmaService.entrarTurma(new CodeRequestDTO("7%$@&57632"), criarEstudante()))
                .doesNotThrowAnyException();
        verify(estudanteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("entrarTurma cadastra um Professor na turma quando bem sucedido")
    void entrarTurma_CadastrarUmProfessorNaTurma_QuandoBemSucedido(){
        assertThatCode(() -> turmaService.entrarTurma(new CodeRequestDTO("7%$@&57632"), criarProfessor()))
                .doesNotThrowAnyException();
        verify(professorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("entrarTurma deve lançar uma InvalidCodeException quando o código da turma passado não existir")
    void entrarTurma_LancaInvalidCodeException_QuandoCodigoDaTurmaNaoExistir(){
        when(turmaRepository.findByTurmaCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> turmaService.entrarTurma(new CodeRequestDTO(""), criarEstudante()))
                .withMessage("Código de turma não existe!");
    }

    @Test
    @DisplayName("entrarTurma deve lançar uma InvalidCodeException quando o código da turma passado já estiver expirado")
    void entrarTurma_LancaInvalidCodeException_QuandoCodigoDaTurmaJaEstiverExpirado(){
        Turma turma = criarTurma();
        turma.setTurmaCode("7%$@&57632");
        turma.setCodeExpirationTime(LocalDateTime.now().minusHours(2));
        when(turmaRepository.findByTurmaCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(turma));
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> turmaService.entrarTurma(new CodeRequestDTO("7%$@&57632"), criarEstudante()))
                .withMessage("Código de turma está expirado!");
    }

    @Test
    @DisplayName("entrarTurma deve lançar uma UserAlreadyBelongsToAnEntityException quando o professor a entrar na turma ja fizer parte dela")
    void entrarTurma_LancaUserAlreadyBelongsToAnEntityException_QuandoProfessorJaFizerParteDaTurma(){
        when(turmaRepository.findByProfessorIdAndTurmaCode(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(criarTurma()));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaService.entrarTurma(new CodeRequestDTO("7%$@&57632"), criarProfessor()))
                .withMessage("Professor já está vinculado a essa turma");
    }

    @Test
    @DisplayName("entrarTurma deve lançar uma UserAlreadyBelongsToAnEntityException quando estudante a entrar na turma já estiver vinculado a outra turma dessa disciplina")
    void entrarTurma_LancaUserAlreadyBelongsToAnEntityException_QuandoEstudanteJaEstiverVinculadoAOutraTurmaDessaDisciplina(){
        Estudante estudante = criarEstudante();
        estudante.setDisciplinas(List.of(criarDisciplina()));
        when(estudanteRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(estudante));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaService.entrarTurma(new CodeRequestDTO("7%$@&57632"), criarEstudante()))
                .withMessage("Estudante já está vinculado a uma turma desta disciplina!");
    }

    @Test
    @DisplayName("entrarTurma deve cadastrar um Estudante na turma percorrendo as diferentes condições das relações entre as classes")
    void entrarTurma_CadastraEstudanteNaTurma_PercorrendoDiferentesCondicoes(){
        Turma turma = criarTurma();
        turma.setEstudantes(null);
        turma.setTurmaCode("7%$@&57632");
        turma.setCodeExpirationTime(LocalDateTime.now().plusHours(2));
        Estudante estudante = criarEstudante();
        estudante.setTurmas(List.of(turma));
        Disciplina disciplina = criarDisciplina();
        disciplina.setEstudantes(List.of(estudante));
        disciplina.setId(2L);
        estudante.setDisciplinas(List.of(disciplina));
        when(turmaRepository.findByTurmaCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(turma));
        when(estudanteRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(estudante));
        assertThatCode(() -> turmaService.entrarTurma(new CodeRequestDTO("7%$@&57632"), criarEstudante()))
                .doesNotThrowAnyException();
    }
}